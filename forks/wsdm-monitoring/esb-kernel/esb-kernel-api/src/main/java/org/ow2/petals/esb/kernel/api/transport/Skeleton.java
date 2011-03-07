package org.ow2.petals.esb.kernel.api.transport;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.transporter.api.transport.TransportException;


public interface Skeleton {
	
	TransportersManager getTransportersManager();

	void accept(Exchange message) throws TransportException;
}
