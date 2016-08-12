Distributed Ticket Reservation System
=====================================

This is the practical excercise which was part of a university course in distributed systems. 
The aim of this project was to implement a distributed ticket booking system using Java Sockets which demonstrated understanding
of the core concept of distributed systems:  
  1. Fault Tolerance
  2. Scalability
  3. Concurrency 
  4. Heterogeneity
  5. Transparency
  6. Openness
  7. Security

The Problem
-----------
The hypothetical scenario was that in a remote region of Sweden there is a village with no internet connection. It is connected to a town
by helicopter. Travel between the village and town is only possible via helicopter. The only networking possible between the town and village
was possible during which 

1.Introduction
--------------
This report describes a distributed ticket reservation system for a helicopter company
based in Scandinavia. As far as the author is concerned it works as it should and
meets the minimum requirements of the assignments but stands to be corrected. The
project is working although it is not the most user friendly piece of software. Included
are instructions to test the program but if this is not clear for any reason I will be able to
answer any questions during the demonstration. I’ve used two technologies. The project
is written in Java(of course) and utilises the MySQL relational database system. As
the design of the project requires a heavily populated database at the beginning of the
project it is necessary to initialise his. To automate this process and save lots of time
PERL scripts were developed to aid with this task. It should be noted that these files are
just used for initialisation and are not technically speaking a part of the project although
to set up the system the first time they will be used. This report contains 7 sections. The
next section details the assumptions made by the design and a high level specification
of the software.

2. Designer Interpretation of Problem and Specification
-------------------------------------------------------
As the specification of the project was very vague it is essential to explain the
assumptions and limitations of the design. In this section the scenario of the solution
presented is explained.

###2.1 Helicopter Companies
In this design the Helicopter Companies are located in the town. In the town it is
assumed that there is quality hardware. Essentially as much data, computers , servers
and space are available as is needed. Within the town the helicopter companies
are based. This is where the helicopter companies store the data. Every helicopter
company has access to it’s own database and it’s own database only. Information is
shared by a question answering protocol. So if a helicopter company wants information
from another helicopter company , it asks the other helicopter and gets one of several
specialised responses. Every helicopter company is able to talk to every other
helicopter company. The helicopter company can be thought of as a building in the
town which operates as a business. Customers of the helicopter company can walk into
the helicopter company and book a flight directly. Think if this as similar to walking into
Dublin airport and booking a flight at the desk. This is the closest a customer can get to
the database and all bookings and cancellations can be confirmed instantly. It is also
assumed that the Helicopter Company has a clock that keeps track of time. This would
be necessary as any company that uses takes reservations usually needs to know what
date it is so that it doesn’t allow people to book for times that have already passed by.

###2.2 Town Clients
As well as this the system also supports town side remote access. What this means
is that if somebody does not want to go to the company directly they can download a
certain version of the program. This program is a client that connects to the servers
in the town and queries them. This feature is added as it represents the situation
most realistically. These GUI’s are hard coded with the IP addresses and ports of the
Helicopter Companies. If any new helicopter companies are headed these GUIs need to
be update and restarted. Apart from going direct to the company these nodes would be
the best way to interact with the helicopter companies. If the network is up and running
successfully and the town clients can access the helicopter companies these nodes
receive booking and cancellation confirmations almost always instantly but this is reliant
on network communication being available. The service provided by these nodes can
be thought of as similar to logging onto a website of a large transportation company
such as Deutsche Bahn or Ryanair although obviously with a lot less features.

###2.3 Helicopters
The helicopters are connection between the remote encampment and the the servers
in the town. All the helicopters in the current system have five seats although this is
easily changed by altering the database configuration files. I have assumed that the
journey from the town for each Helicopter is a twenty four hour round trip journey and
thus each helicopter only makes one trip to the encampment during a 24 hour period.
If a company wants to run more journeys to the camp every day it must purchase more
helicopters and these can be added into our system simply again by adding them
to the database. For simplicity’s sake and initialisation of the system it is assumed
that every helicopter begins it’s life in the town where there is enough space to land
as many helicopters as the helicopter company will ever need. This seems to be a
reasonable assumption given that the town is probably the first town for hundreds of
kilometres and has a lot of room for urban expansion. There is no information stored
on the Helicopters or databases. The helicopters are designed to take reservations or
cancellations from the camp, fly back to town, send the information to the helicopter
company based in the town and carry the response back to the customer in the camp.
The Helicopter does not do any booking or cancellation directly. It is essentially a
middle man. As the helicopters are flying certain times of the day they also have a timer
on board that is the same type of time as the helicopter uses1. This timer is how they
decide when to leave the camp and town. It is assumed that the helicopters can keep
time very well. This is a valid assumption as a lot of modern digital watches are able to
keep time with very high degrees of accuracy or even zero time loss.

###2.4 Encampment Nodes
The encampment node is the only way that a person in the camp can communicate
with the system. It is very much the same thing as the town node but with one slight
alteration in that it listens for incoming connections from the Helicopters. This node
is not fixed in the system. This means that the Helicopter does not know about it’s
existence until the helicopter has been contacted by the encampment node. There can
be as many instances of this Encampment Node as is needed. In theory a user of the
network has this installed on their laptop before they leave to go to the encampment. it i
s assumed that these nodes can be given a unique Ip address. If there is no helicopter
near the encampment this node just loops until a Helicopter becomes available.
When a user enters a booking or a cancellation into the node it loads it up onto
whatever helicopter it can. The node then does nothing until the Helicopter returns
with a message for the camp. This is info on whether the booking or cancellation was
successful or no and it advises the user. It is assumed that this node is never inactive
and runs in a low power state while waiting. If the node is stopped the user will never
find out whether or not they successfully cancelled the booking. Ultimately the biggest
loser in this case is the user of the Encampment GUI as the helicopter companies will
only be inconvenienced by a user that booked a flight not turning up. The user doesn’t
even know if they were successful in this case.
Below is a picture of the situation:
1It is important to note that Helicopters and the Companies that they belong to never really
synchronise time once the system is running. It has been proved that it is not possible to design
a protocol that allows this to occur.
The pictures shows most of what was described above. The lines linking the nodes
indicate that there is a connection between the two nodes. However it is important to
note that the helicopters can only connect to the camp nodes when they are at the
camp and it can only connect to the town nodes when it is at the town. It cannot connect
to both at the same time! This is not obvious from the diagram.

3 System Architecture
---------------------
As my system consists of 20+ classes I am only going to give an outline of the main
functionality of each of these classes.
###3.1 The Helicopter Company class
The Helicopter Company class is the biggest class in the system coming in at around
1200 lines. The idea of it is to represent the helicopter company classes. The key
attribute of this class is the database. The Helicopter Company class contains a
database object and this this object can be thought of as the interface between the
MySQL database and the helicopter company. The Helicopter Company also contains
a GUI. From this GUI flights can be booked and cancelled. As well as controlling the
access to the database the Helicopter Company acts as a Multithreaded Server and
when required doubles as a client of the other helicopter companies(Load Sharing).
Helicopter Companies receive packets from remote nodes located in the town and
depending on the packets received give an appropriate response. So if a booking
request comes from the town the company will try and respond with a booking
confirmation or a cancellation confirmation. This is exactly the same what happens if a
helicopter connects to the helicopter company. The packet is sent from the helicopter
to the company and the helicopter then carries the packet it receives back to the camp.
The diagram below shows the basic structure.
The functionality of the Helicopter class is that it contains the database and has
networking capability to receive incoming connections from other companies,
helicopters and nodes in the town. It only ever acts as a client to contact the other
companies.
###3.2 The Database class
The database class is a class that acts as a buffer between the helicopter company and
the MySQL. The most important aspect of this is class is that it has synchronisation.
As the Helicopter Company is multithreaded numerous different thread try and make
and cancel reservations. As a result the make and cancel reservations in the database
class are synchronised. Deadlock prevention is insured by not allowing either of the
synchronised methods call each other. If a locked method does not call another locked
method deadlock cannot occur.
###3.3 Helicopters
The above diagram outlines the main behavior of the helicopter. The packet vector
is population by either the companies or the camp and then sent to the other side
and offloaded. The multithreaded server runs when the helicopter is in the camp and
receives the connections from the nodes there. On return from the camp the helicopter
sends the packets back to the clients if there are any to be delivered to the camp. At
the town the helicopter attempts to make the bookings by sending the packets received
from the camp to the helicopter companies. It receives the reply and then flys back to
the camp.
The timetable is overkill. It was implemented to ensure that the helicopter follows a
rigorous schedule and leaves the camp and and town at the correct time.
An important point to note here is that access to the packet vector is synchronised as
the each thread of the Multithreaded server adds packets to the vector it is essential
that they do not overwrite each other. As is happens addition to a java vector method
is synchronised automatically so that function is wrapped in another function that is
unsynchronised. This ensures that data is not corrupt. Again as the method has no
nested synchronised method calls deadlock cannot occur.
###3.4 EncampmentGUI
The encampment GUI contains a small multithreaded server. This is to wait for the
helicopter responses when they come back from the town. The client part of the
company is designed to latch onto a helicopter when it appears at the camp. An
important point to note here is that the helicopter company knows nothing about
the Encampment GUI unless the Encampment GUI contacts it with a request. The
Encampment sends its IP and Port to the chopper so that the helicopter knows where to
deliver the response from the town. Below is a screen shot of the GUI
4 Algorithms
------------
In this section the main algorithms of the project are outlined. Each of the important
ones is presented presented in high level pseudo code and explanations are given.
###4.1 Booking Flights
There are three main ways to book a flight in the system. The first one is from the
helicopter company itself. It is the most simplest of all. The user enters the request in
the Helicopter Company's GUI and the following steps occur.
####4.1.1 Booking with Helicopter Company Directly

parse input and ensure that user input is valid
if(valid) then
attemptBookingWithFlight
if(flightBookingSuccess) then
return REFERENCENUMBER
else
contactOtherCompanies to make request
if(successful)then
return referenceFromOtherCompany
else
return noAvailability
else
try again

This algorithm is very self explanatory. It also iinvolves load sharing. If a company is
full on a certain day this algorithm will request availability with other companies. If a
communication failure occurs here the system will continue just ignoring the failure. The
exception is caught and execution continues.

####4.1.2 Booking with Helicopter Company From the Camp
parse input and ensure user input is valid
if(valid) then
searchForHelicopter until helicopter found
send packet to helicopter
wait for confirmation from town
when confirmation received
print success or try again
else
try again
This algorithm is extremely unfair on the users in the camp however it is scalable to a
point. The find helicopter algorithm should be fine as long the the number of helicopters
does not become ridiculously large or unrealistic. However if that was the case the
algorithm could be simply altered to use heuristics when searching for helicopters ie
searching for certain helicopters at certain times. If one was optimistic then the situation
with a ridiculously large amount of helicopters is reasonable as the more helicopters
that there are the higher the probability becomes that there will be a helicopter in the
camp at all times that will eventually be found.
####4.1.3 Booking with Helicopter Company remotely in town
parse input and ensure user input is valid
if(valid) then
connectToAHelicopterCompany
send packet
check response
if unsuccessful
try next helicopter company
else
finish
else
try again
The algorithm here leaves it up to the user to find a flight. Loadsharing is simply done by
giving the user the choice between companies.
The three algorithms presented in the above section implement load sharing. The first
algorithm and last algorithm do it by checking with all companies for availibilty. The
booking from the camp does not load share directly. It leaves the procedure up to the
Helicopter that delivers the packet to the town.
It should also be noticed that there is no complexity involved in these algorithms minus
the searching for a company. Each company at the moment searches with all other
companies. This is not the best solution for scalability but it is scalable. The more
companies there are the more checking the searching company has to do.
A better solution would be to send a packet to a neighbouring company and wait for
this company to get back to you. If he doesn't have availabilty then it will pass it to its
neighbour. This could be considered a round robin.

###4.2 Canceling Flights

The cancelling flight algorithms are not discussed as they are identical to the above
booking algorithms except that the packet is not a booking packet but a cancellation
packet.
###4.3 Helicopter Flight Algorithm
In this section the main algorithm of a flying helicopter is given.
Begin life at camp
wait for time of departure
loop to infinite
if in town
go to camp
turn on server for incoming connections
deliver towns response packets if any
else
go to town
turn of server
deliver packets from camp
if response is companyatisfactory
deliver next packet
else
deliver to next company until satisfactory
response received
end
end

This algorithm is linearly scalable. It suffers the same drawbacks as the helicopter
companies when load sharing. In fact the load sharing is implemented here as the
helicopter will search through all companies if trying to book a flight until it fails. If there
is a communication error this is not regarded as a true failure and the user will be
notified that there may in fact be still availability on that day but the helicopter was not
able to connect to all nodes. Ultimately the helicopter will not try again but will wait for
the user to manually send another request to try again.

###4.4 Return journeys 
Return journes are not implemented in a typical fashion. It was decided that it
would be sufficient to simply give the user in the camp the option of booking flights to
and from both locations from all bookings nodes. This means that the user can book
both flights at once but must simple book the system will see the two flights as two
separate bookings.
###4.5
In the code I have added ability to check whether if a booking or cancellation fails
whether it is a true failure. It is simply considered a true failure if there was no error
connection and a half failure if a network error occurred. This means that a booking or
cancellation might still be possible it’s just that all companies cannot be contacted.

3. Scalability
--------------
The project is scalable in all senses of the word. Helicopter Nodes can be added
easily by inputting their information into the database. Performance of the system will
decrease with many helicopters unless more helicopter companies are added but
the factor of performance decrease is not that much unless the amount of helicopters
becomes extremely large. Adding Helicopter Nodes, Encampment Nodes, and Remote
Town nodes is trivial. You just need to update the encampment nodes with the address
of the helicopter nodes in the system and the town remote nodes with the information
containing the location of all the companies.
Adding Helicopters as already stated requires their time table be added to the database
and the extra instance of a helicopter node must be started.
With regard to helicopter companies the project is also scalable but this is not as
simple as adding other nodes. A database needs to be set up for that node and and
instance of the node. As well as that some of the code in the helicopter company class
requires additional cases. In other words you need to add else statements to some of
the methods. This is not an ideal solution to scalability but is deemed sufficient and
technically it is scalable as there is nothing in the design to stop an infinite number of
helicopter company nodes being added to the system.
4.Test Plan and Testing Results
-------------------------------
As this system was being developed individual parts of it were tested individually and
then parts were combined and tested together.
As each class was being written I wrote a main function in that class which tested its
methods. For example in the database class as it was being written after completion
of each function, the main method of the class was changed in order test the latest
changes to the database. Determining whether or not they were working was a
simple matter of looking at the database and deciding if the results were correct. The
two principle methods of the database class were book and cancel. They were also
synchronised. To test them I tried to get two companies to book at the same time to see
was there a clash but there wasn’t. I did not need to test whether these methods were
deadlock proof or not. The locked code does not request any other locks and therefore
cannot cause a deadlock. The database rolls. This means that as time elapses days
that have already gone by are deleted and new ones added. This method might be
something that should be synchronised but it was figured that only dates that were
already gone by were removed and nobody would be booking those days anyway. If the
day has already passed and the information is lost it is hardly problematic.
After the database was working I built the Helicopter Company with a database and got
the GUI going. To test this it was just a matter of booking and cancelling from the GUI.
To test the load sharing I ensured that the user got a seat on another company if it was
free. This was simply a task of booking all available seats on one day and checking
did bookings carry over to different companies. Cancellations is similar. I made sure
all entries were removed from the database. To test communication failure between
companies ran some of the companies but not all to ensure that the system didn’t halt if
all nodes were not available.
Testing Helicopters was done differently. It was simply set up once completed to fly its
route and print out the status of all of it’s variables at each point in the system.
Testing the camp nodes involved simply trying to get packets and responses from the
town to the server and checking that the database was as it should have been. This
involved lots of simulation.
Testing the town remote nodes was the same as town nodes and camp nodes are very
similar.
The system is designed to work in real time but in the end I sped up the real time
version of the program to simulate days elapsing in two minutes. This allowed me to
run the system in a sped up version and input all sorts of different combinations of
input to see the results. Mostly this was successful. At the time of writing there is a
small problem in receiving cancellation confirmations on the remote nodes but the
cancellations themselves are actually working.
Overall the result of my testing was positive and I am confident that the system although
easily breakable if treated correctly will work.
5. Bugs
-------
There are some minor bugs in the project that are yet to be discovered however they do
not cause the system to crash or any major error.
The first error is when cancelling bookings remotely. The cancellation itself goes
through correctly but for some reason the result is said to fail. This is a slight error in my
algorithm that I have not had time to fix.
The code is written to read the helicopter information from the database and it knows
what helicopters there are to initialise. When there is more than one helicopter an error
occurs. The system doesn’t crash but the other helicopters don’t that. This error is not
really an issue as I can simply hard code the values if I want to demo the project with
more helicopters.
6.Diary
-------
I’ve worked on this project for the total seven weeks. Here is an outline of what I did on
a week by week basis.
###Week 1:
During the first week of the assignment I simply read up on how to write concurrent
programs and how basic sockets work as I had never done this before. I looked at a
knock knock server , done the multithreading tutorial on the oracle website and camp up
with a basic design for the system. Total hours spent 15.
###Week 2:
In the second week i adapted an example Server I found in the Deitel book. I made two
servers one that was based in the encampment and one in the town and I made a client
connect and disconnect between the two nodes saying hello and goodbye. Total hours
spent 15.
###Week 3:
In the third week I began to implement a database. First I made a flatfile database and
wrote a parser for it. Then it was scrapped. I wasted about 4 hours here. Eventually I
switched to MySQL and then began writing the database class that interacts with this
directly. This involved about ten hours of work because I had to keep redesigning the
database. Total hours spent:10
###Week 4:
In the fourth week I spent my time building the helicopter company classes and writing
perl scripts to initialise the database. I built a GUI just to visualise how the user was
going to interact with the system and wrote code to process the input from the GUI
fields. Total hours spent on this were 15.
###Week 5:
During this week I added the Server and Client aspects to the Helicopter Companies. I
also developed inter communication between the helicopter companies. This involved
the development of the packet class. Total hours spent 20 hours
###Week 6:
During week six I spent my time developing the Helicopter. This took about 10 hours.
###Week 7:
In week 7 I carried out testing and finished coding. There were several bugs. This week
was roughly 30 hours.
7.Summary Of Achievements
-------------------------
I have made a lot of progress while doing this project. Having never had any experience
with networked applications or concurrent systems before I learned how to design and
implement one in Java. I also learned how to design a database and use MySQL to
interact with Java. I had taken courses in the past on database design but never used
one that was accessed and queried in an automated fashion. Thus I learned how one
would use MySQL to interact with Java. I learned how one would go about generating
database information using the PERL scripting language.
The project also taught me lots about software engineering and quite a lot about good
programming practiced. Halfway through the project I discovered that I needed to
redesign. I think that a better system design from the outset would have saved me lots
of troubled. If I were to undertake such a project again I would definitely spend longer in
the design stage. During the project after I had my design made out I found that I was
copying and pasting a lot of code and only slightly modifying it. I think the project would
have been much easier for me had I written the code more generically. Essentially I
wrote several servers within functions of different classes. I now know that a better idea
would have been to make a Server class and inherit anything that needs to behave as a
server as one.
It was always said to me to write commented code and give variables meaningful
names. I have never worked on a project of this scale before and never thought this was
essential however if I was to start again I would ensure that variable have meaningful
names. Had I done this from the start I would have saved myself an awful lot of time.
There was one day I spent around 8 hours trying to find a book. In the end I just had to
restart writing the code. With more readable and better written code I would not have
done this.
At the end of the project I discovered that I spent a lot of my time adding features that
were not essential. I decided to add a GUI in at the start of the project just to get the ball
rolling but if I were to do it again I would just leave this out and work with the console
alone. I also would not bother getting the Helicopters and Helicopters using a timer. It
was more work than it was worth. If I was given a project of such vague specification
again I would keep it much simpler. I would start my making the networking side of the
application and get nodes communicating rather than worrying about the input. Had I
done this I would have been done much quicker.
Instructions for execution
--------------------------
The Database submitted is able to handle bookings two years from the date that the
computer is launch. Although the GUI allows more input dont book flights after 2012.
They are not available in the database that is submitted.
To run the program have mysql installed and perl installed. Have all the perl and mysql
files in one folder. Type in the commands
~~~~
perl generatefinnair.pl 2
perl generateklm.pl 2
perl generatelufthansa.pl 2
~~~~
Then you need to set up mysql databases and user accounts for each company. To do
so log into mysql as root
CREATE USER ‘FINNAIR’@’localhost’
CREATE USER ‘KLM’@’localhost’
CREATE USER ‘LUFTHANSA’@’localhost’
Then grant all privelages to all these users on all tables.
then type USE FINNAIR;
Then type source resetall.sql(this must be done everytime you want to start the code).
Then start the FINNAIRHELICOPTER
Then start the KLMHELICOPTER
Then start the LUFTHANSA
Then start the corresponding companies FinnairNode, KlmNode, LufthansaNode.
To start an encampment node run EncampmentGUI and a town node is a TownGUI.
WARNING:- the cancel button on the encampment GUI does nothing. To cancel you
need to click on the cancel tab. Write in the reference number and switch the company
name to any helicopter company name. It does not matter which one. Then go to the
booking tab and click book. This is simply a hack to make cancellations work as I had
problems with the cancellation button. To aid you all the code submitted has localhost
ip addresses hardcoded. If you want to see the database you should simply go to the
MySql on the command line. All output on the encampment nodes and town remote
nodes is to the console.
