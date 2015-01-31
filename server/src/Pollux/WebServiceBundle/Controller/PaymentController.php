<?php

namespace Pollux\WebServiceBundle\Controller;

use Pollux\DomainBundle\Entity\User;
use Pollux\WebServiceBundle\Utils\Headers;
use Pollux\WebServiceBundle\Utils\MimeType;
use Psr\Log\InvalidArgumentException;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

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
    $userId = $request->query->get('sharedSecret');
    $telenorClient = $this->get('service.telenor.client');
    $currentProduct = $this->getDoctrine()->getManager()->getRepository('DomainBundle:Product')->getCurrentProduct();
    if (!$currentProduct) {
      throw new InvalidArgumentException("No current product found");
    }

    /**
     * @var User $user
     */
    $user = $this->getDoctrine()->getManager()->getRepository('DomainBundle:User')->loadUserByUsername($userId);
//    var_dump($user->getUserInfoData());
    $transactionResponse = $telenorClient->getTransaction($user, $currentProduct);
//    var_dump($transactionResponse);
    $locationLinks = $transactionResponse->links[0];
    $locationURL = $locationLinks->href;

//    var_dump($locationURL);
//    return new Response();

    return $this->redirect($locationURL);
  }

  public function successAction(Request $request, $uniqueId) {
    /**
     * @var User $user
     */
    $em = $this->getDoctrine()->getManager();
    $telenorClient = $this->get('service.telenor.client');
    $userId = $request->query->get('user');
    $user = $this->getDoctrine()->getManager()->getRepository('DomainBundle:User')->loadUserByUsername($userId);

    $userRights = $telenorClient->getUserRights($user);
    $this->get('logger')->debug(json_encode($userRights));

    $user->setUserRightsData(json_encode($userRights));
    $em->merge($user);
    $em->flush();

    $url = "polluxmusic://purchase?status=success";
    $this->get('logger')->debug("Redirecting to: " . $url);
    return $this->redirect($url);
  }

  public function cancelAction($uniqueId) {
    $url = "polluxmusic://purchase?status=cancelled";
    return $this->redirect($url);
  }

}
