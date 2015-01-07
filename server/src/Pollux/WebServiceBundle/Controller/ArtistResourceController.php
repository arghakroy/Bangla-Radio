<?php

namespace Pollux\WebServiceBundle\Controller;


use Pollux\WebServiceBundle\Utils\Headers;
use Pollux\WebServiceBundle\Utils\MimeType;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;

class ArtistResourceController extends Controller {

  public function getCollectionAction() {
    $musicClient = $this->get('webservice.musicClient');
    $entities = $musicClient->getArtists();

    $response = $this->render('@WebService/ArtistResource/collection.json.twig', array(
        'entities' => $entities
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

  public function getAction($artistId) {
    $musicClient = $this->get('webservice.musicClient');
    $entity = $musicClient->getArtist($artistId);

    $response = $this->render('@WebService/ArtistResource/entity.json.twig', array(
        'entity' => $entity
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

  public function getSongsAction($artistId) {
    $musicClient = $this->get('webservice.musicClient');
    $entities = $musicClient->getArtistSongs($artistId);

    $response = $this->render('@WebService/SongResource/collection.json.twig', array(
        'entities' => $entities
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

  public function getAlbumsAction($artistId) {
    $musicClient = $this->get('webservice.musicClient');
    $entities = $musicClient->getArtistAlbums($artistId);

    $response = $this->render('@WebService/AlbumResource/collection.json.twig', array(
        'entities' => $entities
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

}
