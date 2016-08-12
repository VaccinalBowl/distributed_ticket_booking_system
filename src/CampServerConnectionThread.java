import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


@SuppressWarnings("unused")
public class CampServerConnectionThread implements Runnable {

	private Socket connection;
	
	private ObjectInputStream inputStream;
	
	
	
	
	public CampServerConnectionThread(Socket socket){
		this.connection=socket;
		
		
	}
	
	
	
	public void run() {
		// TODO Auto-generated method stub
		try {
			//this.outputStream = new ObjectOutputStream( this.connection.getOutputStream());
			this.inputStream  = new ObjectInputStream(this.connection.getInputStream());
			Packet packet = (Packet)this.inputStream.readObject();
			if(packet.getPacketType().equals("BookingConfirmationPacket")){
				System.out.println(packet.toString());
			}else if(packet.getPacketType().equals("CancelationConfirmationPacket")){
				System.out.println("Successfully Cancelled Booking"+ packet.getReservationNumber());
			}else if(packet.getPacketType().equals("CancelationFailurePacket")){
				System.out.println("Could not cancel that booking. Are you sure it was entered correctly?");
			}else {
				System.out.println("No avalilable flights with any company. Try a different day");
			}
		
			
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	
	
	
}
