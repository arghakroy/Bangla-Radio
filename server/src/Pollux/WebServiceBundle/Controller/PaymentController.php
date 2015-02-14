<?php

namespace Pollux\WebServiceBundle\Controller;

use Pollux\DomainBundle\Entity\Payment;
use Pollux\DomainBundle\Entity\User;
use Pollux\DomainBundle\Repository\ProductRepository;
use Pollux\WebServiceBundle\Utils\Headers;
use Pollux\WebServiceBundle\Utils\MimeType;
use Psr\Log\InvalidArgumentException;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class PaymentController extends Controller {

  public function initiatePaymentAction($productId) {
    $em = $this->getDoctrine()->getManager();
    $productRepository = $em->getRepository('DomainBundle:Product');
    $product = $productRepository->find($productId);
    $currentProduct = $productRepository->getCurrentProduct();
    if (!$currentProduct && $currentProduct->getId() != $product->getId()) {
      $this->get('logger')->debug("No current product found with id: $productId for purchasing");
      throw $this->createNotFoundException("No current product found with id: $productId for purchasing");
    }

    $payment = Payment::createPayment()
        ->setUser($this->getUser())
        ->setInitiatedAt(new \DateTime())
        ->setProduct($currentProduct)
        ->setAmount($currentProduct->getPricing());
    $em->persist($payment);
    $em->flush();
    $transactionResponse = $this->get('service.telenor.client')->getTransaction($this->getUser(), $payment);
    $locationLinks = $transactionResponse->links[0];
    $locationURL = $locationLinks->href . "?locale=" . $this->container->getParameter('telenor.client.locale');

    return $this->redirect($locationURL);
  }

  public function successAction(Request $request, $paymentId) {
    /**
     * @var User $user
     */
    $em = $this->getDoctrine()->getManager();
    $telenorClient = $this->get('service.telenor.client');
    $userId = $request->query->get('user');
    $user = $this->getDoctrine()->getManager()->getRepository('DomainBundle:User')->findUserByUsername($userId);
    if(!$user) {
      $this->get('logger')->debug("No user found with id: $userId");
      $this->createNotFoundException("No user found with id: $userId");
    }

    $userRights = $telenorClient->getUserRights($user);
    $this->get('logger')->debug(json_encode($userRights));

    $user->setUserRightsData(json_encode($userRights));
    $em->merge($user);
    $em->flush();

    $url = "polluxmusic://purchase?status=success";
    $this->get('logger')->debug("Redirecting to: " . $url);
    return $this->redirect($url);
  }

  public function cancelAction($paymentId) {
    $url = "polluxmusic://purchase?status=cancelled";
    return $this->redirect($url);
  }

}
