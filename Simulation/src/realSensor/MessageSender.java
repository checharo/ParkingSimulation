/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package realSensor;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import simulation.Message;
import simulation.Sensor;
import simulation.RealSensor;

/**
 *
 * @author Dianita
 */
public class MessageSender {
    private static final int PORT = 67;
 
    public void sendMessage(RealSensor sensor, Message message) throws Exception{
        RadiogramConnection rCon = (RadiogramConnection) (Connector.open("radiogram://"+sensor.getAddress()+":"+PORT));
        try {
            System.out.println("Send in Real Message");
            System.out.println("address:"+"radiogram://"+sensor.getAddress()+":"+PORT);
            Datagram datagram = rCon.newDatagram(rCon.getMaximumLength());
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
            datagram.writeUTF(message.getID());
            datagram.writeUTF(message.getHeader());
            datagram.writeUTF(message.getContent());
            datagram.writeUTF(strStack);
            System.out.println("idmsg:"+message.getID()+"send address:"+sensor.getAddress()+" header:"+message.getHeader()+" content:"+message.getContent()+" stack:"+message.getStack());        
            rCon.send(datagram);
            System.out.println("send");
            
        } catch(Exception ex) {
            message.setHeader(Message.REPLY_ERROR_CENTRAL);
            ex.getStackTrace();
            throw ex;
        }
        finally{
            rCon.close();
        }
    }
}
