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

  public function getAction(Request $request) {
    $userId = $request->query->get('sharedSecret');
    $telenorClient = $this->get('service.telenor.client');
    $currentProduct = $this->getDoctrine()->getManager()->getRepository('DomainBundle:Product')->getCurrentProduct();
    if (!$currentProduct) {
      throw new \InvalidArgumentException("No current product found");
    }

    /**
     * @var User $user
     */
    $user = $this->getDoctrine()->getManager()->getRepository('DomainBundle:User')->findUserByUsername($userId);
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
    $user = $this->getDoctrine()->getManager()->getRepository('DomainBundle:User')->findUserByUsername($userId);

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
