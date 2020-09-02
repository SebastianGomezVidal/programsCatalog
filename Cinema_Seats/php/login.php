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
        
                //Grab the user log-in data from POST
            
                $email = mysqli_real_escape_string($dbc,trim($_POST['email']));
                $password = mysqli_real_escape_string($dbc,trim($_POST['password']));
                
                if(!empty($email) && !empty($password))
                {
                    //Look up the user and password in the database
                    $query = "SELECT userid,email FROM users WHERE email ='$email' AND password = SHA('$password');";
                    
                    $data = mysqli_query($dbc,$query);

                    $row = mysqli_fetch_array($data);

                    mysqli_close($dbc);
                    
                    if( !empty($row))
                    {                     
                        // The log in is ok so set the user ID and username cookies, and redirect to the page
                        // with the options for requiring reservations and purchases

                        session_start();
                                
                        $_SESSION['userid'] = $row['userid'];
                        $_SESSION['email'] = $row['email'];
                        $_SESSION['start_time'] = mktime();
                        $_SESSION['reference'] = "login";
    
                        $welcome = "Welcome to Theatry";
                        
                        $message = seats_validation();
                        
                        $message = $welcome.' '.$message;

                        setcookie("userid",addslashes($_SESSION['userid']),'','/');
                        setcookie("email", htmlspecialchars($_SESSION['email']),'','/');
                        setcookie("banner_display",addslashes('disable'),'', '/');
                        setcookie("message",addslashes($message),'', '/');
                        
                        $url = 'https://'.$_SERVER['HTTP_HOST'].'/'.basename(dirname(__DIR__)).'/index.php';
                        header('Location: '.$url.'');
                        exit();
                        
                                  
                    }
                    else
                    {
                        //The username/password are incorrect
                        $message = "The user or password is wrong!";
                        setcookie("message",addslashes($message),'', '/');

                        $url = 'https://'.$_SERVER['HTTP_HOST'].dirname(dirname($_SERVER['PHP_SELF'])).'/login.html';
                        header('Location: '.$url.'');
                        exit();
                    }      
                }
                else
                {
                        
                        //The username/password weren`t entered
                        $message = "There was a problem with the connection, try agan";
                        setcookie("message",addslashes($message),'', '/');
                    
                        $url = 'https://'.$_SERVER['HTTP_HOST'].dirname(dirname($_SERVER['PHP_SELF'])).'/login.html';
                        header('Location: '.$url.'');
                        exit();
                }
               
        }
    }
?>

