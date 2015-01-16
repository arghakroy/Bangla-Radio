<?php

namespace Pollux\SecurityBundle;

use Symfony\Component\DependencyInjection\ContainerBuilder;
use Symfony\Component\HttpKernel\Bundle\Bundle;

class PolluxSecurityBundle extends Bundle {
  public function build(ContainerBuilder $container) {
    parent::build($container);

    $extension = $container->getExtension('security');
  }
}
