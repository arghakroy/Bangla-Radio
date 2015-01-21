<?php

namespace Pollux\DomainBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Subscription
 *
 * @ORM\Table(name="subscription")
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
   * @ORM\Column(name="order_id", type="string", length=100, nullable=false)
   */
  private $orderId;

  /**
   * @var string
   *
   * @ORM\Column(name="amount", type="decimal", precision=6, scale=2, nullable=false)
   */
  private $amount;

  /**
   * @var string
   *
   * @ORM\Column(name="vat_percentage", type="decimal", precision=6, scale=2, nullable=false)
   */
  private $vatPercentage;

  /**
   * @var string
   *
   * @ORM\Column(name="description", type="string", length=300, nullable=false)
   */
  private $description;

  /**
   * @var boolean
   *
   * @ORM\Column(name="status", type="boolean", nullable=false)
   */
  private $status;

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
   * @var boolean
   *
   * @ORM\Column(name="connect_status", type="boolean", nullable=false)
   */
  private $connectStatus;

  /**
   * @var \DateTime
   *
   * @ORM\Column(name="connect_start_time", type="datetime", nullable=false)
   */
  private $connectStartTime;

  /**
   * @var string
   *
   * @ORM\Column(name="connect_tx_json", type="text", length=65535, nullable=false)
   */
  private $connectTxJson;

  /**
   * @var integer
   *
   * @ORM\Column(name="id", type="integer")
   * @ORM\Id
   * @ORM\GeneratedValue(strategy="IDENTITY")
   */
  private $id;


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
   * Set orderId
   *
   * @param string $orderId
   * @return Subscription
   */
  public function setOrderId($orderId) {
    $this->orderId = $orderId;

    return $this;
  }

  /**
   * Get orderId
   *
   * @return string
   */
  public function getOrderId() {
    return $this->orderId;
  }

  /**
   * Set amount
   *
   * @param string $amount
   * @return Subscription
   */
  public function setAmount($amount) {
    $this->amount = $amount;

    return $this;
  }

  /**
   * Get amount
   *
   * @return string
   */
  public function getAmount() {
    return $this->amount;
  }

  /**
   * Set vatPercentage
   *
   * @param string $vatPercentage
   * @return Subscription
   */
  public function setVatPercentage($vatPercentage) {
    $this->vatPercentage = $vatPercentage;

    return $this;
  }

  /**
   * Get vatPercentage
   *
   * @return string
   */
  public function getVatPercentage() {
    return $this->vatPercentage;
  }

  /**
   * Set description
   *
   * @param string $description
   * @return Subscription
   */
  public function setDescription($description) {
    $this->description = $description;

    return $this;
  }

  /**
   * Get description
   *
   * @return string
   */
  public function getDescription() {
    return $this->description;
  }

  /**
   * Set status
   *
   * @param boolean $status
   * @return Subscription
   */
  public function setStatus($status) {
    $this->status = $status;

    return $this;
  }

  /**
   * Get status
   *
   * @return boolean
   */
  public function getStatus() {
    return $this->status;
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
   * Get id
   *
   * @return integer
   */
  public function getId() {
    return $this->id;
  }
}
