import java.util.Vector;


public class KlmNode  {
	

	public static void main(String[] args){
		String ipFinnair="127.0.0.1";
		int portFinnair = 6000;
		String ipKlm="127.0.0.1";
		int portKlm	=6001;
		String ipLufthansa ="127.0.0.1";
		int portLufthansa = 6002;
		Vector<Pair<String,Integer>> vec = new Vector<Pair<String,Integer>>();
		
		vec.add(new Pair<String,Integer>(ipKlm,portKlm));
		vec.add(new Pair<String,Integer>(ipLufthansa,portLufthansa));
		vec.add(new Pair<String,Integer>(ipFinnair,portFinnair));
		HelicopterCompany klm = new HelicopterCompany("KLM","127.0.0.1",6001,vec);
		System.out.println(klm.getCompanyName()+ " is up and running");
	}


}
