<?php
$link = mysql_connect('localhost', 'darrenli', 'D1abl089.');
$result = mysql_query("SELECT * FROM darrenli_emoto.ice_coverage");
//while($row = mysql_fetch_assoc($result))
  //var_dump($row);
var_dump(mysql_num_rows($result));
mysql_close($link);
?>
