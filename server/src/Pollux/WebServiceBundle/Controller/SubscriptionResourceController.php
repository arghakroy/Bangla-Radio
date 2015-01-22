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
    $secret = $request->headers->get('x-secret');
    $user = $this->getDoctrine()->getManager()->getRepository('DomainBundle:User')->getUserFromSecret($sharedSecret);
    echo $user->getAccessToken();
    

    //get the user info from server based on the secret provided by the client
    

    //if no `secret` send the client a 412 precondition failed error
  }

}
