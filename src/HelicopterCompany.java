
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class HelicopterCompany extends JFrame implements ActionListener, Runnable{
	public static final int KLM_PORT= 6001;
	public static final int FINNAIR_PORT = 6000;
	public static final int LUFTHANSA_PORT = 6002;
	public static final String FINNAIR_IP = "127.0.0.1";
	public static final String KLM_IP = "127.0.0.1";
	public static final String LUFTHANSA_IP = "127.0.0.1";
	
	
	private static final long serialVersionUID = 1L;
	
	/*These variables  are simply used in order to set
	 * up the graphical user interface that the helicopter company uses*/
	private final String companyName;
	private Database database;
	private String databasePath;
	private JLabel label1,label2,label3;
	private JTextField firstname,surname,bookingReferenceToCancel;
	private JButton bookButton,cancelBookingButton;
	private JTextArea displayArea;
	private JPanel panel1,panel2;
	private JMenuBar bar;
	private JTabbedPane tabbedPane;
	private JMenu fileMenu;
	private JMenuItem exitItem;
	private JComboBox dayComboBox,monthComboBox,yearComboBox,directionComboBox,airlineBookedWith;
	private String[] days={"Day","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
	private String[] months={"Month","January","February","March","April","May","June","July","August","September","October","November","December"};
	private String[] years ={"Year","2010","2011","2012","2013","2014","2015","2016","2017","2018","2019","2020"};
	private String[] directions ={"Going To","TOWN","CAMP"};
	private String[] airlines ={"Airline","FINNAIR","KLM","LUFTHANSA"};
	
	
	
	
	
	/*These variables are for the server part of
	 * the helicopter Company. Server runs conncurrently 
	 * in the server thread. Helicopter Company also
	 * acts as a client when it needs to book a flight with
	 * another company*/
	private int portOfServer;
	private ServerSocket serverSocket;
	private Socket connection;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Packet incomingPacket;
	private Thread serverThread;
	private Thread clockThread; 
	private Clock clock; 
	private Time currentTime; 
	private Date currentDate; 
	private Vector<Object[]> timesToday;
	private Time timeOfNextDropAndRoll; 
	private String destinationOfNextDropAndRoll;
	private int flightNumberOfNextDropAndRoll;
	private int currentIndexOfVector = 0;
	private int numberOfIterationsToCarryOut; 
	private Date dateStore;
	private String ipOfServer;
	private Vector<Pair<String,Integer>>otherCompanys;
	
	
	public HelicopterCompany(String name,String ipAddress,int port,Vector<Pair<String,Integer>> otherCompanyData)
	{
		super(name);
		this.otherCompanys=otherCompanyData;
		this.companyName=name;
		this.ipOfServer = ipAddress;
		this.portOfServer = port;
		this.databasePath="jdbc:mysql://localhost/"+this.companyName;
		this.database = new Database(this.databasePath,this.companyName);
		this.serverThread = new Thread(this);
		this.serverThread.start();
		this.initializeHelicopters();/*Warning possibly incorrect*/
		
		this.clock = new Clock(true);
		this.clockThread = new Thread(this.clock);
		this.clockThread.start();
		this.initializeFlightTimesForToday();
		this.incrementNextRollTime();
		this.setUpGUI();
		
		//this.incrementNextRollTime();
		this.operate();
	}
	private void initializeFlightTimesForToday(){
		this.currentTime = this.getHelicopterCompanyTime();
		this.currentDate = this.getHelicopterCompanyDate();
		this.timesToday = this.database.getFlightsOnADay(this.currentDate.getNextDay());
		dateStore = this.currentDate.getNextDay();
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		//System.out.println(this.currentTime);
	//	System.out.println(this.currentDate);
		for(Object[] ob: this.timesToday ){
			System.out.println(ob[0]);
			System.out.println(ob[1]);
			System.out.println(ob[2]);
		}
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		//this.timeOfNextDropAndRoll=(Time)this.timesToday.elementAt(1)[1];
		this.numberOfIterationsToCarryOut=this.timesToday.size();
		
	}
	private void incrementNextRollTime(){
		//this.clearOutTimesThatHavePassed();//usually there are none this is just for testing
		
			Time time;
			Integer flightId;
			String dest;
			
		
		
		
		
		if(!(this.currentIndexOfVector<this.numberOfIterationsToCarryOut)){
			this.currentIndexOfVector=0;
			this.dateStore = this.dateStore.getNextDay();
			this.timesToday    =this.database.getFlightsOnADay(dateStore);
			this.numberOfIterationsToCarryOut = this.timesToday.size();
			 time = (Time)(this.timesToday.elementAt(this.currentIndexOfVector)[1]);
			 flightId = (Integer)(this.timesToday.elementAt(this.currentIndexOfVector)[0]);
			 dest = (String) (this.timesToday.elementAt(this.currentIndexOfVector)[2]);
			 this.timeOfNextDropAndRoll = time;
				this.destinationOfNextDropAndRoll = dest;
				this.flightNumberOfNextDropAndRoll = flightId;
			 this.currentIndexOfVector++;
		}else{
		
		
		this.timesToday.elementAt(this.currentIndexOfVector);
		 time = (Time)(this.timesToday.elementAt(this.currentIndexOfVector)[1]);
		 flightId = (Integer)(this.timesToday.elementAt(this.currentIndexOfVector)[0]);
		 dest = (String) (this.timesToday.elementAt(this.currentIndexOfVector)[2]);
		//this.timeOfNextDropAndRoll=this.timesToday.elementAt(this.currentIndexOfVector);
		this.timeOfNextDropAndRoll = time;
		this.destinationOfNextDropAndRoll = dest;
		this.flightNumberOfNextDropAndRoll = flightId;
		this.currentIndexOfVector++;
		}
		
	}
	
	private boolean timeToRoll(){
		this.currentTime = this.getHelicopterCompanyTime();
		this.currentDate = this.getHelicopterCompanyDate();
		System.out.println("Roll Time:");
		System.out.println(this.timeOfNextDropAndRoll);
		System.out.println(this.dateStore);
		System.out.println(this.currentDate);
		System.out.println(this.currentTime);
		
		if(this.currentTime.isEqual(this.timeOfNextDropAndRoll)&&(this.currentDate.isEqual(this.dateStore))){
			return true;
		}else{
			return false;
		}
		//try {
			//Thread.sleep(5000);
		//} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}
		//return true;
	}
	
	
	
	private void operate(){
		while(true){
			if(this.timeToRoll()){
				//int flight = (Integer)(this.timesToday.elementAt(0))[0];
				this.currentDate = this.getHelicopterCompanyDate();
				//System.out.println("Size:"+this.timesToday.size());
				//System.out.println("Size:"+this.timesToday.elementAt(0)[0]);
				//System.exit(0);
				//Time time = this.timeOfNextDropAndRoll;
			    //String direction = (String)(this.timesToday.elementAt(1))[2];
				System.out.println(this.flightNumberOfNextDropAndRoll+" "+this.currentDate+" "+this.timeOfNextDropAndRoll+" "+this.destinationOfNextDropAndRoll);
				this.database.dropAndRoll(this.flightNumberOfNextDropAndRoll, this.currentDate, this.timeOfNextDropAndRoll, this.destinationOfNextDropAndRoll);
				this.incrementNextRollTime();
			}else{
				System.out.println("Waiting to roll");
				//System.out.println();
			}
			
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		
		
	}

	public String getCompanyName(){
		return this.companyName;
	}
	
	
	public void setDatabase(Database database) {
		this.database = database;
	}

	public Database getDatabase() {
		return database;
	}

	@Override
	public  void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		Pair<String,String> namePair;
		 if (event.getSource()==this.bookButton){
			
			if(this.areDatesValid()){
				namePair = this.processNames(this.firstname.getText(),this.surname.getText());
				if(!namePair.isNull()){
					
					try {
							System.out.println("££££££££££££££££");
							System.out.println(namePair.getFirstElement());
							System.out.println(namePair.getSecondElement());
							this.bookFlightFromGUI(namePair);
						
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (FlightFullException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e){
						e.printStackTrace();
					}
				
				}
			
			}
			
		 }else if(event.getSource()==this.cancelBookingButton){	
			 System.out.println("Cancelling Booking");
			 
			 try {
				this.cancelFlightFromGUI(this.bookingReferenceToCancel.getText(),(String)this.airlineBookedWith.getSelectedItem());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.display("You did not actually book that flight");
			} catch (FlightEmptyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.display("Flight already empty!");
			}
			System.out.println("Crisis averted");
		 }
		
		
	
		
	}

	private void bookFlightFromGUI(Pair<String,String> names) throws SQLException, FlightFullException, ClassNotFoundException {
		this.display("***************************************");
		this.display("Attempting to book flight with details:");
	    this.display("Firstname: "+this.firstname.getText());
	    this.display("Surname: "+this.surname.getText());
	    this.display("On: "+this.dayComboBox.getSelectedItem()+"/" + this.monthComboBox.getSelectedItem()+"/"+this.yearComboBox.getSelectedItem());
	    this.display("To: "+this.directionComboBox.getSelectedItem());
	    //String day= (String) this.dayComboBox.getSelectedItem();
	    String month = getMonthAsNumber((String)this.monthComboBox.getSelectedItem());
	    
	    
	    Date dateOfFlight= new Date((String) this.dayComboBox.getSelectedItem(), month,(String) this.yearComboBox.getSelectedItem());
	    //Time timeOfFlight= new Time("15","00");
	    /*Vector<Pair<Integer,Time>> possibleFlightsAndTimes = this.getFlightsMatchingRequest(dateOfFlight, (String)this.directionComboBox.getSelectedItem());
	    Pair<Integer,Time> pair=null;
	    boolean foundSpace = false;
	    	for(Pair<Integer,Time> tmp:possibleFlightsAndTimes){
	    		System.out.println("Bang Bang Bang");
	    		if(this.database.hasAvailability((Integer)tmp.getFirstElement(), dateOfFlight, (Time) tmp.getSecondElement(), (String) this.directionComboBox.getSelectedItem())){
	    			pair =tmp;
	    			foundSpace=true;
	    			break;
	    		}
	    	}
	    System.out.println(foundSpace);
	    
	   // System.out.println(pair.getFirstElement());
	   // System.out.println(names.getFirstElement());
	   // System.out.println(names.getSecondElement());
	   // System.out.println(dateOfFlight);
	   // System.out.println(pair.getSecondElement());
	    //System.out.println(this.directionComboBox.getSelectedItem());
	   
	    //this.database.printTable("FLIGHTS");
	    //this.database.printTable("BOOKINGS");
	    //this.database.printTable("REFERENCENUMBERS");*/
	    //if(foundSpace){
	    	 //String referenceNumber=this.database.makeReservation(pair.getFirstElement(),names.getFirstElement(), names.getSecondElement(), dateOfFlight, pair.getSecondElement(),(String) this.directionComboBox.getSelectedItem());
	    		Pair<String,Time> referenceNumberTime = this.database.makeReservation(names.getFirstElement(), names.getSecondElement(), dateOfFlight,  (String) this.directionComboBox.getSelectedItem());
	    if(!referenceNumberTime.getFirstElement().equals("FlightsFull")){	
	    		this.display(names.getFirstElement()+" "+names.getSecondElement()+" booked on "+ referenceNumberTime.getSecondElement().toString()+" flight to "+this.directionComboBox.getSelectedItem());
	    		this.display("Flight Details:"+this.selectedDateFormatted()+" Reference Number:"+referenceNumberTime.getFirstElement());
	    
	    }else{
	    	
	    	this.display("All of our flights on "+ this.selectedDateFormatted()+ " to "+this.directionComboBox.getSelectedItem()+" are full.");
	    	//this.display("Please try a different day");
	    	JOptionPane.showMessageDialog(null,"We have no availability on that day. We are going to check with another company for availability","Flight Availability Error",JOptionPane.INFORMATION_MESSAGE);
	    	//String bookedWith = ""; 
	    	
	    	
			Object[] triplet= this.bookWithOtherCarrier(names.getFirstElement(),names.getSecondElement(),dateOfFlight,(String) this.directionComboBox.getSelectedItem());
	    		//this.attemptExternalBooking(names.getFirstElement(), names.getSecondElement(),dateOfFlight,timeOfFlight,(String) this.directionComboBox.getSelectedItem());
			if(triplet!=null){
				this.display(names.getFirstElement()+" "+names.getSecondElement()+" booked on"+ triplet[1] +"flight to "+this.directionComboBox.getSelectedItem());
				this.display("Flight Details:"+this.selectedDateFormatted()+" Reference Number:"+triplet[0]+" flying with:"+triplet[2]);
			}else{
				this.display("There are no flights available");
			}
	    
	    	
	    	
			
				
				
				
	    		
	    	}
	    	
	    	
	    	
	    this.display("***************************************");
	    }
	    
		



	

	private void cancelFlightFromGUI(String referenceNumber,String airlineCompanyToCancelWith) throws SQLException, FlightEmptyException{
		String cancellingWith="";
		String otherIp=null,otherIp1=null,otherIp2=null;
		int otherPort=1,otherPort1=-1,otherPort2=-1;
		if(this.companyName.equals("FINNAIR")){
			if(!airlineCompanyToCancelWith.equals("FINNAIR")){
			otherIp1 = HelicopterCompany.KLM_IP;
			otherPort1 = HelicopterCompany.KLM_PORT;
			otherIp2 = HelicopterCompany.LUFTHANSA_IP;
			otherPort2 = HelicopterCompany.LUFTHANSA_PORT;
				if(airlineCompanyToCancelWith.equals("KLM")){
					otherIp=otherIp1;
					otherPort = otherPort1;
					cancellingWith = "KLM";
					
				}else{
					otherIp = otherIp2;
					otherPort = otherPort2;
					cancellingWith ="LUFTHANSA";
				}
			}
			
		}else if(this.companyName.equals("KLM")){
			if(!airlineCompanyToCancelWith.equals("KLM")){
			otherIp1 = HelicopterCompany.LUFTHANSA_IP;
			otherPort1 = HelicopterCompany.LUFTHANSA_PORT;
			otherIp2 = HelicopterCompany.FINNAIR_IP;
			otherPort2 = HelicopterCompany.FINNAIR_PORT;
				if(airlineCompanyToCancelWith.equals("LUFTHANSA")){
						otherIp = otherIp1;
						otherPort = otherPort1; 
						cancellingWith="LUFTHANSA";
				}else{
					otherIp = otherIp2;
					otherPort = otherPort2; 
					cancellingWith="FINNAIR";
				}
			}
		}else {
			if(!airlineCompanyToCancelWith.equals("LUFTHANSA")){
			otherIp1 = HelicopterCompany.FINNAIR_IP;
			otherPort1 = HelicopterCompany.FINNAIR_PORT;
			otherIp2 = HelicopterCompany.KLM_IP;
			otherPort2 = HelicopterCompany.KLM_PORT;
				if(airlineCompanyToCancelWith.equals("FINNAIR")){
					otherIp = otherIp1;
					otherPort = otherPort1;
					cancellingWith="FINNAIR";
				}else{
					otherIp = otherIp2; 
					otherPort = otherPort2; 
					cancellingWith = "KLM";
				}
			
			
			
			
			}
		}
		
		
		
		
		
		
		/*this.display("Trying to cancel booking:"+referenceNumber);
		System.out.println(referenceNumber);
		boolean success = this.database.cancelReservation(referenceNumber);
		if(success){
			this.display("Successfully cancelled reservation "+referenceNumber);
		}*/
		if(this.companyName.equals(airlineCompanyToCancelWith)){
			boolean success = this.database.cancelReservation(referenceNumber);
		}else{
		
			try {
				Socket cancelationSocket = new Socket(otherIp,otherPort);
				ObjectOutputStream outputS = new ObjectOutputStream(cancelationSocket.getOutputStream());
				ObjectInputStream inputS = new ObjectInputStream(cancelationSocket.getInputStream());
				Packet packet = new Packet(referenceNumber);
				outputS.writeObject(packet);
				Packet reply = (Packet) inputS.readObject();
				if(reply.getPacketType().equals("CancelationConfirmationPacket")){
					
				
					this.displayArea.append(reply.getReservationNumber() +" cancelled with " + cancellingWith);
				}else{
					this.displayArea.append("There was no booking with"+ cancellingWith+" with referenceNumber"+referenceNumber);
				}
			} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
				this.displayArea.append("Booking Cancelation communication failure. You will have to try again later");
				e.printStackTrace();
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//Pair<Pair<String,Integer>,Pair<String,Integer>> connectionPossibilities = this.getConnectionPossibilities();
				    //Socket tmpSocket;
				    //boolean successConnect=false;
				    //boolean successCancel=false;
					//boolean wasConnectionFailure=false;
					//for(int i = 0 ; i < 2 ; i++){
						//try {
						//	tmpSocket = new Socket(connectionPossibilities.getFirstElement().getFirstElement(),connectionPossibilities.getFirstElement().getSecondElement());
						
						
						//} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						//} catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						
					
			
			
		
		
		
		
	}
	
	private Pair<Pair<String,Integer>,Pair<String,Integer>> getConnectionPossibilities(){
		if(this.companyName.equals("FINNAIR")){
			Pair<String,Integer> pair1 = new Pair<String,Integer>(HelicopterCompany.KLM_IP,HelicopterCompany.KLM_PORT);
			Pair<String,Integer> pair2 = new Pair<String,Integer>(HelicopterCompany.LUFTHANSA_IP,HelicopterCompany.LUFTHANSA_PORT);
			Pair<Pair<String,Integer>,Pair<String,Integer>> returns = new Pair<Pair<String,Integer>,Pair<String,Integer>>(pair1,pair2); 
			return returns;
		}else if(this.companyName.equals("KLM")){
			Pair<String,Integer> pair1 = new Pair<String,Integer>(HelicopterCompany.LUFTHANSA_IP,HelicopterCompany.LUFTHANSA_PORT);
			Pair<String,Integer> pair2 = new Pair<String,Integer>(HelicopterCompany.FINNAIR_IP,HelicopterCompany.FINNAIR_PORT);
			Pair<Pair<String,Integer>,Pair<String,Integer>> returns = new Pair<Pair<String,Integer>,Pair<String,Integer>>(pair1,pair2); 
			return returns;
		}else if(this.companyName.equals("LUFTHANSA")){
			Pair<String,Integer> pair1 = new Pair<String,Integer>(HelicopterCompany.FINNAIR_IP,HelicopterCompany.FINNAIR_PORT);
			Pair<String,Integer> pair2 = new Pair<String,Integer>(HelicopterCompany.LUFTHANSA_IP,HelicopterCompany.LUFTHANSA_PORT);
			Pair<Pair<String,Integer>,Pair<String,Integer>> returns = new Pair<Pair<String,Integer>,Pair<String,Integer>>(pair1,pair2); 
			return returns;
		}
		return null;
	}
	
	
	private String selectedDateFormatted(){
		return this.dayComboBox.getSelectedItem().toString()+"/"+this.monthComboBox.getSelectedItem().toString()+"/"+this.yearComboBox.getSelectedItem().toString();
	}
	public static String getMonthAsNumber(String selectedItem) {
		
		if(selectedItem.equals("January"))
			return "01";
		if(selectedItem.equals("February"))
			return "02";
		if(selectedItem.equals("March"))
			return "03";
		if(selectedItem.equals("April"))
			return "04";
		if(selectedItem.equals("May"))
			return "05";
		if(selectedItem.equals("June"))
			return "06";
		if(selectedItem.equals("July"))
			return "07";
		if(selectedItem.equals("August"))
			return "08";
		if(selectedItem.equals("September"))
			return "09";
		if(selectedItem.equals("October"))
			return "10";
		if(selectedItem.equals("November"))
			return "11";
		if(selectedItem.equals("December"))
			return "12";
		
		
		
	
		return "";
	}
	private Pair<String,String> processNames(String string, String string2) {
		
		if((string.equals(""))||string2.equals("")){
			JOptionPane.showMessageDialog(null, "You didn't enter your name properly","Name Error", JOptionPane.ERROR_MESSAGE);
			return new Pair<String,String>(null,null);
		}
		
		
		
		
		
		
		
		Pair<String,String> stringPair = new Pair<String,String>(string,string2);
		String tmp="";
		for(int i =0; i< stringPair.getFirstElement().length();i++){
			if(Character.isLetter(stringPair.getFirstElement().charAt(i))){
				//do nothing
			
				tmp+=stringPair.getFirstElement().charAt(i);
			}
				
		}
		
		stringPair.setFirstElement(tmp);
		tmp="";
		for(int i =0; i< stringPair.getSecondElement().length();i++){
			if(Character.isLetter(stringPair.getSecondElement().charAt(i))){
				//do nothing
			
				tmp+=stringPair.getSecondElement().charAt(i);
			}
				
		}
		
		return stringPair;
		
		
		
	}
	private boolean areDatesValid(){
		
		if(this.dayComboBox.getSelectedItem().equals("Day")||this.monthComboBox.getSelectedItem().equals("Month")||this.yearComboBox.getSelectedItem().equals("Year")){
				
				JOptionPane.showMessageDialog(null,"You didn't enter a full date!","Date Input Error",JOptionPane.ERROR_MESSAGE);
				return false;
			}
			 
			 if((this.monthComboBox.getSelectedItem().equals("April"))||
					 (this.monthComboBox.getSelectedItem().equals("June"))||
					 (this.monthComboBox.getSelectedItem().equals("September"))||
					 (this.monthComboBox.getSelectedItem().equals("November"))){
				 
				 		if(this.dayComboBox.getSelectedItem().equals("31")){
				 			JOptionPane.showMessageDialog(null, "There are only thirty days in "+this.monthComboBox.getSelectedItem()+"\n Please reenter the date","Overflow of days in month error",JOptionPane.ERROR_MESSAGE);
				 			this.dayComboBox.setSelectedIndex(0);
				 			return false;
				 		}
				 
				 
				 
			 }
			 
			if((this.monthComboBox.getSelectedItem().equals("February"))){
				if(this.dayComboBox.getSelectedItem().equals("30")||this.dayComboBox.getSelectedItem().equals("31")){
					
					JOptionPane.showMessageDialog(null, "There are normally only 28 days in "+this.monthComboBox.getSelectedItem()+"\n Please reenter the date","Overflow of days in month error",JOptionPane.ERROR_MESSAGE);
					this.dayComboBox.setSelectedIndex(0);
					return false;
				}else{
					String s = (String) this.yearComboBox.getSelectedItem();
					int yearAsInt = Integer.parseInt(s.trim());
					if ((this.dayComboBox.getSelectedItem().equals("29"))&&(!isLeapYear(yearAsInt))){
						JOptionPane.showMessageDialog(null, "Not a leap year. Please reenter the date!","Overflow of days in month error",JOptionPane.ERROR_MESSAGE);
						this.dayComboBox.setSelectedIndex(0);
						return false;
					}
				}
			}
			if (this.directionComboBox.getSelectedItem().equals("Going To")){
				JOptionPane.showMessageDialog(null,"Must Enter a Destination","Destination Error",JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			
			
			
			return true;
	}
	private void setUpGUI(){
		
		this.tabbedPane= new JTabbedPane();
		this.label1 = new JLabel("Firstname");
		this.label2 = new JLabel("Surname");
		this.panel1 = new JPanel(new FlowLayout());
		this.firstname= new JTextField(10);
		
		this.surname = new JTextField(10);
		this.panel1.add(label1);
		this.panel1.add(this.firstname);
		this.panel1.add(label2);
		this.panel1.add(this.surname);
		
		this.panel1.setBackground(Color.GREEN);
		//panel1.add(label1);
		this.bookButton = new JButton("Book Next Flight");
		//this.buttonHandler = new ButtonHandler();
		//this.bookButton.addActionListener(this.buttonHandler);
	
		//add(this.bookButton);
		this.tabbedPane.addTab("Booking Page",null,panel1,"First Panel");
		
		this.displayArea = new JTextArea(20,40);
		this.dayComboBox = new JComboBox(this.days);
		this.monthComboBox = new JComboBox(this.months);
		this.yearComboBox = new JComboBox(this.years);
		this.dayComboBox.setSelectedIndex(0);
		this.monthComboBox.setSelectedIndex(0);
		this.yearComboBox.setSelectedIndex(0);
		this.directionComboBox= new JComboBox(this.directions);
		this.directionComboBox.setSelectedIndex(0);
		this.panel1.add(this.dayComboBox);
		this.panel1.add(this.monthComboBox);
		this.panel1.add(this.yearComboBox);
		this.panel1.add(this.directionComboBox);
		this.panel1.add(this.bookButton);
		this.panel1.add(new JScrollPane(this.displayArea));
		//this.dayComboBox.addActionListener(this);
		//this.monthComboBox.addActionListener(this);
		this.bookButton.addActionListener(this);
		//this.yearComboBox.addActionListener(this);
		
		this.label3 = new JLabel("Refenence Number");
		this.bookingReferenceToCancel = new JTextField(10);
		
		this.cancelBookingButton = new JButton("Cancel Booking");
		this.cancelBookingButton.addActionListener(this);
		//JLabel label2 = new JLabel("Canceling Panel",SwingConstants.CENTER);
		this.panel2 = new JPanel();
		this.panel2.setBackground(Color.RED);
		this.panel2.add(label3);
		this.panel2.add(this.bookingReferenceToCancel);
		this.airlineBookedWith = new JComboBox(this.airlines);
		this.panel2.add(this.airlineBookedWith);
		this.panel2.add(this.cancelBookingButton);
		//panel2.add(label2);
		this.tabbedPane.addTab("Canceling Page",null,panel2,"Second Panel");
		
		//this.label3 = new JLabel("Output Panel",SwingConstants.CENTER);
		
		//this.tabbedPane.add(new JScrollPane(this.displayArea));
		
		
		
		//this.panel3 = new JPanel();
		//this.panel3.setBackground(Color.ORANGE);
		//this.displayArea.append("Text goes here");
		//this.panel3.add(new JScrollPane(this.displayArea));
		//this.tabbedPane.addTab("Output Page",null,this.panel3,"Third Panel");
		
		
		
		
		
		
		this.fileMenu = new JMenu("File");
		this.fileMenu.setMnemonic('F');
		this.exitItem = new JMenuItem("Shutdown Node");
		this.exitItem.addActionListener(
				
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						System.out.println("Unforgettable");
						System.exit(0);
					}
				}
		);
		
		
		
		this.fileMenu.add(exitItem);
		this.bar = new JMenuBar();
		this.setJMenuBar(bar);
		this.bar.add(fileMenu);
		this.setSize(600,500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(tabbedPane);
		this.setVisible(true);
	
	
	
	
	}
	public static boolean isLeapYear(int year){
	    if (year < 0) {
	        return false;
	      }

	      if (year % 400 == 0) {
	        return true;
	      } else if (year % 100 == 0) {
	        return false;
	      } else if (year % 4 == 0) {
	        return true;
	      } else {
	        return false;
	      }
	}
	private void display(String string){
		String output="\n";
		output=string+output;
		this.displayArea.append(output);
		this.displayArea.setCaretPosition(this.displayArea.getDocument().getLength()); 
	}



	@Override
	public void run() {
		//this.nodeName = nodeName;
	     
		 try {
			serverSocket = new ServerSocket(this.portOfServer);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	     System.out.println(this.getCompanyName()+"is listening on port:"+this.portOfServer); 
		
		
		
		
		
		while(true) {
		       try {
		        System.out.println("Waiting for connections.");
		        this.connection= serverSocket.accept();
		        System.out.println("New Helicopter Thread starting");
		        new Thread (new HelicopterCompanyIncomingConnectionThread(this.connection,this.database)).start();		        
		        
		        
		        
		        
		        
		        
		        /*
		        System.out.println("Accepted a connection from: "+this.connection.getInetAddress());
		        this.output = new ObjectOutputStream(this.connection.getOutputStream());
		        this.output.flush();
		        this.input = new ObjectInputStream(this.connection.getInputStream());
		        this.incomingPacket = (Packet)this.input.readObject();                         //this.getPacketSentFromClient();
		        System.out.println("Got your packet dude");
		        //System.out.println(this.incomingPacket.getPacketType());
		        System.out.println("sjdfskd"+this.incomingPacket.toString());
		        Packet reply = this.processPacket(this.incomingPacket);
		        this.output.writeObject(reply);
		        this.output.flush();
		        
		        this.output.close();
		        this.input.close();
		        this.connection.close();
		        */
		       
		       
		       
		       
		       
		       
		       
		       } catch(Exception e) {
		    	   e.printStackTrace();
		       }
		     }
		
	}
	
	    
	  
	private Packet processPacket(Packet packet) throws SQLException, ClassNotFoundException, FlightEmptyException {
		   	Packet iPacket = packet;
		   	System.out.println(iPacket.getPacketType());
		   	if(iPacket.getPacketType().equals("AvailabilityQueryPacket")){
		   		if(this.database.hasAvailability(1,iPacket.getDateToFly(), iPacket.getTimeToFly(), iPacket.getDestination())){
		   			return new Packet(true);
		   		}else{
		   			return new Packet(false);
		   		}
		   		
		   	
		   	
		   	}else if(iPacket.getPacketType().equals("BookingPacket")){
		   		
		   		/*boolean found=false;
		   		Pair<Integer,Time> pair=null;
		   		Vector<Pair<Integer,Time>> vector = this.getFlightsMatchingRequest(iPacket.getDateToFly(), iPacket.getDestination());
		   		for(Pair<Integer,Time> tmp:vector){
		   			if(this.database.hasAvailability(tmp.getFirstElement(),iPacket.getDateToFly(),tmp.getSecondElement(),iPacket.getDestination())){
		   				found=true;
		   				pair=tmp;
		   				System.out.println("We have space");
		   				System.out.println(pair.getFirstElement().toString());
		   				System.out.println(pair.getSecondElement().toString());
		   				break;
		   			}
		   				
		   			
		   		}*/
		   		Pair<String,Time> sPt = this.database.makeReservation(iPacket.getFirstName(), iPacket.getSurname(), iPacket.getDateToFly(), iPacket.getDestination());
		   		
		   		if(!sPt.getFirstElement().equals("FlightsFull")){
		   			return new Packet(iPacket.getFirstName(),iPacket.getSurname(),iPacket.getDateToFly(),sPt.getSecondElement(),iPacket.getDestination(),sPt.getFirstElement());
		   		}else{
		   			return new Packet(false);
		   		}
		   		
		   		/*if(found){
		   		String reservationNumber = this.database.makeReservation(pair.getFirstElement(),iPacket.getFirstName(), iPacket.getSurname(), iPacket.getDateToFly(), pair.getSecondElement(), iPacket.getDestination());
		   		return new Packet(iPacket.getFirstName(),iPacket.getSurname(),iPacket.getDateToFly(),pair.getSecondElement(),iPacket.getDestination(),reservationNumber);
		   		}else{
		   			return new Packet(false);
		   		}*/
		   	}else if(iPacket.getPacketType().equals("CancelationConfirmationPacket")){
		   		boolean success = this.database.cancelReservation(iPacket.getReservationNumber());
		   		if(success){
		   			return new Packet(iPacket.getReservationNumber(),this.getCompanyName());
		   	
		   		}else{
		   			return new Packet(false);
		   		}
		   	}
		   	
		   	
			
		   	return null;
		}
		   
	   

	
	

	
	private Pair<String, Time> attemptExternalBooking(String firstName,String surname, Date date, String destination,String airlineName) {
		//if(!this.isAvailabilityOn(date, time, destination, airlineName)){//can't remember why I need this?
			//return "CannotBook";
		//}
		Pair<String,Time> toReturn=new Pair<String,Time>("CannotBook",null);
		
		Time t= null;
		Socket con;
		try {
			con = new Socket("127.0.0.1",this.getAirlinePort(airlineName));
			ObjectOutputStream o = new ObjectOutputStream(con.getOutputStream());
			ObjectInputStream i = new ObjectInputStream(con.getInputStream());
			Packet pack = new Packet(firstName,surname,date,t,destination);
			o.writeObject(pack);
			o.flush();
			Packet receivedPacket = ((Packet) i.readObject());
			
			//System.out.println("*&^%£"+receivedPacket.getTimeToFly().toString());
			String packetType = receivedPacket.getPacketType();
			if(packetType.equals("BookingConfirmationPacket")){	
				System.out.println("Packing is a booking packet");
				
				
				toReturn = new Pair<String,Time>(receivedPacket.getReservationNumber(),receivedPacket.getTimeToFly());
			}
			
			
			i.close();
			o.close();
			con.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Type1");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("type2");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Type3");
			e.printStackTrace();
		}
		
		
		

		return toReturn;
	}
	
	private int getAirlinePort(String airlineName){
		int port = -1;
		if(airlineName.equals("FINNAIR")){
			return FINNAIR_PORT;
		}else if(airlineName.equals("KLM")){
			return KLM_PORT;
		}else if(airlineName.equals("LUFTHANSA")){
			return  LUFTHANSA_PORT;
		}
		return port;
		
	}
	
	@SuppressWarnings("unused")
	private boolean isAvailabilityOn(Date date,Time time,String destination,String airlineName) throws UnknownHostException, IOException, ClassNotFoundException{
		Packet packet = new Packet(date,time,destination);
		int port = this.getAirlinePort(airlineName);
		ObjectOutputStream o;
		ObjectInputStream i; 
		Socket connect = new Socket ("127.0.0.1",port);
		o = new ObjectOutputStream(connect.getOutputStream());
		i = new ObjectInputStream(connect.getInputStream());
		o.writeObject(packet);
		o.flush();
		boolean answer =  ((Packet) i.readObject()).getAvailabilityResponse();
		o.close();
		i.close();
		connect.close();
		return answer;
	}
	
	private String[] getOtherCompanyNames(){
		String[] companies = new String[2];
		if (this.companyName.equals("FINNAIR")){
			companies[0]="KLM";
			companies[1]="LUFTHANSA";
		}else if(this.companyName.equals("KLM")){
			companies[0]= "LUFTHANSA";
			companies[1] = "FINNAIR";
		}else if(this.companyName.equals("LUFTHANSA")){
			companies[0]= "FINNAIR";
			companies[1]= "KLM";
		}
		     
		return companies;
	}
	
	
	private Object[] bookWithOtherCarrier(String firstName,String secondName, Date dateOfFlight,String destination)  {
		//Time timeOfFlight=null;
		String[] companies = this.getOtherCompanyNames();
		//String refNumber="";
			//if(this.isAvailabilityOn(dateOfFlight, timeOfFlight, destination, companies[0])){
				//this.display("Booking Flight with "+ companies[0]);
				//refNumber=this.attemptExternalBooking(firstName, secondName, dateOfFlight, timeOfFlight, destination, companies[0]);
				//return new Pair<String,String>(refNumber,companies[0]);
			//}else if(this.isAvailabilityOn(dateOfFlight, timeOfFlight, destination, companies[1])){
				//this.display("Booking Flight with " + companies[1]);
				//refNumber=this.attemptExternalBooking(firstName, secondName, dateOfFlight, timeOfFlight, destination, companies[1]);
				//return new Pair<String,String>(refNumber,companies[1]);
			//}
			
		Object[] obs = new Object[3];
		Pair<String, Time> answer;
		//try {
			answer = this.attemptExternalBooking(firstName, secondName, dateOfFlight, destination, companies[0]);
			if(!answer.getFirstElement().equals("CannotBook")){
				obs[0] = answer.getFirstElement();
				obs[1] = answer.getSecondElement();
				obs[2] = companies[0];
				System.out.println("Ellen Cummins is a bonehead");
				System.out.println(obs[0]);
				System.out.println(obs[1]);
				System.out.println(obs[2]);
				System.out.println("Ellen Cummins is a bonehead");
				return  obs;
			}else{
				answer = this.attemptExternalBooking(firstName, secondName, dateOfFlight, destination, companies[1]);
				if(!(answer.getFirstElement().equals("CannotBook"))){
					obs[0] = answer.getFirstElement();
					obs[1] = answer.getSecondElement();
					obs[2] = companies[1];
					System.out.println(obs[0]);
					System.out.println(obs[1]);
					System.out.println(obs[2]);
					return  obs;
				}
			}
				
		
		return null;
		}


		private void initializeHelicopters(){
			Vector<Object[]> arr1 = this.database.getHelicopterPorts();
			Vector<Object[]> arr2 = this.database.getHelicopterTimes();
			
			System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
			for(Object[] obs: arr1){
				System.out.println(obs[0]);
				System.out.println(obs[1]);
				System.out.println(obs[2]);
			}
			for(Object[] obs:arr2){
				System.out.println(obs[0]);
				System.out.println(obs[1]);
				System.out.println(obs[2]);
			}
			System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
			System.out.println(this.database.getNumberOfFlights());
			
			Vector<Pair<Integer,Packet>> packVector = new Vector<Pair<Integer,Packet>>();
			Packet thePacket; 
			int heliId= -1;
			int heliPort=-1;
			Time townDeparture=null;
			Time campDeparture = null;
			for(int i = 0  ; i < arr1.size();i++){
				heliId=(Integer) arr1.elementAt(i)[0];
				heliPort =(Integer) arr1.elementAt(i)[2];
				townDeparture =(Time) arr2.elementAt(i)[1];
				campDeparture = (Time) arr2.elementAt(i)[2];
				thePacket = new Packet(heliId,townDeparture,campDeparture,this.otherCompanys);
				Pair<Integer,Packet> destPackPair = new Pair<Integer,Packet>(heliPort,thePacket);
				packVector.add(destPackPair);
			}
			
			for(Pair<Integer,Packet> pair:packVector){
				int dest = pair.getFirstElement();
				Packet p = pair.getSecondElement();
				
				try{
					Socket socket = new Socket("127.0.0.1",dest+1);
					ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
					output.writeObject(p);
					output.flush();
					output.close();
					socket.close();
				}catch (Exception e){
					System.err.println("Could not find a helicopter to initialize");
					e.printStackTrace();
				}
			


			}
}

		private Time getHelicopterCompanyTime(){
			this.currentTime=  new Time(Integer.toString(this.clock.getHours()),Integer.toString(this.clock.getMinutes()));
			return this.currentTime;
		}
		
		private Date getHelicopterCompanyDate(){
			this.currentDate =  new Date(Integer.toString(this.clock.getDay()),Integer.toString(this.clock.getMonth()),Integer.toString(this.clock.getYear()));
			return this.currentDate;
		}
		


		





		private Vector<Pair<Integer,Time>> getFlightsMatchingRequest(Date date,String destination){
			Vector<Object[]> vecs = this.database.getFlightsOnADay(date);
			Vector<Pair<Integer,Time>> result= new Vector<Pair<Integer,Time>>();
			Pair<Integer,Time> pair;
			for(Object[] ob: vecs){
				if(ob[2].equals(destination)){
					
					pair = new Pair<Integer,Time>((Integer) ob[0], (Time)ob[1]);
					result.add(pair);
				}
			}
			return result;
		}


	














}
