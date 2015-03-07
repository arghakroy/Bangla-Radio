<?php

namespace Pollux\DomainBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Subscription
 *
 * @ORM\Table(name="subscription", uniqueConstraints={@ORM\UniqueConstraint(name="unique_subscription_payment", columns={"payment_id"})}, indexes={@ORM\Index(name="fk_subscription_payment_idx", columns={"payment_id"}), @ORM\Index(name="fk_subscription_user_idx", columns={"user_id"})})
 * @ORM\Entity(repositoryClass="Pollux\DomainBundle\Repository\SubscriptionRepository")
 */
class Subscription {
  /**
   * @var \DateTime
   *
   * @ORM\Column(name="date_created", type="datetime", nullable=false)
   */
  private $dateCreated;

  /**
   * @var string
   *
   * @ORM\Column(name="connect_tx_json", type="text", length=65535, nullable=false)
   */
  private $connectTxJson;

  /**
   * @var string
   *
   * @ORM\Column(name="connect_tx_id", type="string", length=150, nullable=false)
   */
  private $connectTxId;

  /**
   * @var string
   *
   * @ORM\Column(name="connect_tx_url", type="string", length=350, nullable=false)
   */
  private $connectTxUrl;

  /**
   * @var \DateTime
   *
   * @ORM\Column(name="connect_start_time", type="datetime", nullable=false)
   */
  private $connectStartTime;

  /**
   * @var \DateTime
   *
   * @ORM\Column(name="connect_end_time", type="datetime", nullable=false)
   */
  private $connectEndTime;

  /**
   * @var boolean
   *
   * @ORM\Column(name="connect_status", type="boolean", nullable=false)
   */
  private $connectStatus;

  /**
   * @var integer
   *
   * @ORM\Column(name="id", type="integer")
   * @ORM\Id
   * @ORM\GeneratedValue(strategy="IDENTITY")
   */
  private $id;

  /**
   * @var \Pollux\DomainBundle\Entity\Payment
   *
   * @ORM\OneToOne(targetEntity="Pollux\DomainBundle\Entity\Payment", inversedBy="subscription")
   * @ORM\JoinColumns({
   *   @ORM\JoinColumn(name="payment_id", referencedColumnName="id", unique=true)
   * })
   */
  private $payment;

  /**
   * @var \Pollux\DomainBundle\Entity\User
   *
   * @ORM\ManyToOne(targetEntity="Pollux\DomainBundle\Entity\User")
   * @ORM\JoinColumns({
   *   @ORM\JoinColumn(name="user_id", referencedColumnName="id")
   * })
   */
  private $user;


  /**
   * Set dateCreated
   *
   * @param \DateTime $dateCreated
   * @return Subscription
   */
  public function setDateCreated($dateCreated) {
    $this->dateCreated = $dateCreated;

    return $this;
  }

  /**
   * Get dateCreated
   *
   * @return \DateTime
   */
  public function getDateCreated() {
    return $this->dateCreated;
  }

  /**
   * Set connectTxJson
   *
   * @param string $connectTxJson
   * @return Subscription
   */
  public function setConnectTxJson($connectTxJson) {
    $this->connectTxJson = $connectTxJson;

    return $this;
  }

  /**
   * Get connectTxJson
   *
   * @return string
   */
  public function getConnectTxJson() {
    return $this->connectTxJson;
  }

  /**
   * Set connectTxId
   *
   * @param string $connectTxId
   * @return Subscription
   */
  public function setConnectTxId($connectTxId) {
    $this->connectTxId = $connectTxId;

    return $this;
  }

  /**
   * Get connectTxId
   *
   * @return string
   */
  public function getConnectTxId() {
    return $this->connectTxId;
  }

  /**
   * Set connectTxUrl
   *
   * @param string $connectTxUrl
   * @return Subscription
   */
  public function setConnectTxUrl($connectTxUrl) {
    $this->connectTxUrl = $connectTxUrl;

    return $this;
  }

  /**
   * Get connectTxUrl
   *
   * @return string
   */
  public function getConnectTxUrl() {
    return $this->connectTxUrl;
  }

  /**
   * Set connectStartTime
   *
   * @param \DateTime $connectStartTime
   * @return Subscription
   */
  public function setConnectStartTime($connectStartTime) {
    $this->connectStartTime = $connectStartTime;

    return $this;
  }

  /**
   * Get connectStartTime
   *
   * @return \DateTime
   */
  public function getConnectStartTime() {
    return $this->connectStartTime;
  }

  /**
   * Set connectEndTime
   *
   * @param \DateTime $connectEndTime
   * @return Subscription
   */
  public function setConnectEndTime($connectEndTime) {
    $this->connectEndTime = $connectEndTime;

    return $this;
  }

  /**
   * Get connectEndTime
   *
   * @return \DateTime
   */
  public function getConnectEndTime() {
    return $this->connectEndTime;
  }

  /**
   * Set connectStatus
   *
   * @param boolean $connectStatus
   * @return Subscription
   */
  public function setConnectStatus($connectStatus) {
    $this->connectStatus = $connectStatus;

    return $this;
  }

  /**
   * Get connectStatus
   *
   * @return boolean
   */
  public function getConnectStatus() {
    return $this->connectStatus;
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
   * Set payment
   *
   * @param \Pollux\DomainBundle\Entity\Payment $payment
   * @return Subscription
   */
  public function setPayment(\Pollux\DomainBundle\Entity\Payment $payment = null) {
    $this->payment = $payment;

    return $this;
  }

  /**
   * Get payment
   *
   * @return \Pollux\DomainBundle\Entity\Payment
   */
  public function getPayment() {
    return $this->payment;
  }

  /**
   * Set user
   *
   * @param \Pollux\DomainBundle\Entity\User $user
   * @return Subscription
   */
  public function setUser(\Pollux\DomainBundle\Entity\User $user = null) {
    $this->user = $user;

    return $this;
  }

  /**
   * Get user
   *
   * @return \Pollux\DomainBundle\Entity\User
   */
  public function getUser() {
    return $this->user;
  }

  public static function createSubscription() {
    return new Subscription();
  }
}
