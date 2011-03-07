package org.ow2.petals.timestamp.module;

import org.ow2.petals.jbi.messaging.exchange.MessageExchange;

public class TimeStamperHandler {

	private TimeStamper ts = new TimeStamperImpl();
	
	private static TimeStamperHandler instance = null;
	
	public static TimeStamperHandler getInstance() {
		if(instance == null) {
			instance = new TimeStamperHandler();
		}
		return instance;
	}
	
	public TimeStamper getTimeStamp(MessageExchange me) {
		ts.setMessageExchange(me);
		return ts;
	}
	
}
