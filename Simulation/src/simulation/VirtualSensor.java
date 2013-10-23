package simulation;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.ArrayList;

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

    /* Saves the sensors which have been used as attempts to send a message when an error happens */
    private Hashtable<String,Attempt> attempts;
    private int messageCount;
    
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
        attempts = new Hashtable<String,Attempt>();
        messageCount = 0;
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
        attempts = new Hashtable<String,Attempt>();
        messageCount = 0;
    }
    
    @Override
    public void sendToCentral(Message m) {
        if (toCentral == this) {
            m.push(this);
            central.recieveMessage(m);
        } else {
            /* Message couldn't reach central, try a different path. Check it's not in the reply path as well. */
            if (!m.getStack().contains(toCentral)) {
                sendMessage(toCentral, m);
            } else {
                /* Add the attempt to the memory in case we have to try another path */
                if (!attempts.containsKey(m.getID())) {
                      ArrayList<Sensor> recipients = new ArrayList<Sensor>();
                      recipients.add(toCentral);
                      attempts.put(m.getID(), new Attempt(m.myClone(), recipients));
                } else {
                   ArrayList<Sensor> recipients = attempts.get(m.getID()).getSensors();
                   recipients.add(toCentral);
                }
                /* Message could not be sent, reply error */
                m.setContent("");
                m.setHeader(Message.REPLY_ERROR_CENTRAL);
                this.processMessage(m);
            }
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
        System.out.println(id + " sends to " + s.getId() + " message " + m);
        Color previousColor = leds[6];
        this.setLED(6, SENDING);
        try { Thread.sleep(DELAY); } catch (InterruptedException ie) {}
        
        /* Add the attempt to the memory in case we have to try another path */
        if (!attempts.containsKey(m.getID())) {
	      ArrayList<Sensor> recipients = new ArrayList<Sensor>();
              recipients.add(s);
	      attempts.put(m.getID(), new Attempt(m.myClone(), recipients));
        } else {
           ArrayList<Sensor> recipients = attempts.get(m.getID()).getSensors();
           recipients.add(s);
        }
        /* Push this sensor into the response stack and send the message */
        m.push(this);
        try {
            s.receiveMessage(m);
        } catch (Exception e) {
            /* Message could not be sent, reply error */
            m.setContent("");
            m.setHeader(Message.REPLY_ERROR_CENTRAL);
            System.out.println("pop(" + m.pop() + ")");
            this.processMessage(m);
        }

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
        System.out.println(id + " replies to " + s.getId() + " message " + m);
        Color previousColor = leds[6];
        this.setLED(6, SENDING);
        try { Thread.sleep(DELAY); } catch (InterruptedException ie) {}
        try {
            s.receiveMessage(m);
        } catch (Exception e) {
            /* Reply could not be sent, show error */
            previousColor = ERROR;
            System.out.println(this.toString() + ": "
                    + "Could not reply to message: " + m.getContent());
        }

        /* Remove the message from attempts memory */
        attempts.remove(m.getID());
           
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
        System.out.println(id + " processes: " + m);
        Color previousColor = leds[6];        
        this.setLED(6, RECEIVING);
        System.out.println("a");
        try { Thread.sleep(DELAY); } catch (InterruptedException ie) {}
        
        if (m.getHeader().startsWith("reply")) {
            Sensor top = m.pop();
            if (top == null) {
                /* This message is intended for this Sensor */
                if (m.getHeader().equals("reply-error-tocentral")) {
                    /* Message couldn't reach central, try a different path. Check it's not in the reply path as well. */
                    Attempt attempt = attempts.get(m.getID());
                    ArrayList<Sensor> attempted = attempt.getSensors();
                    if (attempted != null) {
                        if (right != null && !attempted.contains(right) && !m.getStack().contains(right)) {
                            m = attempts.get(m.getID()).getM();
                            sendMessage(right, m);
                        } else if (left != null && !attempted.contains(left) && !m.getStack().contains(left)) {
                            m = attempts.get(m.getID()).getM();
                            sendMessage(left, m);
                        } else if (back != null && !attempted.contains(back) && !m.getStack().contains(back)) {
                            m = attempts.get(m.getID()).getM();
                            sendMessage(back, m);
                        } else {
                            /* else there are no options left, display an error */
                            this.setLED(6, ERROR);
                            System.out.println(this.toString() + ": "
                                    + "Could not reach central: " + m.getContent());
                            attempts.remove(m.getID());
                        }
                    } else {
                        /* If there is no memory display an error */
                        this.setLED(6, ERROR);
                        System.out.println(this.toString() + ": "
                                + "Could not reach central: " + m.getContent());
                        /* Remove the message from memory */
                        attempts.remove(m.getID());
                    }
                } else if (m.getHeader().equals("reply-error-unknownmessage")) {
                    /* A node or central couldn't understand the message */
                    this.setLED(6, ERROR);
                    System.out.println(this.toString() + ": "
                            + "Message was not understood: " + m.getContent());
                    /* Remove the message from memory */
                    attempts.remove(m.getID());
                } else if (m.getHeader().equals("reply-ok")) {
                    /* Else nothing special should happen */
                    this.setLED(6, previousColor);
                    /* Remove the message from memory */
                    attempts.remove(m.getID());
                } else {
                    /* An unkown message arrived as a reply */
                    this.setLED(6, ERROR);
                    System.out.println(this.toString() + ": "
                            + "Unkown message received: " + m.getHeader() + ":"
                            + m.getContent());
                    /* Remove the message from memory */
                    attempts.remove(m.getID());
                }

            } else if (m.getHeader().equals("reply-error-tocentral")) {
                /* Message couldn't reach central, try a different path. Check it's not in the reply path as well. */
                /* In this case we are not the author of the message so if no path is avaible, return to the next Sensor in the reply stack. */
                ArrayList<Sensor> attempted = attempts.get(m.getID()).getSensors();
                if (attempted != null) {
                    if (right != null && !attempted.contains(right) && !m.getStack().contains(right) && !right.equals(top)) {
                        m = attempts.get(m.getID()).getM();
                        sendMessage(right, m);
                    } else if (left != null && !attempted.contains(left) && !m.getStack().contains(left) && !left.equals(top)) {
                        m = attempts.get(m.getID()).getM();
                        sendMessage(left, m);
                    } else if (back != null && !attempted.contains(back) && !m.getStack().contains(back) && !back.equals(top)) {
                        m = attempts.get(m.getID()).getM();
                        sendMessage(back, m);
                    } else {
                        /* else there are no options left, return to the next sensor in the reply stack */
                        sendReply(top, m);
                        attempts.remove(m.getID());
                        this.setLED(6, previousColor);
                    }
                } else {
                    /* If there is no memory display an error and return to the next sensor in the reply stack */
                    this.setLED(6, ERROR);
                    System.out.println(this.toString() + ": "
                            + "Did not find attempt memory for message: " + m.getContent());
                    sendReply(top, m);
                    this.setLED(6, previousColor);
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
            sendReply(top, new Message(m.getID(), "reply-error-unknownmessage", 
                m.getHeader() + ":" + m.getContent()));
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
                    sendToCentral(new Message(generateID(), "tocentral-parkingstate", "occupied"));
                    setLED(7, Color.RED);
                } else {
                    sendToCentral(new Message(generateID(), "tocentral-parkingstate", "vacant"));
                    setLED(7, Color.GREEN);
                }
            }
            previousLight = currentLight; 
            
            /* Process messages in the queue */
            try {
                Message m = messages.poll();
                if (m != null)            
                    owner.processMessage(m);
            } catch (NoSuchElementException nsee) {
                /* Just continue with execution */
            }
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
    
    /* Generates an ID for each message */
    private String generateID() {
       messageCount++; 
       return this.id + "_" + messageCount;
    }
    
}
