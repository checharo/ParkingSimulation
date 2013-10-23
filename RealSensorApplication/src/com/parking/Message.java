package com.parking;

/**
 * Message.java
 * 
 * @author Edwin Salvador
 */
public class Message {
	private static int MSG_ID = 0;
	private String id;
	private String header;
	private String content;
	private String stack;

	public Message(String header, String content, String stack) {
		this.id = Sensor.SPOT_ID + "_" + Integer.toString(++MSG_ID);
		this.header = header;
		this.content = content;
		this.stack = stack;
	}
	public Message(String id, String header, String content, String stack) {
		this.id = id;
		this.header = header;
		this.content = content;
		this.stack = stack;
	}

	public String getId() {
		return id;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStack() {
		return stack;
	}

	public void setStack(String stack) {
		this.stack = stack;
	}
}
