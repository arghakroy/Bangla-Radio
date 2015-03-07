<?php

namespace Pollux\DomainBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Product
 *
 * @ORM\Table(name="product")
 * @ORM\Entity(repositoryClass="Pollux\DomainBundle\Repository\ProductRepository")
 */
class Product {
  /**
   * @var \DateTime
   *
   * @ORM\Column(name="date_created", type="datetime", nullable=false)
   */
  private $dateCreated;

  /**
   * @var string
   *
   * @ORM\Column(name="sku", type="string", length=100, nullable=false)
   */
  private $sku;

  /**
   * @var string
   *
   * @ORM\Column(name="time_spec", type="string", length=100, nullable=false)
   */
  private $timeSpec;

  /**
   * @var string
   *
   * @ORM\Column(name="product_name", type="string", length=100, nullable=false)
   */
  private $productName;

  /**
   * @var string
   *
   * @ORM\Column(name="pricing", type="decimal", precision=6, scale=2, nullable=false)
   */
  private $pricing;

  /**
   * @var \DateTime
   *
   * @ORM\Column(name="start_date", type="date", nullable=false)
   */
  private $startDate;

  /**
   * @var \DateTime
   *
   * @ORM\Column(name="end_date", type="date", nullable=false)
   */
  private $endDate;

  /**
   * @var boolean
   *
   * @ORM\Column(name="status", type="boolean", nullable=false)
   */
  private $status;

  /**
   * @var string
   *
   * @ORM\Column(name="vat_percentage", type="decimal", precision=6, scale=2, nullable=false)
   */
  private $vatPercentage;

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
   * @return Product
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
   * Set sku
   *
   * @param string $sku
   * @return Product
   */
  public function setSku($sku) {
    $this->sku = $sku;

    return $this;
  }

  /**
   * Get sku
   *
   * @return string
   */
  public function getSku() {
    return $this->sku;
  }

  /**
   * Set timeSpec
   *
   * @param string $timeSpec
   * @return Product
   */
  public function setTimeSpec($timeSpec) {
    $this->timeSpec = $timeSpec;

    return $this;
  }

  /**
   * Get timeSpec
   *
   * @return string
   */
  public function getTimeSpec() {
    return $this->timeSpec;
  }

  /**
   * Set productName
   *
   * @param string $productName
   * @return Product
   */
  public function setProductName($productName) {
    $this->productName = $productName;

    return $this;
  }

  /**
   * Get productName
   *
   * @return string
   */
  public function getProductName() {
    return $this->productName;
  }

  /**
   * Set pricing
   *
   * @param string $pricing
   * @return Product
   */
  public function setPricing($pricing) {
    $this->pricing = $pricing;

    return $this;
  }

  /**
   * Get pricing
   *
   * @return string
   */
  public function getPricing() {
    return $this->pricing;
  }

  /**
   * Set startDate
   *
   * @param \DateTime $startDate
   * @return Product
   */
  public function setStartDate($startDate) {
    $this->startDate = $startDate;

    return $this;
  }

  /**
   * Get startDate
   *
   * @return \DateTime
   */
  public function getStartDate() {
    return $this->startDate;
  }

  /**
   * Set endDate
   *
   * @param \DateTime $endDate
   * @return Product
   */
  public function setEndDate($endDate) {
    $this->endDate = $endDate;

    return $this;
  }

  /**
   * Get endDate
   *
   * @return \DateTime
   */
  public function getEndDate() {
    return $this->endDate;
  }

  /**
   * Set status
   *
   * @param boolean $status
   * @return Product
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
   * Set vatPercentage
   *
   * @param string $vatPercentage
   * @return Product
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
   * Get id
   *
   * @return integer
   */
  public function getId() {
    return $this->id;
  }
}
