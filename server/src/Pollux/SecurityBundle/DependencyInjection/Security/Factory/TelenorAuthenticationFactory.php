<?php

namespace Pollux\SecurityBundle\DependencyInjection\Security\Factory;


use Symfony\Bundle\SecurityBundle\DependencyInjection\Security\Factory\SecurityFactoryInterface;
use Symfony\Component\Config\Definition\Builder\NodeDefinition;
use Symfony\Component\DependencyInjection\ContainerBuilder;
use Symfony\Component\DependencyInjection\DefinitionDecorator;
use Symfony\Component\DependencyInjection\Reference;

class TelenorAuthenticationFactory implements SecurityFactoryInterface {

  public function create(ContainerBuilder $container, $id, $config, $userProvider, $defaultEntryPoint) {
    $providerId = 'security.authentication.provider.telenor.' . $id;
    $container
        ->setDefinition($providerId, new DefinitionDecorator('telenor.security.authentication.provider'))
        ->replaceArgument(0, new Reference($userProvider));

    $listenerId = 'security.authentication.listener.telenor.' . $id;
    $container
        ->setDefinition($listenerId, new DefinitionDecorator('telenor.security.authentication.listener'))
        ->replaceArgument(2, $id);

    return array($providerId, $listenerId, $defaultEntryPoint);
  }

  public function getPosition() {
    return 'pre_auth';
  }

  public function getKey() {
    return 'telenor';
  }

  public function addConfiguration(NodeDefinition $node) {
  }

}
