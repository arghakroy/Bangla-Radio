<?php

namespace Pollux\WebServiceBundle\Controller;


use Pollux\DomainBundle\Entity\User;
use Pollux\WebServiceBundle\Utils\Headers;
use Pollux\WebServiceBundle\Utils\MimeType;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class SubscriptionResourceController extends Controller {

  public function getCollectionAction() {
    //get the `secret` header with the request


    //get the user info from server based on the secret provided by the client


    //if no `secret` send the client a 412 precondition failed error
  }

  public function getAction(Request $request) {
    /**
     * @var User $user
     */
    $user = $this->getUser();
    $userRights = json_decode($user->getUserRightsData());

    if(!$userRights) {
      return new Response('No subscription available.', Response::HTTP_FORBIDDEN);
    }

    $entry = $this->getUserRightEntry($userRights->right[0]);
    if($entry) {
      $response = $this->render('WebServiceBundle:SubscriptionResource:entity.json.twig', array('entity' => $entry));
      $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);
      return $response;
    }

    throw $this->createNotFoundException("No active subscription available.");
  }

  private function getUserRightEntry($userRight) {
    list($startTime, $endTime) = explode("/", $userRight->timeInterval);
    $startTime = date_create($startTime);
    $endTime = date_create($endTime);
    $now = new \DateTime();
    if ($now >= $startTime && $now <= $endTime) {
      $entry['sku'] = $userRight->sku;
      $entry['start_date'] = $startTime->format('Y-m-d');
      $entry['end_date'] = $endTime->format('Y-m-d');
      $entry['status'] = $userRight->state;
      return $entry;
    }
    return null;
  }

}
