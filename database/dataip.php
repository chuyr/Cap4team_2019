<?php

$con=mysqli_connect("localhost","root","1234","mysql");

 

if (mysqli_connect_errno($con))

{

   echo "Failed to connect to MySQL: " . mysqli_connect_error();

}

 

$mac = $_GET['mac'];

$result = mysqli_query($con,"select ip from mac_ip where mac = '$mac' order by id desc limit 1;");

 

$row = mysqli_fetch_array($result);

$data = $row[0];

 

if($data){

echo $data;

}

mysqli_close($con);

?>
