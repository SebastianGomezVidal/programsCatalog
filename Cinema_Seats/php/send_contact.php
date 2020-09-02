<?php


$name =     $_POST['name'];
$from =     $_POST['email'];
$subject =  $_POST['subject'];
$message =  $_POST['mess'];

if(isset($_POST['submit'])){

    $to = "juanquindio@gmail.com";
    $header = "from: $from";
    $send_contact = mail ($to, $subject,$message, $header);

    if($send_contact){
        $message = "I have recived your contact information!";
        setcookie("message",addslashes($message),'', '/');
    }else{
        $message = "There was an error sending data!";
        setcookie("message",addslashes($message),'', '/');
    }

    $url = 'https://'.$_SERVER['HTTP_HOST'].dirname(dirname($_SERVER['PHP_SELF'])).'/contact.html';
    header('Location: '.$url.'');
    exit();
}





?>