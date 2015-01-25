<?php

namespace Pollux\WebServiceBundle\Controller;

use Pollux\DomainBundle\Entity\User;
use Pollux\WebServiceBundle\Utils\Headers;
use Pollux\WebServiceBundle\Utils\MimeType;
use Psr\Log\InvalidArgumentException;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\BrowserKit\Response;

class PaymentController extends Controller {

  public function getCollectionAction() {
    $entities = $this->getDoctrine()->getManager()->getRepository('DomainBundle:Subscription')->findAll();

    $response = $this->render('WebServiceBundle:SubscriptionResource:collection.json.twig', array(
        'entities' => $entities,
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

  public function getAction() {
    $telenorClient = $this->get('service.telenor.client');
    $currentProduct = $this->getDoctrine()->getManager()->getRepository('DomainBundle:Product')->getCurrentProduct();
    if (!$currentProduct) {
      throw new InvalidArgumentException("No current product found");
    }

    $transactionResponse = $telenorClient->getTransaction($this->getUser(), $currentProduct);
    $locationLinks = $transactionResponse->links[0];
    $locationURL = $locationLinks->href;

    return $this->redirect($locationURL);
  }

  public function successAction($uniqueId) {
    /**
     * @var User $user
     */
    $em = $this->getDoctrine()->getManager();
    $telenorClient = $this->get('service.telenor.client');
    $user = $this->getUser();
    $userRights = $telenorClient->getUserRights($user);

    $user->setUserRightsData(json_encode($userRights));
    $em->merge($user);
    $em->flush();

    $url = "polluxmusic://purchase?status=success";
    return $this->redirect($url);
  }

  public function cancelAction($uniqueId) {
    $url = "polluxmusic://purchase?status=cancelled";
    return $this->redirect($url);
  }

}
