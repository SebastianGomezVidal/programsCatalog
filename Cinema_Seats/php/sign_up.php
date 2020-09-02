<?php
require_once 'db_connection.php';
include 'seats_validation.php';

 //If the user isnt logged in, try to log them in
 if(!isset($_SESSION['email']))
 {
     if(isset($_POST['submit']))
    {
        //Connecting to the DataBase
        $dbc = mysqli_connect(db_host,db_user,db_password,db_name) or die ("Error Connecting to DB");
    
        //Grab the profile data from POST
        $email = mysqli_real_escape_string($dbc,trim($_POST['email']));
    
        $password1 = mysqli_real_escape_string($dbc,trim($_POST['password1']));
    
        $password2 = mysqli_real_escape_string($dbc,trim($_POST['password2']));
    
        if(!empty($email) && !empty($password1) && !empty($password2) && 
        ( $password1 == $password2))
        {
            //Checking the user has not been registered before
            $query = "SELECT * FROM users WHERE email ='$email'";
            $data = mysqli_query($dbc,$query);

            if(mysqli_num_rows($data) == 0) 
            {
                //The user is unique, so insert the data into the database
                $query = "INSERT INTO users (email, password, date) VALUES ('$email',SHA('$password1'),NOW())";
                mysqli_query($dbc,$query);

                //Look up the user and password in the database
                $query = "SELECT userid,email FROM users WHERE email ='$email' AND password = SHA('$password1');"; 

                $data = mysqli_query($dbc,$query);
                $row = mysqli_fetch_array($data);
                mysqli_close($dbc);

                session_start();

                $_SESSION['userid'] = $row['userid'];
                $_SESSION['email'] = $row['email'];
                $_SESSION['start_time'] = mktime();
                $_SESSION['reference'] = "login";
    
                $message = seats_validation();

                setcookie("userid",addslashes($_SESSION['userid']),'','/');
                setcookie("email", htmlspecialchars($_SESSION['email']),'','/');
                setcookie("banner_display",addslashes('disable'),'', '/');
                setcookie("message",addslashes($message),'', '/');
                        
                $url = 'https://'.$_SERVER['HTTP_HOST'].'/'.basename(dirname(__DIR__)).'/'.'/index.php';
                header('Location: '.$url.'');
                exit();
            }
            else
            {
                //The username/password are incorrect
                $message = "This user already exists!";
                setcookie("message",addslashes($message),'', '/');

                $url = 'https://'.$_SERVER['HTTP_HOST'].dirname(dirname($_SERVER['PHP_SELF'])).'/sign_up.html';
                header('Location: '.$url.'');
                exit();
            }     
        }
        else
        {
                 //The username/password weren`t entered
                 $message = "There was a problem with the connection, try agan";
                 setcookie("message",addslashes($message),'', '/');
             
                 $url = 'https://'.$_SERVER['HTTP_HOST'].dirname(dirname($_SERVER['PHP_SELF'])).'/sign_up.html';
                 exit();
        }
    }
}
?>

