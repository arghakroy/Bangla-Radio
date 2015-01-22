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
    return $this->endpoint . "oauth/authorize";
  }

  public function getUserInfoUrl() {
    return $this->endpoint . "oauth/userinfo";
  }

  public function getTokenUrl() {
    return $this->endpoint . "oauth/token";
  }

  public function getRightsUrl() {
    return $this->endpoint . "/users/";
  }
  
  public function getTransactionUrl() {
    return  "https://staging-payment-payment2.comoyo.com/transactions";
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
          "Authorization: Bearer $accessToken"
        ),
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_CONNECTTIMEOUT => 3,
        CURLOPT_TIMEOUT => 20,
    ));

    $output = curl_exec($curl);
    curl_close($curl);

    return json_decode($output);
  }


  public function getUsersRight($id, $accessToken) {

    $url = $this->getRightsUrl() . $id . "/rights";
    $curl = curl_init();
    curl_setopt_array($curl, array(
        CURLOPT_URL => $url,
        CURLOPT_HTTPHEADER => array(
          "Authorization: Bearer $accessToken"
        ),
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_CONNECTTIMEOUT => 3,
        CURLOPT_TIMEOUT => 20,
    ));

    $output = curl_exec($curl);
    curl_close($curl);

    var_dump($output);

    return json_decode($output);

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
  
  public function getTransaction($accessToken,$connectId,$product) {
    
    $transactionRedirectUrl = $this->router->generate('webservice.purchase.success', array('uniqueId'=>'testId'),  UrlGeneratorInterface::ABSOLUTE_URL);
    $transactionCancelUrl = $this->router->generate('webservice.purchase.cancel', array('uniqueId'=>'testId'), UrlGeneratorInterface::ABSOLUTE_URL);

    $productArray = array(
        'name' => $product->getProductName(),
        'price' => "MYR ".$product->getPricing(),
        'vatRate' => $product->getVatPercentage(),
        'sku' => $product->getSku(),
        'timeSpec' => $product->getTimeSpec()
    );
    $parameters = array(
        "orderId" => uniqid(),
        "purchaseDescription" => "Product description",
        "amount" => "MYR ".$product->getPricing(),
        'vatRate' => $product->getVatPercentage(),
        'merchantName' => 'lyltechnology-banglaradio-android',
        'connectId' => $connectId,
        "successRedirect" => $transactionRedirectUrl,
        'allowedPaymentMethods' => ['DOB'],
        'cancelRedirect' => $transactionCancelUrl,
        "products" => [$productArray]
    );
    $parameterString = json_encode($parameters);

    $curl = curl_init();
    curl_setopt_array($curl, array(
        CURLOPT_URL => $this->getTransactionUrl(),
        CURLOPT_HTTPHEADER => array(
          'Content-Type: application/json',
          'Content-Length: ' . strlen($parameterString),
          "Authorization: Bearer $accessToken"
        ),
        CURLOPT_POSTFIELDS => $parameterString,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_CONNECTTIMEOUT => 3,
        CURLOPT_TIMEOUT => 20,
        CURLOPT_POST => 1,
    ));

    $output = curl_exec($curl);
    curl_close($curl);
    
    return json_decode($output);
  }

}
