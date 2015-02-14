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
        ->setStatus(Payment::STATE_INITIATED)
        ->setProduct($currentProduct)
        ->setAmount($currentProduct->getPricing());
    $em->persist($payment);
    $em->flush();
    $this->get('logger')->debug(sprintf('Initiated payment for user: %s with payment: %s', $this->getUser(), $payment->getId()));

    $transactionResponse = $this->get('service.telenor.client')->getTransaction($this->getUser(), $payment);
    $transactionResponse_string = json_encode($transactionResponse);

    $payment->setTransactionResponse($transactionResponse_string);
    $em->persist($payment);
    $em->flush();

    $paymentLink = $this->getLink($transactionResponse->links, 'payment');
    $locationURL = $paymentLink->href . "?locale=" . $this->container->getParameter('telenor.client.locale');

    return $this->redirect($locationURL);
  }

  public function successAction($paymentId) {
    $em = $this->getDoctrine()->getManager();
    $payment = $em->getRepository('DomainBundle:Payment')->find($paymentId);
    if(!$payment) {
      $this->get('logger')->debug('Payment not found with id: ' . $paymentId);
      throw $this->createNotFoundException('Payment not found with id: ' . $paymentId);
    }

    $payment->setCompletedAt(new \DateTime());
    $payment->setStatus(Payment::STATE_CANCELLED);
    $em->merge($payment);
    $em->flush();

    $user = $payment->getUser();
    $userRights = $this->get('service.telenor.client')->getUserRights($user);

    $user->setUserRightsData(json_encode($userRights));
    $em->merge($user);
    $em->flush();

    $url = "polluxmusic://purchase?status=success";
    return $this->redirect($url);
  }

  public function cancelAction($paymentId) {
    $em = $this->getDoctrine()->getManager();
    /* @var Payment $payment */
    $payment = $em->getRepository('DomainBundle:Payment')->find($paymentId);
    if(!$payment) {
      $this->get('logger')->debug('Payment not found with id: ' . $paymentId);
      throw $this->createNotFoundException('Payment not found with id: ' . $paymentId);
    }
    $payment->setCompletedAt(new \DateTime());
    $payment->setStatus(Payment::STATE_CANCELLED);
    $em->merge($payment);
    $em->flush();

    $url = "polluxmusic://purchase?status=cancelled";
    return $this->redirect($url);
  }

  /**
   * @param array $links
   * @param $string
   * @return \stdClass|null
   */
  private function getLink(array $links, $string) {
    foreach($links as $link) {
      if($link->rel == $string) {
        return $link;
      }
    }

    return null;
  }

}
