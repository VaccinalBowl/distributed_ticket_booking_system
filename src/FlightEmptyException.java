
public class FlightEmptyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FlightEmptyException(){
		super("FLIGHT IS EMPTY");
		this.printStackTrace();
	}
}
