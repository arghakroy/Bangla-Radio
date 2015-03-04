<?php

namespace Pollux\SecurityBundle\Service;


use Doctrine\ORM\EntityManager;
use Pollux\DomainBundle\Entity\Payment;
use Pollux\DomainBundle\Entity\Product;
use Pollux\DomainBundle\Entity\User;
use Pollux\WebServiceBundle\Utils\Headers;
use Pollux\WebServiceBundle\Utils\MimeType;
use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Routing\Router;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

class TelenorClient {

  const DATE_TIME_FORMAT = 'Y-m-d\TH:i:s.uZ';

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
    return $this->endpoint . "id/users/" . $userInfo->sub . "/rights";
  }

  public function getRefreshTokenUrl() {
    return $this->endpoint . "oauth/token";
  }

  public function getTransactionUrl() {
    return "https://staging-payment-payment2.comoyo.com/transactions";
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

    $output = $this->executeInternal($curl);

    return json_decode($output);
  }

  public function getUserRights(User $user) {
    $accessToken = $this->getAccessToken($user);
    $curl = curl_init();
    curl_setopt_array($curl, array(
        CURLOPT_URL => $this->getRightsUrl($user),
        CURLOPT_HTTPHEADER => array(
            "Authorization: Bearer $accessToken"
        ),
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_CONNECTTIMEOUT => 3,
        CURLOPT_TIMEOUT => 20,
    ));

    $output = $this->executeInternal($curl);

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

    $output = $this->executeInternal($curl);

    return json_decode($output);
  }

  public function getTransaction(User $user, Payment $payment) {
    $product = $payment->getProduct();
    $queryParameters = array('paymentId' => $payment->getId());
    $transactionRedirectUrl = $this->router->generate('webservice.purchase.success', $queryParameters, UrlGeneratorInterface::ABSOLUTE_URL);
    $transactionCancelUrl = $this->router->generate('webservice.purchase.cancel', $queryParameters, UrlGeneratorInterface::ABSOLUTE_URL);
    $userInfo = json_decode($user->getUserInfoData());

    $productArray = array(
        'name' => $product->getProductName(),
        'price' => "MYR ".$product->getPricing(),
        'vatRate' => (string) $product->getVatPercentage(),
        'sku' => $product->getSku(),
        'timeSpec' => $product->getTimeSpec()
    );
    $parameters = array(
        "orderId" => $payment->getId(),
        "purchaseDescription" => "Product description",
        "amount" => "MYR ".$product->getPricing(),
        'vatRate' => (string) $product->getVatPercentage(),
        'merchantName' => $this->clientId,
        'connectId' => $userInfo->sub,
        "successRedirect" => $transactionRedirectUrl,
        'allowedPaymentMethods' => ['DOB'],
        'cancelRedirect' => $transactionCancelUrl,
        "products" => [$productArray]
    );
    $parameterString = json_encode($parameters);

    $curl = curl_init();
    curl_setopt_array($curl, array(
        CURLOPT_URL => $this->getTransactionUrl(),
        CURLOPT_HTTPHEADER => $this->prepareHeaders(array(
            Headers::CONTENT_TYPE => MimeType::APPLICATION_JSON,
            Headers::ACCEPT => MimeType::APPLICATION_JSON,
            Headers::Content_Length => strlen($parameterString),
            Headers::AUTHORIZATION => "Bearer " . $this->getAccessToken($user)
        )),
        CURLOPT_POSTFIELDS => $parameterString,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_CONNECTTIMEOUT => 3,
        CURLOPT_TIMEOUT => 20,
        CURLOPT_POST => 1,
    ));

    $output = $this->executeInternal($curl);
    $this->logger->debug($output);

    return json_decode($output);
  }

  public function addUserRight(User $user, Product $product, \DateTime $startTime, \DateTime $endTime) {
    $parameterString = json_encode(array(
        "sku" => $product->getSku(),
        "grantorId" => $this->clientId,
        "timeInterval" => $startTime->format('Y-m-d') . "/" . $endTime->format('Y-m-d'),
    ));

    $curl = curl_init();
    curl_setopt_array($curl, array(
        CURLOPT_URL => $this->getRightsUrl($user),
        CURLOPT_HTTPHEADER => $this->prepareHeaders(array(
            Headers::CONTENT_TYPE => MimeType::APPLICATION_JSON,
            Headers::ACCEPT => MimeType::APPLICATION_JSON,
            Headers::Content_Length => strlen($parameterString),
            Headers::AUTHORIZATION => "Bearer " . $this->getAccessToken($user),
        )),
        CURLOPT_POSTFIELDS => $parameterString,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_CONNECTTIMEOUT => 3,
        CURLOPT_TIMEOUT => 20,
        CURLOPT_POST => 1,
    ));

    $output = $this->executeInternal($curl);

    return json_decode($output);
  }

  private function getAccessToken(User $user) {
    $currentTime = new \DateTime();

    $accessToken = $user->getAccessToken();
    if ($currentTime >= $user->getExpireTime()) {
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

  private function refreshToken($refreshToken) {
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

    $output = $this->executeInternal($curl);

    return json_decode($output);
  }

  private function executeInternal($curl) {
    $output = curl_exec($curl);
    if(!curl_errno($curl)) {
      $info = curl_getinfo($curl);

      $statusCode = $info['http_code'];
      if($statusCode >= 300) {
        curl_close($curl);
        $this->logger->debug(sprintf("Can not handle status code: %s, response: \n%s", $statusCode, $output));
        throw new TelenorException(sprintf("Can not handle status code: %s", $statusCode));
      }
    }
    curl_close($curl);

    return $output;
  }

  private static function prepareQueryUrl(array $parameters) {
    $query = '';
    foreach ($parameters as $key => $value) {
      $query .= $key . "=" . $value . "&";
    }

    return $query;
  }

  private function prepareHeaders(array $parameters) {
    $headers = array();
    foreach ($parameters as $key => $value) {
      $headers[] = $key . ": " . $value;
    }

    return $headers;
  }


  /**
   * @param $links
   * @param $rel
   * @return \stdClass|null
   */
  public static function getLink($links, $rel) {
    $rel = strtoupper($rel);
    foreach($links as $link) {
      if((strtoupper($link->rel))== $rel) {
        return $link;
      }
    }

    return null;
  }

}
