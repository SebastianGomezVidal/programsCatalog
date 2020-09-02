<?php 

$from = 'test@hostinger-tutorials.com';

$to = 'juanquindio@gmail.com';

$subject = "Checking PHP mail";

$message = "PHP mail works just fine";

$headers =
    "Content-Type: text/plain; charset=UTF-8" . "\r\n" .
    "MIME-Version: 1.0" . "\r\n" .
    "From: $from" . "\r\n" .
    "X-Mailer: PHP/" . phpversion();

$value = mail ($to,$subject,$message,$headers);

echo $value;




?>