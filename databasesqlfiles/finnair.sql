



CREATE TABLE REFERENCENUMBERS
(
	reference_Id varchar(255 )NOT NULL,
	PRIMARY KEY(reference_Id)

);




CREATE TABLE BOOKINGS
(
reference_Id varchar(255) NOT NULL ,
flightNumber int,
FirstName varchar(255),
Surname varchar(255),
dateTime DATETIME,
direction varchar(255),
PRIMARY KEY(reference_Id)
);


CREATE TABLE FLIGHTS
(
flight_Id int,
dateTime DATETIME,
seatsRemaining int DEFAULT 5,
direction varchar(255),
PRIMARY KEY (flight_Id,dateTime,direction)
);

CREATE TABLE HELICOPTERPORTS
(
  flight_Id int, 
  ipAdress varchar(255) DEFAULT "127.0.0.1",
  port int , 
  PRIMARY KEY (flight_Id) 
);