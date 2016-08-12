
import java.sql.*;
import java.util.Random;
import java.util.Vector;


import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;

public class Database {
	
	private final String DRIVER = "com.mysql.jdbc.Driver";
	private final String DATABASEURL;// "jdbc:mysql://localhost/FINNAIR";
	private final String PASSWORD;
	private final String DATABASE_NAME;
	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	private int numberOfFlights;
	private int numberOfBookings;
	private ResultSetMetaData metaData;
	private Vector<Integer> helicopterIds;
	
	private Date lastDateStoredInDb;
	private int stepForRolling=-1;
	
	
	public int getNumberOfBookings() {
		return numberOfBookings;
	}

	public int getNumberOfFlights() {
		return numberOfFlights;
	}
	
 
	
	
	
	public Database( final String URL,final String PWORD){
		this.DATABASEURL= URL;//"jdbc:mysql://localhost/FINNAIR";
		this.PASSWORD=PWORD;
		this.DATABASE_NAME=PWORD;
		this.connection=null;
		this.statement= null;
		this.resultSet=null;
		this.helicopterIds = new Vector<Integer>();
		
		try{
			Class.forName(DRIVER);
			connection=DriverManager.getConnection(this.DATABASEURL,this.PASSWORD,this.PASSWORD);//username and password are the same
			statement= connection.createStatement();
			//this.numberOfFlights=this.countRowsInTable("FLIGHTS");			
			this.numberOfBookings=this.countRowsInTable("BOOKINGS");
			this.numberOfFlights = this.countRowsInTable("HELICOPTERPORTS");
			//System.out.println(this.numberOfBookings);
			this.initialiseHelicoptersInDatabaseVector();
			this.lastDateStoredInDb=this.getLastDateStoredInFlightTable();
			System.out.println(this.lastDateStoredInDb);
		}catch(SQLException sqlException){
			sqlException.printStackTrace();
		}catch(ClassNotFoundException classNotFound){
			classNotFound.printStackTrace();
		}
	}
	
	//Returns true if flight booked successfully
	//obsolete
	public synchronized String makeReservation(String firstname, String surname, Date date, Time time,String destination) throws SQLException, ClassNotFoundException{
		
		String sqlDateTime = this.generateSqlDateTime(date, time);
		int[] flightAvailability = this.getAvailabilityOnFlight(sqlDateTime, destination);
		String refN;
		if(flightAvailability[1]>0){
			flightAvailability[1]--;
			String sql;
			this.statement.executeUpdate("UPDATE FLIGHTS SET seatsRemaining="+Integer.toString(flightAvailability[1])+" WHERE flight_Id="+Integer.toString(flightAvailability[0]));
			refN=this.generateUniqueReferenceNumber();
			this.generateUniqueReferenceNumber();
			sql="INSERT INTO BOOKINGS (reference_Id,flightNumber,FirstName,Surname) VALUES ('"+refN+"',"+Integer.toString(flightAvailability[0])+",'"+firstname+"','"+surname+"')";
			//refN=this.getReferenceNumber();
			this.statement.executeUpdate(sql);
			sql = "INSERT INTO REFERENCENUMBERS (reference_Id) VALUES ('"+refN+"')";
			this.statement.executeUpdate(sql);
			
			
			
			notifyAll();
			return refN;
		}else{
		// throw new FlightFullException();
		notifyAll(); 
		return "FlightFull";
		}
	}
	
	//public synchronized String makeReservation(int helicopterNumber,String firstname, String surname, Date date, Time time,String destination) throws SQLException, ClassNotFoundException{
	public synchronized Pair<String,Time> makeReservation(String firstname, String surname, Date date,String destination) throws SQLException, ClassNotFoundException{	
		Pair<String,Time> returnDuo;
		Vector<Pair<Integer,Time>> possibleFlightsAndTimes = this.getFlightsSuitingQuery(date, destination);
	    Pair<Integer,Time> pair=null;
	    boolean foundSpace = false;
	    	for(Pair<Integer,Time> tmp:possibleFlightsAndTimes){
	    		System.out.println("Bang Bang Bang");
	    		if(this.hasAvailability((Integer)tmp.getFirstElement(), date, (Time) tmp.getSecondElement(), destination)){
	    			pair =tmp;
	    			foundSpace=true;
	    			break;
	    		}
	    	}
	    System.out.println(foundSpace);
		
			if(foundSpace){
				int flight_Id = pair.getFirstElement();
				Date date1=date ;
				Time time2=pair.getSecondElement(); 
				String sqlDateTime = this.generateSqlDateTime(date1, time2);
				int flightAvailability = this.getAvailabilityOnFlight(flight_Id,sqlDateTime, destination);
				System.out.println(flightAvailability);
				String refN;
				if(flightAvailability>0){
					flightAvailability--;
					String sql;
					this.statement.executeUpdate("UPDATE FLIGHTS SET seatsRemaining="+Integer.toString(flightAvailability)+" WHERE flight_Id="+Integer.toString(flight_Id)+" AND dateTime='"+sqlDateTime+"' AND direction='"+destination+"'");
					refN=this.generateUniqueReferenceNumber();
					this.generateUniqueReferenceNumber();
					sql="INSERT INTO BOOKINGS (reference_Id,flightNumber,FirstName,Surname,dateTime,direction) VALUES ('"+refN+"',"+Integer.toString(flight_Id)+",'"+firstname+"','"+surname+"',\""+sqlDateTime+"\",'"+destination+"')";
					System.out.println(sql)
		;			//refN=this.getReferenceNumber();
					this.statement.executeUpdate(sql);
					sql = "INSERT INTO REFERENCENUMBERS (reference_Id) VALUES ('"+refN+"')";
					this.statement.executeUpdate(sql);
					notifyAll();
					return new Pair<String,Time>(refN,time2);
				}else{
					notifyAll();
					return new Pair<String,Time>("FlightsFull",null);
				}
				
			}else{
				notifyAll();
				return new Pair<String,Time>("FlightsFull",null);
			}
		
		/*String sqlDateTime = this.generateSqlDateTime(date, time);
		int flightAvailability = this.getAvailabilityOnFlight(helicopterNumber,sqlDateTime, destination);
		System.out.println(flightAvailability);
		String refN;
		if(flightAvailability>0){
			flightAvailability--;
			String sql;
			this.statement.executeUpdate("UPDATE FLIGHTS SET seatsRemaining="+Integer.toString(flightAvailability)+" WHERE flight_Id="+Integer.toString(helicopterNumber)+" AND dateTime='"+sqlDateTime+"' AND direction='"+destination+"'");
			refN=this.generateUniqueReferenceNumber();
			this.generateUniqueReferenceNumber();
			sql="INSERT INTO BOOKINGS (reference_Id,flightNumber,FirstName,Surname,dateTime,direction) VALUES ('"+refN+"',"+Integer.toString(helicopterNumber)+",'"+firstname+"','"+surname+"',\""+sqlDateTime+"\",'"+destination+"')";
			System.out.println(sql)
;			//refN=this.getReferenceNumber();
			this.statement.executeUpdate(sql);
			sql = "INSERT INTO REFERENCENUMBERS (reference_Id) VALUES ('"+refN+"')";
			this.statement.executeUpdate(sql);
			
			
			
			notifyAll();
			return refN;
		}else{
		// throw new FlightFullException();
		 return "FlightFull";
		}*/
	}
	
	
	
	
	/*public synchronized boolean cancelReservation(String number) throws SQLException, FlightEmptyException{
		String sql="DELETE FROM REFERENCENUMBERS WHERE reference_Id='"+number+"'";
		this.statement.executeUpdate(sql);
		int flightIdToAddSeatTo = this.getValueInColumn("BOOKINGS", "flightNumber", "reference_Id", number);
		sql = "DELETE FROM BOOKINGS WHERE reference_Id='"+number+"'";
		this.statement.executeUpdate(sql);
		this.vacateSeatOnFlight(flightIdToAddSeatTo);
		notifyAll();
		return true;
	}*/
	
	public synchronized boolean cancelReservation(String number) throws SQLException, FlightEmptyException {
		String sql="DELETE FROM REFERENCENUMBERS WHERE reference_Id='"+number+"'";
		this.statement.executeUpdate(sql);
		sql="SELECT flightNumber,dateTime,direction FROM BOOKINGS WHERE reference_Id='"+number+"'";
		this.resultSet = this.statement.executeQuery(sql);
		this.metaData = this.resultSet.getMetaData();
		this.resultSet.next();
		int flight_Id;
		try{
		flight_Id= this.resultSet.getInt(1);
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		String destination = this.resultSet.getString(3);
		String dateTime = this.resultSet.getString(2);
		
		
		sql = "DELETE FROM BOOKINGS WHERE reference_Id='"+number+"'";
		this.statement.executeUpdate(sql);
		this.vacateSeatOnFlight(flight_Id,dateTime,destination);
		notifyAll();
		return true;
	}
	
	
	
	
	
	
	public void printTable(String tableName) throws SQLException {
		
		System.out.println("##########################################################");
		try{
		this.resultSet = this.statement.executeQuery("SELECT * FROM "+tableName );
		}catch(MySQLSyntaxErrorException exception){
			exception.printStackTrace();
			System.err.println("Table did not exist. Error Handled and Program  will continue");
			return;
		}
     // process query results
     this.metaData = this.resultSet.getMetaData();
     int numberOfColumns = this.metaData.getColumnCount();     
     System.out.println( tableName+" Table of "+this.DATABASE_NAME+" database\n" );
     
     for ( int i = 1; i <= numberOfColumns; i++ )
        System.out.printf( "%-8s\t", metaData.getColumnName( i ) );
     System.out.println();
     
     while ( resultSet.next() ) 
     {
        for ( int i = 1; i <= numberOfColumns; i++ )
           System.out.printf( "%-8s\t", resultSet.getObject( i ) );
        System.out.println();
     } // end while
     System.out.println("##########################################################");
     
     
	}
	
	
	
	
	private int countRowsInTable(String tablename) throws SQLException{
		resultSet= statement.executeQuery("SELECT COUNT(*) FROM "+tablename);
		this.metaData = resultSet.getMetaData();
		resultSet.next();
		return resultSet.getInt(1);
	}
	private String generateSqlDateTime(Date d,Time t){
		return d.toString()+" "+t.toString();
	}
	//obsolete
	private int[] getAvailabilityOnFlight(String sqlDateTime,String destination) throws SQLException{
		resultSet = this.statement.executeQuery("SELECT flight_Id,seatsRemaining FROM FLIGHTS WHERE dateTime='"+sqlDateTime+"' AND direction='"+destination+"'");
		this.metaData=resultSet.getMetaData();
		resultSet.next();
		int[] flightIdSeats = new int[2];
		flightIdSeats[0]=this.resultSet.getInt(1);
		flightIdSeats[1]=this.resultSet.getInt(2);
		return flightIdSeats;
	}
	
	private int getAvailabilityOnFlight(int helicopterNumber,String sqlDateTime,String destination) throws SQLException{
		resultSet = this.statement.executeQuery("SELECT seatsRemaining FROM FLIGHTS WHERE flight_Id="+helicopterNumber+" AND dateTime='"+sqlDateTime+"' AND direction='"+destination+"'");
		this.metaData=resultSet.getMetaData();
		resultSet.next();
		return this. resultSet.getInt(1);
	}
	
	
	
	
	
	
	private void vacateSeatOnFlight(int i ,String dTime, String dest) throws SQLException, FlightEmptyException{
		
		int j;
		this.resultSet = this.statement.executeQuery("SELECT seatsRemaining FROM FLIGHTS WHERE flight_Id="+Integer.toString(i)+" AND dateTime='"+dTime+"' AND direction='"+dest+"'");
		this.metaData = resultSet.getMetaData();
		this.resultSet.next();
		j=this.resultSet.getInt(1);
		if(j<5){
			j++;
			this.statement.executeUpdate("UPDATE FLIGHTS SET seatsRemaining="+Integer.toString(j)+" WHERE flight_Id="+Integer.toString(i)+" AND dateTime='"+dTime+"' AND direction='"+dest+"'");
		}else{
			throw new FlightEmptyException();
		}
	}
	private String generateUniqueReferenceNumber() throws ClassNotFoundException, SQLException{
		String referenceNumber="";
		int iterations=0;
		Random generator = new Random();
		int random; 
			
		while(!this.isUniqueReferenceNumber(referenceNumber)){
		referenceNumber="";
		do{
				random =generator.nextInt(25);
				random=random+1;
				switch (random){
			
					case 1:
						referenceNumber+= "A";
						break;
				case 2:
					referenceNumber+= "B";
					break;
			
				case 3:
					referenceNumber+="C";
					break;
				case 4:
					referenceNumber+= "D";
					break;
				case 5:
					referenceNumber+= "E";
					break;
			
				case 6:
					referenceNumber+="F";
					break;
				case 7:
					referenceNumber+= "G";
					break;
				case 8:
					referenceNumber+= "H";
					break;
			
				case 9:
					referenceNumber+="I";
					break;
			
				case 10:
					referenceNumber+= "J";
					break;
				case 11:
					referenceNumber+= "K";
					break;
			
				case 12:
					referenceNumber+="L";
					break;
				case 13:
					referenceNumber+= "M";
					break;
				case 14:
					referenceNumber+= "N";
					break;
			
				case 15:
					referenceNumber+="O";
					break;
				case 16:
					referenceNumber+= "P";
					break;
				case 17:
					referenceNumber+= "Q";
					break;
			
				case 18:
					referenceNumber+="R";
					break;
			
				case 19:
					referenceNumber+="S";
					break;
				
				case 20:
					referenceNumber+= "T";
					break;
				case 21:
					referenceNumber+= "U";
					break;
			
				case 22:
					referenceNumber+="V";
					break;
				case 23:
					referenceNumber+= "W";
					break;
				case 24:
					referenceNumber+= "X";
					break;
			
				case 25:
					referenceNumber+="Y";
					break;
			
				case 26:
					referenceNumber+="Z";
					break;
			
			
			
			}
		
		iterations++;
		
		
		}while((iterations<10));
			
		}
	
		return referenceNumber;
	
	
	}
	private boolean isUniqueReferenceNumber(String referenceNumber) throws ClassNotFoundException, SQLException {
 		boolean answer = false;
 		if(referenceNumber.equals(""))
 			return answer;
 		Connection conn;
 		Statement statem ;
 		Class.forName(DRIVER);
		conn=DriverManager.getConnection(this.DATABASEURL,this.PASSWORD,this.PASSWORD);//username and password are the same
		statem= conn.createStatement();
		ResultSet rs = statem.executeQuery("SELECT reference_Id FROM REFERENCENUMBERS WHERE reference_Id='"+referenceNumber+"'");
		
		if(!rs.next()){
			answer=true;
			
		}
 	
 	
	
 	
 		return answer;
 	}
	
	
	//TO BE UPDATED//
	public boolean hasAvailability(int flightNumber, Date date, Time time, String destination) throws SQLException{
		String sqldatetime = this.generateSqlDateTime(date, time);
		this.resultSet = this.statement.executeQuery("SELECT seatsRemaining FROM FLIGHTS WHERE dateTime='"+sqldatetime+"' AND direction='"+destination+"' AND flight_Id="+flightNumber);
		this.metaData= resultSet.getMetaData();
		this.resultSet.next();
		if(this.resultSet.getInt(1)>0){
			return true;
		}else{
		return false;
		}
	}

	
	
	public int numberOfHelicopters(){
		return this.helicopterIds.size();
	}
	
	
	private void initialiseHelicoptersInDatabaseVector(){
		String sql = "SELECT DISTINCT flight_Id FROM FLIGHTS";
		try {
			this.resultSet = this.statement.executeQuery(sql);
			this.metaData = this.resultSet.getMetaData();
			while(this.resultSet.next()){
				this.helicopterIds.add(this.resultSet.getInt(1));
			}
		
		
		
		
		
		
		
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Vector<Integer> getHelicopterIds(){
		return this.helicopterIds;
	}
	
	
	
	public  Vector<Object[]> getHelicopterPorts(){ 
		String sql ;
		Object[] tmp = new Object[3];
		
		Vector<Object[]>	idNums = new Vector<Object[]>();
		sql = "SELECT * FROM HELICOPTERPORTS";
		try {
			this.resultSet= this.statement.executeQuery(sql);
			this.metaData = this.resultSet.getMetaData();
			while(this.resultSet.next()){
				for(int i = 0 ; i < 3 ; i++)
					tmp[i] = resultSet.getObject(i+1);
			
			
				idNums.add(tmp);
			}
		
		
		
		
		
		
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
		return idNums;
	}
	
	public Vector<Object[]> getHelicopterTimes(){
		String sql ;
		Object[] tmp;
		
		Vector<Object[]>	idNTimes = new Vector<Object[]>();
		try{
		for(int i = 0 ; i < this.getNumberOfFlights();i++){
			tmp = new Object[3];
			sql = "SELECT dateTime FROM FLIGHTS WHERE flight_Id="+(i+1)+" AND direction='CAMP'";
			this.resultSet = this.statement.executeQuery(sql);
			this.metaData = this.resultSet.getMetaData(); 
			this.resultSet.next(); 
			Timestamp dateTime=	(Timestamp) this.resultSet.getObject(1);
			
			System.out.println(this.extractTimeFromTimeStamp(dateTime).toString());
			Time t1 = this.extractTimeFromTimeStamp(dateTime);
			tmp[0]=i+1;
			tmp[1]=t1;
			
			
			sql = "SELECT dateTime FROM FLIGHTS WHERE flight_Id="+(i+1)+" AND direction='TOWN'";
			this.resultSet = this.statement.executeQuery(sql);
			this.metaData = this.resultSet.getMetaData(); 
			this.resultSet.next(); 
			this.resultSet.getObject(1);
			dateTime = (Timestamp) this.resultSet.getObject(1);
			System.out.println(this.extractTimeFromTimeStamp(dateTime).toString());
			t1 = this.extractTimeFromTimeStamp(dateTime);
			tmp[2]=t1;
			idNTimes.add(tmp);
		}
		}catch(SQLException exception){
			exception.printStackTrace(); 
		}
		
		
		
		
		
		
		
		return idNTimes;
	}
	
	
	@SuppressWarnings("deprecation")
	private Time extractTimeFromTimeStamp(Timestamp t){
		
		
		return new Time(Integer.toString(t.getHours()),Integer.toString(t.getMinutes())); 
	}
	
	private Date getLastDateStoredInFlightTable(){
		try {
		this.resultSet=	this.statement.executeQuery("SELECT *  FROM FLIGHTS  ORDER BY dateTime DESC LIMIT 1");
		this.resultSet.getMetaData();
		this.resultSet.next();
		Timestamp timestamp = (Timestamp) this.resultSet.getObject(2);
		return this.extractDateFromTimeStamp(timestamp);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	
	
	
	private Date extractDateFromTimeStamp(Timestamp timestamp) {
		// TODO Auto-generated method stub
		
		String day,month,year;
		String timeStampString = timestamp.toString();
		System.out.println(timeStampString);
		day = timeStampString.substring(8, 10);
		month = timeStampString.substring(5,7);
		year = timeStampString.substring(0,4);
		return new Date(day,month,year);
	}

	private boolean deleteRowFromFlights(int flight,String dateTime,String direction){
		String sql = "DELETE FROM FLIGHTS WHERE flight_Id="+flight+" AND dateTime=\""+dateTime+"\" AND direction=\""+direction+"\"";
		try {
			this.statement.executeUpdate(sql);
			sql = "SELECT reference_Id FROM BOOKINGS WHERE flightNumber="+flight+" AND dateTime=\""+dateTime+"\" AND direction=\""+direction+"\"";
			this.resultSet = this.statement.executeQuery(sql);
			this.metaData = this.resultSet.getMetaData();
			
			while(this.resultSet.next()){
				sql = "DELETE FROM BOOKINGS WHERE reference_Id=\""+(String)this.resultSet.getObject(1)+"\"";
				this.statement.executeUpdate(sql);
				sql = "DELETE FROM REFERENCENUMBERS WHERE reference_Id=\""+(String)this.resultSet.getObject(1)+"\"";
				this.statement.executeUpdate(sql);
			}	
		
		
		} catch (SQLException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
		return true;  
	}
	private boolean addRowToFlights(int flight,String dateTime, String direction){
		String sql = "INSERT INTO FLIGHTS (flight_Id,dateTime,direction) VALUES ("+flight+",\""+dateTime+"\",\""+direction+"\")";
		try {
			System.out.println("Adding");
			System.out.println(flight);
			System.out.println(dateTime);
			System.out.println(direction);
			this.statement.executeUpdate(sql);
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Could not add row to flight");
			return false;
		}
		
		
		
		
		return true;
	}

	public boolean dropAndRoll(int flight,Date date, Time time,String direction){
		String dateTime = this.generateSqlDateTime(date, time);
		System.out.println(dateTime);
		boolean deleted = this.deleteRowFromFlights(flight, dateTime, direction);
		/*this will only ever be true the first time this method is called */
		if(this.stepForRolling==-1){
			this.stepForRolling = Integer.parseInt(this.lastDateStoredInDb.getYear())-Integer.parseInt(date.getYear());
		}
			//Integer step = this.stepForRolling;
		Integer newYear =  Integer.parseInt(date.getYear())+this.stepForRolling;
		Date dateInAFewYears = new Date(date.getDay(),date.getMonth(),newYear.toString());
		dateTime = this.generateSqlDateTime(dateInAFewYears, time);
		
		boolean added = this.addRowToFlights(flight, dateTime, direction);
		
		if(added&&deleted)
		 return true;
	
		return false;
	}

	public Vector<Object[]> getFlightsOnADay(Date d){
		Time tmpTime = new Time("00","00");
		String dateTimeLower = this.generateSqlDateTime(d, tmpTime);
		String dateTimeHigher= this.generateSqlDateTime(d.getNextDay(),tmpTime);
		String sql ="Select flight_Id,dateTime,direction FROM FLIGHTS WHERE ('"+dateTimeLower+"'<=dateTime AND  dateTime<'"+dateTimeHigher+"') ORDER BY dateTime";
		//Pair<Date,Time> thePair; 
		Vector<Object[]> returnVector = new Vector<Object[]>();
		Object[] tmp = new Object[3];
		try {
			this.resultSet = this.statement.executeQuery(sql);
			this.metaData = this.resultSet.getMetaData();
			
			while(this.resultSet.next()){
				Timestamp timeStamp = (Timestamp)this.resultSet.getObject(2);
				//Date newDate = this.extractDateFromTimeStamp(timeStamp);
				Time newTime = this.extractTimeFromTimeStamp(timeStamp);
				//thePair = new Pair<Date,Time>(newDate,newTime);
				//System.out.println("Argh");
				//System.out.println(newTime);
				
				
				
				tmp[1]= newTime;
				
				tmp[0]=this.resultSet.getInt(1);
				//System.out.println(tmp[0]);
				tmp[2]=this.resultSet.getObject(3);
				//System.out.println(tmp[2]);
				//System.out.println("Gaga");
				returnVector.add(tmp);
				tmp=new Object[3];
			}	
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
		return returnVector;
	}
	
	
	

	public static void main(String[] args) throws SQLException, FlightFullException, FlightEmptyException, ClassNotFoundException, InterruptedException{
		//Date d= new Date("01","12","2010");
		//Time t= new Time("06","00");
		Database db = new Database("jdbc:mysql://localhost/FINNAIR","FINNAIR");
		/*Vector<String> v = new Vector<String>();
		String[] theCrazyOHallorans = {};
		//String[] theCrazyOHallorans={"Peter","Trish","Siobhan","Aisling","Ciara"};
		for(String anOHalloran : theCrazyOHallorans){
			v.add(db.makeReservation(1,anOHalloran, "OHalloran", d, t, "CAMP"));
			if (db.hasAvailability(1,d, t, "CAMP")){
				System.out.println("available");
			}else{
				System.out.println("noavailability");
			}
		
		}
		db.cancelReservation("CRPFRDXIQI");
			/*if(!db.hasAvailability(d, t, "CAMP")){
				System.out.println("noavailable");
			}
			
		//db.makeReservation("Jonathan", "Cummins", d, t, "CAMP");
		//db.cancelReservation("EPSVLYHNIX");
		//db.cancelReservation(4);
		//db.cancelReservation(5);
		//db.cancelReservation(2);
		//db.cancelReservation(6);
		//db.printTable("REFERENCENUMBERS");
		//db.printTable("BOOKINGS");
		//db.printTable("FLIGHTS");
		//System.out.println("Made it");
		//System.out.println("Thread sleeping for 10 seconds");
		//Thread.sleep(10000);
		//for (String str : v)
			//db.cancelReservation(str);
	
		*/
	
		//for(Integer i : db.getHelicopterIds()){
			//System.out.println(i);
		//}
		
		//db.getHelicopterPorts();
		//db.getHelicopterTimes(); 
		//Time t = new Time("06","00");
		Date d = new Date("14","11","2010");
		//db.dropAndRoll(1, d, t, "CAMP");
		Vector<Object[]> vec = db.getFlightsOnADay(d);
		for(Object[] oa :vec){
			System.out.println(oa[0]);
			System.out.println(oa[1]);
			System.out.println(oa[2]);
		}
	}

	private Vector<Pair<Integer,Time>> getFlightsSuitingQuery(Date date, String destination){
		Vector<Object[]> vecs = this.getFlightsOnADay(date);
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
