import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public abstract class Server implements Runnable{
	protected ServerSocket serverSocket; 
	protected Socket connection;
	protected boolean acceptingConnections;
	protected ObjectInputStream input;
	protected ObjectOutputStream output;
	
	
	
	public Server(int listenOn,int numberOfConnectionsAllowed){
		try {
			this.serverSocket = new ServerSocket(listenOn,numberOfConnectionsAllowed);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	



	public void setAcceptingConnections(boolean acceptingConnections) {
		this.acceptingConnections = acceptingConnections;
	}



	public boolean isAcceptingConnections() {
		return acceptingConnections;
	}
	
	
}
