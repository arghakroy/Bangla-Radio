<?php

namespace Pollux\DomainBundle\Repository;

use Doctrine\ORM\EntityRepository;
use Doctrine\ORM\Query;

class ProductRepository extends EntityRepository {

  public function getCurrentProduct() {
    $query = $this->createQueryBuilder('p')
        ->select('p')
        ->where(':currentDate BETWEEN p.startDate AND p.endDate')
        ->setParameter('currentDate', new \DateTime())
        ->getQuery();

    return $query->getOneOrNullResult();
  }

}
