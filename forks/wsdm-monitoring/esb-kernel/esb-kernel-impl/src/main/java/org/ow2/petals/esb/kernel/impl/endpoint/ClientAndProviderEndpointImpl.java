package org.ow2.petals.esb.kernel.impl.endpoint;

import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.easywsdl.schema.util.XMLPrettyPrinter;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.ClientAndProviderEndpoint;
import org.ow2.petals.esb.kernel.api.transport.Stub;
import org.ow2.petals.esb.kernel.impl.transport.StubImpl;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.exchange.api.ExchangeException;
import org.ow2.petals.transporter.api.transport.TransportException;

@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller="primitive")
public class ClientAndProviderEndpointImpl extends ProviderEndpointImpl implements ClientAndProviderEndpoint {

	private static Logger log = Logger.getLogger(ClientAndProviderEndpointImpl.class.getName());

	
	private Stub stub = null;

	public Stub getStub() {
		if(this.stub == null) {
			this.stub = new StubImpl(this);
		}
		return this.stub;
	}

	public void setStub(Stub stub) {
		this.stub = stub;
	}

	public Exchange createExchange() throws ExchangeException {
		return this.getStub().createExchange();
	}

	@Override
	public void accept(Exchange exchange) throws TransportException {
		super.accept(exchange);
	}

	public void send(Exchange message) throws TransportException {
		this.getStub().send(message);
	}

	public Exchange sendSync(Exchange message, long timeout)
			throws TransportException {
		return this.getStub().sendSync(message, timeout);
	}

	public Description getDescriptionOfProviderEndpoint(QName name)
			throws ESBException {
		return this.getStub().getDescriptionOfProviderEndpoint(name);
	}

	


}
