<?php

function microtime_float() {
  list($usec, $sec) = explode(" ", microtime());
  return ((float)$usec + (float)$sec);
}

$time_start = microtime_float();

$handle = fopen("http://coastwatch.pfeg.noaa.gov/erddap/griddap/erdAIicov1day.csv?ice[(2011-05-22T12:00:00Z)][%20%20%20%20(0.0)][(-85.0):(85.0)][(0.0):(360.0)]&.draw=surface&.vars=longitude%7Clatitude%7Cice&.colorBar=%7C%7C%7C%7C%7C", "rb");
//http://upwell.pfeg.noaa.gov/erddap/griddap/noaa_pfeg_5148_25eb_e2ea.csv?ice[(2011-05-22T12:00:00Z)][(0.0)][(-85.0):(85.0)][(0.0):(360.0)]&.draw=surface&.vars=longitude|latitude|ice&.colorBar=|||||", "rb");
$i = 0;
$matches = 0;
while(!feof($handle) && $i < 100000):
  $i++;
  $line = fgets($handle);
  //echo substr($line,-4, 3);
  if(!strpos($line,"NaN"))
    $matches++;
    //echo fgets($handle)."<br>";
endwhile;
fclose($handle);


$time_end = microtime_float();
$time = $time_end - $time_start;

echo "<br><br>Script took $time seconds to process $i lines and match $matches lines<br>";

?>
