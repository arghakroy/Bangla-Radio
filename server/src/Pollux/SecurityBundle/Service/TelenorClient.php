<?php

namespace Pollux\SecurityBundle\Service;


use Symfony\Bundle\FrameworkBundle\Routing\Router;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

class TelenorClient {

  /**
   * @var Router
   */
  private $router;
  private $endpoint;
  private $clientId;
  private $clientSecret;

  public function getAuthorizeUrl() {
    return $this->endpoint . "/authorize";
  }

  public function getUserInfoUrl() {
    return $this->endpoint . "/userinfo";
  }

  public function getTokenUrl() {
    return $this->endpoint . "/token";
  }

  public function __construct(Router $router, $endpoint, $clientId, $clientSecret) {
    $this->router = $router;
    $this->endpoint = $endpoint;
    $this->clientId = $clientId;
    $this->clientSecret = $clientSecret;
  }

  public function getUserInfo($accessToken) {
    $curl = curl_init();
    curl_setopt_array($curl, array(
        CURLOPT_URL => $this->getUserInfoUrl(),
        CURLOPT_HTTPHEADER => array(
          'Authorization' => "Bearer $accessToken"
        ),
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_CONNECTTIMEOUT => 3,
        CURLOPT_TIMEOUT => 20,
    ));

    $output = curl_exec($curl);
    curl_close($curl);

    return $output;
  }

  public function getToken($authorizationCode) {
    $tokenRedirectUrl = $this->router->generate('telenor.authentication.callback', array(), UrlGeneratorInterface::ABSOLUTE_URL);
    $parameters = array(
        "grant_type" => "authorization_code",
        "client_id" => $this->clientId,
        "redirect_uri" => $tokenRedirectUrl,
        'code' => $authorizationCode,
    );

    $curl = curl_init();
    curl_setopt_array($curl, array(
        CURLOPT_URL => $this->getTokenUrl(),
        CURLOPT_POSTFIELDS => $this->prepareQueryUrl($parameters),
        CURLOPT_HTTPAUTH => CURLAUTH_BASIC,
        CURLOPT_USERPWD => $this->clientId . ":" . $this->clientSecret,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_CONNECTTIMEOUT => 3,
        CURLOPT_TIMEOUT => 20,
        CURLOPT_POST => 1,
    ));

    $output = curl_exec($curl);
    curl_close($curl);

    return json_decode($output);
  }

  private static function prepareQueryUrl(array $parameters) {
    $query = '';
    foreach ($parameters as $key => $value) {
      $query .= $key . "=" . $value . "&";
    }

    return $query;
  }

}