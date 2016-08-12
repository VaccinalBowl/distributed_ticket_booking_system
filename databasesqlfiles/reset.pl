

#!usr/bin/perl
use strict;
use warnings;
use Mysql;

#my $result = system(`mysql -h localhost -u FINNAIR -p`);
#$result = system(`FINNAIR`);

 



 @db=   {"FINNAIR", "KLM" ,"LUFTHANSA"};
foreach $p (){

    $host = "localhost";
    $p = "FINNAIR";

    $connect = Mysql->connect($host,$p,$p,$p);
    $connect->selectdb($p);
    $statement = "source reset", $p.lc() , ".sql";
    $execute = $connect -> $statement;
}
