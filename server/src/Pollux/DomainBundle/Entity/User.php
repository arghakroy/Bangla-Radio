<?php

namespace Pollux\DomainBundle\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Security\Core\User\UserInterface;

/**
 * User
 *
 * @ORM\Table(name="user", uniqueConstraints={@ORM\UniqueConstraint(name="unique_username", columns={"username"})})
 * @ORM\Entity(repositoryClass="Pollux\DomainBundle\Repository\UserRepository")
 */
class User implements UserInterface, \Serializable {
  /**
   * @var string
   *
   * @ORM\Column(name="username", type="string", length=50, nullable=false)
   */
  private $username;

  /**
   * @var string
   *
   * @ORM\Column(name="access_token", type="string", length=64, nullable=true)
   */
  private $accessToken;

  /**
   * @var \DateTime
   *
   * @ORM\Column(name="expire_time", type="datetime", nullable=true)
   */
  private $expireTime;

  /**
   * @var string
   *
   * @ORM\Column(name="access_token_data", type="text", length=65535, nullable=true)
   */
  private $accessTokenData;

  /**
   * @var string
   *
   * @ORM\Column(name="user_info_data", type="text", length=65535, nullable=true)
   */
  private $userInfoData;

  /**
   * @var string
   *
   * @ORM\Column(name="user_rights_data", type="text", length=65535, nullable=true)
   */
  private $userRightsData;

  /**
   * @var string
   *
   * @ORM\Column(name="shared_secret", type="string", length=512, nullable=true)
   */
  private $sharedSecret;

  /**
   * @var integer
   *
   * @ORM\Column(name="id", type="integer")
   * @ORM\Id
   * @ORM\GeneratedValue(strategy="IDENTITY")
   */
  private $id;

  /**
   * @var \Doctrine\Common\Collections\Collection
   *
   * @ORM\ManyToMany(targetEntity="Pollux\DomainBundle\Entity\Role")
   * @ORM\JoinTable(name="user_role",
   *   joinColumns={
   *     @ORM\JoinColumn(name="user", referencedColumnName="id")
   *   },
   *   inverseJoinColumns={
   *     @ORM\JoinColumn(name="role", referencedColumnName="id")
   *   }
   * )
   */
  private $roles;

  /**
   * Constructor
   */
  public function __construct() {
    $this->roles = new \Doctrine\Common\Collections\ArrayCollection();
  }


  /**
   * Set username
   *
   * @param string $username
   * @return User
   */
  public function setUsername($username) {
    $this->username = $username;

    return $this;
  }

  /**
   * Get username
   *
   * @return string
   */
  public function getUsername() {
    return $this->username;
  }

  /**
   * Set accessToken
   *
   * @param string $accessToken
   * @return User
   */
  public function setAccessToken($accessToken) {
    $this->accessToken = $accessToken;

    return $this;
  }

  /**
   * Get accessToken
   *
   * @return string
   */
  public function getAccessToken() {
    return $this->accessToken;
  }

  /**
   * Set expireTime
   *
   * @param \DateTime $expireTime
   * @return User
   */
  public function setExpireTime($expireTime) {
    $this->expireTime = $expireTime;

    return $this;
  }

  /**
   * Get expireTime
   *
   * @return \DateTime
   */
  public function getExpireTime() {
    return $this->expireTime;
  }

  /**
   * Set accessTokenData
   *
   * @param string $accessTokenData
   * @return User
   */
  public function setAccessTokenData($accessTokenData) {
    $this->accessTokenData = $accessTokenData;

    return $this;
  }

  /**
   * Get accessTokenData
   *
   * @return string
   */
  public function getAccessTokenData() {
    return $this->accessTokenData;
  }

  /**
   * Set userInfoData
   *
   * @param string $userInfoData
   * @return User
   */
  public function setUserInfoData($userInfoData) {
    $this->userInfoData = $userInfoData;

    return $this;
  }

  /**
   * Get userInfoData
   *
   * @return string
   */
  public function getUserInfoData() {
    return $this->userInfoData;
  }

  /**
   * @return string
   */
  public function getUserRightsData() {
    return $this->userRightsData;
  }

  /**
   * @param string $userRightsData
   * @return User
   */
  public function setUserRightsData($userRightsData) {
    $this->userRightsData = $userRightsData;

    return $this;
  }


  /**
   * Set sharedSecret
   *
   * @param string $sharedSecret
   * @return User
   */
  public function setSharedSecret($sharedSecret) {
    $this->sharedSecret = $sharedSecret;

    return $this;
  }

  /**
   * Get sharedSecret
   *
   * @return string
   */
  public function getSharedSecret() {
    return $this->sharedSecret;
  }

  /**
   * Get id
   *
   * @return integer
   */
  public function getId() {
    return $this->id;
  }

  /**
   * Add roles
   *
   * @param \Pollux\DomainBundle\Entity\Role $roles
   * @return User
   */
  public function addRole(\Pollux\DomainBundle\Entity\Role $roles) {
    $this->roles[] = $roles;

    return $this;
  }

  /**
   * Remove roles
   *
   * @param \Pollux\DomainBundle\Entity\Role $roles
   */
  public function removeRole(\Pollux\DomainBundle\Entity\Role $roles) {
    $this->roles->removeElement($roles);
  }

  /**
   * Get roles
   *
   * @return \Doctrine\Common\Collections\Collection
   */
  public function getRoles() {
    return $this->roles->toArray();
  }

  /**
   * @inheritdoc
   */
  public function getPassword() {
    return $this->username;
  }

  /**
   * @inheritdoc
   */
  public function eraseCredentials() {
  }

  public function getSalt() {
    return "";
  }

  /**
   * @inheritdoc
   */
  public function serialize() {
    return serialize(array(
        $this->id,
    ));
  }

  /**
   * @inheritdoc
   */
  public function unserialize($serialized) {
    list ($this->id,) = unserialize($serialized);
  }

}
