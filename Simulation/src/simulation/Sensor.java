
package simulation;

/**
 *
 * @author cesar
 */
public abstract class Sensor {
    
    /* Three possible neighbors in a lane */
    protected Sensor left;
    protected Sensor right;
    protected Sensor back;
    
    /* The sensor that is chosen as the route to Central, if this is connected
     * to central then it will be a reference to self */
    protected Sensor toCentral;
    
    /* If the sensor is occupied or not. Initially it will not be occupied */
    protected boolean occupied;
    
    /* Indicates if the sensor is selected in the GUI */
    protected boolean selected;
  
    public Sensor() {}
    
    /* Is the id of the sensor, for virtual the ij value of the matrix, or
     * real, the address of the sensor. 
     */
    protected String id;
    
    public Sensor(String id) {
        this.left = null;
        this.right = null;
        this.back = null;
        this.toCentral = null;
        this.occupied = false;
        this.selected = false;
        this.id = id;
    }
    
    public Sensor(String id, Sensor left, Sensor right, Sensor back, Sensor toCentral) {
        this.left = left;
        this.right = right;
        this.back = back;
        this.toCentral = toCentral;
        this.occupied = false;
        this.selected = false;
        this.id = id;
    }
    
    /* To implement in virtual or real sensors */
    public abstract void setLED(int ledNumber, Color ledColor);
    public abstract Color[] getLEDs();
    public abstract void sendMessage(Sensor s, Message m);
    public abstract void sendReply(Sensor s, Message m);
    public abstract void sendToCentral(Message m);
    public abstract void receiveMessage(Message m)throws Exception;

    /**
     * @return the left
     */
    public Sensor getLeft() {
        return left;
    }

    /**
     * @param left the left to set
     */
    public void setLeft(Sensor left) {
        this.left = left;
    }

    /**
     * @return the right
     */
    public Sensor getRight() {
        return right;
    }

    /**
     * @param right the right to set
     */
    public void setRight(Sensor right) {
        this.right = right;
    }

    /**
     * @return the back
     */
    public Sensor getBack() {
        return back;
    }

    /**
     * @param back the back to set
     */
    public void setBack(Sensor back) {
        this.back = back;
    }

    /**
     * @return the toCentral
     */
    public Sensor getToCentral() {
        return toCentral;
    }

    /**
     * @param toCentral the toCentral to set
     */
    public void setToCentral(Sensor toCentral) {
        this.toCentral = toCentral;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    @Override
    public boolean equals(Object sensor){
        if(((Sensor)sensor).getId().equals(this.id)){
            return true;
        }
        return false;
    }
}
