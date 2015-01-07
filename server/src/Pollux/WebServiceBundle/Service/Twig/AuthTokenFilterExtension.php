<?php

namespace Pollux\WebServiceBundle\Service\Twig;


class AuthTokenFilterExtension extends \Twig_Extension {

  private $tokenPath;
  private $authQuery;

  function __construct($tokenPath) {
    $this->tokenPath = $tokenPath;
  }

  public function getFilters() {
    return array(
        new \Twig_SimpleFilter('filterAuthToken', array($this, 'filter')),
    );
  }

  public function filter($url = null) {
    return $url ? str_replace($this->getAuthQuery(), "" , $url) : null;
  }

  public function getName() {
    return 'csa_extension';
  }

  private function getAuthQuery() {
    if(!$this->authQuery) {
      $token = file_get_contents($this->tokenPath);
      $this->authQuery = "&auth=" . $token;
    }

    return $this->authQuery;
  }

}
