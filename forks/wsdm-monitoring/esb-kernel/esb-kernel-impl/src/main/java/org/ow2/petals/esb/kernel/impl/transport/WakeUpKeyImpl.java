package org.ow2.petals.esb.kernel.impl.transport;

import org.ow2.petals.esb.kernel.api.transport.WakeUpKey;
import org.ow2.petals.exchange.api.Exchange;

public class WakeUpKeyImpl implements WakeUpKey {

	
	public Exchange exchange = null;


	public Exchange getExchange() {
		return exchange;
	}

	public void setExchange(Exchange exchange) {
		this.exchange = exchange;
	}
	
}
