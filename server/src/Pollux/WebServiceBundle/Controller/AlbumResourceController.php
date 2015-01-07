<?php

namespace Pollux\WebServiceBundle\Controller;


use Pollux\WebServiceBundle\Utils\Headers;
use Pollux\WebServiceBundle\Utils\MimeType;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;

class AlbumResourceController extends Controller {

  public function getCollectionAction() {
    $musicClient = $this->get('webservice.musicClient');
    $entities = $musicClient->getAlbums();

    $response = $this->render('@WebService/AlbumResource/collection.json.twig', array(
        'entities' => $entities
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

  public function getSongsAction($albumId) {
    $musicClient = $this->get('webservice.musicClient');
    $entities = $musicClient->getAlbumSongs($albumId);

    $response = $this->render('@WebService/SongResource/collection.json.twig', array(
        'entities' => $entities
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

  public function getAction($albumId) {
    $musicClient = $this->get('webservice.musicClient');
    $entity = $musicClient->getAlbum($albumId);

    $response = $this->render('@WebService/AlbumResource/entity.json.twig', array(
        'entity' => $entity
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

}
