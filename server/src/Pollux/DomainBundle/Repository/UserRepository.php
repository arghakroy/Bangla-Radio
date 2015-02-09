<?php

namespace Pollux\DomainBundle\Repository;

use Doctrine\ORM\EntityRepository;
use Doctrine\ORM\NoResultException;
use Doctrine\ORM\Query;
use Pollux\DomainBundle\Entity\User;
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
      $message = sprintf('User identified by "%s" is not found.', $username);
      throw new UsernameNotFoundException($message, 0, $e);
    }

    return $user;
  }

  /**
   * @param $username
   * @return User|null
   * @throws \Doctrine\ORM\NonUniqueResultException
   */
  public function findUserByUsername($username) {
    $query = $this->createQueryBuilder('u')
        ->select('u, r')
        ->leftJoin('u.roles', 'r')
        ->where('u.username = :username')
        ->setParameter('username', $username)
        ->getQuery();

    return $query->getOneOrNullResult();
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

  public function getUserFromSecret($secret) {
    $query = $this->createQueryBuilder('u')
        ->select('u')
        ->where('u.sharedSecret = :sharedSecret')
        ->setParameter('sharedSecret', $secret)
        ->getQuery();

    $user = NULL;
    try {
      if($query->getResult() != NULL) {
        $user = $query->getSingleResult();
      }
    }
    catch (NoResultException $e) {
      $message = sprintf('User identified by "%s" is not found.', $secret);
      throw new UsernameNotFoundException($message, 0, $e);
    }

    return $user;
  }

}
