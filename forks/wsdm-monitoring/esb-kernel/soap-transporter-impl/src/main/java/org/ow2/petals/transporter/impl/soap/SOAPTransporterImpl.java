package org.ow2.petals.transporter.impl.soap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.easywsdl.schema.api.abstractElmt.AbstractSchemaElementImpl;
import org.ow2.easywsdl.schema.api.extensions.NamespaceMapperImpl;
import org.ow2.easywsdl.schema.api.extensions.SchemaLocatorImpl;
import org.ow2.petals.base.fractal.impl.FractalComponentImpl;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.exchange.api.ExchangeException;
import org.ow2.petals.exchange.impl.ExchangeImpl;
import org.ow2.petals.transporter.TransportException_Exception;
import org.ow2.petals.transporter.api.transport.TransportContext;
import org.ow2.petals.transporter.api.transport.TransportException;
import org.ow2.petals.transporter.api.transport.Transporter;

import petals.ow2.org.exchange.ExchangeType;


@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller="primitive")
public class SOAPTransporterImpl extends FractalComponentImpl implements Transporter {

	private Transporter_TransporterSOAP_Client client;

	private Transporter_TransporterSOAP_Server server;

	private SOAPTransportContext context = new SOAPTransportContext();

	public SOAPTransporterImpl() throws ExchangeException {
		client = new Transporter_TransporterSOAP_Client();
	}

	public SOAPTransporterImpl(QName endpoint, String address) throws ExchangeException {
		client = new Transporter_TransporterSOAP_Client();
		SOAPTransportContext context = new SOAPTransportContext();
		context.setNodeName(endpoint);
		context.setHttpAddress(address);
		this.setContext(context);
	}
	
	

	public TransportContext getContext() {
		return this.context;
	}

	public void setContext(TransportContext context) {
		this.context = (SOAPTransportContext) context;
		if(server != null) {
			server.stop();
		}
		server = new Transporter_TransporterSOAP_Server(this.context.getHttpAddress());
	}

	public Map<QName, String> getListOfTransporters() {
		return this.client.getListOfTransporters();
	}

	public Exchange pull(QName providerEndpointName, QName nodeEndpointName) throws TransportException {
		Exchange res = null;
		ExchangeType exchange = null;
		try {
			if(providerEndpointName == null) {
				throw new TransportException("provider endpoint name cannot be null");	
			}
			if(nodeEndpointName == null) {
				throw new TransportException("node name cannot be null");	
			}
			if(this.context.getNodeName() == null) {
				throw new TransportException("node name cannot be null");	
			}

			if(nodeEndpointName.equals(this.context.getNodeName())) {
				exchange = this.server.pull(providerEndpointName.toString());
			} else {

				String endpointAddress = this.client.getListOfTransporters().get(nodeEndpointName);
				if(endpointAddress == null) {
					throw new TransportException("Impossible to find transporter address to endpoint:" + endpointAddress);
				}
				
				exchange = this.client.pull(providerEndpointName, nodeEndpointName);
			}

			if(exchange != null) {
				res = new ExchangeImpl(new URI("."), exchange, new NamespaceMapperImpl(), new SchemaLocatorImpl());
			}
		} catch (URISyntaxException e) {
			throw new TransportException(e);
		} catch (TransportException_Exception e) {
			throw new TransportException(e);
		}

		return res;
	}

	public Exchange pull(UUID uuid, QName providerEndpointName, QName nodeEndpointName) throws TransportException {
		Exchange res = null;
		ExchangeType exchange = null;
		try {
			if(providerEndpointName == null) {
				throw new TransportException("provider endpoint name cannot be null");	
			}
			if(nodeEndpointName == null) {
				throw new TransportException("node name cannot be null");	
			}
			if(this.context.getNodeName() == null) {
				throw new TransportException("node name cannot be null");	
			}
			
			if(nodeEndpointName.equals(this.context.getNodeName())) {
				org.ow2.petals.transporter.PullWithId _pullWithId_parameters = new org.ow2.petals.transporter.PullWithId();
				_pullWithId_parameters.setUuid(uuid.toString());
				_pullWithId_parameters.setEndpointName(providerEndpointName.toString());
				exchange = this.server.pullWithId(_pullWithId_parameters);
			} else {
				String endpointAddress = this.client.getListOfTransporters().get(nodeEndpointName);
				if(endpointAddress == null) {
					throw new TransportException("Impossible to find transporter address to endpoint:" + endpointAddress);
				}
				exchange = this.client.pullWithId(uuid, providerEndpointName, nodeEndpointName);
			}

			if(exchange != null) {
				res = new ExchangeImpl(new URI("."), exchange, new NamespaceMapperImpl(), new SchemaLocatorImpl());
			}
		} catch (URISyntaxException e) {
			throw new TransportException(e);
		} catch (TransportException_Exception e) {
			throw new TransportException(e);
		}

		return res;
	}

	public void push(Exchange exchange, QName endpointNodeName) throws TransportException {
		try {
			if(endpointNodeName == null) {
				throw new TransportException("endpoint name cannot be null");	
			}
			if(this.context.getNodeName() == null) {
				throw new TransportException("node name cannot be null");	
			}

			ExchangeType exch = (ExchangeType) ((AbstractSchemaElementImpl)exchange).getModel();

			if(endpointNodeName.equals(this.context.getNodeName())) {
				org.ow2.petals.transporter.PushRequest _push_parameters = new org.ow2.petals.transporter.PushRequest();
				_push_parameters.setExchange(exch);
				_push_parameters.setEndpointNodeName(endpointNodeName.toString());
				this.server.push(_push_parameters);
			} else {
				String endpointAddress = this.client.getListOfTransporters().get(endpointNodeName);
				if(endpointAddress == null) {
					throw new TransportException("Impossible to find transporter address to endpoint:" + endpointAddress);
				}
				this.client.push(endpointNodeName, exch);
			}		
		} catch (TransportException_Exception e) {
			throw new TransportException(e);
		}
	}


	public void stop() {
		this.server.stop();
	}
}
