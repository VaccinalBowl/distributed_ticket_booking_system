import java.io.Serializable;





public class Date implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String day;
	private String month;
	private String year;
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public Date(String day, String month, String year) {
		super();
		this.day = day;
		this.month = month;
		this.year = year;
	}
	@Override
	public String toString() {
		return this.year+"-"+this.month+"-"+this.day;
	}
	public  Date getNextDay() {
		
		int d = Integer.parseInt(this.day);
		int m = Integer.parseInt(this.month);
		int y = Integer.parseInt(this.year);
		
		int daysInMonth=-1;
		switch(m){
		 case 2:
			 if(HelicopterCompany.isLeapYear(y)){
				daysInMonth=29;
			 }else{
				daysInMonth=28;
			 }
			 break;
		 case 4:
			 daysInMonth=30;
			 break;
			 
		 case 6:
			 daysInMonth=30;
			 break;
		 case 9:
			 daysInMonth = 30;
			 break;
		 case 11:
			 daysInMonth = 30;
			 break;
		 default:
			  daysInMonth = 31;
			  break;
		}
		
		
		
		
		boolean monthShouldRoll = false,yearShouldRoll=false;
		if(d<daysInMonth){
			d++;
		}else{
			d=1;
			monthShouldRoll = true;
		}
		if(monthShouldRoll){
			if((m<12)){
				m++;
			}else{
			m=1;
			yearShouldRoll = true;
			}
		}
		if(yearShouldRoll){
			y++;
		}
		
		
		return new Date(Integer.toString(d),Integer.toString(m),Integer.toString(y));
	}
	

	public boolean isEqual(Date otherDate){
		if((this.day.equals(otherDate.day))&&(this.month.equals(otherDate.month))&&(this.year.equals(otherDate.year))){
			return true;
		}else{
			return false;
		}
	}

	
		 
	


}
