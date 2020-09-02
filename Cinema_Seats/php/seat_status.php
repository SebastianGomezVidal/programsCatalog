<?php 
require_once 'db_connection.php';

session_start();
if(!empty($_SESSION['userid']))
{
   if (!empty($_SESSION['start_time'])) {
      $timeDiffernce = time() - $_SESSION['start_time'];
         if ($timeDiffernce > 120) { // expire after 2 minutes
                $message="Your session has expired!, you ought to login again";
                
                setcookie("userid","anonymous",'','/');
                setcookie("email",'' ,'','/');
                setcookie('banner_display','disable','','/'); 
                setcookie('seats_for_db','','','/'); 
                setcookie("message",$message,'','/');

                $url = 'https://'.$_SERVER['HTTP_HOST'].dirname(dirname($_SERVER['PHP_SELF'])).'/login.html';
                header('Location: '.$url.'');
                exit();
         } else $_SESSION['start_time'] = time();
      }else $_SESSION['start_time'] = time();;
}

// Array variable to return results
$value = array();

$row = $_POST['row'];
$column = $_POST['column'];

// Connecting to db
$dbc = mysqli_connect(db_host,db_user,db_password,db_name) or die ("Error Connecting to DB");

// Consulting the status of the specific seat
$query = "SELECT `$column` FROM seats WHERE ID =$row";

// Retrieving data from the DataBase
$data = mysqli_query($dbc,$query);

// Fecthing the data into an array variable
$result = mysqli_fetch_row($data);

// Closing db connection
mysqli_close($dbc);

// returning the result to javascript
echo json_encode($result);

// Emptying variable for any incoming new operation
$value=[];

?>