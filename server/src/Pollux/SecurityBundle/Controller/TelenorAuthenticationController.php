<?php

namespace Pollux\SecurityBundle\Controller;


use Pollux\SecurityBundle\Security\Provider\TelenorAuthenticationProvider;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\EventListener\RouterListener;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

class TelenorAuthenticationController extends Controller {

  const STATE_KEY = 'oauthState';

  public function loginAction() {
    $state = uniqid();
    $this->get('session')->set(self::STATE_KEY, $state);
    $authorizeUrl = $this->prepareAuthorizeUrl($state);

    return $this->redirect($authorizeUrl);
  }

  public function callbackAction(Request $request) {
    $logger = $this->get('logger');

    $error = $request->query->get('error');
    $errorDescription = $request->query->get('error_description');
    if ($error || $errorDescription) {
      $logger->debug("Failed to authenticate $error=$errorDescription");
    }

    if ($request->query->get('state') != $this->get('session')->get(self::STATE_KEY)) {
      $logger->debug("Invalid state");
    }

    $code = $request->query->get('code');

    $logger->debug("===========================> code $code");

    $token = $this->getToken($code);
    $this->get('session')->set(TelenorAuthenticationProvider::AUTHORIZATION_CODE_KEY, $token);

    return $this->redirectToRoute('webservice.endpoint');
  }

  public function testAction(Request $request) {
    $code = $this->get('session')->get(TelenorAuthenticationProvider::AUTHORIZATION_CODE_KEY);
    var_dump($code);
    var_dump($request);
    var_dump($this->getUser());

    return new Response();
  }

  public function getTokenRedirectAction(Request $request) {
    $code = $this->get('session')->get(TelenorAuthenticationProvider::AUTHORIZATION_CODE_KEY);
    var_dump($code);
    var_dump($request);
    return new Response();
  }

  private function getToken($authorizationCode) {
    $parameters = array(
        "grant_type" => "authorization_code",
        "client_id" => $this->container->getParameter('telenor.client.id'),
        "redirect_uri" => $this->generateUrl('telenor.authentication.getTokenRedirect', null, UrlGeneratorInterface::ABSOLUTE_URL),
        'code' => $authorizationCode,
    );
    $basicAuthentication = $this->container->getParameter('telenor.client.id') . ":" . $this->container->getParameter('telenor.client.secret');

    $curl = curl_init();
    curl_setopt_array($curl, array(
        CURLOPT_URL => $this->container->getParameter('telenor.url.token'),
        CURLOPT_POSTFIELDS => $this->prepareQueryUrl($parameters),
        CURLOPT_HTTPAUTH => CURLAUTH_BASIC,
        CURLOPT_USERPWD => $basicAuthentication,
        CURLOPT_RETURNTRANSFER => 1,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_CONNECTTIMEOUT => 3,
        CURLOPT_TIMEOUT => 20
    ));

    $output = curl_exec($curl);
    curl_close($curl);

    return $output;
  }

  /**
   * @param $state
   * @return string
   */
  protected function prepareAuthorizeUrl($state) {
    $callbackUrl = $this->generateUrl('telenor.authentication.callback', array(), UrlGeneratorInterface::ABSOLUTE_URL);
    $callbackUrl = str_replace("localhost", "162.248.162.2", $callbackUrl);
    $callbackUrl = str_replace("http", "https", $callbackUrl);
    $queryParameters = array(
        'response_type' => 'code',
        'scope' => 'openid profile email phone',
        'redirect_uri' => $callbackUrl,
        'state' => $state,
        'client_id' => $this->container->getParameter('telenor.client.id'),
    );
    $queryParameters = http_build_query($queryParameters);
    $baseUrl = $this->container->getParameter('telenor.url.authorize');
    $authorizeUrl = $baseUrl . "?" . $queryParameters;
    return $authorizeUrl;
  }

  private function prepareQueryUrl(array $parameters) {
    $query = '';
    foreach ($parameters as $key => $value) {
      $query .= $key . "=" . $value . "&";
    }

    return $query;
  }

}
