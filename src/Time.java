import java.io.Serializable;




public class Time implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -545476127701552985L;
	private String hours;
	private String minutes;
	public String getHours() {
		return hours;
	}
	public void setHours(String hours) {
		this.hours = hours;
	}
	public String getMinutes() {
		return minutes;
	}
	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}
	public Time(String hours, String minutes) {
		super();
		this.hours = hours;
		this.minutes = minutes;
	}
	
	
	public boolean isEqual(Time otherTime){
		if((Integer.parseInt(this.hours)==Integer.parseInt(otherTime.hours))&&(Integer.parseInt(this.minutes)== Integer.parseInt(otherTime.minutes))){
			return true;
		}else{
			return false;   
		}
	}

	public boolean isBefore(Time otherTime){
		if(Integer.parseInt(this.hours)<Integer.parseInt(otherTime.hours)){
			return true;
		}else if(Integer.parseInt(this.hours)>Integer.parseInt(otherTime.hours)){
			return false; 
		}else if(Integer.parseInt(this.minutes)<Integer.parseInt(otherTime.minutes)){
			return true;
		}else{
			return false; 
		}
	
	}
	
	
	public boolean isAfter(Time otherTime){
		if((!this.isBefore(otherTime))&&(!this.isEqual(otherTime))){
			return true;
		}else{
			return false; 
		}
	}
	
	
	public boolean isStrictlyBetween(Time lowerTime, Time higherTime){
		if((this.isAfter(lowerTime))&&(this.isBefore(higherTime))){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isBetween(Time lowerTime, Time higherTime){
		if(((this.isAfter(lowerTime))||(this.isEqual(lowerTime)))&&((this.isEqual(higherTime))||(this.isBefore(higherTime)))){
			return true;
		}else {
			return false; 
		}
	}
	
	
	public String toString(){
		return this.hours+":"+this.minutes+":"+"00";
	}

	public static void main(String[] args){
		Time t1 = new Time("15","02");
		Time t2 = new Time("15","01");
		if(t1.isAfter(t2)){
			System.out.println("yes");
		}else{
			System.out.println("No");
		}
		
	}

	
}
