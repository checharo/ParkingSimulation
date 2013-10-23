package com.parking;

import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.resources.transducers.LEDColor;

/**
 * Sensor.java
 * 
 * @author Edwin Salvador
 */
public class Sensor {
	public static final String MSG_REPLY_ERROR_UNKNOWN = "reply-error-unknownmessage";
	public static final String MSG_REPLY_ERROR_TOCENTRAL = "reply-error-tocentral";
	
	protected static final LEDColor LIGHT_ERROR = LEDColor.ORANGE;
	protected static final LEDColor LIGHT_SENDING = LEDColor.BLUE;
    protected static final LEDColor LIGHT_RECEIVING = LEDColor.MAGENTA;
	protected static final LEDColor LIGHT_OCCUPIED = LEDColor.RED;
	protected static final LEDColor LIGHT_VACANT = LEDColor.GREEN;
	
	protected static final String SPOT_ID = System.getProperty("IEEE_ADDRESS");
	/**
	 * Address for the basestation.
	 */
	protected static final String baseStation  = "0014.4F01.0000.4A64";
	
	protected static final int PORT = 67;
	
	protected static void setLight(LEDColor color, int led, int delay){
		ITriColorLEDArray leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
		LEDColor prev_color = leds.getLED(led).getColor();
		leds.getLED(led).setOff();
		leds.getLED(led).setColor(color);
		leds.getLED(led).setOn();
		
		if(delay != 0){
			try {
				Thread.sleep(delay);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			leds.getLED(led).setOff();
		}
		System.out.println("PREV COLOR "+prev_color);
		if(prev_color.equals(LIGHT_ERROR)){
			leds.getLED(led).setOff();
			leds.getLED(led).setColor(prev_color);
			leds.getLED(led).setOn();
		}
	}
}
