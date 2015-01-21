<?php

namespace Pollux\SecurityBundle\Security\Firewall;


use Csa\WebServiceBundle\Utils\Headers;
use Pollux\SecurityBundle\Security\Authentication\Token\FacebookUserToken;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\Event\GetResponseEvent;
use Symfony\Component\Security\Core\Authentication\AuthenticationManagerInterface;
use Symfony\Component\Security\Core\Authentication\Token\UsernamePasswordToken;
use Symfony\Component\Security\Core\Exception\AuthenticationException;
use Symfony\Component\Security\Core\SecurityContextInterface;
use Symfony\Component\Security\Http\Firewall\ListenerInterface;

class FacebookListener implements ListenerInterface {
  const X_FACEBOOK = 'X-FACEBOOK';

  private $securityContext;
  private $authenticationManager;
  private $providerKey;

  public function __construct(SecurityContextInterface $securityContext, AuthenticationManagerInterface $authenticationManager, $providerKey) {
    if (empty($providerKey)) {
      throw new \InvalidArgumentException('$providerKey must not be empty.');
    }

    $this->securityContext = $securityContext;
    $this->authenticationManager = $authenticationManager;
    $this->providerKey = $providerKey;
  }

  public function handle(GetResponseEvent $event) {
    $request = $event->getRequest();
    if (false === $username = $request->headers->get('PHP_AUTH_USER', false)) {
      return;
    }

    if (null !== $token = $this->securityContext->getToken()) {
      if ($token instanceof UsernamePasswordToken && $token->isAuthenticated() && $token->getUsername() === $username) {
        return;
      }
    }

    try {
      $accessToken = $request->headers->get('PHP_AUTH_PW');
      $facebookToken = new FacebookUserToken($this->providerKey, $username, $accessToken, array());
      $token = $this->authenticationManager->authenticate($facebookToken);
      $this->securityContext->setToken($token);
      return;
    }
    catch (AuthenticationException $failed) {
      /**
       * @var FacebookUserToken $token
       */
      $token = $this->securityContext->getToken();
      if ($token instanceof FacebookUserToken && $this->providerKey === $token->getProviderKey()) {
        $this->securityContext->setToken(null);
      }
    }
  }

}
