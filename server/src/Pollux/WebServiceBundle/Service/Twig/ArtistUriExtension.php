<?php

namespace Pollux\WebServiceBundle\Service\Twig;


use Pollux\WebServiceBundle\Service\AmpacheClient;

class ArtistUriExtension extends \Twig_Extension {

  /**
   * @var AmpacheClient
   */
  private $ampacheClient;

  function __construct(AmpacheClient $ampacheClient) {
    $this->ampacheClient = $ampacheClient;
  }

  public function getFilters() {
    return array(
        new \Twig_SimpleFilter('artistUri', array($this, 'filter')),
    );
  }

  public function filter($id) {
    return $this->ampacheClient->getArtistPreviewUrl($id);
  }

  public function getName() {
    return 'pollux_extension';
  }

}
