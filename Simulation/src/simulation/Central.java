package simulation;

import javax.swing.JOptionPane;


/**
 * Represents the central control of the network. 
 * @author César Martínez D'Granda (cesarm@student.unimelb.edu.au)
 * @author Diana Carolina Barreto Arias (dianaba@student.unimelb.edu.au)
 */
public class Central {
    
    /* Represents the sensor matrix. */
    private Sensor[][] matrix;
    /* The number of spaces available and the number of cars available */
    private int spaces;
    private int cars;
    /* The reference to the GUI */
    private MainFrame mainFrame;
    
    public Central(Sensor[][] matrix, MainFrame mainFrame) {
        this.matrix = matrix;
        this.mainFrame = mainFrame;
        
        /* We calculate the free spaces available, at first every space is available */
        spaces = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] != null)
                    spaces++;
            }
        }
        cars = 0;      
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
        /* Sensor to reply to */
        Sensor top = m.pop();
        
        /* Process message and prepare reply */
        if (m.getHeader().equals("tocentral-parkingstate")) {
            if (m.getContent().equals("vacant")) {
                /* One space is liberated and a additional car is circulating */
                cars++;
                mainFrame.refreshState();
                m.setHeader("reply-ok");
                m.setContent("");
            } else if (m.getContent().equals("occupied")) {
                /* One space is occupied and one less car is circulating */
                cars--;
                mainFrame.refreshState();
                m.setHeader("reply-ok");
                m.setContent("");
            } else {
                /* Couldn't understand message */
                String header = m.getHeader();
                m.setHeader("reply-error-unknownmessage");
                m.setContent(header + ":" + m.getContent());
            }
        }
        
        /* Reply back acknowledge */
        try {
            top.receiveMessage(m);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "There is problem with the connection with the Real Sensors.", "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public synchronized void refreshCanvas() {
        mainFrame.repaintCanvas();
    }
 
    public RealSensor findSensorByAddress(String address){
        
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] instanceof RealSensor && ((RealSensor) matrix[i][j]).getAddress().equals(address)){
                    return (RealSensor)matrix[i][j];
                }
            }
        }
        return null;
    } 

    public Sensor findSensorById(String id){
        if(id.contains(",")){
            //"(" + i + "," + j + ")"
            id = id.replace("(", "");
            id = id.replace(")", "");
            System.out.println("id:"+id);
            String[] positions = id.split(",");
            System.out.println("positions"+positions[0]+"-"+positions[1]);
            int i = Integer.parseInt(positions[0]);
            int j = Integer.parseInt(positions[1]);
            return matrix[i][j];
        }
        return null;
    } 

    
    /**
     * Simulates a car entering the parking lot 
     */
    public synchronized void carEnters() {
        cars++;
        spaces--;
        mainFrame.refreshState();
    }
    
    /**
     * Simulates a car exiting the parking lot 
     * @throws NumberFormatException If there are no more cars to take out.
     */
    public synchronized void carExits() throws NumberFormatException {
        if (cars <= 0)
            throw new NumberFormatException("No more cars to take out.");
        cars--;
        spaces++;
        mainFrame.refreshState();
    }
    
    /**
     * @return the spaces
     */
    public int getSpaces() {
        return spaces;
    }

    /**
     * @return the cars
     */
    public int getCars() {
        return cars;
    }

}