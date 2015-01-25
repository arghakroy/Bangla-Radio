<?php

namespace Pollux\WebServiceBundle\Controller;


use Pollux\WebServiceBundle\Utils\Headers;
use Pollux\WebServiceBundle\Utils\MimeType;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\EventListener\RouterListener;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

class SubscriptionResourceController extends Controller {

  public function getCollectionAction() {
    //get the `secret` header with the request


    //get the user info from server based on the secret provided by the client


    //if no `secret` send the client a 412 precondition failed error
  }

  public function getAction() {
    $telenorClient = $this->get('service.telenor.client');
    $user = $this->getUser();
    $userRights = $telenorClient->getUserRights($user);

    if(!$userRights) {
      return new Response('', Response::HTTP_FORBIDDEN);
    }

    $sku = $userRights->right[0]->sku;
    $timeInterval = $userRights->right[0]->timeInterval ;

    list($startTime, $endTime) = explode("/", $timeInterval );

    $entity['sku'] = $sku;
    $entity['start_date'] = $startTime;
    $entity['end_date'] = $endTime;
    $entity['status'] = $userRights->right[0]->state;

    $response = $this->render('WebServiceBundle:SubscriptionResource:entity.json.twig', array('entity' => $entity));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);
    return $response;
  }

}
