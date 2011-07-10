<?php
error_reporting(E_ALL);
ini_set("display_errors", 1);

set_time_limit(0);


function microtime_float() {
  list($usec, $sec) = explode(" ", microtime());
  return ((float)$usec + (float)$sec);
}




$offset = mktime(0, 0, 0, date("m")  , date("d")-7, date("Y"));
echo date("m-d-Y",$offset)."\n";
$year = date("Y",$offset);
$month = date("m",$offset);
$day = date("d",$offset);

$time_start = microtime_float();

$url = 'http://coastwatch.pfeg.noaa.gov/erddap/griddap/erdAIicov1day.csv?ice[('.$year.'-'.$month.'-'.$day.'T12:00:00Z)][%20%20%20%20(0.0)][(-85.0):(85.0)][(0.0):(360.0)]&.draw=surface&.vars=longitude%7Clatitude%7Cice&.colorBar=%7C%7C%7C%7C%7C';
echo $url."\n";
$path = 'local.csv';

$ch = curl_init($url);
if(!$ch) {
  echo "Failed to open external .csv\n";
  exit();
}

if(unlink($path))
  echo "truncated $path\n";
else
  echo "failed to truncate $path\n";
$fp = fopen($path, 'w');
//$ch = curl_init($url);
curl_setopt($ch, CURLOPT_FILE, $fp);

$data = curl_exec($ch);

curl_close($ch);
fclose($fp);

$time_end = microtime_float();
$time = $time_end - $time_start;

echo "Script took $time seconds to download to local.csv\n";



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

echo "Script took $time seconds to set up array\n";



$time_start = microtime_float();

$handle = fopen("local.csv", "rb");

$i = 0;
$matches = 0;
if($handle):
  while(!feof($handle)):
    $line = stream_get_line($handle, 65535, "\n");
    if(!$line):
      echo "ERROR -> failed to open local file\n";
      exit();
      break;
    endif;
    if(!strpos($line,"NaN")):
      if($i < 2)
        continue;

      $info = preg_split("(T|Z,|,|,|,)", $line);

      $lat = (int)$info[3];
      $lng = (int)$info[4];

      $ice_coverage[$lat][$lng] += (double)$info[5];
      $coverage_count[$lat][$lng]++;

      $matches++;
    endif;
    $i++;
  endwhile;
  fclose($handle);
else:
  echo "ERROR OPENING LOCAL CSV\n";
endif;

$time_end = microtime_float();
$time = $time_end - $time_start;

echo "Script took $time seconds to process $i lines and match $matches lines\n";

$time_start = microtime_float();

for($i=-85; $i<=85; $i++):
  for($j=0; $j<=360; $j++):
    if($coverage_count[$i][$j] != 0)
      $ice_coverage[$i][$j] /= 64.0;
  endfor;
endfor;

$time_end = microtime_float();
$time = $time_end - $time_start;

echo "$time seconds to process compiled data\n";

for($i=-85; $i<=85; $i++):
  for($j=0; $j<=360; $j++):
    if($coverage_count[$i][$j] != 0)
      echo "(".$coverage_count[$i][$j].")";
    echo $ice_coverage[$i][$j]."  ";
  endfor;
  echo "\n";
endfor;

$link = mysql_connect('localhost', 'darrenli', 'D1abl089.');
if(!$link):
  echo "Failure to connect to database\n";
else:
  //truncate yesterdays table
  //copy from today to yesterday
  //truncate todays table
  //insert into today

  echo "\n\n";
  echo "truncating yesterday's table\n";
  var_dump(mysql_query("TRUNCATE TABLE  darrenli_emoto.ice_coverage_yesterday"));
  echo "\ncopying today to yesterday\n";
  var_dump(mysql_query("INSERT INTO  darrenli_emoto.ice_coverage_yesterday SELECT * FROM  darrenli_emoto.ice_coverage_today"));
  echo "\ntruncating today's table\n";
  var_dump(mysql_query("TRUNCATE TABLE  darrenli_emoto.ice_coverage_today"));

  echo "\ninserting into today's table\n";
  for($i=-85; $i<=85; $i++):
    for($j=0; $j<=360; $j++):
      $result = mysql_query("INSERT INTO darrenli_emoto.ice_coverage_today (latitude, longitude, ice, of64) VALUES('$i', '$j', '".$ice_coverage[$i][$j]."', '".$coverage_count[$i][$j]."')");
    endfor;
  endfor;
endif;

mysql_close($link);
?>
