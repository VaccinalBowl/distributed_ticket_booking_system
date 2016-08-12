import java.util.*;
import java.io.*;
import java.net.*;


public abstract class Helicopter{
	protected int port1;
	protected int port2;
	protected int port3;
	protected Vector<Pair<Packet,Integer>> townPacketsVector;
	private boolean canWait=false;
	protected SharedVector packetsForTown;
	protected Vector<Pair<Packet,Object[]>> packetsForCamp;
	protected Clock clock;
	protected Thread clockThread;
	
	
	
	protected String companyName;
	private boolean connectToTownNext;  
	private Time currentTime; 
	private Date currentDate; 
	
	private Time timeToDepartCurrentLocation; 
	private Date dateToDepartCurrentLocation; 
	private int currentLocation; //-1 for town, 0 for in air, 1 for Camp
	
	private int helicopterId;
	private Time timeToDepartTheTown = new Time("06","00");
	private Time timeToDepartTheCamp = new Time("15","00");
	
	private Thread serverThread;	
	private MultiThreadedServer server;
	private int listensToPort;
	private Vector<Pair<String,Integer>> portsToCheck;
	
	
	
	
	public Helicopter(String compName,int p1,int p2 , int p3, Time dTimeForHeli,int currentL,int mainServerPort){
		this.port1 = p1;
		this.port2 = p2;
		this.port3 = p3;
		this.timeToDepartCurrentLocation = dTimeForHeli; 
		this.currentLocation = currentL;
		this.listensToPort=mainServerPort;
		
		
		
		this.initializeHelicoptersIdentity();
		
		
		this.clock = new Clock(true);
		this.clockThread = new Thread(this.clock);
		this.clockThread.start();
		//this.currentTime = this.clock.getTime();
		//this.currentDate = this.clock.getDate();
		

		//this.townPacketsVector = packs;
		this.packetsForTown = new SharedVector();
		this.packetsForCamp = new Vector<Pair<Packet,Object[]>>();
		this.companyName=compName;
		//System.out.println(packs.size());
		this.server = new MultiThreadedServer(this.listensToPort,100,packetsForTown);
		this.serverThread = new Thread(this.server);
		this.serverThread.start();
		
		
		System.out.println("Helicopter up and running");
		this.beginOperating();
	

	}



	private Time getHelicopterTime(){
		this.currentTime=  new Time(Integer.toString(this.clock.getHours()),Integer.toString(this.clock.getMinutes()));
		return this.currentTime;
	}
	
	private Date getHelicopterDate(){
		this.currentDate =  new Date(Integer.toString(this.clock.getDay()),Integer.toString(this.clock.getMonth()),Integer.toString(this.clock.getYear()));
		return this.currentDate;
	}
	public void printHelicopterDateAndTime(){
		//System.out.println("Time: "+this.clock.getHours()+":"+this.clock.getMinutes()+":"+this.clock.getSeconds());
		//System.out.println("Date: "+this.clock.getDay()+"/"+this.clock.getMonth()+"/"+this.clock.getYear());
		System.out.println(this.getHelicopterTime().toString());
		System.out.println(this.getHelicopterDate().toString());
	
	}
	
	public void printPacketsForTown(){
		
		for(Pair<Packet,Object[]> pair : this.packetsForTown.getData()){
			Packet packet = pair.getFirstElement();
			Object[] address = pair.getSecondElement();
			System.out.println(packet.toString() +":"+ address[0]+address[1]);
		
		
		}
	}

	public void printPacketsForCamp(){
		//for(int i = 0 ; i < this.packetsForTown.getLength();i++)
		
		for(Pair<Packet,Object[]> pair : this.packetsForCamp){
			Packet packet = pair.getFirstElement();
			Object[] address = pair.getSecondElement();
			System.out.println(packet.toString() + ":"+address);
		
		
		}
	}
	
	
	
	private void carryOutTransactionWithCamp(){
		Socket connectionToCampComputer;
		ObjectOutputStream output;
		//ObjectInputStream input;
		//this.server.setOpen(true);
		System.out.println("**********");
		this.printPacketsForCamp();
		System.out.println("**********");
		for(Pair<Packet,Object[]> pair : this.packetsForCamp){
			Packet packet = pair.getFirstElement();
			Object[] destinationComputer = pair.getSecondElement();
			try {
				System.out.println("Im here man!");
			
				connectionToCampComputer = new Socket((String)destinationComputer[0],(Integer)destinationComputer[1]);
				//System.out.println("Connected");
				output = new ObjectOutputStream(connectionToCampComputer.getOutputStream());
				//System.out.println("ConnectedOne");
				//input  = new ObjectInputStream(connectionToCampComputer.getInputStream());
				//System.out.println("ConnectedTwo");
				//System.out.println(output);
				//System.out.println(input);
				
				System.out.println("putting in buffer");
				System.out.println(packet.toString());
				output.writeObject(packet);
				output.flush();
				System.out.println("Waiting for input");
				//System.out.println((String) input.readObject());
				output.close();
				//input.close();
				connectionToCampComputer.close();
			
			
			
			
			
			
			
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			//}
		
		
		
		
		
		
		
		
		}
		this.packetsForCamp.clear();
	}
	
	
	
	/*private void carryOutTransactionWithTown(){
		for(Pair<Packet,Integer> pairFromCamp:this.packetsForTown.getData()){
			Socket connectionToTown;
			ObjectOutputStream outputStream;
			ObjectInputStream inputStream;
			this.packetsForCamp = new Vector<Pair<Packet,Integer>>();
			
				try {
					connectionToTown = new Socket("127.0.0.1",6000);
					outputStream = new ObjectOutputStream(connectionToTown.getOutputStream());
					inputStream = new ObjectInputStream(connectionToTown.getInputStream());
					Packet packet = pairFromCamp.getFirstElement();
					outputStream.writeObject(packet);
					outputStream.flush();
					Packet response =  (Packet)(inputStream.readObject());
					this.packetsForCamp.add(new Pair<Packet,Integer>(response,pairFromCamp.getSecondElement()));
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
		this.packetsForTown.empty();
	}*/
	private Pair<Packet,Boolean>sendPacketToTown(Packet packetToSend){
		Socket connectionToTown;
		ObjectOutputStream outputStream;
		ObjectInputStream inputStream;
		String latestPacketType = "";
		boolean toReturn = true;
		for(Pair<String,Integer> ipStringPair :this.portsToCheck){
			
			   try {
				connectionToTown = new Socket(ipStringPair.getFirstElement(),ipStringPair.getSecondElement());
				outputStream = new ObjectOutputStream(connectionToTown.getOutputStream());
				inputStream = new ObjectInputStream(connectionToTown.getInputStream());
				
				latestPacketType = packetToSend.getPacketType();
				outputStream.writeObject(packetToSend);
				outputStream.flush();
				Packet response =  (Packet)(inputStream.readObject());
				//if(response.getPacketType().equals(arg0)){
				if(packetToSend.getPacketType().equals("BookingPacket")){
					if(response.getPacketType().equals("BookingConfirmationPacket")){
						return new Pair<Packet,Boolean>(response,true);
					}else{
						continue;
					}
				}
				if(packetToSend.equals("CancelationPacket")){
					if(response.getPacketType().equals("CancelationConfirmationPacket")){
						return new Pair<Packet,Boolean>(response,true);
					}else{
						continue;
					}
				}
				
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				toReturn=false;
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				toReturn=false;
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				toReturn = false;
			}
			
		}		
		
		if((latestPacketType.equals("BookingPacket"))&&(toReturn)){
			return new Pair<Packet,Boolean>(new Packet(false),true);
		}else if ((latestPacketType.equals("BookingPacket"))&&(!toReturn)){
			return new Pair<Packet,Boolean>(new Packet(false),false);
		}
		if((latestPacketType.equals("CancelationPacket"))&&(toReturn)){
			return new Pair<Packet,Boolean>(new Packet(packetToSend.getReservationNumber(),7),true);
		}else{
			return new Pair<Packet,Boolean>(new Packet(packetToSend.getReservationNumber(),7),false);
		}
		
		
		//return null;
	}
	
	
	
	private void carryOutTransactionWithTown(){
		this.packetsForCamp = new Vector<Pair<Packet,Object[]>>();
		for(Pair<Packet,Object[]> pairFromCamp:this.packetsForTown.getData()){
			Pair<Packet,Boolean> resolvedPair = this.sendPacketToTown(pairFromCamp.getFirstElement());
			if(resolvedPair.getSecondElement()==true){
				this.packetsForCamp.add(new Pair<Packet,Object[]>(resolvedPair.getFirstElement(),pairFromCamp.getSecondElement()));
		
			}else{
				this.packetsForCamp.add(new Pair<Packet,Object[]> (new Packet(1,resolvedPair.getFirstElement()),pairFromCamp.getSecondElement()));
			}
		}	
				
		
		
		
		
		this.packetsForTown.empty();
	}
	
	
	
/*	private void carryOutTransactionWithTown() {
		Packet availabilityPacket,availabilityResponse,bookingPacket,bookingResponse,bookingConfirmationPacket;
		Socket sockOut;
		ObjectOutputStream oos;
		ObjectInputStream ois;
		boolean isAvailability;
		this.packetsForCamp = new Vector<Pair<Packet,Integer>>();
		//System.out.println(this.packetsForTown.size());
		//this.server.setOpen(false);
		
		try{
		for(Pair<Packet,Integer> pair : this.packetsForTown.getData()){
			Packet p =pair.getFirstElement();
			Integer toBeDeliveredTo = pair.getSecondElement();
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println(toBeDeliveredTo.toString());
			System.out.println(p.toString());
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~");
			sockOut = new Socket("127.0.0.1",port1);
		    if(!p.getPacketType().equals("CancelationPacketWithCompanyName")){
			bookingPacket = new Packet(p.getFirstName(),p.getSurname(),p.getDateToFly(),null,p.getDestination());
			oos = new ObjectOutputStream(sockOut.getOutputStream());
			ois = new ObjectInputStream(sockOut.getInputStream());	
			oos.writeObject(bookingPacket);
			oos.flush();
			bookingResponse=(Packet) ois.readObject(); 
			
			String packetType = bookingResponse.getPacketType();
			ois.close();
			oos.close();
			sockOut.close();
			System.out.println("You should see this");
			if(packetType.equals("BookingConfirmationPacket")){
				this.packetsForCamp.add(new Pair<Packet,Integer>(bookingResponse,toBeDeliveredTo));
				System.out.println("You should see this if the booking was right");
				
			}else{
				sockOut = new Socket("127.0.0.1",port2);
				   bookingPacket = new Packet(p.getFirstName(),p.getSurname(),p.getDateToFly(),null,p.getDestination());
					oos = new ObjectOutputStream(sockOut.getOutputStream());
					ois = new ObjectInputStream(sockOut.getInputStream());	
					oos.writeObject(bookingPacket);
					oos.flush();
					bookingResponse=(Packet) ois.readObject(); 
					
					packetType = bookingResponse.getPacketType();
					ois.close();
					oos.close();
					sockOut.close();
				
					if(packetType.equals("BookingConfirmationPacket")){
						this.packetsForCamp.add(new Pair<Packet,Integer>(bookingResponse,toBeDeliveredTo));
						
						
					}else{
				
				
				
						sockOut = new Socket("127.0.0.1",port3);
						   bookingPacket = new Packet(p.getFirstName(),p.getSurname(),p.getDateToFly(),null,p.getDestination());
							oos = new ObjectOutputStream(sockOut.getOutputStream());
							ois = new ObjectInputStream(sockOut.getInputStream());	
							oos.writeObject(bookingPacket);
							oos.flush();
							bookingResponse=(Packet) ois.readObject(); 
							
							packetType = bookingResponse.getPacketType();
							ois.close();
							oos.close();
							sockOut.close();
						
							if(packetType.equals("BookingConfirmationPacket")){
								this.packetsForCamp.add(new Pair<Packet,Integer>(bookingResponse,toBeDeliveredTo));
								
								
							}else{
				
				
								this.packetsForCamp.add(new Pair<Packet,Integer>(new Packet(false),toBeDeliveredTo));
							}
					}
					
			    }
			}else{
				System.out.println(p.getReservationNumber());
				Packet cancelPacket = new Packet(p.getReservationNumber());
				String companyBookedWith = p.getCompanyName();
				System.out.println(p.getCompanyName());
				int portOfCancelCompany=0;
				System.out.println("Waves this is looking good!!!");
				if(companyBookedWith.equals("FINNAIR")){
					portOfCancelCompany=HelicopterCompany.FINNAIR_PORT;
				}else if(companyBookedWith.equals("KLM")){
					portOfCancelCompany=HelicopterCompany.KLM_PORT;
				}else {
					portOfCancelCompany=HelicopterCompany.LUFTHANSA_PORT;
				}
				String referenceNumber = p.getReservationNumber();
				Socket cancelationSocket = new Socket("127.0.0.1",6000);
				ObjectOutputStream outputS = new ObjectOutputStream(cancelationSocket.getOutputStream());
				//ObjectInputStream inputS = new ObjectInputStream(cancelationSocket.getInputStream());
				Packet packet = new Packet(referenceNumber);
				outputS.writeObject(packet);
				System.out.println("Made it ");
				//Packet reply = (Packet) inputS.readObject();
				//if(cancelationResponse.equals("CancelationConfirmationPacket")){
					//this.packetsForCamp.add(new Pair<Packet,Integer>(cancelationResponse,toBeDeliveredTo));
					
					
				//}else{
	
	
					//this.packetsForCamp.add(new Pair<Packet,Integer>(cancelationResponse,toBeDeliveredTo));
			
				//}
			
			}
		    
		
		
		
		}
		}catch(Exception e){
						e.printStackTrace();
					}
		
			
			this.packetsForTown.empty();
			
			
			
			
					
					//this.printPacketsForCamp();
}*/
		
	
	
	@SuppressWarnings("unused")
	private String findCompanyBookedWith(int portConnectedTo){
		//System.out.println(this.companyName);
		if(this.companyName.equals("FINNAIR")){
			switch(portConnectedTo){
			case 1:
				
				return "FINNAIR";
			
			case 2:
				return "KLM";
				
			case 3:
				return "LUFTHANSA";
				
			}
		}
			
		if(this.companyName.equals("KLM")){
			switch(portConnectedTo){
			case 1:
				
				return "KLM";
			
			case 2:
				return "LUFTHANSA";
				
			case 3:
				return "FINNAIR";
				
			}
		}
		
		if(this.companyName.equals("LUFTHANSA")){
			switch(portConnectedTo){
			case 1:
				
				return "LUFTHANSA";
			
			case 2:
				return "FINNAIR";
				
			case 3:
				return "KLM";
				
			}
		}
	
	
		return null;//never called in a context where this can happen!
	
	
	}


	
	private void beginOperating(){
		boolean timeToLeaveCurrentLocation; 
		this.timeToDepartCurrentLocation = this.timeToDepartTheTown;
			
		
		
		if(this.getHelicopterTime().isBefore(this.timeToDepartCurrentLocation)){
			this.dateToDepartCurrentLocation = this.getHelicopterDate();
		}else{
			this.dateToDepartCurrentLocation = this.getHelicopterDate().getNextDay();
		}
	
		
		
		
		
		while(true){
			timeToLeaveCurrentLocation = isTimeToLeave();
			if(timeToLeaveCurrentLocation){
				this.depart();
				
			}else{
				this.stay();
				
			}
			
			
			//System.out.println(timeToLeaveCurrentLocation);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	
	
	
	
	
	
	
	}
	
	private boolean isTimeToLeave(){
		String goingTo = "";
		if(this.currentLocation==-1){
			goingTo="Camp";
		}else{
			goingTo="Town";
		}
		
		
		this.currentTime = this.getHelicopterTime();
		this.currentDate = this.getHelicopterDate();
		System.out.println("****************************");
		System.out.println("Current Time:");
		System.out.println(this.currentTime.toString());
		System.out.println(this.currentDate.toString());
		System.out.println("Departure to "+goingTo  );
		System.out.println(this.timeToDepartCurrentLocation.toString());
		System.out.println(this.dateToDepartCurrentLocation.toString());
		//this.printPacketsForTown();
		//this.printPacketsForCamp();
		//this.printPacketsForTown();
		//this.printPacketsForCamp();
		System.out.println("*****************************");
		//if(this.currentLocation==0)
			//return false;
		if((this.dateToDepartCurrentLocation.isEqual(this.currentDate))&&(this.currentTime.isEqual(this.timeToDepartCurrentLocation))){
			return true;
		}else{
			return false; 
		}
	
		
		
	
	
	
	}
	
	
	private void depart(){
		System.out.println("Leave Now Dude");
		
		if(this.currentLocation == -1){
			System.out.println("Going to camp");
			this.connectToTownNext=false;//meaning connect to encampment
			
		}else{
			System.out.println("Going to town");
			this.connectToTownNext=true;
		
		
		}
		//this.currentLocation=0; 
		System.out.println("Helicopter is flying");
		//int sleepTime = 10000;//this.calculateFlightTime();//
		//System.out.println("flew to camp");
		
		
		if(this.connectToTownNext){
			System.out.println("Transacting with town");
			this.timeToDepartCurrentLocation = this.timeToDepartTheTown; 
			this.dateToDepartCurrentLocation = this.getHelicopterDate().getNextDay();
			this.carryOutTransactionWithTown();
		    this.currentLocation=-1;	
		}else{
			System.out.println("Transacting with camp");
			this.timeToDepartCurrentLocation = this.timeToDepartTheCamp;  
			this.carryOutTransactionWithCamp();
			this.currentLocation = 1; 
		}
	}
	private void stay(){
		System.out.println("Waiting to depart.... patience is key");
	}
	

	private void initializeHelicoptersIdentity() {
		try {
			System.out.println(this.listensToPort);
			ServerSocket initialisationServerSocket= new ServerSocket(this.listensToPort+1,1);
			Socket initialisationSocket = initialisationServerSocket.accept();
			ObjectInputStream inputFromCompany = new ObjectInputStream(initialisationSocket.getInputStream());
			Packet timeTableInformation = (Packet) inputFromCompany.readObject();
			this.helicopterId = timeTableInformation.getHelicopterFlightNumber();
			this.timeToDepartTheCamp = timeTableInformation.getCampDepartureTime();
			this.timeToDepartTheTown = timeTableInformation.getTownDepartureTime(); 
			System.out.println(this.helicopterId);
			System.out.println(this.timeToDepartTheCamp);
			System.out.println(this.timeToDepartTheTown);
			
			this.portsToCheck = new Vector<Pair<String,Integer>>(); 
			this.portsToCheck = timeTableInformation.getCompanies();
			
			inputFromCompany.close();
			initialisationSocket.close();
		
		
		} catch (IOException e) {
			System.err.println("Could not initialise helicopter = Fatal");
			System.exit(1);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	

}
