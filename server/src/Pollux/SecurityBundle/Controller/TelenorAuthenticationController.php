<?php

namespace Pollux\SecurityBundle\Controller;


use Pollux\DomainBundle\Entity\Payment;
use Pollux\DomainBundle\Entity\Product;
use Pollux\DomainBundle\Entity\Role;
use Pollux\DomainBundle\Entity\Subscription;
use Pollux\DomainBundle\Entity\User;
use Pollux\SecurityBundle\Service\TelenorClient;
use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpKernel\EventListener\RouterListener;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

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

      $this->updateUser($phoneNumber, $accessToken, $userInfo);
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
  private function updateUser($phoneNumber, $accessToken, $userInfo) {
    $user = $this->getDoctrine()->getManager()->getRepository('DomainBundle:User')->findUserByUsername($userInfo->sub);
    if (!$user) {
      $this->get('logger')->debug("New user found with sub: $userInfo->sub");
      $user = $this->addNewUser($userInfo);
    }

    $this->updateAccessTokenFor($user, $accessToken, $userInfo);
  }

  /**
   * @param $userInfo
   * @return User
   */
  private function addNewUser($userInfo) {
    $em = $this->getDoctrine()->getManager();
    $roleUser = $em->getRepository('DomainBundle:Role')->loadRoleByName(Role::ROLE_USER);
    $user = new User();
    $user->setUsername($userInfo->sub);
    $user->setSharedSecret(uniqid("", true));
    $em->persist($user);

    $user->addRole($roleUser);
    $em->persist($user);
    $em->flush();

    $this->addFreeTrial($user);
    return $user;
  }

  /**
   * @param User $user
   * @param $accessToken
   * @param $userInfo
   */
  private function updateAccessTokenFor(User $user, $accessToken, $userInfo) {
    $em = $this->getDoctrine()->getManager();
    $expireTime = new \DateTime();
    $expireTime->add(new \DateInterval("PT3600S"));
    $user->setExpireTime($expireTime);

    $user->setAccessToken($accessToken->access_token);
    $user->setAccessTokenData(json_encode($accessToken));
    $user->setUserInfoData(json_encode($userInfo));
    $em->merge($user);
    $em->flush();
  }

  /**
   * @param $user
   */
  private function addFreeTrial(User $user) {
    $freeProduct = $this->getDoctrine()->getManager()->getRepository('DomainBundle:Product')->getCurrentProduct();
    $userRights = $this->get('service.telenor.client')->getUserRights($user);

    foreach ($userRights->rights as $userRight) {
      if ($this->hasFreeSku($userRight, $freeProduct)) {
        $freePayment = $this->addFreeProduct($user, $freeProduct);
        $subscription = $this->createSubscription($freePayment, $userRight, $user);

        $em = $this->getDoctrine()->getManager();
        $em->persist($subscription);
        $em->flush();
        break;
      }
    }
  }

  private function hasFreeSku($userRight, Product $freeProduct) {
    return $userRight->sku == $freeProduct->getSku();
  }

  private function createSubscription(Payment $payment, $userRight, $user) {
    list($start, $end) = explode("/", $userRight->timeInterval);
    return Subscription::createSubscription()
        ->setUser($user)
        ->setPayment($payment)
        ->setDateCreated(new \DateTime())
        ->setConnectTxId($userRight->rightId)
        ->setConnectTxUrl(TelenorClient::getLink($userRight->link, 'self')->href)
        ->setConnectStatus($userRight->active)
        ->setConnectStartTime(date_create_from_format(TelenorClient::DATE_TIME_FORMAT, $start))
        ->setConnectEndTime(date_create_from_format(TelenorClient::DATE_TIME_FORMAT, $end))
        ->setConnectTxJson(json_encode($userRight));
  }

  private function addFreeProduct(User $user, Product $freeProduct) {
    $em = $this->getDoctrine()->getManager();
    $now = new \DateTime();

    $freePayment = Payment::createPayment()
        ->setUser($user)
        ->setInitiatedAt($now)
        ->setCompletedAt($now)
        ->setAmount($freeProduct->getPricing())
        ->setProduct($freeProduct)
        ->setTransactionResponse('{"free": true}');
    $em->persist($freePayment);
    return $freePayment;
  }

}
