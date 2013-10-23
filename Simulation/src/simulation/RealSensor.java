/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import simulation.Color;
import simulation.Message;
import simulation.Sensor;
import realSensor.MessageSender;
import static simulation.VirtualSensor.OFF;
/**
 *
 * @author Dianita
 */
public class RealSensor extends Sensor{
    /*address to send messages to the real sensor*/
    Central central;
    String address;
    public static final Color REAL = new Color(140, 140, 140);
    public static final Color ERROR = new Color(255, 204, 51);

    public Central getCentral() {
        return central;
    }

    public void setCentral(Central central) {
        this.central = central;
    }
    /* The virtual leds */
    private Color[] leds;
    /* The previous and current light sensed values */
    //private int currentLight;
    //private int previousLight;
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    /**
     * Initializes a real sensor with no connections. 
     */
    public RealSensor(String id) {
        super(id);
        this.leds = new Color[8];
        for (int i = 0; i < 8; i++) {
            this.leds[i] = REAL;
        }
        
        //currentLight = 50;
        //previousLight = 50;

    /*messages = new LinkedList<Message>();*/
    }

    public void sendMessage(Sensor sensor, Message msg) {
        //MessageSender.sendMessage(sensor,msg);
    }

    public void receiveMessage(Message msg) throws Exception 
    { 
        System.out.println("receiveMessage:RealSensor");
        MessageSender sender = new MessageSender();
        sender.sendMessage(this, msg);
        //throw new java.lang.UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void sendToCentral(Message msg) {
        //throw new java.lang.UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setLED(int led, Color ledColor) {
        leds[led] = ledColor;
        /* Virtual sensors will be the ones to notify central when to refresh canvas */
        central.refreshCanvas();
    }

    public Color[] getLEDs() {
        return leds;
        //throw new java.lang.UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void sendReply(Sensor sensor, Message msg) {
        //throw new java.lang.UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
