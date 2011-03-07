package org.ow2.petals.esb.kernel.api.transport;

import org.ow2.petals.exchange.api.Exchange;

public interface WakeUpKey {

	Exchange getExchange();

	void setExchange(Exchange exchange);
	
}
