package org.ow2.petals.esb.kernel.api.endpoint.behaviour.proxy;

import java.net.URI;
import java.util.Map;

import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.transporter.api.transport.TransportException;
import org.w3c.dom.Document;

public interface ClientProxyBehaviour {
	
	Map<URI, Document> getImports();
	
	Exchange sendExchange2InternalProviderEndpoint(Exchange exchange) throws TransportException;
}
