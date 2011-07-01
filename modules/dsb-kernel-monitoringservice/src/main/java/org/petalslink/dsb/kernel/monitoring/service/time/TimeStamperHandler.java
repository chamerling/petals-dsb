package org.petalslink.dsb.kernel.monitoring.service.time;

import org.ow2.petals.jbi.messaging.exchange.MessageExchange;

public class TimeStamperHandler {

	private final TimeStamper ts = new TimeStamperImpl();

	private static TimeStamperHandler instance = null;

	public static TimeStamperHandler getInstance() {
		if (instance == null) {
			instance = new TimeStamperHandler();
		}
		return instance;
	}

	public TimeStamper getTimeStamp(MessageExchange me) {
		this.ts.setMessageExchange(me);
		return this.ts;
	}

}
