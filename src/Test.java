import java.net.*;
public class Test{

    public static void main(String[] args){
    try{
    Socket socket = new Socket("127.0.0.1",50000);
    }catch (Exception exception){
	System.err.println("Fuck Sake"); 
	exception.printStackTrace();
    }
}
}