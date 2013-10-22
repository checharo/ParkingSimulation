/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package realSensor;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import simulation.Color;
import simulation.Message;
import simulation.Sensor;
import simulation.VirtualSensor;
import simulation.RealSensor;

/**
 *
 * @author Dianita
 */
public class MessageSender {
    private static final int PORT = 67;
 
    public void sendMessage(RealSensor sensor, Message message){
        try {
            System.out.println("Send in Real Message");
            RadiogramConnection radioConn = (RadiogramConnection) (Connector.open("radiogram://"+sensor.getAddress()+":"+PORT));
            System.out.println("address:"+"radiogram://"+sensor.getAddress()+":"+PORT);
            Datagram datagram = radioConn.newDatagram(100);
            datagram.reset();
            Vector stack = message.getStack();
            String strStack = "";
            
            for(int i = 0; i<stack.size();i++){
                Sensor sensorItem =(Sensor)stack.get(i);
                if(sensorItem instanceof RealSensor){
                    strStack = strStack+((RealSensor)sensorItem).getAddress()+";";
                }
                else{
                    strStack = strStack+sensorItem.getId()+";";
                }
            }
            if(strStack.length() !=0){
                strStack = strStack.substring(0, (strStack.length()-1));
                strStack = strStack.replace("(", "");
                strStack = strStack.replace(")", "");
            }
            System.out.println("stack2"+strStack);
            datagram.writeUTF(message.getHeader());
            datagram.writeUTF(message.getContent());
            //datagram.writeUTF(idIni);
            datagram.writeUTF(strStack);
            System.out.println("send address:"+sensor.getAddress()+" header:"+message.getHeader()+" content:"+message.getContent()+" stack:"+message.getStack());        
            radioConn.send(datagram);
            System.out.println("send");
            radioConn.close();    
            
        } catch(Exception ex) {
            sensor.setLED(6, RealSensor.ERROR);
            ex.printStackTrace();
        }
    }
}
