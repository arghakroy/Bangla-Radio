<?php

namespace Pollux\WebServiceBundle\Controller;

use Pollux\DomainBundle\Entity\Payment;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;

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

  public function successAction($paymentId) {
    $em = $this->getDoctrine()->getManager();
    $payment = $em->getRepository('DomainBundle:Payment')->find($paymentId);
    if(!$payment) {
      $this->get('logger')->debug('Payment not found with id: ' . $paymentId);
      throw $this->createNotFoundException('Payment not found with id: ' . $paymentId);
    }
    $user = $payment->getUser();
    $userRights = $this->get('service.telenor.client')->getUserRights($user);
    $this->get('logger')->debug(json_encode($userRights));

    $user->setUserRightsData(json_encode($userRights));
    $em->merge($user);
    $em->flush();

    $url = "polluxmusic://purchase?status=success";
    $this->get('logger')->debug("Redirecting to: " . $url);
    return $this->redirect($url);
  }

  public function cancelAction($paymentId) {
    $em = $this->getDoctrine()->getManager();
    $payment = $em->getRepository('DomainBundle:Payment')->find($paymentId);
    if(!$payment) {
      $this->get('logger')->debug('Payment not found with id: ' . $paymentId);
      throw $this->createNotFoundException('Payment not found with id: ' . $paymentId);
    }

    $url = "polluxmusic://purchase?status=cancelled";
    return $this->redirect($url);
  }

}
