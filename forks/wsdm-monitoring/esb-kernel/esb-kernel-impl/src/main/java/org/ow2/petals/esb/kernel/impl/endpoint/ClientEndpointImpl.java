package org.ow2.petals.esb.kernel.impl.endpoint;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.ClientEndpoint;
import org.ow2.petals.esb.kernel.api.transport.Stub;
import org.ow2.petals.esb.kernel.impl.transport.StubImpl;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.exchange.api.ExchangeException;
import org.ow2.petals.transporter.api.transport.TransportException;

@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller="primitive")
public class ClientEndpointImpl extends EndpointImpl implements ClientEndpoint {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Stub stub;
	
	private QName name;
	
	public ClientEndpointImpl() {
		this.stub = new StubImpl(this);
	}

	public void send(Exchange exchange) throws TransportException {
		this.stub.send(exchange);
	}
	
	public Exchange sendSync(Exchange exchange, long timeout) throws TransportException {
		return this.stub.sendSync(exchange, timeout);
	}
	
	public Stub getStub() {
		return this.stub;
	}

	public void setStub(Stub stub) {
		this.stub = stub;
	}

	public void setQName(QName name) {
		this.name = name;
	}

	@Override
	public QName getQName() {
		return this.name;
	}

	public Exchange createExchange() throws ExchangeException {
		return this.stub.createExchange();
	}

	public Description getDescriptionOfProviderEndpoint(QName name)
			throws ESBException {
		return this.stub.getDescriptionOfProviderEndpoint(name);
	}



}
