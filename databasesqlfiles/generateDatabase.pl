



#!/usr/local/bin/perl



sub isLeapYear{ 
	$y =$_[0];  
	if ($y < 0) {
	        return 0;
	}

	if ($y % 400 == 0) {
	        return 1;
	} elsif ($y % 100 == 0) {
	        return 0;
	} elsif ($y % 4 == 0) {
	        return 1;
	} else {
	        return 0;
	}
}
open (MYFILE, '>flighttableinfo.sql');
my($day, $month, $year)=(localtime)[3,4,5];

if(!defined($ARGV[0])){
    die "Need to enter how many years\n";
}

if(!defined($ARGV[1])){
    $flights = 1; 
}else{
    $flights = $ARGV[1];
}



$flightNumber = 1; 

	
%daysInMonth = ('1'=>31 ,'2'=>28,'3'=> 31,'4'=>30,'5'=> 31,'6'=>30,'7'=>31,'8'=>31,'9'=>30,'10'=>31,'11'=>30,'12'=>31);



#print "$day-".($month+1)."-".($year+1900)."\n";
	$year=$year+1900;
	$month=$month+1;
	$trigger=0;
	
#for($flightNumber=1;$flightNumber<=$flights;$flightNumber++){


	for($j=$year;$j<$year+$ARGV[0];$j++){
		if(&isLeapYear($j)){
			print MYFILE "INSERT INTO FLIGHTS (flight_Id,dateTime,direction) VALUES ($flightNumber,\"$j\-2\-29 06:00:00\",\"CAMP\");\n";	
			print MYFILE "INSERT INTO FLIGHTS (flight_Id,dateTime,direction) VALUES ($flightNumber,\"$j\-2\-29 15:00:00\",\"TOWN\");\n";	
		}		
	}


	for($i = 0 ; $i < 365*$ARGV[0] ; $i++){
		
		print MYFILE "INSERT INTO FLIGHTS (flight_Id,dateTime,direction) VALUES ($flightNumber,\"$year\-$month\-$day 06:00:00\",\"CAMP\");\n";	
		print MYFILE "INSERT INTO FLIGHTS (flight_Id,dateTime,direction) VALUES ($flightNumber,\"$year\-$month\-$day 15:00:00\",\"TOWN\");\n";
	       	if(($daysInMonth{$month}!=$day)){
					$day++;
			
      		}else{
			 					$day=1;
				

					if($month!=12){
						$month++;
					}else{
						$month=1;
						$year++;
					}			
			}
		
	}
#}
	 
close (MYFILE);

