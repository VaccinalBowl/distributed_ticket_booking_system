
import java.util.Vector;


public class SharedVector  {
	private Vector<Pair<Packet,Object[]>> data;
	private int length;
	
	public SharedVector(){
		this.data= new Vector<Pair<Packet,Object[]>>();
		this.length = 0; 
	}
	/*public synchronized int getLength(){
		notifyAll();
		return this.length;
		
	}*/
	
	
	public void add(Pair<Packet,Object[]> pair){
		this.data.add(pair);
		
	
		
		this.length++;
		
	}
	

	
	

	public synchronized void empty(){
		this.data.clear();
	
		notifyAll();
	}

	public synchronized Vector<Pair<Packet,Object[]>> getData(){
		notifyAll();
		return this.data;
	}
	
	
	


}
