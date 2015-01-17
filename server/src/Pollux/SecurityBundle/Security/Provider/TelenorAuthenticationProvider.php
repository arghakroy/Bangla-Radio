<?php

namespace Pollux\SecurityBundle\Security\Provider;


use Doctrine\ORM\EntityManager;
use Pollux\SecurityBundle\Security\Authentication\Token\TelenorUserToken;
use Psr\Log\LoggerInterface;
use Symfony\Component\HttpFoundation\Session\Session;
use Symfony\Component\Security\Core\Authentication\Provider\AuthenticationProviderInterface;
use Symfony\Component\Security\Core\Authentication\Token\TokenInterface;
use Symfony\Component\Security\Core\Exception\AuthenticationException;
use Symfony\Component\Security\Core\Exception\NonceExpiredException;

class TelenorAuthenticationProvider implements AuthenticationProviderInterface {
  const AUTHORIZATION_CODE_KEY = 'telenor.authorization.code';

  private $logger;

  private $cacheDir;
  private $providerKey;
  /**
   * @var EntityManager
   */
  private $entityManager;
  /**
   * @var Session
   */
  private $session;

  public function __construct($providerKey, $cacheDir, EntityManager $entityManager, Session $session, LoggerInterface $logger) {
    $this->cacheDir = $cacheDir;
    $this->logger = $logger;

    $this->providerKey = $providerKey;
    $this->entityManager = $entityManager;
    $this->session = $session;
  }

  public function authenticate(TokenInterface $token) {
    if ($token instanceof TelenorUserToken) {
      $secret = $this->session->get(self::AUTHORIZATION_CODE_KEY);
      $this->logger->debug("secret = $secret");
      if ($this->validateDigest($token, $secret)) {
        $this->logger->debug("Passed");
        return new TelenorUserToken($this->providerKey, $token->getDigest(), $token->getNonce(), $token->getCreated(), array('USER') );
      }
      $this->logger->debug("Failed");
    }

    throw new AuthenticationException('The WSSE authentication failed.');
  }

  /**
   * This function is specific to Wsse authentication and is only used to help this example
   *
   * For more information specific to the logic here, see
   * https://github.com/symfony/symfony-docs/pull/3134#issuecomment-27699129
   *
   * @param TelenorUserToken $token
   * @param $secret
   * @return bool
   */
  protected function validateDigest(TelenorUserToken $token, $secret) {
    if (!is_dir($this->cacheDir)) {
      mkdir($this->cacheDir, 0777, true);
    }
    file_put_contents($this->cacheDir . '/' . $token->getNonce(), time());

    // Validate Secret
    $expected = base64_encode(sha1(base64_decode($token->getNonce()) . $token->getCreated() . $secret, true));

    return $token->getDigest() === $expected;
  }

  public function supports(TokenInterface $token) {
    return $token instanceof TelenorUserToken
        && $this->providerKey == $token->getProviderKey()
        && $this->validateIfTokenIsNotExpired($token);
  }

  /**
   * @param TelenorUserToken $token
   * @return bool true if token is not expired
   * @throws NonceExpiredException if the token is expired
   */
  private function validateIfTokenIsNotExpired(TelenorUserToken $token) {
    // Check created time is not in the future
    if (strtotime($token->getCreated()) > time()) {
      $this->logger->debug("WSSE Token generated for future time: " . $token->getCreated());
      throw new NonceExpiredException("WSSE Token generated for future time: " . $token->getCreated());
    }

    // Expire timestamp after 5 minutes
    if (time() - strtotime($token->getCreated()) > 300) {
      $this->logger->debug("WSSE Token expired: " . $token->getCreated());
      throw new NonceExpiredException("WSSE Token expired: " . $token->getCreated());
    }

    // Validate that the nonce is *not* used in the last 5 minutes
    // if it has, this could be a replay attack
    if (file_exists($this->cacheDir . '/' . $token->getNonce()) && (int)file_get_contents($this->cacheDir . '/' . $token->getNonce()) + 300 > time()) {
      $this->logger->debug("Previously used nonce detected: " . $token->getNonce());
      throw new NonceExpiredException("Previously used nonce detected: " . $token->getNonce());
    }

    return true;
  }

}
