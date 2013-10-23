package com.parking;

import java.util.Vector;

/**
 * Attempt.java
 * 
 * @author Edwin Salvador
 */
public class Attempt {
	private Message msg;
	private Vector IDSensors;

	public Attempt(Message msg, Vector IDSensors) {
		this.msg = msg;
		this.IDSensors = IDSensors;
	}

	public Message getMsg() {
		return msg;
	}

	public void setMsg(Message msg) {
		this.msg = msg;
	}

	public Vector getIDSensors() {
		return IDSensors;
	}

	public void setIDSensors(Vector IDSensors) {
		this.IDSensors = IDSensors;
	}
	
	
}
