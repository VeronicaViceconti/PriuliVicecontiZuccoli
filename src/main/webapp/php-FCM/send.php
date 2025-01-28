<?php

use Google\Auth\Credentials\ServiceAccountCredentials;
use Google\Auth\HttpHandler\HttpHandlerFactory;

require 'vendor/autoload.php';

$credential = new ServiceAccountCredentials(
	"https://www.googleapis.com/auth/firebase.messaging",
	json_decode(file_get_contents("pvKey.json"), true)
);

$token = $credential->fetchAuthToken(HttpHandlerFactory::build());

$ch = curl_init("https://fcm.googleapis.com/v1/projects/studentsandcompanies-780ac/messages:send");

curl_setopt($ch, CURLOPT_HTTPHEADER,[
	'Content-Type: application/json',
	'Authorization: Bearer ' . $token['access_token']
]);

curl_setopt($ch, CURLOPT_POSTFIELDS, '{
  "message": {
    "token": "cSIF9yNnbckZGDzQVTPP7s:APA91bGQu38tpATQ_6QpbxpgValohTbB6gp12MHtd9GQcKaJr9Nxgm-FcPnBhINsTKoEYmOp8dEj2DtGMWY0drE8PuTf0QKiKUiVLsOhgVGQ6Hg6qzmatIY",
    "notification": {
      "title": "Notification Title",
      "body": "Notificatio body"
    },
    "webpush": {
      "fcm_options": {
        "link": "https://google.com"
      }
    }
  }
}');

curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "post");

$response = curl_exec($ch);

curl_close($ch);

echo $response;

?>