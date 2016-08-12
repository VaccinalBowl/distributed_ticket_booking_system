import java.util.Vector;


public class FinnairNode {


	public static void main(String[] args){
		
		String ipFinnair="127.0.0.1";
		int portFinnair = 6000;
		String ipKlm="127.0.0.1";
		int portKlm	=6001;
		String ipLufthansa ="127.0.0.1";
		int portLufthansa = 6002;
		Vector<Pair<String,Integer>> vec = new Vector<Pair<String,Integer>>();
		vec.add(new Pair<String,Integer>(ipFinnair,portFinnair));
		vec.add(new Pair<String,Integer>(ipKlm,portKlm));
		vec.add(new Pair<String,Integer>(ipLufthansa,portLufthansa));
		HelicopterCompany finnair = new HelicopterCompany("FINNAIR","127.0.0.1",6000,vec);
		System.out.println(finnair.getCompanyName()+ " is up and running");
	
		//HelicopterCompanyServer helicompanyServer = new HelicopterCompanyServer("FINNAIR",6000);
		//System.out.println(helicompanyServer.getCompanyName()+ " is up and running");
	
	
	
	
	
	}



}
