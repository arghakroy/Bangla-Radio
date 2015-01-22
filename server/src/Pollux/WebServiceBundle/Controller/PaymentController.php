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

    $sharedSecret = $request->query->get('sharedSecret');
    
    /*
     * Get user from sharedSecret
     */
    $user = $this->getDoctrine()->getManager()->getRepository('DomainBundle:User')->getUserFromSecret($sharedSecret);
    
    if(!$user){
      throw $this->createNotFoundException("User not found");
    }
    $accessToken = $user->getAccessToken();
    $userInfoData = json_decode($user->getUserInfoData());
   
    /*
     * Get product from current Date
     */
    $today = new \DateTime();
    $product = $this->getDoctrine()->getManager()->getRepository('DomainBundle:Product')->getProduct($today);
    if(!$product){
      throw $this->createNotFoundException("User not found");
    }
    
    /*
     * Get telenor service for transaction
     */
    $telenorClient = $this->get('service.telenor.client');
    $transactionResponse = $telenorClient->getTransaction($accessToken,$userInfoData->sub,$product);
    $locationLinks = $transactionResponse->links[0];
    $locationURL = $locationLinks->href;
    
    return $this->redirect($locationURL,303);
    
  }
  
  public function successAction($uniqueId) {
    /*
     *  save to subscription table need to do with uniqueId as orderId
     */
    
    $url = "polluxmusic://purchase?status=success"; 
    return $this->redirect($url);
  }
  
  public function cancelAction($uniqueId) {
    /*
     *  save to subscription table need to do with uniqueId as orderId
     */
    
    $url = "polluxmusic://purchase?status=cancelled"; 
    return $this->redirect($url);
  }

}
