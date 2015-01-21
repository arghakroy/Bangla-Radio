<?php

namespace Pollux\WebServiceBundle\Controller;


use Pollux\WebServiceBundle\Utils\Headers;
use Pollux\WebServiceBundle\Utils\MimeType;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;

class ProductResourceController extends Controller {

  public function getCollectionAction() {
    $entities = $this->getDoctrine()->getManager()->getRepository('DomainBundle:Product')->findAll();

    $response = $this->render('WebServiceBundle:ProductResource:collection.json.twig', array(
        'entities' => $entities,
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

  public function getAction($productId) {
    $entity = $this->getDoctrine()->getManager()->getRepository('DomainBundle:Product')->find($productId);
    if (!$entity) {
      $this->get('logger')->debug("Product not found with id: " . $productId);
      throw $this->createNotFoundException();
    }

    $response = $this->render('WebServiceBundle:ProductResource:entity.json.twig', array('entity' => $entity));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);
    return $response;
  }

}
