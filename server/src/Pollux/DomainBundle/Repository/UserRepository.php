<?php

namespace Pollux\DomainBundle\Repository;

use Doctrine\ORM\EntityRepository;
use Doctrine\ORM\NoResultException;
use Doctrine\ORM\Query;
use Symfony\Component\Security\Core\Exception\UnsupportedUserException;
use Symfony\Component\Security\Core\Exception\UsernameNotFoundException;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Security\Core\User\UserProviderInterface;

class UserRepository extends EntityRepository implements UserProviderInterface {

  /**
   * @inheritdoc
   */
  public function loadUserByUsername($username) {
    $query = $this->createQueryBuilder('u')
        ->select('u, r')
        ->leftJoin('u.roles', 'r')
        ->where('u.username = :username')
        ->setParameter('username', $username)
        ->getQuery();

    try {
      $user = $query->getSingleResult();
    }
    catch (NoResultException $e) {
      $message = sprintf('Csa:User object identified by "%s".', $username);
      throw new UsernameNotFoundException($message, 0, $e);
    }

    return $user;
  }

  /**
   * @inheritdoc
   */
  public function refreshUser(UserInterface $user) {
    $class = get_class($user);
    if (!$this->supportsClass($class)) {
      throw new UnsupportedUserException(sprintf('Instances of "%s" are not supported.', $class));
    }

    return $this->find($user->getId());
  }

  /**
   * @inheritdoc
   */
  public function supportsClass($class) {
    return $this->getEntityName() === $class || is_subclass_of($class, $this->getEntityName());
  }

}
