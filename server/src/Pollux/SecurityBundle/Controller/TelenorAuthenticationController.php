<?php

namespace Pollux\SecurityBundle\Controller;


use Pollux\DomainBundle\Entity\Role;
use Pollux\DomainBundle\Entity\User;
use Pollux\SecurityBundle\Service\TelenorException;
use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\EventListener\RouterListener;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Symfony\Component\Security\Core\Exception\UsernameNotFoundException;

class TelenorAuthenticationController extends Controller {

  const TELENOR_OAUTH_STATE = 'telenor.oauth.state';
  const PHONE_NUMBER = 'internal.user.phone.number';

  public function loginAction() {
    $phoneNumber = "notimportant";
    $telenorAuthState = uniqid();
    $this->get('session')->set(self::PHONE_NUMBER, $phoneNumber);
    $this->get('session')->set(self::TELENOR_OAUTH_STATE, $telenorAuthState);
    $authorizeUrl = $this->prepareAuthorizeUrl($telenorAuthState);

    return $this->redirect($authorizeUrl);
  }

  public function callbackAction(Request $request) {
    $telenorClient = $this->get('service.telenor.client');
    $logger = $this->get('logger');

    $this->validateIncomingRequest($request, $logger);
    $code = $request->query->get('code');

    $accessToken = $telenorClient->getToken($code);
    $userInfo = $telenorClient->getUserInfo($accessToken->access_token);
    $logger->debug("User Info $accessToken->access_token");

    $url = "polluxmusic://cancelled";
    if($this->isValidUserInfo($userInfo, $request->query->get('state'))) {
      $phoneNumber = $this->get('session')->remove(self::PHONE_NUMBER);
      $this->get('session')->remove(self::TELENOR_OAUTH_STATE);

      $user = $this->updateUser($phoneNumber, $accessToken, $userInfo);
      $this->addFreeTrial($user);

      $url = "polluxmusic://success?sharedSecret={$userInfo->sub}";
    }

    return $this->redirect($url);
  }

  /**
   * @param $state
   * @return string
   */
  protected function prepareAuthorizeUrl($state) {
    $callbackUrl = $this->generateUrl('telenor.authentication.callback', array(), UrlGeneratorInterface::ABSOLUTE_URL);
    $queryParameters = array(
        'response_type' => 'code',
        'client_id' => $this->container->getParameter('telenor.client.id'),
        'scope' => 'payment.transactions.read payment.transactions.write payment.agreements.read payment.agreements.write id.user.right.read openid profile',
        'state' => $state,
        'redirect_uri' => $callbackUrl,
        'telenordigital_authentication_method' => 'signup_msisdn',
        'locale' => $this->container->getParameter('telenor.client.locale')
    );
    $queryParameters = http_build_query($queryParameters);

    $authorizeUrl = $this->get('service.telenor.client')->getAuthorizeUrl() . "?" . $queryParameters;
    $this->get('logger')->debug("Authorizing with URL: " . $authorizeUrl);

    return $authorizeUrl;
  }

  /**
   * @param Request $request
   * @param $logger
   */
  public function validateIncomingRequest(Request $request, LoggerInterface $logger) {
    $error = $request->query->get('error');
    $errorDescription = $request->query->get('error_description');
    if ($error || $errorDescription) {
      $message = "Failed to authenticate $error=$errorDescription";
      $logger->debug($message);
      throw new \InvalidArgumentException($message);
    }

    $telenorOauthState = $request->query->get('state');
    $savedTelenorState = $this->get('session')->get(self::TELENOR_OAUTH_STATE);
    if ($telenorOauthState != $savedTelenorState) {
      $message = "Possible session hijacking. $telenorOauthState != $savedTelenorState";
      $logger->debug($message);
      throw new \InvalidArgumentException($message);
    }
  }

  /**
   * @param $userInfo
   * @return bool
   */
  public function isValidUserInfo($userInfo, $queryStateValue) {
    $sessionStateValue = $this->get('session')->get(self::TELENOR_OAUTH_STATE);
    return $sessionStateValue && $sessionStateValue == $queryStateValue;
//      && property_exists($userInfo, 'phone_number_verified')
//      && $userInfo->phone_number_verified
//      && property_exists($userInfo, 'phone_number')
//      && $userInfo->phone_number == $this->get('session')->get(self::PHONE_NUMBER);
  }

  /**
   * @param $phoneNumber
   * @param $accessToken
   * @param $userInfo
   * @return User
   */
  public function updateUser($phoneNumber, $accessToken, $userInfo) {
    $em = $this->getDoctrine()->getManager();
    $user = null;
    try {
      $user = $em->getRepository('DomainBundle:User')->loadUserByUsername($userInfo->sub);
    }
    catch(UsernameNotFoundException $ex) {
      $this->get('logger')->debug("New user found with sub: $userInfo->sub");
    }

    if (!$user) {
      $roleUser = $em->getRepository('DomainBundle:Role')->loadRoleByName(Role::ROLE_USER);
      $user = new User();
      $user->setUsername($userInfo->sub);
      $em->persist($user);

      $user->addRole($roleUser);
      $em->persist($user);

      $em->flush();
    }

    $expireTime = new \DateTime();
    $expireTime->add(new \DateInterval("PT3600S"));
    $user->setExpireTime($expireTime);

    $user->setSharedSecret(uniqid("", true));
    $user->setAccessToken($accessToken->access_token);
    $user->setAccessTokenData(json_encode($accessToken));
    $user->setUserInfoData(json_encode($userInfo));
    $em->merge($user);
    $em->flush();

    return $user;
  }

  private function addFreeTrial(User $user) {
    $em = $this->getDoctrine()->getManager();
    $currentProduct = $em->getRepository('DomainBundle:Product')->getCurrentProduct();
    $now = new \DateTime();
    if($currentProduct->getSku() === 'MY-RADIO-RADIOBANGLA-TRIAL-M'
        && $now >= $currentProduct->getStartDate()
        && $now <= $currentProduct->getEndDate()) {
      $startTime = new \DateTime();
      $endTime = clone $startTime;
      $endTime->add(new \DateInterval("P30D"));
      try {
        $userRights = $this->get('service.telenor.client')->addUserRight($user, $currentProduct, $startTime, $endTime);
        if($userRights) {
          $user->setUserRightsData(json_encode($userRights));
          $em->merge($user);
          $em->flush();
        }
      }
      catch(TelenorException $ex) {
        $this->get('logger')->warning("Failed to add free trial for user " . $user);
      }
    }
  }
}
