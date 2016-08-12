
import java.net.*;



public class FinnairHelicopter  extends Helicopter{
	Pair<Integer,Packet>[] packets;
	Socket socket;

	
	
	public FinnairHelicopter(int loc,int lP){
		super("FINNAIR",HelicopterCompany.FINNAIR_PORT,HelicopterCompany.KLM_PORT, HelicopterCompany.LUFTHANSA_PORT,new Time("15","00"),loc,lP);
		
		
	}

	
	
	

	public static void main(String[] args){
		
		/*Date date = new Date("01","12","10");
		Time time = new Time("15","00");
		Packet p1 = new Packet("Jonathan","Cummins",date,time,"CAMP");
		Packet p2 = new Packet("Aisling","OHalloran",date,time,"CAMP");
		Packet p3 = new Packet("Sandra","Cummins",date,time,"CAMP");
		Packet p4 = new Packet("Denis","Cummins",date,time,"CAMP");
		Packet p5 = new Packet("Ciara","OHalloran",date,time,"CAMP");
		Packet p6 = new Packet("Siobhan","OHalloran",date,time,"CAMP");
		Packet p7 = new Packet("J","Cummins",date,time,"CAMP");
		Packet p8 = new Packet("A","OHalloran",date,time,"CAMP");
		Packet p9 = new Packet("S","Cummins",date,time,"CAMP");
		Packet p10 = new Packet("D","Cummins",date,time,"CAMP");
		Packet p11 = new Packet("C","OHalloran",date,time,"CAMP");
		Packet p12 = new Packet("S","OHalloran",date,time,"CAMP");
		Packet p13 = new Packet("Jonathan","C",date,time,"CAMP");
		Packet p14 = new Packet("Aisling","OH",date,time,"CAMP");
		Packet p15 = new Packet("Sandra","C",date,time,"CAMP");
		Packet p16 = new Packet("Denis","C",date,time,"CAMP");
		Packet p17 = new Packet("Ciara","O",date,time,"CAMP");
		Packet p18 = new Packet("Siobhan","O",date,time,"CAMP");
		
		
		
		
		
		
		
		Vector<Pair<Packet,Integer>> vectorPack = new Vector<Pair<Packet,Integer>>();
		vectorPack.add(new Pair<Packet,Integer>(p1,1));
		vectorPack.add(new Pair<Packet,Integer>(p2,2));
		vectorPack.add(new Pair<Packet,Integer>(p3,3));
		vectorPack.add(new Pair<Packet,Integer>(p4,4));
		vectorPack.add(new Pair<Packet,Integer>(p5,5));
		vectorPack.add(new Pair<Packet,Integer>(p6,6));
		vectorPack.add(new Pair<Packet,Integer>(p7,7));
		vectorPack.add(new Pair<Packet,Integer>(p8,8));
		vectorPack.add(new Pair<Packet,Integer>(p9,9));
		vectorPack.add(new Pair<Packet,Integer>(p10,10));
		vectorPack.add(new Pair<Packet,Integer>(p11,11));
		vectorPack.add(new Pair<Packet,Integer>(p12,12));
		vectorPack.add(new Pair<Packet,Integer>(p13,13));
		vectorPack.add(new Pair<Packet,Integer>(p14,14));
		vectorPack.add(new Pair<Packet,Integer>(p15,15));
		vectorPack.add(new Pair<Packet,Integer>(p16,16));
		
		vectorPack.add(new Pair<Packet,Integer>(p17,17));
		vectorPack.add(new Pair<Packet,Integer>(p18,18));*/
		
	
		@SuppressWarnings("unused")
		FinnairHelicopter fh = new FinnairHelicopter(-1,55000);//new helicopter in town
		//System.out.println("HEre");
		//while(true){
			
			//try {
				//Thread.sleep(10000);
			//} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			//}
			//fh.printHelicopterDateAndTime();
	
		//}
		//fh.beginOperating();
		
		//fh.printPacketsForTown();
		//fh.printPacketsForTown();
		//fh.packetsForTown = vectorPack;
		//fh.beginFlying();
		//System.out.println("Sleeping");
		//Thread.sleep(120000);
		//System.out.println("Walking");
		//fh.printPacketsForTown();
	}























}
