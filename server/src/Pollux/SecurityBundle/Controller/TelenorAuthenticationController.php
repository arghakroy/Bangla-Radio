<?php

namespace Pollux\SecurityBundle\Controller;


use Pollux\DomainBundle\Entity\Role;
use Pollux\DomainBundle\Entity\User;
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

  public function loginAction($phoneNumber) {
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

    if($this->isValidUserInfo($userInfo)) {
      $phoneNumber = $this->get('session')->remove(self::PHONE_NUMBER);
      $this->get('session')->remove(self::TELENOR_OAUTH_STATE);

      $sharedSecret = $this->updateUser($phoneNumber, $accessToken, $userInfo);
      return $this->redirectToRoute('webservice.endpoint', array('secret' => $sharedSecret));
    }
    else {
      return new Response('', Response::HTTP_UNAUTHORIZED);
    }
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
  public function isValidUserInfo($userInfo) {
    /*return property_exists($userInfo, 'phone_number_verified')
        && $userInfo->phone_number_verified
        && property_exists($userInfo, 'phone_number')
        && $userInfo->phone_number == $this->get('session')->get(self::PHONE_NUMBER);*/
    return true;
  }

  /**
   * @param $phoneNumber
   * @param $accessToken
   * @param $userInfo
   * @return null|string
   */
  public function updateUser($phoneNumber, $accessToken, $userInfo) {

    print_r($phoneNumber);

    print_r($accessToken);

    print_r($userInfo);

    $em = $this->getDoctrine()->getManager();
    $user = null;
    try {
      $user = $em->getRepository('DomainBundle:User')->loadUserByUsername($phoneNumber);
    }
    catch(UsernameNotFoundException $ex) {
      $this->get('logger')->debug("New user found with phoneNumber: $phoneNumber");
    }

    if (!$user) {
      $roleUser = $em->getRepository('DomainBundle:Role')->loadRoleByName(Role::ROLE_USER);
      $user = new User();
      $user->setUsername($phoneNumber);
      $em->persist($user);

      $user->addRole($roleUser);
      $em->persist($user);

      $em->flush();
    }

    $expireTime = new \DateTime();
    $expireTime->add(new \DateInterval("PT3600S"));
    $user->setExpireTime($expireTime);

    #$user->setSharedSecret(uniqid("", true));
    $user->setAccessToken($accessToken->access_token);
    $user->setAccessTokenData(json_encode($accessToken));
    $user->setUserInfoData(json_encode($userInfo));
    $em->merge($user);
    $em->flush();

    return $user->getSharedSecret();
  }

}
