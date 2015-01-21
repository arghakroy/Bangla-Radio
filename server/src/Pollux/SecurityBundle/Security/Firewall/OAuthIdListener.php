<?php

namespace Pollux\SecurityBundle\Security\Firewall;


use Pollux\SecurityBundle\Security\Authentication\Token\OAuthIDToken;
use Psr\Log\LoggerInterface;
use Symfony\Component\Config\Definition\Exception\Exception;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\Event\GetResponseEvent;
use Symfony\Component\Security\Core\Authentication\AuthenticationManagerInterface;
use Symfony\Component\Security\Core\Authentication\Token\UsernamePasswordToken;
use Symfony\Component\Security\Core\Exception\AuthenticationException;
use Symfony\Component\Security\Core\SecurityContextInterface;
use Symfony\Component\Security\Http\Firewall\ListenerInterface;

class OAuthIdListener implements ListenerInterface {

  private $securityContext;
  private $authenticationManager;
  private $providerKey;
  private $oauthProviders;
  private $logger;

  public function __construct(SecurityContextInterface $securityContext, AuthenticationManagerInterface $authenticationManager, $providerKey, LoggerInterface $logger, array $oauthProviders) {
    if (empty($providerKey)) {
      throw new \InvalidArgumentException('$providerKey must not be empty.');
    }

    $this->securityContext = $securityContext;
    $this->authenticationManager = $authenticationManager;
    $this->providerKey = $providerKey;
    $this->oauthProviders = $oauthProviders;
    $this->logger = $logger;
  }

  public function handle(GetResponseEvent $event) {
    $request = $event->getRequest();
    if($request->headers->get('PHP_AUTH_USER', false)) {
      return;
    }

    list($authenticationHeader, $oauthProviderName) = $this->getAuthenticationHeader($request);
    if(!$authenticationHeader) {
      return;
    }

    $username = $this->getUsername($authenticationHeader);
    if(!$username) {
      return;
    }
    if (null !== $token = $this->securityContext->getToken()) {
      if ($token instanceof UsernamePasswordToken && $token->isAuthenticated() && $token->getUsername() === $username) {
        return;
      }
    }

    try {
      $oauthIDToken = new OAuthIDToken($this->providerKey, $username, $oauthProviderName, array());
      $token = $this->authenticationManager->authenticate($oauthIDToken);
      $this->securityContext->setToken($token);
      return;
    }
    catch (AuthenticationException $failed) {
      $token = $this->securityContext->getToken();
      if ($token instanceof OAuthIDToken && $this->providerKey === $token->getProviderKey()) {
        $this->securityContext->setToken(null);
      }
    }

    $response = new Response();
    $response->setStatusCode(Response::HTTP_UNAUTHORIZED);
    $event->setResponse($response);
  }

  private function getAuthenticationHeader($request) {
    $authenticationHeader = null;
    $oauthProviderName = null;
    foreach ($this->oauthProviders as $oauthProviderName => $value) {
      $headerName = 'X-' . $oauthProviderName;
      $authenticationHeader = $request->headers->get($headerName, false);

      if ($authenticationHeader) {
        break;
      }
    }
    return array($authenticationHeader, $oauthProviderName);
  }

  private function getUsername($authenticationHeader) {
    try {
      list($username, $password) = explode(':' , base64_decode(substr($authenticationHeader, 6)));
      return $username;
    }
    catch(Exception $ex) {
      return null;
    }
  }

}
