<?php 
require_once 'db_connection.php';

// Array variable to return results
$value = array();

// Connecting to the db
$dbc = mysqli_connect(db_host,db_user,db_password,db_name) or die ("There was en error Connecting to Database");

// Selecting all the seats values in the db
$query = "SELECT * FROM seats";

// Retrieving query result from the DataBase
$data = mysqli_query($dbc,$query) or die ("Error consulting Database");

//Fecthing the data into an array variable
while($result = mysqli_fetch_array($data,MYSQLI_NUM))
{
   $value[] = $result;   
}

// Emptying variables for any incoming new operation
$query="";
$data="";

// Closing the database connection
mysqli_close($dbc);

// returning the result to javascript
echo json_encode($value);

// Emptying variable for any incoming new operation
$value=[];
?>
