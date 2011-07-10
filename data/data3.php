<?php

set_time_limit(0);

function microtime_float() {
  list($usec, $sec) = explode(" ", microtime());
  return ((float)$usec + (float)$sec);
}

$time_start = microtime_float();
$ice_coverage = array();
$coverage_count = array();
for($i=-85; $i<=85; $i++):
  $ice_coverage[$i] = array();
  $coverage_count[$i] = array();
  for($j=0; $j<=360; $j++):
    $ice_coverage[$i][$j] = 0.0;
    $coverage_count[$i][$j] = 0;
  endfor;
endfor;
//var_dump($ice_coverage);
$time_end = microtime_float();
$time = $time_end - $time_start;

echo "Script took $time seconds to set up array<br>\n";




$time_start = microtime_float();

$handle = fopen("http://coastwatch.pfeg.noaa.gov/erddap/griddap/erdAIicov1day.csv?ice[(2011-05-22T12:00:00Z)][%20%20%20%20(0.0)][(-85.0):(85.0)][(0.0):(360.0)]&.draw=surface&.vars=longitude%7Clatitude%7Cice&.colorBar=%7C%7C%7C%7C%7C", "rb");
$i = 0;
$matches = 0;
while(!feof($handle) && $i<1000000):
  $line = stream_get_line($handle, 65535, "\n");
  if(!$line):
    echo "ERROR -> BREAK<br>\n";
    break;
  endif;
  if(!strpos($line,"NaN")) {
    if($i < 2)
      continue;

    $info = preg_split("(T|Z,|,|,|,)", $line);
    //var_dump($info);
    //$lng = (int)preg_split(".",$info[3]);
    //$lat = (int)preg_split(".",$info[4]);
    $lat = (int)$info[3];
    $lng = (int)$info[4];
    //echo "($lat,$lng): ".$info[5];
    $ice_coverage[$lat][$lng] += (double)$info[5];
    $coverage_count[$lat][$lng]++;

    //echo "<br>\n";
    $matches++;
  }
  $i++;
endwhile;
fclose($handle);


$time_end = microtime_float();
$time = $time_end - $time_start;

echo "Script took $time seconds to process $i lines and match $matches lines<br>\n";



$time_start = microtime_float();

for($i=-85; $i<=85; $i++):
  for($j=0; $j<=360; $j++):
    if($coverage_count[$i][$j] != 0)
      $ice_coverage[$i][$j] /= 64.0;
  endfor;
endfor;
//var_dump($ice_coverage);

$time_end = microtime_float();
$time = $time_end - $time_start;

echo "$time seconds to process compiled data<br>\n";

for($i=-85; $i<=85; $i++):
  for($j=0; $j<=360; $j++):
    if($coverage_count[$i][$j] != 0)
      echo "(".$coverage_count[$i][$j].")";
    echo $ice_coverage[$i][$j]."  ";
  endfor;
  echo "<br>\n";
endfor;

?>
