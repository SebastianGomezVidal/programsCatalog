<?php 
        session_start();
        
        setcookie('PHPSESSID', '', time() - 86400, '/');
        setcookie("userid","anonymous",'','/');
        setcookie("email",'' ,'','/');
        setcookie('banner_display','disable','','/'); 
        setcookie('seats_for_db','','','/'); 

        $message="Your have log out succesfully";
        setcookie("message",addslashes($message),'', '/');

        $url = 'https://'.$_SERVER['HTTP_HOST'].dirname($_SERVER['PHP_SELF']).'/index.php';
        header('Location: '.$url.'');

        session_unset();
        session_destroy();
        
        exit();
?>