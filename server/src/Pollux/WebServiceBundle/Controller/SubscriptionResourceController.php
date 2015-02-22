<?php

namespace Pollux\WebServiceBundle\Controller;


use Pollux\DomainBundle\Entity\Subscription;
use Pollux\DomainBundle\Entity\User;
use Pollux\WebServiceBundle\Utils\Headers;
use Pollux\WebServiceBundle\Utils\MimeType;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Response;

class SubscriptionResourceController extends Controller {

  public function getCurrentSubscriptionAction() {
    /**
     * @var User $user
     * @var Subscription $subscription
     */
    $user = $this->getUser();
    $now = new \DateTime();
    foreach($user->getSubscriptions() as $subscription) {
      if($now <= $subscription->getConnectEndTime() && $now >= $subscription->getConnectStartTime()) {
        $response = $this->render('WebServiceBundle:SubscriptionResource:entity.json.twig', array('entity' => $subscription));
        $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);
        return $response;
      }
    }

    return new Response('No subscription available.', Response::HTTP_NOT_FOUND);
  }

}
