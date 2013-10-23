package simulation;


import java.util.Vector;

/**
 *
 * @author cesar
 */
public class Message {
    public static String REPLY_ERROR_CENTRAL = "reply-error-tocentral"; 
    private String id;
    private String header;

    public void setStack(Vector stack) {
        this.stack = stack;
    }
    private String content;
    /* Stack for routing back a reply to a message or stop flooding */
    private Vector stack;

    public Vector getStack() {
        return stack;
    }

    public Message(String id) {
        this.stack = new Vector();
	this.id = id;
    }
    
    /* Create a new Message with content */
    public Message(String id, String header, String content) {
        this.header = header;
        this.content = content;
        this.stack = new Vector();
	this.id = id;
    }
    
    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }
    
    public void push(Sensor m) {
        stack.add(m);
    }
    
    /**
     * Pops the top sensor in the stack. If the stack is already empty then
     * it returns null;
     * @return 
     */
    public Sensor pop() {
        if (stack.isEmpty())
            return null;
        else
            return (Sensor) stack.remove(stack.size() - 1);
    }
    
    public void printStack() {
        System.out.println(stack);
    }

    /**
     * @return the id
     */
    public String getID() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setID(String id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        return "Message(" + id + "," + header + ":" + content + "," + stack + ")";
    }
    
    public Message myClone() {
        Message m = new Message(id, header, content);
        Vector newStack = (Vector) stack.clone();
        m.setStack(newStack);
        return m;
    }
}

