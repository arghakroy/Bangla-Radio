<?php

namespace Pollux\WebServiceBundle\Controller;


use Pollux\WebServiceBundle\Utils\Headers;
use Pollux\WebServiceBundle\Utils\MimeType;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;

class EndPointController extends Controller {

  public function getAction() {
    $product = $this->getDoctrine()->getManager()->getRepository('DomainBundle:Product')->getCurrentProduct();

    $response = $this->render('WebServiceBundle:EndPoint:index.json.twig', array(
        'currentProduct' => $product
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);
    return $response;
  }

}
