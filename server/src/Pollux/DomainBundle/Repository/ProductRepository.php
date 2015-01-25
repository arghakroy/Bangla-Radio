<?php

namespace Pollux\DomainBundle\Repository;

use Doctrine\ORM\EntityRepository;
use Doctrine\ORM\NoResultException;
use Doctrine\ORM\Query;

class ProductRepository extends EntityRepository {

  public function getCurrentProduct() {
    $query = $this->createQueryBuilder('p')
        ->select('p')
        ->where('p.startDate < :currentDate')
        ->andWhere('p.endDate > :currentDate')
        ->setParameter('currentDate', new \DateTime())
        ->getQuery();

    return $query->getOneOrNullResult();
  }

}
