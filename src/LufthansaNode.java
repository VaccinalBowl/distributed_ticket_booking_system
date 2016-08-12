import java.util.Vector;


public class LufthansaNode {
	
	public static void main(String[] args){
		
		String ipFinnair="127.0.0.1";
		int portFinnair = 6000;
		String ipKlm="127.0.0.1";
		int portKlm	=6001;
		String ipLufthansa ="127.0.0.1";
		int portLufthansa = 6002;
		Vector<Pair<String,Integer>> vec = new Vector<Pair<String,Integer>>();
		
		vec.add(new Pair<String,Integer>(ipLufthansa,portLufthansa));
		vec.add(new Pair<String,Integer>(ipFinnair,portFinnair));
		vec.add(new Pair<String,Integer>(ipKlm,portKlm));
		
		
		HelicopterCompany lufthansa = new HelicopterCompany("LUFTHANSA","127.0.0.1",6002,vec);
		System.out.println(lufthansa.getCompanyName()+ " is up and running");
	}


}