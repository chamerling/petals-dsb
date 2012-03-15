package org.petalslink.dsb.kernel.monitoring.service.time;

import org.ow2.petals.jbi.messaging.exchange.MessageExchangeWrapper;

public class TimeStamperHandler {

	private final TimeStamper ts = new TimeStamperImpl();

	private static TimeStamperHandler instance = null;

	public static TimeStamperHandler getInstance() {
		if (instance == null) {
			instance = new TimeStamperHandler();
		}
		return instance;
	}

	public TimeStamper getTimeStamp(MessageExchangeWrapper me) {
		this.ts.setMessageExchange(me);
		return this.ts;
	}

}
