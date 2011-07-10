<?php
$file = file_get_contents('http://scp.byu.edu/data/iceberg/ascat/a027.ascat');

$info = array();

foreach(preg_split("/(\r?\n)/", $file) as $line):
  if($line == "")
    break;

  $info = preg_split("(lat:  | lon:  | day: | file: .+? backscat: )", $line);

/*
  array_push($lat,$info[1]);
  array_push($lng,$info[2]);
  array_push($dayArray,$info[3]);
  array_push($bscat,$info[4]);
*/

  var_dump($info);

  $day = (int)substr($info[3],0,3);
  $year = (int)substr($info[3],4,4);
  var_dump($day, $year);
endforeach;

function getTimestamp($days, $year) {

  
}

function getIcebergInfo() {
  
}

?>
