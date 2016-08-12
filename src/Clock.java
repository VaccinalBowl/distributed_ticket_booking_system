import java.text.*;
import java.util.Calendar;

public class Clock implements Runnable {

	private int hours;
	private int minutes;
	private int seconds;
	private int day;
	private int month;
	private int year;
	private Date currentDate;
	private Time currentTime;
	private boolean customTime; 
	
	public Time getTime(){
		this.currentTime= new Time(Integer.toString(this.hours),Integer.toString(this.minutes));
		return this.currentTime;
	}
	
	public Date getDate(){
		this.currentDate =  new Date(Integer.toString(this.day),Integer.toString(this.month),Integer.toString(this.year));
		return this.currentDate;
	}
	
	
	
	
	public void tick(){
		if(this.incrementSecond()){
			if(this.incrementMinute()){
				this.incrementHour();
			}
		}
	}
	
	public void customTick(){
		this.calibrate();
		if(this.incrementCustomSecond()){
			if(this.incrementCustomMinute()){
				if(this.incrementHour()){
					this.incrementDate();
				}
			}
		}
	}
	
	private void calibrate(){

		do{
			
			if(this.minutes%10==0)
				break;
			
			this.minutes++;
			
		}while(this.minutes%10!=0);
		if(this.minutes==60){
			this.minutes=0; 
			this.incrementHour();
		}
		
		
	}
	
	public void incrementDate(){
		Date newDate = new Date(Integer.toString(this.day),Integer.toString(this.month),Integer.toString(this.year));
		newDate = newDate.getNextDay();
		this.day = Integer.parseInt( newDate.getDay());
		this.month = Integer.parseInt(newDate.getMonth());
		this.year = Integer.parseInt(newDate.getYear());
	}
		
	
	
	
	
	public boolean incrementCustomSecond(){	
		return true; 
	}
	
	
	
	
	private boolean incrementCustomMinute(){
		int difference; 
		this.minutes+=10;
		if(this.minutes>=60){
			difference = this.minutes-60;
			this.minutes=difference;
			return true;
		}return false;
	}
	
	
		
	
	
	
	
	


	private boolean incrementSecond(){  
		if(this.seconds!=59){
			this.seconds++;
			return false;
		}else{
			this.seconds = 0 ;
			return true;
		}
	}
	
	private boolean  incrementMinute(){
		if(this.minutes!=59){
			this.minutes++;
			return false;
		}else{
			this.minutes=0;
			return true;
		}
	}
	  
	private boolean incrementHour(){
		if(this.hours!=23){
			this.hours++;
			return false;
		}else{
			this.hours = 0;
			return true; 
		}
	}
	
	
	
	public void run(){  
		if(!this.customTime){
		while(true){
			this.tick();
			try{
			Thread.sleep(1000);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		}else{
			while(true){
				this.customTick();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
			}
		}
	
	}
	  
	
	public Clock(boolean customTime){
		String now   = this.now("yyyy/MM/dd HH:mm:ss");
		//System.out.println(now);
		this.hours 	 = Integer.parseInt(now.substring(11, 13));
		this.minutes = Integer.parseInt(now.substring(14, 16));
		this.seconds = Integer.parseInt(now.substring(17,19));
		this.day 	 = Integer.parseInt(now.substring(8,10));
		this.month   = Integer.parseInt(now.substring(5, 7));
		this.year	 = Integer.parseInt(now.substring(0,4));
		this.customTime = customTime;
		
	}

	








	  private String now(String dFormat) {
		    Calendar cal = Calendar.getInstance();
		    SimpleDateFormat sdFormat = new SimpleDateFormat(dFormat);
		    
		    return sdFormat.format(cal.getTime());

	  }

	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
	  


	public static void main(String[] args){
		//Clock clock = new Clock(false);
		//clock.calibrate(61);
	}




	

















	
}
