package simulation;


/**
 * Represents the central control of the network. 
 * @author César Martínez D'Granda (cesarm@student.unimelb.edu.au)
 */
public class Central {
    
    /* Represents the sensor matrix. */
    private Sensor[][] matrix;
    private MainFrame mainFrame;
    
    public Central(Sensor[][] matrix, MainFrame mainFrame) {
        this.matrix = matrix;
        this.mainFrame = mainFrame;
    }
    
    public Sensor[][] getMatrix() {
        return matrix;
    }
    
    /**
     * Receives a message from a Sensor. Virtual Sensors will call this method
     * directly, while the Real Sensors will send the message through the 
     * data input connection and then this method will be called.
     * @param m The message received.
     */
    public synchronized void recieveMessage(Message m) {
        /* DO NOTHING XD!!! ... for the moment */
        
        /* Reply back acknowledge */
        Sensor top = m.pop();
        m.setHeader("reply-ok");
        m.setContent("");
        top.receiveMessage(m);
    }
    
    public synchronized void refreshCanvas() {
        mainFrame.repaintCanvas();
    }
}
