<?php 
require_once 'db_connection.php';

session_start();
if(!empty($_SESSION['userid']))
{
   if (!empty($_SESSION['start_time'])) {
      $timeDiffernce = mktime() - $_SESSION['start_time'];
         if ($timeDiffernce > 120) {
            $abort = true;  
            session_unset();
            session_destroy();           
         } else $_SESSION['start_time'] = time();
      }else $_SESSION['start_time'] = time();;
}


if(!isset ($abort))
{

$row = $_POST['row'];
$column = $_POST['column'];

// Connecting to db
$dbc = mysqli_connect(db_host,db_user,db_password,db_name) or die ("Error Connecting to DB");

// Consulting the status of the specific seat
$query = "UPDATE seats SET `$column` = 0 WHERE ID = $row";

// Retrieving data from the DataBase
$data = mysqli_query($dbc,$query);

// Closing db connection
mysqli_close($dbc);

if(!$data){
    echo "There was a problem deleting your selection, try again!";
}else{
    echo "Your reservation was succesfully deleted";
}
}else echo "Error: This session has time out";
?>