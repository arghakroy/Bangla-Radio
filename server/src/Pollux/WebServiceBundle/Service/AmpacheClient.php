<?php

namespace Pollux\WebServiceBundle\Service;


use Psr\Log\LoggerInterface;
use Symfony\Component\HttpFoundation\Response;

class AmpacheClient {

  private $logger;

  private $endpoint;
  private $username;
  private $password;
  private $tokenPath;

  private $token;

  function __construct($endpoint, $username, $password, $tokenPath, LoggerInterface $logger) {
    $this->endpoint = $endpoint;
    $this->username = $username;
    $this->password = $password;
    $this->tokenPath = $tokenPath;

    $this->logger = $logger;
  }

  public function getSongs() {
    return $this->execute('songs');
  }

  public function getSong($id) {
    $root = $this->execute('song', array(
        'filter' => $id
    ));
    return $root->song;
  }

  public function getAlbums() {
    return $this->execute('albums');
  }

  public function getAlbum($id) {
    $root = $this->execute('album', array(
        'filter' => $id
    ));
    return $root->album;
  }

  public function getAlbumSongs($albumId) {
    return $this->execute('album_songs', array(
        'filter' => $albumId
    ));
  }

  public function getArtists() {
    return $this->execute('artists');
  }

  public function getArtist($id) {
    $root = $this->execute('artist', array(
        'filter' => $id
    ));
    return $root->artist;
  }

  public function getArtistSongs($artistId) {
    return $this->execute('artist_songs', array(
        'filter' => $artistId
    ));
  }

  public function getArtistAlbums($artistId) {
    return $this->execute('artist_albums', array(
        'filter' => $artistId
    ));
  }

  private function execute($action, array $parameters = array()) {
    if(!$this->token) {
      $this->lazyLoadToken();
    }

    $params = "";
    foreach ($parameters as $key => $value) {
      $params .= "&{$key}={$value}";
    }

    $url = "{$this->endpoint}?auth={$this->token}&action={$action}{$params}";
    $curl = curl_init();
    curl_setopt_array($curl, array(
        CURLOPT_RETURNTRANSFER => 1,
        CURLOPT_URL => $url
    ));

    $output = curl_exec($curl);
    curl_close($curl);

    $xml = simplexml_load_string($output);
    if (isset($xml->error) && $xml->error->attributes()['code'] = Response::HTTP_UNAUTHORIZED) {
      $this->getTokenFromServer();
      return $this->execute($action, $parameters);
    }
    return $xml;
  }

  private function getTokenFromServer() {
    $time = time();
    $passPhrase = hash('sha256', $time . hash('sha256', $this->password));
    $url = "{$this->endpoint}?action=handshake&auth={$passPhrase}&timestamp={$time}&version=350001&user={$this->username}";

    $this->logger->debug("Token URL: " . $url);
    $curl = curl_init();
    curl_setopt_array($curl, array(
        CURLOPT_RETURNTRANSFER => 1,
        CURLOPT_URL => $url
    ));

    $output = curl_exec($curl);
    curl_close($curl);
    $this->logger->debug("Token: " . $output);
    $xml = simplexml_load_string($output);
    $token = "" . $xml->auth;
    file_put_contents($this->tokenPath, $token);
    $this->token = $token;
    return $this->token;
  }

  private function getTokenFromFile() {
    return file_get_contents($this->tokenPath);
  }

  private function lazyLoadToken() {
    $token = $this->getTokenFromFile();
    if (!$token) {
      $token = $this->getTokenFromServer();
    }

    $this->token = $token;
  }

}
