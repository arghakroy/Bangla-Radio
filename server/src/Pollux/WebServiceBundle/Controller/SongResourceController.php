<?php

namespace Pollux\WebServiceBundle\Controller;


use Pollux\WebServiceBundle\Utils\Headers;
use Pollux\WebServiceBundle\Utils\MimeType;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Response;

class SongResourceController extends Controller {

  public function getCollectionAction() {
    $musicClient = $this->get('webservice.musicClient');
    $songs = $musicClient->getSongs();

    $response = $this->render('@WebService/SongResource/collection.json.twig', array(
        'entities' => $songs
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

  public function getAction($songId) {
    $musicClient = $this->get('webservice.musicClient');
    $song = $musicClient->getSong($songId);

    $response = $this->render('@WebService/SongResource/entity.json.twig', array(
        'entity' => $song
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

  public function getStreamUriAction($songId) {
    $musicClient = $this->get('webservice.musicClient');
    $song = $musicClient->getSong($songId);

    return new Response($song->url, 200, array(
      Headers::CONTENT_TYPE => MimeType::TEXT_URI_LIST
    ));
  }

}
