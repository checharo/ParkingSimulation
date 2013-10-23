package com.parking;


import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.NoRouteException;
import java.io.IOException;
import javax.microedition.io.Connector;
import net.mypapit.java.StringTokenizer;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Sender.java
 * 
 * @author Edwin Salvador
 */
public class Sender {
	
	private String MSG_TO_CENTRAL = "TO_CENTRAL";
	
	private RadiogramConnection connSend = null;
    private Radiogram send_dg;
	private String targetAddress;
	
	
	public static Hashtable attempts = new Hashtable();
	
	public Sender(String targetAddress){
		this.targetAddress = targetAddress;
	}
	
	public boolean connect() throws IOException {
		try {
			StringTokenizer st = new StringTokenizer(targetAddress,".");
			
			if(st.countTokens() == 1 || targetAddress.equals(Sensor.SPOT_ID))
				connSend = (RadiogramConnection)Connector.open("radiogram://" + Sensor.baseStation + ":" + Sensor.PORT);
			else
				connSend = (RadiogramConnection)Connector.open("radiogram://" + targetAddress + ":" + Sensor.PORT);

            send_dg = (Radiogram)connSend.newDatagram(connSend.getMaximumLength());
		}catch(IllegalArgumentException iae){
			try {
				System.out.println("Failed to connect trying again in 2secs...");
				iae.printStackTrace();
				Thread.sleep(2000);
				connect();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("Error opening connections: " + e);
//			Sensor.setLight(Sensor.LIGHT_ERROR, 6, 0);
			closeConnection();
		}
		return connSend != null;
	}
	
	public void sendMessage(Message msg) throws IOException{
			if(connect()){
				System.out.println("Conected to " + this.targetAddress + " " + send_dg.getAddress());
				try{
					Vector addresses;
					Sensor.setLight(Sensor.LIGHT_SENDING, 6, 150);
					String[] hst = new StringTokenizer(msg.getHeader(), "-").toArray();

					send_dg.reset();
					send_dg.writeUTF(msg.getId());
					send_dg.writeUTF(msg.getHeader());
					send_dg.writeUTF(msg.getContent());
					send_dg.writeUTF(msg.getStack());

					if(hst[0].equals("reply")) {
						attempts.remove(msg.getId());
					} else {
						if(!attempts.containsKey(msg.getId())){
							addresses = new Vector();
							addresses.addElement(this.targetAddress);
							Attempt a = new Attempt(msg, addresses);
							attempts.put(msg.getId(), a);
						} else {
							Attempt a = (Attempt)attempts.get(msg.getId());
							addresses = a.getIDSensors();
							addresses.addElement(targetAddress);
						}
					}
					// if the targetaddress is my ID, it means that 
					// I'm connected to central and should send the 
					// message to the basestation
					if(targetAddress.equals(Sensor.SPOT_ID))
						send_dg.writeUTF(MSG_TO_CENTRAL);
					else
						send_dg.writeUTF(targetAddress);
					System.out.println("About to send message: " + msg.getId() + " " + msg.getHeader() + " " + msg.getContent() + " " + msg.getStack());
					connSend.send(send_dg);
					System.out.println("Message Sent to " + targetAddress);
					closeConnection();	
				} catch (NoRouteException nre) {
					closeConnection();
					String sendTo = RealSensor.getNextAvailableNeighbour(msg.getStack(), msg.getId());
					System.out.println("No route found: sending to " + sendTo);
					if(sendTo != null) {
						Sender sender = new Sender(sendTo);
						sender.sendMessage(msg);
					} else {
						System.out.println("No route found no more routes available.");
						StringTokenizer st = new StringTokenizer(msg.getStack(), ";");

						StringTokenizer stack_st = new StringTokenizer(msg.getStack(),";");
						String stack = "";
						if(stack_st.countTokens() > 1){
							while(stack_st.hasMoreElements() && stack_st.countTokens() > 2){
								if(stack.equals(""))
									stack = stack_st.nextToken();
								else
									stack = stack + ";" + stack_st.nextToken();

							}
							System.out.println("Sending " + Sensor.MSG_REPLY_ERROR_TOCENTRAL + " with stack: " +stack );
							String target = stack_st.nextToken();
							Sender sender = new Sender(target);
							Message reply_msg = new Message(msg.getId(), Sensor.MSG_REPLY_ERROR_TOCENTRAL, "", stack);
							sender.sendMessage(reply_msg);
							System.out.println("SENDING: header: " + Sensor.MSG_REPLY_ERROR_TOCENTRAL + " content: " + "" + " stack: " + stack + " ID" + target);
							Sender.attempts.remove(msg.getId());
						} else			
							Sensor.setLight(Sensor.LIGHT_ERROR, 6, 0);
					}
					closeConnection();
				} catch (Exception e) {
					Sensor.setLight(Sensor.LIGHT_ERROR, 6, 0);
					System.err.println("Unknown Error sending message: " + e);
					e.printStackTrace();
					closeConnection();
				}
			} else {
				System.out.println("Failed to connect to " + targetAddress);
			}
	}
	
	public void closeConnection() {
		if(connSend != null)
			try {
				connSend.close();
				connSend = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}
