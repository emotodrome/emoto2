<?php
error_reporting(E_ALL);
ini_set("display_errors", 1);

$SQL_HOST = "localhost";
$SQL_USER = "darrenli";
$SQL_PASS = "D1abl089.";
$SQL_SCHEMA = "darrenli_emoto";
$SQL_TABLE_TODAY = "ice_coverage_today";
$SQL_TABLE_YESTERDAY = "ice_coverage_yesterday";

$link = mysql_connect($SQL_HOST, $SQL_USER, $SQL_PASS);



if(!$link):
  echo "Connection to database failed.";
  exit();
endif;

$longitude = 0;
$latitude = 0;
if(isset($_GET['longitude']) && isset($_GET['latitude'])):
  $longitude = (int)$_GET['longitude'];
  $latitude = (int)$_GET['latitude'];
endif;

$i=0;
$result = mysql_query("SELECT * FROM $SQL_SCHEMA.$SQL_TABLE_TODAY
  WHERE longitude >= '".($longitude-5)."' AND longitude <= '".($longitude+5)."'
  AND latitude >= '".($latitude-5)."' AND latitude <= '".($latitude+5)."'");
while($row = mysql_fetch_assoc($result)):
  echo "(".$row['latitude'].",".$row['longitude']."): (".$row['of64'].")".$row['ice']."<br>";
  
  //echo ++$i;
  //var_dump($row);
endwhile;


mysql_close($link);

?>
