package org.ow2.petals.esb.kernel.api.transport;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.exchange.api.ExchangeException;
import org.ow2.petals.transporter.api.transport.TransportException;


public interface Stub {

	TransportersManager getTransportersManager();
	
	Exchange createExchange()  throws ExchangeException;
	
	void send(Exchange message) throws TransportException;

	Exchange sendSync(Exchange message, long timeout) throws TransportException;

	Description getDescriptionOfProviderEndpoint(QName name) throws ESBException;
}
