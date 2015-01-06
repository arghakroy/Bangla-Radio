<?php

namespace Pollux\WebServiceBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;

class DefaultController extends Controller
{
  public function indexAction($name)
  {
    return $this->render('WebServiceBundle:Default:index.html.twig', array('name' => $name));
  }
}
