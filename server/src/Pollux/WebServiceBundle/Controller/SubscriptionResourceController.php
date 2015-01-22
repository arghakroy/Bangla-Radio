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
    $sharedSecret = $request->headers->get('x-secret');

    $t = $request->headers->get('X-SECRET');
    $s = $request->headers->get('x_secret');
    $u = $request->headers->get('X_SECRET');

    var_dump($sharedSecret);


    var_dump($t);

    var_dump($s);

    var_dump($u);

    $user = $this->getDoctrine()->getManager()->getRepository('DomainBundle:User')->getUserFromSecret($sharedSecret);

    var_dump($user);
    //echo $user->getAccessToken();
    
    $response = $this->render('WebServiceBundle:SubscriptionResource:entity.json.twig', array('entity' => $entity));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);
    return $response;
    

    //get the user info from server based on the secret provided by the client
    

    //if no `secret` send the client a 412 precondition failed error
  }

}
