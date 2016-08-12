

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;


public  class Packet implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private boolean availabilityResponse;
	private String firstName=null;
	private String surname=null;
	private Date dateToFly=null;
	private Time timeToFly=null;
	private Time townDepartureTime=null;
	private Time campDepartureTime=null;
	private String destination=null; 
	private String reservationNumber = null;
	private String companyName = null;
	private int helicopterNumber;
	private int tryingTo;
	Packet failedPacket;
	private HashMap<String,Boolean> packetTypes = new HashMap<String,Boolean>(); 
	private Vector<Pair<String,Integer>> companies;
	
	public Vector<Pair<String,Integer>> getCompanies(){
		return this.companies;
	}
	
	public String getCompanyName(){
		return this.companyName;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public boolean getAvailabilityResponse(){
		return this.availabilityResponse;
	}
	                                        

	public String getSurname() {
		return surname;
	}



	public Date getDateToFly() {
		return dateToFly;
	}



	public Time getTimeToFly() {
		return timeToFly;
	}



	public String getDestination() {
		return destination;
	}



	public String getReservationNumber() {
		return reservationNumber;
	}







	
	

	
	
	public Packet(String f, String s, Date d , Time t, String dest){
		
		this.firstName=f;
		this.surname=s;
		this.dateToFly = d;
		this.timeToFly = t;
		this.destination = dest; 
		

		this.setupHashMap();
		this.packetTypes.put("BookingPacket", true);
		
	}
	
	
	
	public Packet(String refer){
		this.reservationNumber = refer;
		this.setupHashMap();
		this.packetTypes.put("CancelationPacket", true);
	
		
	}
	
	public Packet(Date d, Time t , String destinatio){
		this.setupHashMap();
		this.dateToFly = d;
		this.timeToFly = t;
		this.destination = destinatio;
		this.packetTypes.put("AvailabilityQueryPacket", true);

	
	
	
	
	
	}
	
	
	public String getPacketType(){
		for(String key : this.packetTypes.keySet()){
			if (this.packetTypes.get(key)==true){
				return key;
			}
		}
		return null;
	}
	
	
	
	
	
	
	
	private void setupHashMap(){
		this.packetTypes.put("BookingPacket", false);
		this.packetTypes.put("CancelationPacket", false);
		this.packetTypes.put("AvailabilityQueryPacket", false);
		this.packetTypes.put("AvailabilityResponse",false);
		this.packetTypes.put("BookingConfirmationPacket", false);
		this.packetTypes.put("CancelationConfirmationPacket", false);
		this.packetTypes.put("PacketFromHelicopter", false);
		this.packetTypes.put("BookingConfirmationPacketWithCompanyName",false);
		this.packetTypes.put("HelicopterInitialisationPacket", false);
		this.packetTypes.put("CancelationFailurePacket", false);
		this.packetTypes.put("CancelationPacketWithCompanyname", false);
		this.packetTypes.put("ConnectionFailurePacket",false);
	}

	
	public Packet (boolean b){
		this.availabilityResponse=b;
		this.setupHashMap();
		this.packetTypes.put("AvailabilityResponse", true);
		
	}

	
	public Packet(String f, String s,Date d, Time t, String dest, String referenceNumber,String companyNam){
		this.firstName=f;
		this.surname=s;
		this.dateToFly= d;
		this.timeToFly=t;
		this.destination=dest;
		this.reservationNumber=referenceNumber;
		this.companyName=companyNam;
		this.setupHashMap();
		this.packetTypes.put("BookingConfirmationPacketWithCompanyName", true);
	}
	
	
	public Packet(String f, String s, Date d , Time t, String dest,String referenceNum){
		
		this.firstName=f;
		this.surname=s;
		this.dateToFly = d;
		this.timeToFly = t;
		this.destination = dest; 
		this.reservationNumber = referenceNum;

		this.setupHashMap();
		this.packetTypes.put("BookingConfirmationPacket", true);
		
	}
	public Packet (String referenceNumber,String compName){
		this.setupHashMap();
		this.packetTypes.put("CancelationConfirmationPacket",true);
		this.reservationNumber = referenceNumber;
		this.companyName = compName;
	}
	
	public Packet(String referenceNumber,String companyNam,boolean justForSanity){
		this.setupHashMap();
		this.packetTypes.put("CancelationPacketWithCompanyName",true);
		this.companyName = companyNam;
		this.reservationNumber = referenceNumber;
	}
	
	public Packet(String referenceNumber,int i){
		this.setupHashMap();
		if(i==8){
		this.packetTypes.put("CancelationConfirmationPacket", true);
		}else if (i==7){
		this.packetTypes.put("CancelationFailurePacket", true);
		}
		this.reservationNumber=referenceNumber;
	}
	
	public Packet(int flightId,Time townDeparture, Time campDeparture,Vector<Pair<String,Integer>> comps){
		this.setupHashMap();
		this.packetTypes.put("HelicopterInitialisationPacket", true);
		this.helicopterNumber = flightId;
		this.townDepartureTime = townDeparture;
		this.campDepartureTime = campDeparture; 
		this.companies = comps;
	}
	
	public Packet(int i,Packet fp){
		this.setupHashMap();
		if(i==1){
			this.packetTypes.put("ConnectionFailurePacket",true);
			; 
			this.failedPacket = fp;
		}
	}
	
	public String toString(){
		if(this.getPacketType().equals("BookingConfirmationPacket")){
			return this.firstName + " "+this.surname + " "+this.dateToFly.toString() +" "+ this.timeToFly.toString()+" " + this.destination +" "+ this.reservationNumber;
		} else if(this.getPacketType().equals("BookingPacket")){
			return this.firstName + " "+this.surname +" "+ this.dateToFly.toString() +" " + this.destination;
		}else if(this.getPacketType().equals("CancelationPacket")){
			return this.reservationNumber;
		}else if(this.getPacketType().equals("AvailabilityQueryPacket")){
			return this.dateToFly.toString() + " "+this.timeToFly.toString()+" " + this.destination;
		}else if(this.getPacketType().equals("AvailabilityResponse")){
			return Boolean.toString(this.availabilityResponse);
		}else if(this.getPacketType().equals("CancelationConfirmationPacket")){
			return this.reservationNumber + " cancelled";
		}else if(this.getPacketType().equals("BookingConfirmationPacketWithCompanyName")){
			return this.firstName + " "+this.surname + " "+this.dateToFly.toString() +" "+ this.timeToFly.toString()+" " + this.destination +" "+ this.reservationNumber+" "+this.companyName;
		}else if(this.getPacketType().equals("HelicopterInitialisationPacket")){
			return this.helicopterNumber+" "+this.townDepartureTime.toString()+" "+this.campDepartureTime.toString();
		}else if(this.getPacketType().equals("CancelationPacketWithCompanyName")){
			return this.companyName +":"+ this.reservationNumber;
		}else if(this.getPacketType().equals("ConnectionFailurePacket")){
			return (this.failedPacket.toString());
		}else{
			return this.getPacketType();
		}
		
	}
	
		
		
		
		
		
		
	
	
	
	/*public static void main(String[] args){
		Socket socket;
		try {
			socket = new Socket("127.0.0.1",6000);
			ObjectOutputStream o = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream i = new ObjectInputStream (socket.getInputStream());
			Date d = new Date("29","10","2010");
			Time t = new Time("15","00");
			Packet packet = new Packet(d,t,"TOWN");
			o.writeObject(packet);
			o.flush();
			Packet p = (Packet) i.readObject();
			System.out.println(p.toString());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}*/
	
	public static void main(String[] args){
		System.out.println("What is wrong with this?");
	}

	public int getHelicopterFlightNumber() {
		return this.helicopterNumber;
		
	}

	public Time getCampDepartureTime() {
		// TODO Auto-generated method stub
		
		
		
		return this.campDepartureTime;
	}

	public Time getTownDepartureTime() {
		// TODO Auto-generated method stub
		return this.townDepartureTime;
	}
	


}
