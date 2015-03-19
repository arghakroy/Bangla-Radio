<?php

namespace Pollux\WebServiceBundle\Controller;


use Pollux\DomainBundle\Entity\User;
use Pollux\WebServiceBundle\Utils\Headers;
use Pollux\WebServiceBundle\Utils\MimeType;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;

class ProfileResourceController extends Controller {

  public function getProfile() {
    /**
     * @var User $user
     */
    $user = $this->getUser();
    $userInfo = json_decode($user->getUserInfoData());

    $response = $this->render('WebServiceBundle:SubscriptionResource:entity.json.twig', array(
        'phoneNumber' => $userInfo->phone_number
    ));
    $response->headers->set(Headers::CONTENT_TYPE, MimeType::APPLICATION_JSON);
    return $response;
  }

}
