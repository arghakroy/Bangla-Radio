<?php

namespace Pollux\SecurityBundle\Security\Firewall;


use Pollux\SecurityBundle\Security\Authentication\Token\WsseUserToken;
use Psr\Log\LoggerInterface;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\Event\GetResponseEvent;
use Symfony\Component\Security\Core\Authentication\AuthenticationManagerInterface;
use Symfony\Component\Security\Core\Exception\AuthenticationException;
use Symfony\Component\Security\Core\Exception\NonceExpiredException;
use Symfony\Component\Security\Core\SecurityContextInterface;
use Symfony\Component\Security\Http\Firewall\ListenerInterface;

class WsseListener implements ListenerInterface {
  const WSSE_REGEX = '/UsernameToken Username="([^"]+)", PasswordDigest="([^"]+)", Nonce="([^"]+)", Created="([^"]+)"/';
  const WSSE_HEADER = 'x-wsse';

  private $securityContext;
  private $authenticationManager;
  private $logger;

  public function __construct(SecurityContextInterface $securityContext, AuthenticationManagerInterface $authenticationManager, LoggerInterface $logger) {
    $this->securityContext = $securityContext;
    $this->authenticationManager = $authenticationManager;
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

    $token = new WsseUserToken();
    $token->setUser($matches[1]);

    $token->digest = $matches[2];
    $token->nonce = $matches[3];
    $token->created = $matches[4];

    try {
      $authToken = $this->authenticationManager->authenticate($token);
      $this->securityContext->setToken($authToken);

      return;
    }
    catch (NonceExpiredException $failed) {
      $this->logger->debug("Nonce expired: " . $wsseHeader);
    }
    catch (AuthenticationException $failed) {
      $this->logger->debug("Authentication failed: " . $failed->getMessage());
    }

    $token = $this->securityContext->getToken();
    if ($token instanceof WsseUserToken) {
      $this->securityContext->setToken(null);
    }

    $response = new Response();
    $response->setStatusCode(Response::HTTP_UNAUTHORIZED);
    $event->setResponse($response);
  }
}
