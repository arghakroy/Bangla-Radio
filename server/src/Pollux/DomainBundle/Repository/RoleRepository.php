<?php

namespace Pollux\DomainBundle\Repository;


use Doctrine\ORM\EntityRepository;
use Pollux\DomainBundle\Entity\Role;

class RoleRepository extends EntityRepository {

  /**
   * @param $roleName
   * @return Role
   */
  public function loadRoleByName($roleName) {
    return $this->findOneBy(array(
      "name" => $roleName
    ));
  }

}
