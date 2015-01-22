<?php

namespace Pollux\DomainBundle\Repository;

use Doctrine\ORM\EntityRepository;
use Doctrine\ORM\NoResultException;
use Doctrine\ORM\Query;

class ProductRepository extends EntityRepository {

  public function getProduct($date) {
    $query = $this->createQueryBuilder('p')
        ->select('p')
        ->where('p.startDate < :currentDate')
        ->andWhere('p.endDate > :currentDate')
        ->setParameter('currentDate', $date)
        ->getQuery();

    $product = NULL;
    if ($query->getResult() != NULL) {
      $product = $query->getSingleResult();
    }
    return $product;
  }

}
