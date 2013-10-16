
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * Class that renders the parking lot on a AWT Canvas. 
 * @author César Martínez D'Granda (cesarm@student.unimelb.edu.au)
 */
 public class ParkingCanvas extends JPanel {

    private Sensor[][] matrix;
    private int width = 0;
    private int height = 0;
    
    public ParkingCanvas(Sensor[][] matrix) {
        this.matrix = matrix;
    }
    
    public void setMatrix(Sensor[][] matrix) {
        this.matrix = matrix;
    }
    
    public void modifySize(int width, int height) {
        this.width = width;
        this.height = height;
        this.setSize(new Dimension(width, height));
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        /* Determine the size of each square unit in the matrix */
        int sqWidth = width / matrix[0].length;
        int sqHeight = height / matrix.length;
        
        /* Go through the matrix and paint the respective components */
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                /* Determine the position of the object */
                int x = j * sqWidth;
                int y = i * sqHeight;
                
                if (matrix[i][j] == null) {
                /* If null paint a street according to neighbors */   
                    g.setColor(new Color(255, 255, 255));
                    try {
                        if (matrix[i][j - 1] != null) {
                            g.drawRect(x, y, 1, sqHeight);
                        }
                    } catch (ArrayIndexOutOfBoundsException aioobe) {}
                    try {
                        if (matrix[i][j + 1] != null) {
                            g.drawRect(x + sqWidth - 1, y, 1, sqHeight);
                        }
                    } catch (ArrayIndexOutOfBoundsException aioobe) {}
                    try {
                        if (matrix[i - 1][j] != null) {
                            g.drawRect(x, y, sqWidth, 1);
                        }
                    } catch (ArrayIndexOutOfBoundsException aioobe) {}
                    try {
                        if (matrix[i + 1][j] != null) {
                            g.drawRect(x, y + sqHeight - 1, sqWidth, 1);
                        }
                    } catch (ArrayIndexOutOfBoundsException aioobe) {}
                } else {
                /* Paint a sensor according to its properties */
                    Sensor sensor = matrix[i][j];
                    g.setColor(new Color(0, 0, 0));
                    g.fillRect(x + 3, y + 3, sqWidth - 6, sqHeight - 6);
                    
                    int miniWidth = sqWidth / 5;
                    int miniHeight = sqHeight / 5;
                    /* We now just use LEDs 6 and 7 */
                    Color[] leds = sensor.getLEDs();
                    g.setColor(leds[6]);
                    g.fillRect(x + miniWidth, y + miniHeight * 3, miniWidth, 
                            miniHeight);
                    g.setColor(leds[7]);
                    g.fillRect(x + miniWidth * 3, y + miniHeight * 3, miniWidth, 
                            miniHeight);
                }
            }
        }
        
    }
    
}
