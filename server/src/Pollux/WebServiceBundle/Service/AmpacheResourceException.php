<?php

namespace Pollux\WebServiceBundle\Service;


use Symfony\Component\HttpKernel\Exception\HttpException;

class AmpacheResourceException extends HttpException {
  /**
   * Constructor.
   *
   * @param string $action
   * @param array $parameters
   * @param string $message The internal exception message
   * @param int $statusCode
   * @param \Exception $previous The previous exception
   */
  public function __construct($action, array $parameters = array(), $message = "", \Exception $previous = null, $statusCode = 404)
  {
    $message = "Failed to process ampache request for action: $action parameters: " . json_encode($parameters);
    parent::__construct($statusCode, $message, $previous);
  }

}
