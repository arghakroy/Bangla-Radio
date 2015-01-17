<?php

namespace Pollux\WebServiceBundle\Controller;


use Pollux\WebServiceBundle\Utils\Headers;
use Pollux\WebServiceBundle\Utils\MimeType;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;

class TagResourceController extends Controller {

  public function getCollectionAction() {
    $musicClient = $this->get('webservice.musicClient');
    $entities = $musicClient->getTags();

    $response = $this->render('@WebService/TagResource/collection.json.twig', array(
        'entities' => $entities
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

  public function getAction($tagId) {
    $musicClient = $this->get('webservice.musicClient');
    $entity = $musicClient->getTag($tagId);

    $response = $this->render('@WebService/TagResource/entity.json.twig', array(
        'entity' => $entity
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

  public function getArtistsAction($tagId) {
    $musicClient = $this->get('webservice.musicClient');
    $entities = $musicClient->getTag($tagId);

    $response = $this->render('@WebService/TagResource/entity.json.twig', array(
        'entities' => $entities
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

  public function getAlbumsAction($tagId) {
    $musicClient = $this->get('webservice.musicClient');
    $entities = $musicClient->getTagAlbums($tagId);

    $response = $this->render('@WebService/AlbumResource/collection.json.twig', array(
        'entities' => $entities
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

  public function getSongsAction($tagId) {
    $musicClient = $this->get('webservice.musicClient');
    $entities = $musicClient->getTagSongs($tagId);

    $response = $this->render('@WebService/SongResource/collection.json.twig', array(
        'entities' => $entities
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

}
