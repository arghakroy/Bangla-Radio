<?php

namespace Pollux\WebServiceBundle\Controller;

use Doctrine\ORM\EntityManager;
use Pollux\DomainBundle\Entity\Payment;
use Pollux\DomainBundle\Entity\Product;
use Pollux\DomainBundle\Entity\Subscription;
use Pollux\SecurityBundle\Service\TelenorClient;
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

    $payment = $this->createPayment($currentProduct, $em);
    $this->get('logger')->debug(sprintf('Initiated payment for user: %s with payment: %s', $this->getUser(), $payment->getId()));

    $transactionResponse = $this->get('service.telenor.client')->getTransaction($this->getUser(), $payment);
    $transactionResponse_string = json_encode($transactionResponse);

    $payment->setTransactionResponse($transactionResponse_string);
    $em->persist($payment);
    $em->flush();

    $paymentLink = TelenorClient::getLink($transactionResponse->links, 'payment');
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

    $url = "polluxmusic://purchase?status=cancelled";
    if ($payment->getStatus() == Payment::STATE_SUCCESS) {
      $url = "polluxmusic://purchase?status=success";
    }
    else if ($payment->getStatus() == Payment::STATE_INITIATED) {
      $this->updateTransaction($payment);
      $url = "polluxmusic://purchase?status=success";
    }

    return $this->redirect($url);
  }

  public function cancelAction($paymentId) {
    $em = $this->getDoctrine()->getManager();
    /* @var Payment $payment */
    $payment = $em->getRepository('DomainBundle:Payment')->find($paymentId);
    if(!$payment || $payment->getStatus() != Payment::STATE_INITIATED) {
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

  private function updateTransaction(Payment $payment) {
    $user = $payment->getUser();
    $userRights = $this->get('service.telenor.client')->getUserRights($user);
    foreach($userRights->rights as $userRight) {
      $grantorContext = json_decode($userRight->grantorContext);
      if($grantorContext->orderId == $payment->getId()) {
        $subscription = $this->createSubscription($payment, $userRight, $user);
        $payment->setCompletedAt(new \DateTime());
        $payment->setStatus(Payment::STATE_SUCCESS);

        $em = $this->getDoctrine()->getManager();
        $em->persist($subscription);
        $em->merge($payment);
        $em->flush();

        break;
      }
    }
  }

  /**
   * @param Payment $payment
   * @param $userRight
   * @param $user
   * @return Subscription
   */
  private function createSubscription(Payment $payment, $userRight, $user) {
    list($start, $end) = explode("/", $userRight->timeInterval);
    return Subscription::createSubscription()
        ->setUser($user)
        ->setPayment($payment)
        ->setDateCreated(new \DateTime())
        ->setConnectTxId($userRight->rightId)
        ->setConnectTxUrl(TelenorClient::getLink($userRight->link, 'self')->href)
        ->setConnectStatus($userRight->active)
        ->setConnectStartTime(date_create_from_format(TelenorClient::DATE_TIME_FORMAT, $start))
        ->setConnectEndTime(date_create_from_format(TelenorClient::DATE_TIME_FORMAT, $end))
        ->setConnectTxJson(json_encode($userRight));
  }

  private function createPayment(Product $currentProduct, EntityManager $em) {
    $payment = Payment::createPayment()
        ->setUser($this->getUser())
        ->setInitiatedAt(new \DateTime())
        ->setStatus(Payment::STATE_INITIATED)
        ->setProduct($currentProduct)
        ->setAmount($currentProduct->getPricing());
    $em->persist($payment);
    $em->flush();

    return $payment;
  }

}
