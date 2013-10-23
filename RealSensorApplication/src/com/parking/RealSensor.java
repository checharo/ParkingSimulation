package com.parking;

/**
 * RealSensor.java
 *
 * author: Edwin Salvador
 */ 

import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.Condition;
import com.sun.spot.resources.transducers.IConditionListener;
import com.sun.spot.resources.transducers.ILightSensor;
import com.sun.spot.resources.transducers.LightSensorEvent;
import com.sun.spot.resources.transducers.SensorEvent;
import com.sun.squawk.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import net.mypapit.java.StringTokenizer;

 
public class RealSensor extends MIDlet {

	private static final int SAMPLE_PERIOD = 3 * 1000;  // in milliseconds
	
	private static final String MESSAGE_PARKINGSTATE = "tocentral-parkingstate";
	private static final String MESSAGE_OCCUPIED = "occupied";
	private static final String MESSAGE_VACANT = "vacant";
	
	private String baseStation = this.getAppProperty("BaseStation");
	
	private int currentLight;
	private int previousLight = -1;
	
	public static String left;
	public static String right;
	public static String back;
	public static String toCentral;
	
	private EvaluateLight model = new EvaluateLight();
	
	
	/**
     * startApp() is the MIDlet call that starts the application.
     */
    protected void startApp() throws MIDletStateChangeException { 		
		try {
			System.out.println("BaseStation address:" + baseStation);
			Sensor.setLight(Sensor.LIGHT_VACANT, 7, 0);
			
			ILightSensor lightSensor = (ILightSensor) Resources.lookup(ILightSensor.class);

			readMatrix();		
			Thread receiver = new Receiver();
			receiver.start();

			// define the callback
			IConditionListener lightListener = new IConditionListener() {
				public void conditionMet(SensorEvent evt, Condition condition) {
					String changedTo;
					//get the light measuremnet
					if (currentLight < 20) {
						Sensor.setLight(Sensor.LIGHT_OCCUPIED, 7, 0); // Car
						changedTo = MESSAGE_OCCUPIED;
					} else {
						Sensor.setLight(Sensor.LIGHT_VACANT, 7, 0); // No Car
						changedTo = MESSAGE_VACANT;
					}

					try {
						//send message
						Sender sender = new Sender(toCentral);
						Message msg = new Message(MESSAGE_PARKINGSTATE, changedTo, Sensor.SPOT_ID);
						sender.sendMessage(msg);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			};

			// define the condition - check light sensor every 3 seconds
			Condition checkLightLevel = new Condition(lightSensor, lightListener, SAMPLE_PERIOD) {
				public boolean isMet(SensorEvent evt) {
					currentLight = ((LightSensorEvent) evt).getValue();
					if( previousLight == -1 ){
						previousLight = currentLight;
					}
					boolean change = model.evaluateLight(currentLight, previousLight);
					previousLight = currentLight;
					return change;
				}
			};
			checkLightLevel.start();    // start monitoring the condition
        } catch (IOException ex) { //A problem in reading the sensors. 
            ex.printStackTrace();
        }
    }

	public void readMatrix() throws IOException, ArrayIndexOutOfBoundsException {
        
		try{
			String matrixFile = this.getAppProperty("MatrixFile");
			System.out.println("matrixFile=" + matrixFile);
			InputStream is = getClass().getResourceAsStream(matrixFile);
			InputStreamReader isr= new InputStreamReader(is);
			BufferedReader fileIn = new BufferedReader(isr);
			
			Vector temp = new Vector();
			
			/* We need the first line to determine dimensions */
			String line = fileIn.readLine();
			StringTokenizer st = new StringTokenizer(line,",");
			int width = st.countTokens();
			String[] sensor_properties;
			int thisRow = -1;
			int thisCol = -1;
			/* We load the file in a temporary Vector, each representing a row
			 * in the file. */
			int thisRowTemp = 0;
			do {
				st = new StringTokenizer(line, ",");
				String[] row = new String[st.countTokens()];
				int i = 0;
				
				while (st.hasMoreElements()) {
					row[i] = st.nextToken();
					
					sensor_properties = new StringTokenizer(row[i], ";").toArray();

					if(thisRow == -1){
						// if it is a real sensor it MUST have the SPOT_ID like 2;XXXX.XXXX.XXXX.XXXX
						if(sensor_properties.length > 1){
							
							System.out.println("Property: " + sensor_properties[1] + " SPOT_ID " + Sensor.SPOT_ID);
							
							if(sensor_properties[1].equalsIgnoreCase(Sensor.SPOT_ID)){
								thisCol = i; //get the col where this sensor is located in the matrix
								thisRow = thisRowTemp;
							}
						}
					}
					
					i++;
				}
				thisRowTemp++; //get the row where this sensor is located in the matrix
				temp.addElement(row);
				
			} while ((line = fileIn.readLine()) != null);
			
			System.out.println("This sensor position: " + thisRow + ", " + thisCol);
			
			/* We transform the temporary Vector into a bi-dimensional String 
			 * array with the values of the file. At the same time we initialize a
			 * the final matrix of Sensors, with their neighbors set to null. Where
			 * a - is found in the file, then that element will be set to null in 
			 * the final matrix */
			String[][] pre = new String[temp.size()][width];
			String[][] res = new String[pre.length][pre[0].length];
			String[] stID;
			for (int i = 0; i < pre.length; i++) {
				String[] row = (String[])temp.elementAt(i);
				for (int j = 0; j < pre[i].length; j++) {
					pre[i][j] = row[j];
					if (!pre[i][j].equals("-")) {
						//get the virtual ID or real ID
						stID = new StringTokenizer(pre[i][j], ";").toArray();
						if(stID.length == 1)
							res[i][j] = i + "," + j;
						else
							res[i][j] = stID[1];
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

			int proximity;
			proximity = getProximity(pre[thisRow][thisCol]);
			if (proximity == 0) {
				toCentral = Sensor.SPOT_ID;
			}

			try {
				if (!pre[thisRow][thisCol + 1].equals("-")) {
					right = res[thisRow][thisCol + 1];
					int rprox = getProximity(pre[thisRow][thisCol + 1]);
					if (rprox < proximity) toCentral = right;
				}
			} catch (ArrayIndexOutOfBoundsException aioobe) {}
			
			try {
				if (!pre[thisRow][thisCol - 1].equals("-")) {
					left = res[thisRow][thisCol - 1];
					int lprox = getProximity(pre[thisRow][thisCol - 1]);
					if (lprox < proximity) toCentral = left;
				}
			} catch (ArrayIndexOutOfBoundsException aioobe) {}
			
			try {
				if (!pre[thisRow - 1][thisCol].equals("-")) {
					back = res[thisRow - 1][thisCol];
					int bprox = getProximity(pre[thisRow - 1][thisCol]);
					if (bprox < proximity) toCentral = back;
				} 
			} catch (ArrayIndexOutOfBoundsException aioobe) {}
			
			try {
				if (!pre[thisRow + 1][thisCol].equals("-")) {
					back = res[thisRow + 1][thisCol];
					int bprox = getProximity(pre[thisRow + 1][thisCol]);
					if (bprox < proximity) toCentral = back;
				} 
			} catch (ArrayIndexOutOfBoundsException aioobe) {}
			
		} catch(Exception e){
			System.out.println("Error reading matrix file: " + e);
			e.printStackTrace();
			Sensor.setLight(Sensor.LIGHT_ERROR, 6, 0);
		}
		
		System.out.println("toCentral:" + toCentral);
		System.out.println("right:" + right);
		System.out.println("left:" + left);
		System.out.println("back:" + back);
    }
	
	public static int getProximity(String sensor_properties){
		String[] st = new StringTokenizer(sensor_properties, ";").toArray();
		return Integer.parseInt(st[0]);
    }
	
	public static String getNextAvailableNeighbour(String stack, String msg_id){
		StringTokenizer st = new StringTokenizer(stack, ";");
		String neighbour = null;
		Vector addresses = new Vector();
		System.out.println("Get avail neighb stack: " + stack);
		if(Sender.attempts.containsKey(msg_id)){
			Attempt a = (Attempt)Sender.attempts.get(msg_id);
			addresses = a.getIDSensors();
		}
		
		Vector stack_vector = new Vector();
		System.out.println(stack);
		while(st.hasMoreTokens()){
			String nt = (String)st.nextToken();
			System.out.println("Adding "+nt);
			stack_vector.addElement(nt);
		}
		if(RealSensor.toCentral != null && !addresses.contains(RealSensor.toCentral) 
		   && !stack_vector.contains(RealSensor.toCentral)) {
			neighbour = RealSensor.toCentral;
		}else if(RealSensor.right != null && !addresses.contains(RealSensor.right) 
		   && !stack_vector.contains(RealSensor.right)) {
			neighbour = RealSensor.right;
		} else if(RealSensor.left != null && !addresses.contains(RealSensor.left) 
				  && !stack_vector.contains(RealSensor.left)) {
			neighbour = RealSensor.left;
		} else if(RealSensor.back != null && !addresses.contains(RealSensor.back) 
				  && !stack_vector.contains(RealSensor.back)) {
			neighbour = RealSensor.back;
		}
		return neighbour;
	}

    /**
     * This will never be called by the Squawk VM.
     */
    protected void pauseApp() { 
    }

    /**
     * Called if the MIDlet is terminated by the system.
     * @param unconditional If true the MIDlet must cleanup and release all resources.
     */
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
	}

}
