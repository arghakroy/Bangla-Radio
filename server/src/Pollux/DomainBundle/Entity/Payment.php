<?php

namespace Pollux\DomainBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Payment
 *
 * @ORM\Table(name="payment", indexes={@ORM\Index(name="fk_payment_products_idx", columns={"product_id"}), @ORM\Index(name="fk_payment_user_idx", columns={"user_id"})})
 * @ORM\Entity
 */
class Payment {
  /**
   * @var \DateTime
   *
   * @ORM\Column(name="initiated_at", type="datetime", nullable=false)
   */
  private $initiatedAt;

  /**
   * @var \DateTime
   *
   * @ORM\Column(name="completed_at", type="datetime", nullable=true)
   */
  private $completedAt;

  /**
   * @var string
   *
   * @ORM\Column(name="amount", type="decimal", precision=10, scale=2, nullable=true)
   */
  private $amount;

  /**
   * @var integer
   *
   * @ORM\Column(name="id", type="integer")
   * @ORM\Id
   * @ORM\GeneratedValue(strategy="IDENTITY")
   */
  private $id;

  /**
   * @var \Pollux\DomainBundle\Entity\Product
   *
   * @ORM\ManyToOne(targetEntity="Pollux\DomainBundle\Entity\Product")
   * @ORM\JoinColumns({
   *   @ORM\JoinColumn(name="product_id", referencedColumnName="id")
   * })
   */
  private $product;

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
   * Set initiatedAt
   *
   * @param \DateTime $initiatedAt
   * @return Payment
   */
  public function setInitiatedAt($initiatedAt) {
    $this->initiatedAt = $initiatedAt;

    return $this;
  }

  /**
   * Get initiatedAt
   *
   * @return \DateTime
   */
  public function getInitiatedAt() {
    return $this->initiatedAt;
  }

  /**
   * Set completedAt
   *
   * @param \DateTime $completedAt
   * @return Payment
   */
  public function setCompletedAt($completedAt) {
    $this->completedAt = $completedAt;

    return $this;
  }

  /**
   * Get completedAt
   *
   * @return \DateTime
   */
  public function getCompletedAt() {
    return $this->completedAt;
  }

  /**
   * Set amount
   *
   * @param string $amount
   * @return Payment
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
   * Get id
   *
   * @return integer
   */
  public function getId() {
    return $this->id;
  }

  /**
   * Set product
   *
   * @param \Pollux\DomainBundle\Entity\Product $product
   * @return Payment
   */
  public function setProduct(\Pollux\DomainBundle\Entity\Product $product = null) {
    $this->product = $product;

    return $this;
  }

  /**
   * Get product
   *
   * @return \Pollux\DomainBundle\Entity\Product
   */
  public function getProduct() {
    return $this->product;
  }

  /**
   * Set user
   *
   * @param \Pollux\DomainBundle\Entity\User $user
   * @return Payment
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
}
