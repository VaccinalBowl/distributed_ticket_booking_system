
import java.net.Socket;
import java.io.*;

public class ServerConnectionThread implements Runnable{
	private Socket connection;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private Pair<Packet,Object[]> pairReceivedFromCampInterface;
	private SharedVector sharedData;
	
	
	public ServerConnectionThread(Socket socket,SharedVector sharedStuff){
		this.connection=socket;
		this.sharedData = sharedStuff;
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void run() {
		// TODO Auto-generated method stub
		try {
			this.outputStream = new ObjectOutputStream( this.connection.getOutputStream());
			this.inputStream  = new ObjectInputStream(this.connection.getInputStream());
			this.pairReceivedFromCampInterface =(Pair<Packet,Object[]>) this.inputStream.readObject();//possibly dodgy?
			//Integer address = this.pairReceivedFromCampInterface.getSecondElement();
			//Packet packet = this.pairReceivedFromCampInterface.getFirstElement();
			System.out.println("******"+pairReceivedFromCampInterface.getFirstElement() +"*********");
			//System.out.println(pairReceivedFromCampInterface.ge);
			//this.outputStream.writeObject("You're booking has been loaded on to the Helicopter\n" +
				//	"You must now wait for confirmation that you are booked on the flight\n" +
			//		//"Otherwise you must try again\n");
			//this.outputStream.flush();
			//this.outputStream.close();
			this.inputStream.close();
			this.connection.close();
			sharedData.add(this.pairReceivedFromCampInterface);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
