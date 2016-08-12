#!usr/bin/perl

open(FINNAIR,'>finnairports.sql');



    $port = 57000;
    for($i = 1 ; (($i < $ARGV[0])&&($i<24));$i=$i+2){
	#$port = $i 
	print MYFILE "INSERT INTO HELICOPTERPORTS (flight_Id,ipAdress,port)  VALUES ($i,\"127.0.0.1\",$port);";
	$port = $port + 2; 
    }



