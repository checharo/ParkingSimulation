
package simulation;

import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Class used to generate random input for the simulation.
 * @author César Martínez D'Granda (cesarm@student.unimelb.edu.au)
 */
public class RandomSimulator extends Thread {
    
    /* How quick will it generate a new input */
    private static final int PACE = 300;
    /* A reference to the Central logic. */
    private Central central;
    
    public RandomSimulator(Central central) {
        this.central = central;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(PACE);
                double random = Math.random();
                
                /* Every 1/5 of cases add a car to the parking lot */
                if (random < 0.2d) {
                    if (central.getSpaces() > 0)
                        central.carEnters();
                /* Every 1/5 of cases remove a car from the parking lot */
                } else if (random >= 0.2d && random < 0.4d) {
                    if (central.getCars() > 0)
                        try {
                            central.carExits();
                        } catch (NumberFormatException nfe) {
                            /* No cars to take out, just ignore the case */
                        }
                /* Every 2/5 of cases allocate one of those cars to a space */
                } else if (random >= 0.4d && random < 0.8d) {
                    if (central.getCars() > 0) {
                        Sensor[][] matrix = central.getMatrix();
                        ArrayList<VirtualSensor> availableSensors = 
                                new ArrayList<VirtualSensor>();
                        for (int i = 0; i < matrix.length; i++) {
                            for (int j = 0; j < matrix[i].length; j++) {
                                if (matrix[i][j] instanceof VirtualSensor) {
                                    if (matrix[i][j].getLEDs()[7] == Color.GREEN) {
                                        availableSensors.add((VirtualSensor) matrix[i][j]);
                                    }
                                }
                            }
                        }
                        if (availableSensors.size() > 0) {
                            int randomS = (int) (Math.random() * availableSensors.size());
                            VirtualSensor vsensor = availableSensors.get(randomS);
                            vsensor.setCurrentLight(0);
                            if (vsensor.getEventThread() != null 
                                    && vsensor.getEventThread().isAlive()) {
                                vsensor.notifyEvent();
                            } else {
                                JOptionPane.showMessageDialog(null, 
                                        "The Virtual Sensor is not running. "
                                            + "Please restart application.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                /* Every 1/5 of cases vacate one of the taken spots */    
                } else if (random >= 0.8d && random < 1.0d) {
                    Sensor[][] matrix = central.getMatrix();
                    ArrayList<VirtualSensor> availableSensors = 
                            new ArrayList<VirtualSensor>();
                    for (int i = 0; i < matrix.length; i++) {
                        for (int j = 0; j < matrix[i].length; j++) {
                            if (matrix[i][j] instanceof VirtualSensor) {
                                if (matrix[i][j].getLEDs()[7] == Color.RED) {
                                    availableSensors.add((VirtualSensor) matrix[i][j]);
                                }
                            }
                        }
                    }
                    if (availableSensors.size() > 0) {
                        int randomS = (int) (Math.random() * availableSensors.size());
                        VirtualSensor vsensor = availableSensors.get(randomS);
                        vsensor.setCurrentLight(50);
                        if (vsensor.getEventThread() != null 
                                && vsensor.getEventThread().isAlive()) {
                            vsensor.notifyEvent();
                        } else {
                            JOptionPane.showMessageDialog(null, 
                                    "The Virtual Sensor is not running. "
                                        + "Please restart application.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } 
                }
            } catch (InterruptedException ie) {
                /* Stop if the thread is interrupted */
                break; 
            }
        }
    }
}
