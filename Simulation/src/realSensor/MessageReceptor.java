/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package realSensor;

import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.util.IEEEAddress;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import simulation.Central;
import simulation.Message;
import simulation.RealSensor;
import simulation.Sensor;


/**
 *
 * @author Dianita
 */
public class MessageReceptor extends Thread{
    private static final int HOST_PORT = 67;
    private static String TO_CENTRAL = "TO_CENTRAL";
    RadiogramConnection rCon;
    Radiogram dg;
    Central central;
    
    public MessageReceptor(Central central){
        this.central = central;
    }
    public void run() {
        System.out.println("Initiate the thread");
        try {
            // Open up a server-side broadcast radiogram connection
            // to listen for sensor readings being sent by different SPOTs
            rCon = (RadiogramConnection) Connector.open("radiogram://:" + HOST_PORT);
            dg = (Radiogram)rCon.newDatagram(rCon.getMaximumLength());
        } catch (Exception e) {
             System.err.println("setUp caught " + e.getMessage());
             e.printStackTrace();
        }

        // Main data collection loop
        while (true) {
            try {
                // Read sensor sample received over the radio
                Vector stack = null;
                rCon.receive(dg);
                String address =dg.getAddress();
                String idMsg =dg.readUTF();
                String header = dg.readUTF();
                String content = dg.readUTF();
                String strStack = dg.readUTF();
                String id = dg.readUTF();
                System.out.println("id:"+id+"receive addres:"+address+" header:"+header+" content:"+content+" stack:"+strStack+" id:"+id);
                //creating the message
                Message message = new Message(idMsg);
                message.setHeader(header);
                message.setContent(content);
                stack = getStack(strStack);
                message.setStack(stack);
                //if the message go to central
                System.out.println(id);
                if(id.equals(TO_CENTRAL)){
                    central.recieveMessage(message);
                }
                else{
                    //find the virtual sensor with the address
                    Sensor sensor = central.findSensorById(id);
                    sensor.receiveMessage(message);
                }
                //Create the messages to send to the central
            } catch (Exception e) {
                System.err.println("Caught " + e +  " while reading sensor samples.");
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
//        MessageReceptor app = new MessageReceptor();
  //      app.run();
    }
    public Vector getStack(String strStack){
        String[] aStack = strStack.split(";");
        Vector stack = new Vector();
        Sensor sensor = null;
        for(int i = 0; i<aStack.length;i++){
            System.out.println(i+" stack:"+aStack[i]); 
            sensor = central.findSensorById(aStack[i]);
            
            if(sensor!=null){
                stack.add(sensor);
            }
            else{
                sensor = central.findSensorByAddress(aStack[i]);
                stack.add(sensor);
            }
        }
        return stack;
    }
}
