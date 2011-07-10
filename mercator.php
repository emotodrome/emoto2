<?php

echo "(".longToWorldSpace((double)$_GET['long']).",";
echo latToWorldSpace((double)$_GET['lat']).")";

function longToWorldSpace($lng) {
  return deg2rad($lng);
}

function latToWorldSpace($lat) {
  $latrad = deg2rad($lat);
  return log(tan(pi()/4+$latrad/2));
}
?>

