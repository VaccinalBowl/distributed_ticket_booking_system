import java.io.*;
import java.net.*;

public class CampNodeServer implements Runnable{

	//private SharedVector dataReceivedFromHelicopter;
	private int listenOn;
	private int numberOfConnectionsAllowed;
	private ServerSocket serverSocket; 
	private Socket connection;
	//private ObjectInputStream input;
	//private ObjectOutputStream output;
	public CampNodeServer(int listenOn, int numberOfConnectionsAllowed) {
		this.listenOn=listenOn;
		this.numberOfConnectionsAllowed=numberOfConnectionsAllowed;
		try {
			this.serverSocket = new ServerSocket(this.listenOn,this.numberOfConnectionsAllowed);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}


	public void run() {
		System.out.println("Camp Server Listening on port: "+this.listenOn);
		while(true){
		try {
			System.out.println("Waiting");
			this.connection = this.serverSocket.accept();
			
			new Thread(new CampServerConnectionThread(this.connection)).start();
			
			/*System.out.println("Received Connection From Helicopter");
			this.input = new ObjectInputStream(this.connection.getInputStream());
			System.out.println("Received Input from thing");
		    Packet confirmationPacket = (Packet) this.input.readObject();
		    
		    System.out.println(confirmationPacket.toString());
		//    this.output = new ObjectOutputStream(this.connection.getOutputStream());
		    System.out.println("Thanking chopper");
		  //  this.output.writeObject("Thanks Dude");
		    //this.output.flush();
		    this.input.close();
		    //this.output.close();
		    this.connection.close();
		    
		    if(confirmationPacket.getPacketType().equals("BookingConfirmationPacketWithCompanyName")){
		    	System.out.println("\n Flight Booked");
		    	System.out.println("\n"+confirmationPacket.getFirstName()+" "+
		    			confirmationPacket.getSurname()+" "+ 
		    			confirmationPacket.getDateToFly()+" "+
		    			confirmationPacket.getTimeToFly()+" "+
		    			confirmationPacket.getDestination()+" "+
		    			confirmationPacket.getCompanyName());
		    }else{
		    	//this.sharedDisplay.append("No flights are available. You should try again");
		    	System.out.println("no flights man. You gotta start again.Life is not fair");
		    }*/
		    
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	
		}
	}


	



}
