
package simulation;

import java.util.ArrayList;

/**
 *
 * @author Dianita
 */
public class Attempt {
    private Message m;
    private ArrayList<Sensor> sensors;
    
    public Attempt() {}
    
    public Attempt(Message m, ArrayList<Sensor> sensors) {
        this.m = m;
        this.sensors = sensors;
    }

    /**
     * @return the m
     */
    public Message getM() {
        return m;
    }

    /**
     * @param m the m to set
     */
    public void setM(Message m) {
        this.m = m;
    }

    /**
     * @return the sensors
     */
    public ArrayList<Sensor> getSensors() {
        return sensors;
    }

    /**
     * @param sensors the sensors to set
     */
    public void setSensors(ArrayList<Sensor> sensors) {
        this.sensors = sensors;
    }
    
    public String toString() {
        return m.toString() + sensors.toString();
    }
}
