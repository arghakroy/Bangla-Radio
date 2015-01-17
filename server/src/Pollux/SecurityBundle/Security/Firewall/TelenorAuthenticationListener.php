<?php

namespace Pollux\SecurityBundle\Security\Firewall;


use Pollux\SecurityBundle\Security\Authentication\Token\TelenorUserToken;
use Pollux\WebServiceBundle\Utils\Headers;
use Psr\Log\LoggerInterface;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Session\Session;
use Symfony\Component\HttpKernel\Event\GetResponseEvent;
use Symfony\Component\Routing\Router;
use Symfony\Component\Security\Core\Authentication\AuthenticationManagerInterface;
use Symfony\Component\Security\Core\Exception\AuthenticationException;
use Symfony\Component\Security\Core\Exception\NonceExpiredException;
use Symfony\Component\Security\Core\SecurityContextInterface;
use Symfony\Component\Security\Http\Firewall\ListenerInterface;

class TelenorAuthenticationListener implements ListenerInterface {
  const WSSE_REGEX = '/UsernameToken Username="([^"]+)", PasswordDigest="([^"]+)", Nonce="([^"]+)", Created="([^"]+)"/';
  const WSSE_HEADER = 'x-wsse';

  /**
   * @var LoggerInterface
   */
  private $logger;

  private $securityContext;
  private $authenticationManager;
  private $providerKey;
  /**
   * @var Router
   */
  private $router;
  /**
   * @var Session
   */
  private $session;

  public function __construct(SecurityContextInterface $securityContext, AuthenticationManagerInterface $authenticationManager, $providerKey, Router $router, Session $session, LoggerInterface $logger) {
    $this->securityContext = $securityContext;
    $this->authenticationManager = $authenticationManager;
    $this->providerKey = $providerKey;

    $this->router = $router;
    $this->session = $session;
    $this->logger = $logger;
  }

  public function handle(GetResponseEvent $event) {
    $request = $event->getRequest();

    $wsseHeader = $request->headers->get(self::WSSE_HEADER, false);
    if (!$wsseHeader || 1 !== preg_match(self::WSSE_REGEX, $wsseHeader, $matches)) {
      $event->setResponse(new Response('', Response::HTTP_FORBIDDEN, array(
          'WWW-Authenticate' => 'WSSE realm="webservice", profile="ApplicationToken"'
      )));
      return;
    }

    $token = new TelenorUserToken($this->providerKey, $matches[2], $matches[3], $matches[4]);
    $token->setUser($matches[1]);

    try {
      $authenticatedToken = $this->authenticationManager->authenticate($token);
      $this->securityContext->setToken($authenticatedToken);

      return;
    }
    catch (NonceExpiredException $failed) {
      $this->logger->debug("Nonce expired: " . $wsseHeader);
    }
    catch (AuthenticationException $failed) {
      $this->logger->debug("Authentication failed: " . $failed->getMessage());
    }

    $token = $this->securityContext->getToken();
    if ($token instanceof TelenorUserToken) {
      $this->securityContext->setToken(null);
    }

    $response = new Response("", Response::HTTP_UNAUTHORIZED, array(
        Headers::LOCATION => $this->router->generate('telenor.authentication.login'),
    ));
    $event->setResponse($response);
  }

}
