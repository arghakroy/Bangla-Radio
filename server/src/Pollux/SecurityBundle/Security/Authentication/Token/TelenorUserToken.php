<?php

namespace Pollux\SecurityBundle\Security\Authentication\Token;


use Symfony\Component\Security\Core\Authentication\Token\AbstractToken;

class TelenorUserToken extends AbstractToken {

  private $digest;
  private $nonce;
  private $created;

  private $providerKey;

  function __construct($providerKey, $digest, $nonce, $created, array $roles = array()) {
    parent::__construct($roles);

    if (empty($providerKey)) {
      throw new \InvalidArgumentException('$providerKey must not be empty.');
    }

    $this->digest = $digest;
    $this->nonce = $nonce;
    $this->created = $created;
    $this->providerKey = $providerKey;

    $this->setAuthenticated(count($roles) > 0);
  }

  public function getCredentials() {
    return null;
  }

  /**
   * @return string
   */
  public function getProviderKey() {
    return $this->providerKey;
  }

  /**
   * @return mixed
   */
  public function getDigest() {
    return $this->digest;
  }

  /**
   * @return mixed
   */
  public function getNonce() {
    return $this->nonce;
  }

  /**
   * @return mixed
   */
  public function getCreated() {
    return $this->created;
  }

}
