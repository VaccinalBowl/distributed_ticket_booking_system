
public class FlightFullException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String s= "Flight Full Exception";

	public FlightFullException(){
		super (s);
		this.printStackTrace();
	}


}
