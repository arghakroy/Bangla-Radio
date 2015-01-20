<?php

namespace Pollux\SecurityBundle\Controller;


use Pollux\SecurityBundle\Security\Provider\TelenorAuthenticationProvider;
use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpKernel\EventListener\RouterListener;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

class TelenorAuthenticationController extends Controller {

  const TELENOR_OAUTH_STATE = 'telenor.oauth.state';

  public function loginAction() {
    $telenorAuthState = uniqid();
    $this->get('session')->set(self::TELENOR_OAUTH_STATE, $telenorAuthState);
    $authorizeUrl = $this->prepareAuthorizeUrl($telenorAuthState);

    return $this->redirect($authorizeUrl);
  }

  public function callbackAction(Request $request) {
    $telenorClient = $this->get('service.telenor.client');
    $logger = $this->get('logger');

    $this->validateIncomingRequest($request, $logger);
    $code = $request->query->get('code');
    $token = $telenorClient->getToken($code);
    $logger->debug("Code => $code");
    $logger->debug("Token => $token");
    $this->get('session')->set(TelenorAuthenticationProvider::AUTHORIZATION_CODE_KEY, $token);

    return $this->redirectToRoute('webservice.endpoint');
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

}
