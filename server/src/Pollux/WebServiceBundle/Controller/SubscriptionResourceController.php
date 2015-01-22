<?php

namespace Pollux\WebServiceBundle\Controller;


use Pollux\WebServiceBundle\Utils\Headers;
use Pollux\WebServiceBundle\Utils\MimeType;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\EventListener\RouterListener;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Symfony\Component\Security\Core\Exception\UsernameNotFoundException;

class SubscriptionResourceController extends Controller {

  const TELENOR_RIGHTS = "https://stagingapi.comoyo.com/id";

  public function getCollectionAction() {
    //get the `secret` header with the request
    

    //get the user info from server based on the secret provided by the client
    

    //if no `secret` send the client a 412 precondition failed error
  }

  public function getAction(Request $request) {

    //get the `secret` header with the request
    //HTTP_X_SECRET
    $telenorClient = $this->get('service.telenor.client');
    $sharedSecret = $request->headers->get('http-x-secret');

    if($sharedSecret == '')
    {
      //if no `secret` send the client a 412 precondition failed error
      return new Response('No client secret', Response::HTTP_PRECONDITION_FAILED);
    }
    
    //get the user info from server based on the secret provided by the client
    $user = $this->getDoctrine()->getManager()->getRepository('DomainBundle:User')->getUserFromSecret($sharedSecret);

    if(!$user)
    {
      throw $this->createNotFoundException("User not found");
    }
    $accessToken = $user->getAccessToken();
    $userInfoData = json_decode($user->getUserInfoData());

    
    $id = $userInfoData->sub;
    $token = $user->getAccessToken();

    //get the rights info
    $content = $telenorClient->getUsersRight($id, $token);

    
    if(is_null($content)) 
    {
      return new Response('', Response::HTTP_NO_CONTENT);
    }


    $j = json_decode($content);
    $sku = $j->right[0]->sku;
    $timeInterval = $j->right[0]->timeInterval ;

    $k = explode("/", $timeInterval );

    $start_time = $k[0];
    $end_time = $k[1];

    

    $entity['sku'] = $sku;
    $entity['start_date'] = $start_time;
    $entity['end_date'] = $end_time;
    $entity['status'] = $j->right[0]->state;

    $response = $this->render('WebServiceBundle:SubscriptionResource:entity.json.twig', array('entity' => $entity));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);
    return $response;


  }

  public function postAction() {


  }


}
