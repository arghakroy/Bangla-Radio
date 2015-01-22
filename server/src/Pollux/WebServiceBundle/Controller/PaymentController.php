<?php

namespace Pollux\WebServiceBundle\Controller;

use Symfony\Component\HttpFoundation\Request;
use Pollux\WebServiceBundle\Utils\Headers;
use Pollux\WebServiceBundle\Utils\MimeType;
use Pollux\DomainBundle\Entity\User;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;

class PaymentController extends Controller {

  public function getCollectionAction() {
    $entities = $this->getDoctrine()->getManager()->getRepository('DomainBundle:Subscription')->findAll();

    $response = $this->render('WebServiceBundle:SubscriptionResource:collection.json.twig', array(
        'entities' => $entities,
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);

    return $response;
  }

  public function getAction(Request $request) {

    //$sharedSecret = $request->headers->get('HTTP-X-SECRET');
    $sharedSecret = "54c0e75117ea95.21045659";
    $user = $this->getDoctrine()->getManager()->getRepository('DomainBundle:User')->getUserFromSecret($sharedSecret);
    
    if(!$user){
      throw $this->createNotFoundException("User not found");
    }
    $accessToken = $user->getAccessToken();
    $userInfoData = $user->getUserInfoData();
    
    $today = new \DateTime();
    
    $product = $this->getDoctrine()->getManager()->getRepository('DomainBundle:Product')->getProduct($today);
    if(!$product){
      throw $this->createNotFoundException("User not found");
    }
    //$product->getSku();       
    $telenorClient = $this->get('service.telenor.client');
    $transactionResponse = $telenorClient->getTransaction($accessToken,$product);
    echo $transactionResponse;
    exit;
    
    
//    $entity = NULL;
//    if (!$entity) {
//      $this->get('logger')->debug("Subscription not found with id: " . $subscriptionId);
//      throw $this->createNotFoundException();
//    }
//
//    $response = $this->render('WebServiceBundle:SubscriptionResource:entity.json.twig', array('entity' => $entity));
//    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);
//    return $response;
  }

}
