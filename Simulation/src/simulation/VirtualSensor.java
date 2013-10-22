package simulation;

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author cesar
 */
public class VirtualSensor extends Sensor {
    
    /* The virtual leds */
    private Color[] leds;
    /* The previous and current light sensed values */
    private int currentLight;
    private int previousLight;
    /* Represents different colors for the leds */
    public static final Color OFF = new Color(127, 127, 127);
    public static final Color ERROR = new Color(255, 204, 51);
    public static final Color SENDING = new Color(51, 204, 255);
    public static final Color RECEIVING = new Color(255, 51, 229);
    
    private EventThread eventThread;
    /* A reference to the central logic */
    private Central central;
    /* A queue for incoming messages */
    private Queue<Message> messages;
    
    /* Flag for determining if the event thread is waiting or not */
    private boolean waiting = false;
    
    /* Delay time of communication for the simulation of Virtual Sensors */
    private static final int DELAY = 150;
    
    /**
     * Initializes a virtual sensor with no connections. The initial state of
     * light will be 50 meaning there is no car.
     */
    public VirtualSensor(String id) {
        super(id);
        leds = new Color[8];
        for (int i = 0; i < 8; i++) {
            leds[i] = OFF;
        }
        leds[7] = Color.GREEN;
        currentLight = 50;
        previousLight = 50;
        messages = new LinkedList<Message>();
    }
    
    /**
     * Initializes a virtual sensor. The initial state of
     * light will be 50 meaning there is no car.
     * @param left The sensor position to the left.
     * @param right The sensor position to the right
     * @param back The sensor position behind this one (i + 1) or (i - 1).
     * @param toCentral The sensor that will get a message to Central
     */
    public VirtualSensor(String id, Sensor toCentral, Sensor left, Sensor right, 
            Sensor back) {
        super(id, left, right, back, toCentral);
        leds = new Color[8];
        for (int i = 0; i < 8; i++) {
            leds[i] = OFF;
        }
        leds[7] = Color.GREEN;
        currentLight = 50;
        previousLight = 50;
        messages = new LinkedList<Message>();
    }
    
    @Override
    public void sendToCentral(Message m) {
        if (toCentral == this) {
            m.push(this);
            central.recieveMessage(m);
        } else {
            sendMessage(toCentral, m);
        }
    }
    
    /**
     * Sends a message to one of the neighboring sensors. Since only Virtual Sensors
     * can be neighbors to Virtual Sensors then it directly calls receiveMessage().
     * @param s
     * @param m 
     */
    @Override
    public void sendMessage(Sensor s, Message m) {
        System.out.println("class of the sensor to send message:"+s.getClass());
        Color previousColor = leds[6];
        this.setLED(6, SENDING);
        try { Thread.sleep(DELAY); } catch (InterruptedException ie) {}
        m.push(this);
        s.receiveMessage(m);

        this.setLED(6, previousColor);
    }
    
    /**
     * Sends a reply to the corresponding neighbor. Since only Virtual Sensors
     * can be neighbors to Virtual Sensors then it directly calls receiveMessage().
     * @param s
     * @param m 
     */
    @Override
    public void sendReply(Sensor s, Message m) {
        Color previousColor = leds[6];
        this.setLED(6, SENDING);
        try { Thread.sleep(DELAY); } catch (InterruptedException ie) {}
        s.receiveMessage(m);
        
        this.setLED(6, previousColor);
    }
    
    /**
     * Receive message in the virtual sensor will just add the message to the
     * queue and let the event thread process it. This avoids holding the sender
     * until the message is processed.
     * @param m 
     */
    @Override
    public void receiveMessage(Message m) {
        messages.add(m);
        notifyEvent();
    }
    
    @Override
    public void setLED(int led, Color ledColor) throws ArrayIndexOutOfBoundsException {
        leds[led] = ledColor;
        /* Virtual sensors will be the ones to notify central when to refresh canvas */
        central.refreshCanvas();
    }
    
    @Override
    public Color[] getLEDs() {
        return leds;
    }
    
    public void setCurrentLight(int currentLight) {
        this.currentLight = currentLight;
    }
    
    public int getCurrentLight() {
        return currentLight;
    }
    
    public void setCentral(Central central) {
        this.central = central;
    }
    
    /**
     * Processes messages found in the message queue
     * @param m Message to process
     */
    public void processMessage(Message m) {
        Color previousColor = leds[6];
        this.setLED(6, RECEIVING);
        try { Thread.sleep(DELAY); } catch (InterruptedException ie) {}
        if (m.getHeader().startsWith("reply")) {
            Sensor top = m.pop();
            if (top == null) {
                /* This message is intended for this Sensor */
                if (m.getHeader().equals("reply-error")) {
                    /* Message couldn't reach central, display error */
                    this.setLED(6, ERROR);
                    System.out.println(System.currentTimeMillis() + " " + "Could not reach central: " + 
                            m.getHeader() + ":" + m.getContent());
                } else if (m.getHeader().equals("reply-ok")) {
                    /* Else nothing special should happen */
                    this.setLED(6, previousColor);
                } else {
                    /* Unknown message */
                    this.setLED(6, ERROR);
                    System.out.println(System.currentTimeMillis() + " " + "Unkown message received: " + 
                            m.getHeader() + ":" + m.getContent());
                }
            } else {
                /* Forward the reply to the corresponding Sensor */
                sendReply(top, m);
                this.setLED(6, previousColor);
            }
        } else if (m.getHeader().startsWith("tocentral")) {
            sendToCentral(m);
            this.setLED(6, previousColor);
        } else {
            /* If an unkown message is received, reply to sender with error */
            Sensor top = m.pop();
            sendReply(top, new Message("reply-error", "unkownmessage"));
            this.setLED(6, previousColor);
        }
    }
    
    /**
     * Starts the event thread that listen to simulated events and returns a 
     * reference for the MainFrame to notify changes requested by the user in
     * the GUI.
     * @return The thread that is initiated listening to simulated events.
     */
    public Thread startEventThread() {
        /* Start the event listening thread */
        eventThread = new EventThread(this);
        eventThread.start();
        return eventThread;
    }
    
    /**
     * Returns the event thread reference for the MainFrame to notify changes 
     * requested by the user in the GUI.
     * @return 
     */
    public Thread getEventThread() {
        return eventThread;
    }
    
    /**
     * Necessary to notify the event thread from another class.
     */
    public void notifyEvent() {
        if (waiting)
            eventThread.notifyEvent();
    }
    
    public void setWaiting(boolean b) {
        waiting = b;
    }
    
    /* Class that implements the thread that is listening to simulated events. 
     * The events are generated by the user using the GUI. This thread is notified
     * when the user changes the light value for example, simulating the event
     * of real light changing in a real sensor */
    public class EventThread extends Thread {
        
        VirtualSensor owner;
        
        public EventThread(VirtualSensor owner) {
            this.owner = owner;
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    this.waitForEvent();
                } catch (InterruptedException ie) {
                    /* If this thread is interrupted it stops running */
                    break;
                }
            }
        }
        
        private synchronized void waitForEvent() throws InterruptedException {
            
            if (messages.size() == 0) {
                /* If there are messages do not wait */
                /* Instead of waiting, sleep 100 ms to avoid notify deadlocks */
                owner.setWaiting(true);
                wait();
                owner.setWaiting(false);
            }
            /* Event has happened */
            /* Evaluate if the light has changed */
            boolean changed = (new EvaluateLight()).evaluateLight(currentLight, previousLight);
            if (changed) {
                occupied = !occupied;
                if (occupied) {
                    sendToCentral(new Message("tocentral-parkingstate", "occupied"));
                    setLED(7, Color.RED);
                } else {
                    sendToCentral(new Message("tocentral-parkingstate", "vacant"));
                    setLED(7, Color.GREEN);
                }
            }
            previousLight = currentLight; 
            
            /* Process messages in the queue */
            Message m = messages.poll();
            if (m != null)            
                owner.processMessage(m);

        }
        
        public synchronized void notifyEvent() {
            notify();
        }
    }
    
    @Override
    public String toString() {
        return "VirtualSensor" + getId();
    }
    
    public Queue<Message> getMessages() {
        return messages;
    }
    
}
