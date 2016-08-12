import java.net.*;
import java.sql.SQLException;
import java.io.*;
public class HelicopterCompanyIncomingConnectionThread implements Runnable {
	private Socket connection;
	private ObjectInputStream input;
	private ObjectOutputStream output; 
	private Database dataCopy;
	private int i=0;
	public HelicopterCompanyIncomingConnectionThread(Socket socket,Database db){
		this.connection = socket;
		try {
			this.input = new ObjectInputStream(this.connection.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.output = new ObjectOutputStream(this.connection.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.dataCopy = db;
	
	}
	
	
	public void run(){
		try {
			Packet incomingPacket = (Packet) this.input.readObject();
			Packet outgoingPacket =	this.processPacket(incomingPacket);
			this.output.writeObject(outgoingPacket);
			this.output.flush();
			this.output.close();
			this.input.close();
			this.connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Packet processPacket(Packet input){
		Packet iPacket = input;
		
		//if( i ==1){
			//while(true){
			//System.out.println(iPacket.getPacketType());}}
		//i++;
		
	   	//System.out.println(iPacket.getPacketType());
	   	if(iPacket.getPacketType().equals("AvailabilityQueryPacket")){
	   		try{
	   		if(this.dataCopy.hasAvailability(1,iPacket.getDateToFly(), iPacket.getTimeToFly(), iPacket.getDestination())){
	   			return new Packet(true);
	   		}else{
	   			return new Packet(false);
	   		}
	   		}catch(SQLException sqlException){
	   			sqlException.printStackTrace();
	   		}
	   	
	   	
	   	}else if(iPacket.getPacketType().equals("BookingPacket")){
	   		
	   		try{
	   		//Thread.sleep(10000);
	   		}catch(Exception ex){
	   			ex.printStackTrace();
	   		}
	   		/*boolean found=false;
	   		Pair<Integer,Time> pair=null;
	   		Vector<Pair<Integer,Time>> vector = this.getFlightsMatchingRequest(iPacket.getDateToFly(), iPacket.getDestination());
	   		for(Pair<Integer,Time> tmp:vector){
	   			if(this.database.hasAvailability(tmp.getFirstElement(),iPacket.getDateToFly(),tmp.getSecondElement(),iPacket.getDestination())){
	   				found=true;
	   				pair=tmp;
	   				System.out.println("We have space");
	   				System.out.println(pair.getFirstElement().toString());
	   				System.out.println(pair.getSecondElement().toString());
	   				break;
	   			}
	   				
	   			
	   		}*/
	   		Pair<String, Time> sPt;
			try {
				sPt = this.dataCopy.makeReservation(iPacket.getFirstName(), iPacket.getSurname(), iPacket.getDateToFly(), iPacket.getDestination());
		  		if(!sPt.getFirstElement().equals("FlightsFull")){
		   			return new Packet(iPacket.getFirstName(),iPacket.getSurname(),iPacket.getDateToFly(),sPt.getSecondElement(),iPacket.getDestination(),sPt.getFirstElement());
		   		}else{
		   			return new Packet(false);
		   		}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   		
	 
	   		
	   		/*if(found){
	   		String reservationNumber = this.database.makeReservation(pair.getFirstElement(),iPacket.getFirstName(), iPacket.getSurname(), iPacket.getDateToFly(), pair.getSecondElement(), iPacket.getDestination());
	   		return new Packet(iPacket.getFirstName(),iPacket.getSurname(),iPacket.getDateToFly(),pair.getSecondElement(),iPacket.getDestination(),reservationNumber);
	   		}else{
	   			return new Packet(false);
	   		}*/
	   	}else if(iPacket.getPacketType().equals("CancelationPacket")){
	   		System.out.println("In the correct process");
	   	
	   		
	   		boolean success=false;
	   		try {
				success = this.dataCopy.cancelReservation(iPacket.getReservationNumber());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FlightEmptyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(success){
				return new Packet(iPacket.getReservationNumber(),8);
			}else{
				return new Packet(iPacket.getReservationNumber(),7);
			}
		}
	   	
	   	
	   	
		
	   	return null;
	}
	
	
}
