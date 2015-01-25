<?php

namespace Pollux\SecurityBundle\Service;


use Doctrine\ORM\EntityManager;
use Pollux\DomainBundle\Entity\User;
use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Routing\Router;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

class TelenorClient {

  /**
   * @var LoggerInterface
   */
  private $logger;
  /**
   * @var EntityManager
   */
  private $entityManager;
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

  public function getRightsUrl(User $user) {
    $userInfo = json_decode($user->getUserInfoData());
    return $this->endpoint . "id/users/". $userInfo->sub . "/rights";
  }

  public function getRefreshTokenUrl() {
    return $this->endpoint . "oauth/token";
  }

  public function getTransactionUrl() {
    return  "https://staging-payment-payment2.comoyo.com/transactions";
  }

  public function __construct(EntityManager $entityManager, Router $router, LoggerInterface $logger, $endpoint, $clientId, $clientSecret) {
    $this->router = $router;
    $this->endpoint = $endpoint;
    $this->clientId = $clientId;
    $this->clientSecret = $clientSecret;
    $this->entityManager = $entityManager;
    $this->logger = $logger;
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

  public function getUserRights(User $user) {
    $url = $this->getRightsUrl($user);
    $accessToken = $this->getAccessToken($user);
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

    $userRights = json_decode($output);
    return $userRights;
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

  public function refreshToken($refreshToken){
    $parameters = array(
        "grant_type" => "refresh_token",
        "refresh_token" => $refreshToken,
    );

    $curl = curl_init();
    curl_setopt_array($curl, array(
        CURLOPT_URL => $this->getRefreshTokenUrl(),
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

  public function getTransaction($accessToken,$connectId,$product) {
    /*
     * Generate orderId as an unique number
     */
    $orderId = uniqid();

    $transactionRedirectUrl = $this->router->generate('webservice.purchase.success', array('uniqueId'=>$orderId),  UrlGeneratorInterface::ABSOLUTE_URL);
    $transactionCancelUrl = $this->router->generate('webservice.purchase.cancel', array('uniqueId'=>$orderId), UrlGeneratorInterface::ABSOLUTE_URL);

    $productArray = array(
        'name' => $product->getProductName(),
        'price' => "MYR ".$product->getPricing(),
        'vatRate' => $product->getVatPercentage(),
        'sku' => $product->getSku(),
        'timeSpec' => $product->getTimeSpec()
    );
    $parameters = array(
        "orderId" => $orderId,
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

  private function getAccessToken(User $user) {
    $currentTime = new \DateTime();

    $accessToken = $user->getAccessToken();
    if($currentTime >= $user->getExpireTime()) {
      $accessTokenData = json_decode($user->getAccessTokenData());
      $tokenResponse = $this->refreshToken($accessTokenData->refresh_token);
      $accessToken = $tokenResponse->access_token;

      $currentTime->add(new \DateInterval("PT3600S"));
      $user->setAccessToken($accessToken);
      $user->setExpireTime($currentTime);
      $user->setAccessTokenData(json_encode($tokenResponse));

      $this->entityManager->merge($user);
      $this->entityManager->flush();
    }

    return $accessToken;
  }

  private static function prepareQueryUrl(array $parameters) {
    $query = '';
    foreach ($parameters as $key => $value) {
      $query .= $key . "=" . $value . "&";
    }

    return $query;
  }

}
