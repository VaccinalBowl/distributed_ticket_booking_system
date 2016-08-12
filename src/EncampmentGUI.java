

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.io.*;
import java.net.*;
import java.util.Vector;
public class EncampmentGUI extends JFrame implements ActionListener{


	/**
	 * 
	 */
	
	private Thread serverThread;
	private CampNodeServer server;
	private Vector<Pair<String,Integer>> helicoptersToConnectTo;
	private String myIp; 
	private int myPort;

	@SuppressWarnings("unused")
	private  Vector<Pair<String,Integer>> portsOfArrivingHelicopters ;
	
	private Object[] addressPort = new Object[2];
	private static final long serialVersionUID = -2673922109257304741L;
	private JLabel label1,label2,label3;
	private JTextField firstname,surname,bookingReferenceToCancel;
	private JButton bookButton,cancelBookingButton;
	private JTextArea displayArea;
	private JPanel panel1,panel2;
	private JMenuBar bar;
	private JTabbedPane tabbedPane;
	private JMenu fileMenu;
	private JMenuItem exitItem; 
	private JComboBox dayComboBox,monthComboBox,yearComboBox,directionComboBox,airlineCompanyCombo;
	private String[] days={"Day","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
	private String[] months={"Month","January","February","March","April","May","June","July","August","September","October","November","December"};
	private String[] years ={"Year","2010","2011","2012","2013","2014","2015","2016","2017","2018","2019","2020"};
	private String[] directions ={"Going To","TOWN","CAMP"};
	private String[] airlines = {"Cancel with","FINNAIR","KLM","LUFTHANSA"};
	public EncampmentGUI(Vector<Pair<String,Integer>> vec,String ip,int port){
		this.setupGUI();
		this.myPort = port;
		this.myIp = ip;
		this.addressPort[0]=ip;
		this.addressPort[1]=port;
		this.helicoptersToConnectTo = vec;
		this.server = new CampNodeServer(this.myPort,20);
		this.serverThread = new Thread(this.server);
		this.serverThread.start();
		
	}

	private void setupGUI() {
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
		this.airlineCompanyCombo = new JComboBox(this.airlines);
		this.panel2 = new JPanel();
		this.panel2.setBackground(Color.RED);
		this.panel2.add(label3);
		this.panel2.add(this.bookingReferenceToCancel);
		this.panel2.add(this.airlineCompanyCombo);
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

	@Override
	public void actionPerformed(ActionEvent event) {
		Pair<String,String> namePair;
		boolean success = false;
		
		Pair<Packet,Object[]> pairToSendToChopper; 
		ObjectOutputStream output;
		ObjectInputStream input;
		Packet packet; 
		Date date ;
		Time time;
		boolean canPrintErrorMessage = true; 
		Socket connectionToChopper = null;
		 if (event.getSource()==this.bookButton){
				
				if(this.areDatesValid()){
					namePair = this.processNames(this.firstname.getText(),this.surname.getText());
					if(!namePair.isNull()){
					
						
					
						//while(!success){
							
													//needs to be changed again;;;;;;
								
								for(Pair<String,Integer> flights:this.helicoptersToConnectTo){
									try{
									connectionToChopper = new Socket(flights.getFirstElement(),flights.getSecondElement());
									success=true;
									}catch(Exception e){
										e.printStackTrace();
										
									}
									if(success)
										break;
								}
								if(success){
								try{
								output  = new ObjectOutputStream(connectionToChopper.getOutputStream());
								input = new ObjectInputStream(connectionToChopper.getInputStream());
								String monthAsNumber = HelicopterCompany.getMonthAsNumber((String)this.monthComboBox.getSelectedItem());
								date = new Date((String)this.dayComboBox.getSelectedItem(),monthAsNumber,(String) this.yearComboBox.getSelectedItem());
								time = new Time("15","00");
								if(this.airlineCompanyCombo.getSelectedItem().toString().equals("Cancel with")){
									packet = new Packet(this.firstname.getText(),this.surname.getText(),date,time,(String)this.directionComboBox.getSelectedItem());
								}else{
									packet = new Packet(this.bookingReferenceToCancel.getText());
								}
								pairToSendToChopper = new Pair<Packet,Object[]>(packet,this.addressPort);
								output.writeObject(pairToSendToChopper);
								output.flush();
								System.out.println(packet.toString()+ "has been loaded to chopper");
								output.close();
								input.close();
								connectionToChopper.close();
								} catch (IOException e) {
									if(canPrintErrorMessage)
									System.err.println("Waiting until helicopter lands");
								
									canPrintErrorMessage = false;
								}
								}else{
									System.out.println("No helicopters found");
								}
								
								
								
								//success = true;
							
						 
					
					
							//}
					
					
					
					
					
						}
					
					
					}
				
				
				
			 }/*else if (event.getSource()==this.cancelBookingButton){
					System.out.println("Registered");
					String cancelationReferenceNumber = this.bookingReferenceToCancel.getText();
					String airline = (String) this.airlineCompanyCombo.getSelectedItem();	
					
					
						try {
							Socket cancelConnectionToChopper = new Socket("127.0.0.1",55000);
							ObjectOutputStream outputs = new ObjectOutputStream(cancelConnectionToChopper.getOutputStream());
							//ObjectInputStream inputs = new ObjectInputStream(cancelConnectionToChopper.getInputStream());
							
							Packet toCancel = new Packet(cancelationReferenceNumber);//,airline,false);
							//System.out.println(toCancel.toString());
							Pair<Packet,Integer> nextPair;
							nextPair = new Pair<Packet,Integer>(toCancel,myPort);
							outputs.writeObject(nextPair);
							outputs.flush();
							outputs.close();
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				}*/
		
	}
	private boolean isLeapYear(int year){
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
	
	
	
	


	public static void main(String args[]){
		String myIp = "127.0.0.1";
		int myPort = 60000;
		String ipFinnair="127.0.0.1";
		int portFinnair = 55000;
		String ipKlm="127.0.0.1";
		int portKlm	=56000;
		String ipLufthansa ="127.0.0.1";
		int portLufthansa = 57000;
		Vector<Pair<String,Integer>> vec = new Vector<Pair<String,Integer>>();
		vec.add(new Pair<String,Integer>(ipFinnair,portFinnair));
		vec.add(new Pair<String,Integer>(ipKlm,portKlm));
		vec.add(new Pair<String,Integer>(ipLufthansa,portLufthansa));
		
			EncampmentGUI egui = new EncampmentGUI(vec,myIp,myPort);
	
	}


	



}
