<?php
function microtime_float() {
  list($usec, $sec) = explode(" ", microtime());
  return ((float)$usec + (float)$sec);
}

$time_start = microtime_float();

$url  = 'http://coastwatch.pfeg.noaa.gov/erddap/griddap/erdAIicov1day.csv?ice[(2011-05-22T12:00:00Z)][%20%20%20%20(0.0)][(-85.0):(85.0)][(0.0):(360.0)]&.draw=surface&.vars=longitude%7Clatitude%7Cice&.colorBar=%7C%7C%7C%7C%7C';
//$url = "http://www.dartmouth.edu/~scg/";
$path = 'local.csv';

$fp = fopen($path, 'w');

$ch = curl_init($url);
curl_setopt($ch, CURLOPT_FILE, $fp);

$data = curl_exec($ch);

curl_close($ch);
fclose($fp);

$time_end = microtime_float();
$time = $time_end - $time_start;

echo "Script took $time seconds to download to local.txt<br>\n";
?>
