
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author César Martínez D'Granda (cesarm@student.unimelb.edu.au)
 */
public class MatrixUtil {
    
    /**
     * Reads an input file to build the Sensor Matrix. In the input file the 
     * nodes represented by 0 are connected to Central, any other number is the 
     * distance to central. A - means that there is no node there (street).
     * @param file The name of the input file
     * @return A bi-dimensional matrix of Sensor objects, with their neighbors
     * and toCentral nodes already set. 
     * @throws FileNotFoundException In case the file does not exit
     * @throws IOException In case there is an error reading the file
     * @throws ArrayIndexOutOfBoundsException In case the file format is wrong
     * because the width of the rows is different. All rows must have the same
     * amount of elements.
     */
    public static Sensor[][] readMatrix(String file) throws 
            FileNotFoundException, IOException, ArrayIndexOutOfBoundsException {
        
        BufferedReader fileIn = new BufferedReader(new FileReader(file));
        ArrayList<String[]> temp = new ArrayList<String[]>();
        
        /* We need the first line to determine dimensions */
        String line = fileIn.readLine();
        StringTokenizer st = new StringTokenizer(line, ",");
        int width = st.countTokens();
        
        /* We load the file in a temporary ArrayList, each representing a row
         * in the file. */
        do {
            st = new StringTokenizer(line, ",");
            String[] row = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreElements()) {
                row[i] = st.nextToken();
                i++;
            }
            temp.add(row);
        } while ((line = fileIn.readLine()) != null);
        
        /* We transform the temporary ArrayListit into a bi-dimensional String 
         * array with the values of the file. At the same time we initialize a
         * the final matrix of Sensors, with their neighbors set to null. Where
         * a - is found in the file, then that element will be set to null in 
         * the final matrix */
        String[][] pre = new String[temp.size()][width];
        Sensor[][] res = new Sensor[pre.length][pre[0].length];
        for (int i = 0; i < pre.length; i++) {
            String[] row = temp.get(i);
            for (int j = 0; j < pre[i].length; j++) {
                pre[i][j] = row[j];
                if (!pre[i][j].equals("-")) {
                    res[i][j] = new VirtualSensor("(" + i + "," + j + ")");
                } else {
                    res[i][j] = null;
                }
            }
        }
        
        /* We define the neighbors of each Sensor. We use the pre array to do 
         * this, if - is found then that neighbor is set to null. The back 
         * neighbor can be either the one on top, or the one below, it takes the
         * top if it finds it first since there should be ONLY one back neighbor.
         * It also sets the toCentral node as itself if the distance is 0, or to
         * the first node it finds that has a less distance to central.
         */
        for (int i = 0; i < pre.length; i++) {
            for (int j = 0; j < pre[i].length; j++) {
                Sensor ij = res[i][j];
                if (ij == null) continue;
                
                int proximity;
                proximity = Integer.parseInt(pre[i][j]);
                if (proximity == 0) {
                    ij.setToCentral(ij);
                }
                
                try {
                    if (!pre[i][j + 1].equals("-")) {
                        Sensor right = res[i][j + 1];
                        ij.setRight(right);
                        int rprox = Integer.parseInt(pre[i][j + 1]);
                        if (rprox < proximity) ij.setToCentral(right);
                    }
                } catch (ArrayIndexOutOfBoundsException aioobe) {}
                try {
                    if (!pre[i][j - 1].equals("-")) {
                        Sensor left = res[i][j - 1];
                        ij.setLeft(left);
                        int lprox = Integer.parseInt(pre[i][j - 1]);
                        if (lprox < proximity) ij.setToCentral(left);
                    }
                } catch (ArrayIndexOutOfBoundsException aioobe) {}
                try {
                    if (!pre[i - 1][j].equals("-")) {
                        Sensor back = res[i - 1][j];
                        ij.setBack(back);
                        int bprox = Integer.parseInt(pre[i - 1][j]);
                        if (bprox < proximity) ij.setToCentral(back);
                    } 
                } catch (ArrayIndexOutOfBoundsException aioobe) {}
                try {
                    if (!pre[i + 1][j].equals("-")) {
                        Sensor back = res[i + 1][j];
                        ij.setBack(back);
                        int bprox = Integer.parseInt(pre[i + 1][j]);
                        if (bprox < proximity) ij.setToCentral(back);
                    } 
                } catch (ArrayIndexOutOfBoundsException aioobe) {} 
                
                /* Start the event thread of the Virtual Sensors */
                if (ij instanceof VirtualSensor) {
                    ((VirtualSensor) ij).startEventThread();
                }
            }
        }
        
        return res;
    }
}
