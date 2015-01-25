<?php

namespace Pollux\WebServiceBundle\Controller;

use Symfony\Component\HttpFoundation\Request;
use Pollux\WebServiceBundle\Utils\Headers;
use Pollux\WebServiceBundle\Utils\MimeType;
use Pollux\DomainBundle\Entity\User;
use Pollux\DomainBundle\Entity\Subscription;
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
    $telenorClient = $this->get('service.telenor.client');
    
    /*
     * Get user from sharedSecret
     */
    $user = $this->getDoctrine()->getManager()->getRepository('DomainBundle:User')->getUserFromSecret($sharedSecret);
    
    if(!$user){
      throw $this->createNotFoundException("User not found");
    }
    
    $currentTime = new \DateTime();
    
    $accessToken = $this->validateAndReturnAccessToken($currentTime, $user, $telenorClient);
    
    
    
    $userInfoData = json_decode($user->getUserInfoData());
   
    /*
     * Get product from current Date
     */
    $product = $this->getDoctrine()->getManager()->getRepository('DomainBundle:Product')->getProduct($currentTime);
    if(!$product){
      throw $this->createNotFoundException("User not found");
    }
    
    /*
     * Get telenor service for transaction
     */
    
    $transactionResponse = $telenorClient->getTransaction($accessToken,$userInfoData->sub,$product);
    $locationLinks = $transactionResponse->links[0];
    $locationURL = $locationLinks->href;
    
    return $this->redirect($locationURL,303);
    
  }
  
  public function successAction($uniqueId) {
    /*
     *  save to subscription table need to do with uniqueId as orderId
     */
//    $em = $this->getDoctrine()->getManager();
//    $subscriptionObject = new Subscription();
//    $subscriptionObject->setStatus($status);
//    $em->persist($subscriptionObject);
//    $em->flush();
    
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
  
  private function validateAndReturnAccessToken($currentTime,$user,$telenorClient) {
    
    $em = $this->getDoctrine()->getManager();
    
    $accessToken = $user->getAccessToken();
    if($currentTime >= $user->getExpireTime()) {
      $accessTokenData = json_decode($user->getAccessTokenData());
      $tokenResponse = $telenorClient->refreshToken($accessTokenData->refresh_token);
      $accessToken = $tokenResponse->access_token;
      
      $user->setAccessToken($accessToken);
      
      $expireTime = new \DateTime();
      $expireTime->add(new \DateInterval("PT3600S"));
      $user->setExpireTime($expireTime);
      
      $user->setAccessTokenData(json_encode($tokenResponse));
    
      $em->merge($user);
      $em->flush();
      
    } 
    return $accessToken;
  }

}
