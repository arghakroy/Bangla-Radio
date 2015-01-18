<?php

namespace Pollux\WebServiceBundle\Controller;


use Pollux\WebServiceBundle\Utils\Headers;
use Pollux\WebServiceBundle\Utils\MimeType;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;

class PlaylistResourceController extends Controller {

  public function getCollectionAction() {
    $musicClient = $this->get('webservice.musicClient');
    $entities = $musicClient->getPlaylists();

    $response = $this->render('@WebService/PlaylistResource/collection.json.twig', array(
        'entities' => $entities
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

  public function getAction($playlistId) {
    $musicClient = $this->get('webservice.musicClient');
    $entity = $musicClient->getPlaylist($playlistId);

    $response = $this->render('@WebService/PlaylistResource/entity.json.twig', array(
        'entity' => $entity
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

  public function getSongsAction($playlistId) {
    $musicClient = $this->get('webservice.musicClient');
    $entities = $musicClient->getPlaylistSongs($playlistId);

    $response = $this->render('@WebService/SongResource/collection.json.twig', array(
        'entities' => $entities
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

}
