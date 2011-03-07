package org.ow2.petals.esb.kernel.impl.endpoint.behaviour.proxy;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Logger;

import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.WSDL4ComplexWsdlException;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.ClientProxyEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.AbstractBehaviourImpl;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.BusinessException;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.MarshallerException;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.proxy.ClientProxyBehaviour;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.transporter.api.transport.TransportException;
import org.w3c.dom.Document;

public class ClientProxyBehaviourImpl extends AbstractBehaviourImpl<Object> implements ClientProxyBehaviour {

	private static Logger log = Logger
	.getLogger(ClientProxyBehaviourImpl.class.getName());
	
	private boolean firstReading = true;

	private Map<URI, Document> imports = null;

	public ClientProxyBehaviourImpl(ClientProxyEndpoint ep) {
		super(ep);
	}

	public void execute(Exchange exchange) throws BusinessException {
		try {
			this.sendExchange2InternalProviderEndpoint(exchange);
		} catch (TransportException e) {
			throw new BusinessException(e);
		}
	}


	public Exchange sendExchange2InternalProviderEndpoint(Exchange exchange)
	throws TransportException {
		// set echange
		exchange.setSource(((ClientProxyEndpoint)this.endpoint).getQName());
		exchange.setDestination(((ClientProxyEndpoint)this.endpoint).getProviderEndpointName());
//		exchange.setPattern(PatternType.IN_OUT);

		log.info("external request send to " + exchange.getDestination()+ " on operation: " + exchange.getOperation());
	
		
		// send exchange
		Exchange ex = ((ClientProxyEndpoint)this.endpoint).sendSync(exchange, 0);
		
		
		return ex;
	}

	public Object marshall(Document document) throws MarshallerException  {
		throw new UnsupportedOperationException();
	}

	public Document unmarshall(Object object) throws MarshallerException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Description getDescription() {
		if(firstReading) {
			Description desc = null;
			try {

				desc = ((ClientProxyEndpoint)this.endpoint).getDescriptionOfProviderEndpoint(((ClientProxyEndpoint)this.endpoint).getProviderEndpointName());
				this.imports = desc.deleteImportedDocumentsInWsdl(new URI(((ClientProxyEndpoint)this.endpoint).getExternalAddress() +"?wsdl="));
			} catch (ESBException e) {
				e.printStackTrace();
				desc = null;
			} catch (WSDL4ComplexWsdlException e) {
				e.printStackTrace();
				desc = null;
			} catch (URISyntaxException e) {
				e.printStackTrace();
				desc = null;
			}
			if(desc != null) {
				super.setDescription(desc);
			}
			this.firstReading = false;
		}
		return super.getDescription();
	}


	@Override
	public void setDescription(Description desc) {
		if(desc == null) {
			this.firstReading = false;
		}
		throw new UnsupportedOperationException();
	}

	public Map<URI, Document> getImports() {
		return imports;
	}

}
