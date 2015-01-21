<?php

namespace Pollux\DomainBundle\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Security\Core\Role\RoleInterface;

/**
 * Role
 *
 * @ORM\Table(name="role")
 * @ORM\Entity
 */
class Role implements RoleInterface {
  /**
   * @var string
   *
   * @ORM\Column(name="name", type="string", length=45, nullable=false)
   */
  private $name;

  /**
   * @var string
   *
   * @ORM\Column(name="detail_name", type="string", length=45, nullable=false)
   */
  private $detailName;

  /**
   * @var integer
   *
   * @ORM\Column(name="id", type="integer")
   * @ORM\Id
   * @ORM\GeneratedValue(strategy="IDENTITY")
   */
  private $id;


  /**
   * Set name
   *
   * @param string $name
   * @return Role
   */
  public function setName($name) {
    $this->name = $name;

    return $this;
  }

  /**
   * Get name
   *
   * @return string
   */
  public function getName() {
    return $this->name;
  }

  /**
   * Set detailName
   *
   * @param string $detailName
   * @return Role
   */
  public function setDetailName($detailName) {
    $this->detailName = $detailName;

    return $this;
  }

  /**
   * Get detailName
   *
   * @return string
   */
  public function getDetailName() {
    return $this->detailName;
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
   * @inheritdoc
   */
  public function getRole() {
    return $this->getName();
  }

}
