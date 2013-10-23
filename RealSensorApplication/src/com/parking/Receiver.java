package com.parking;

import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import java.util.Vector;
import javax.microedition.io.Connector;
import net.mypapit.java.StringTokenizer;

/**
 * Receiver.java
 * 
 * @author Edwin Salvador
 */
public class Receiver extends Thread {
	
	public Receiver(){}
	public void run(){
		try{
			RadiogramConnection connReceive = (RadiogramConnection)Connector.open("radiogram://:" + Sensor.PORT);
			Radiogram receive_dg = (Radiogram)connReceive.newDatagram(connReceive.getMaximumLength());
			
			while(true){
				receive_dg.reset();
				connReceive.receive(receive_dg);
				Sensor.setLight(Sensor.LIGHT_RECEIVING, 6, 150);
				String msg_id = receive_dg.readUTF();
				String header = receive_dg.readUTF();
				String content = receive_dg.readUTF();
				String stack = receive_dg.readUTF();

				System.out.println("RECEIVED: header: " + header + " content: " + content + " stack: " + stack);

				StringTokenizer st = new StringTokenizer(stack, ";");
				String[] hst = new StringTokenizer(header, "-").toArray();
				Sender sender;
				if(st.countTokens() == 0){
					if(header.equals(Sensor.MSG_REPLY_ERROR_TOCENTRAL)){
						//check if there are more neighbours available to try another path
						String sendTo = RealSensor.getNextAvailableNeighbour(stack, msg_id);
						System.out.println("Error to central to myself sending to " + sendTo);
						if(sendTo != null) {
							sender = new Sender(sendTo);
							Attempt a = (Attempt)Sender.attempts.get(msg_id);
							sender.sendMessage(a.getMsg());
						} else
							Sensor.setLight(Sensor.LIGHT_ERROR, 6, 0);
					} else {
						if(header.equals(Sensor.MSG_REPLY_ERROR_UNKNOWN)){
							Sensor.setLight(Sensor.LIGHT_ERROR, 6, 0);
						} else {
							// it is a reply OK for me, so delete the message from memory
							Sender.attempts.remove(msg_id);
						}
					}
				} else {
					String target = "";
					
					if(hst[0].equals("tocentral")){
						stack = stack + ";" + Sensor.SPOT_ID;
						Message msg = new Message(msg_id, header, content, stack);
						String sendTo = RealSensor.getNextAvailableNeighbour(stack, msg_id);
						System.out.println("RECEIVED Message to central sending to " + sendTo);
						if(sendTo != null) {
							sender = new Sender(sendTo);
//							if(Sender.attempts.containsKey(msg_id)){
//								Attempt a = (Attempt)Sender.attempts.get(msg_id);
								sender.sendMessage(msg);
//							}
							
						} else {
							stack = "";
							
							target = st.nextToken();
							sender = new Sender(target);
							Message reply_msg = new Message(msg_id, Sensor.MSG_REPLY_ERROR_TOCENTRAL, "", stack);
							sender.sendMessage(reply_msg);
							System.out.println("SENDING AFTER RECV tocentral: header: " + Sensor.MSG_REPLY_ERROR_TOCENTRAL + " content: " + "" + " stack: " + stack + " ID" + target);
//							Sender.attempts.remove(msg.getId());
						}
					} else {
						if(header.equals(Sensor.MSG_REPLY_ERROR_TOCENTRAL)){
							//check if there are more neighbours available to try another path
							System.out.println("Its an error-tocentral with MSG_ID: " + msg_id);
							if(Sender.attempts.containsKey(msg_id)){
								Attempt a = (Attempt)Sender.attempts.get(msg_id);
								String sendTo = RealSensor.getNextAvailableNeighbour(stack, a.getMsg().getId());
								System.out.println("Error to central sending to " + sendTo);
								if(sendTo != null) {
									sender = new Sender(sendTo);
									System.out.println("SENDING1: header: " + a.getMsg().getHeader() + " content: " + a.getMsg().getContent() + " stack: " + a.getMsg().getStack());
									sender.sendMessage(a.getMsg());
								} else {
									stack = "";
									while(st.hasMoreElements() && st.countTokens() > 1){
										if(stack.equals(""))
											stack = st.nextToken();
										else
											stack = stack + ";" +st.nextToken();
									}
									target = st.nextToken();
									sender = new Sender(target);
									Message msg = new Message(msg_id, header, content, stack);
									System.out.println("SENDING2: header: " + header + " content: " + content + " stack: " + stack + " ID" + target);
									sender.sendMessage(msg);
									Sender.attempts.remove(msg_id);
								}
							} else {
								stack = "";

								while(st.hasMoreElements() && st.countTokens() > 1){
									if(stack.equals(""))
										stack = st.nextToken();
									else
										stack = stack + ";" +st.nextToken();
								}
								target = st.nextToken();
								sender = new Sender(target);
								Message msg = new Message(msg_id, header, content, stack);
								System.out.println("SENDING3: header: " + header + " content: " + content + " stack: " + stack + " ID" + target);
								sender.sendMessage(msg);
								Sender.attempts.remove(msg_id);
							}
						} else {
							stack = "";

							while(st.hasMoreElements() && st.countTokens() > 1){
								if(stack.equals(""))
									stack = st.nextToken();
								else
									stack = stack + ";" +st.nextToken();
							}
							target = st.nextToken();
							sender = new Sender(target);
							Message msg = new Message(msg_id, header, content, stack);
							sender.sendMessage(msg);
							System.out.println("SENDING4: header: " + header + " content: " + content + " stack: " + stack + " ID" + target);
							Sender.attempts.remove(msg_id);
						}
					}		
				}
			}	
		} catch(Exception e){
			e.printStackTrace();
			System.err.println("Error receiving message: " + e);
			Sensor.setLight(Sensor.LIGHT_ERROR, 6, 0);
		}
	}
}
