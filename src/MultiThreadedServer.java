import java.io.IOException;



public class MultiThreadedServer extends Server{

	private SharedVector data;
	private boolean isOpen;
	public MultiThreadedServer(int listenOn,int numberOfConnectionsAllowed,SharedVector dat){
		super(listenOn,numberOfConnectionsAllowed);
		this.data = dat;
		//this.isOpen=false;
	}
	

	
	public void run() {
		
		while(true){
		try {
	//		if(this.isOpen){
			System.out.println("HelicopterServerOnStandby");
			super.connection = this.serverSocket.accept();
			new Thread(new ServerConnectionThread(super.connection,this.data)).start();
		
		
		//	}
				
			
			//super.connection.close();
		
		
		
		
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	
		
		
		}
	
	
	
	
	
	
	}



	//public void setOpen(boolean acceptingConnections) {
		//this.isOpen = acceptingConnections;
	//}



	//public boolean isAcceptingConnections() {
		//return this.isOpen;
	//}
	
	public SharedVector getVector(){
		return this.data;
	}

}
