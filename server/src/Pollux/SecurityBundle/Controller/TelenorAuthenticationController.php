<?php

namespace Pollux\SecurityBundle\Controller;


use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\EventListener\RouterListener;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

class TelenorAuthenticationController extends Controller {

  const TELENOR_OAUTH_STATE = 'telenor.oauth.state';
  const PHONE_NUMBER = 'internal.user.phone.number';

  public function loginAction(Request $request) {
    $telenorAuthState = uniqid();
    $phoneNumber = $request->get('phone_number');
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
      return $this->redirectToRoute('webservice.endpoint');
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
    return property_exists($userInfo, 'phone_number_verified')
        && $userInfo->phone_number_verified
        && property_exists($userInfo, 'phone_number')
        && $userInfo->phone_number == $this->get('session')->get(self::PHONE_NUMBER);
  }

}
