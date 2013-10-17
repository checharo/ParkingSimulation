package simulation;


import java.util.Vector;


/**
 *
 * @author cesar
 */
public class Message {
    
    private String header;
    private String content;
    /* Stack for routing back a reply to a message or stop flooding */
    private Vector stack;

    public Message() {
        this.stack = new Vector();
    }
    
    /* Create a new Message with content */
    public Message(String header, String content) {
        this.header = header;
        this.content = content;
        this.stack = new Vector();
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
    
}
