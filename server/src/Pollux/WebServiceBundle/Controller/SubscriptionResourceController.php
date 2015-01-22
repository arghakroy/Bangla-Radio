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

  public function getCollectionAction() {
    //get the `secret` header with the request
    

    //get the user info from server based on the secret provided by the client
    

    //if no `secret` send the client a 412 precondition failed error
  }

  public function getAction(Request $request) {

    //get the `secret` header with the request
    //HTTP_X_SECRET
    $sharedSecret = $request->headers->get('http-x-secret');

    if($sharedSecret == '')
    {
      //if no `secret` send the client a 412 precondition failed error
      return new Response('No client secret', Response::HTTP_PRECONDITION_FAILED);
    }

    print_r($sharedSecret);
    
    //get the user info from server based on the secret provided by the client
    $user = $this->getDoctrine()->getManager()->getRepository('DomainBundle:User')->getUserFromSecret($sharedSecret);

    if(!is_null($user))
    {
      print_r($user->getAccessToken());

      print_r($user->getUserInfoData());
    }

    
  }

}
