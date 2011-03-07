package org.ow2.petals.esb.kernel.api.endpoint.behaviour.proxy;

import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.transporter.api.transport.TransportException;

public interface ProviderProxyBehaviour {

	
	
	Exchange sendExchange2ExternalProviderEndpoint(Exchange exchange) throws TransportException;
}
