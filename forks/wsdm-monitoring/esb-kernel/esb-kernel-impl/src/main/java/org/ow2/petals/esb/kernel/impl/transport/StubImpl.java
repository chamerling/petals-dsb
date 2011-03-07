package org.ow2.petals.esb.kernel.impl.transport;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ow2.easywsdl.extensions.wsdl4complexwsdl.WSDL4ComplexWsdlFactory;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.WSDL4ComplexWsdlReader;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.impl.inout.WSDL4ComplexWsdlReaderImpl;
import org.ow2.easywsdl.schema.util.XMLPrettyPrinter;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.WSDLReader.FeatureConstants;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.ClientEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.transport.Stub;
import org.ow2.petals.esb.kernel.api.transport.TransportersManager;
import org.ow2.petals.esb.kernel.api.transport.WakeUpKey;
import org.ow2.petals.exchange.ExchangeFactory;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.exchange.api.ExchangeException;
import org.ow2.petals.transporter.api.transport.TransportException;
import org.w3c.dom.Document;

import petals.ow2.org.exchange.PatternType;
import petals.ow2.org.exchange.RoleType;
import petals.ow2.org.exchange.StatusType;


public class StubImpl implements Stub {

	private static Logger log = Logger.getLogger(StubImpl.class.getName());

	private ClientEndpoint clientEndpoint;

	private DocumentBuilderFactory documentBuilderFactory;
	
	private WakeUpKey locked = new WakeUpKeyImpl();

	private static WSDL4ComplexWsdlReader reader;

	static {
		try {
			reader = getReader();
			Map<FeatureConstants, Object> features = new HashMap<FeatureConstants, Object>();
			features.put(FeatureConstants.IMPORT_DOCUMENTS, false);
			((WSDL4ComplexWsdlReaderImpl)reader).setFeatures(features);
		} catch (WSDLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public StubImpl(ClientEndpoint consumerEndpoint) {
		this.clientEndpoint = consumerEndpoint;
	}

	public TransportersManager getTransportersManager() {
		return this.clientEndpoint.getNode().getTransportersManager();
	}

	public Exchange createExchange() throws ExchangeException {
		Exchange res = ExchangeFactory.getInstance().newExchange();
		res.setUuid(UUID.randomUUID());
		res.setRole(RoleType.CONSUMER);
		res.setStatus(StatusType.ACTIVE);
		res.setSource(clientEndpoint.getQName());
		return res;
	}

	public void send(Exchange exchange) throws TransportException {
		log.finest("exchange sent to " + exchange.getDestination() + ":\n" + exchange);
		
		// find destination node
		Endpoint destination = this.clientEndpoint.getNode().getRegistry().getEndpoint(exchange.getDestination());
		if(destination == null) {
			throw new TransportException("Impossible to find node having this endpoint: " + exchange.getDestination());
		}
		
		// verify operation
		String operationName = exchange.getOperation();
		if(operationName == null) {
			throw new TransportException("Impossible to send message without operation");
		}
		try {
			QName.valueOf(operationName);
		} catch(IllegalArgumentException e) {
			throw new TransportException("The operation name must be a qname!!!");
		}
		
		this.getTransportersManager().push(exchange, destination.getNode().getQName());
	}

	public Exchange sendSync(Exchange exchange, long timeout) throws TransportException {
		log.finest("exchange sent to " + exchange.getDestination() + ":\n" + exchange);
		Exchange res = null; 
		
		// find destination node
		Endpoint destination = this.clientEndpoint.getNode().getRegistry().getEndpoint(exchange.getDestination());
		if(destination == null) {
			throw new TransportException("Impossible to find node having this endpoint: " + exchange.getDestination());
		}
		
		// verify operation
		String operationName = exchange.getOperation();
		if(operationName == null) {
			throw new TransportException("Impossible to send message without operation");
		}
		try {
			QName.valueOf(operationName);
		} catch(IllegalArgumentException e) {
			throw new TransportException("The operation name must be a qname!!!");
		}
		
		this.locked.setExchange(null);
		this.getTransportersManager().getStub2awake().put(exchange.getUuid(), this.locked);
		
		
		try {
			
			synchronized(this.locked) {
				this.getTransportersManager().push(exchange, destination.getNode().getQName());
				this.locked.wait(timeout);
			}
			
			res = this.locked.getExchange();
			
			if(res == null) {
				throw new TransportException("Timeout exceeded!!!!");
			}
			
			// copy out in fisrt exchange
			exchange.getOut().getHeader().setContent(res.getOut().getHeader().getContent());
			exchange.getOut().getBody().setContent(res.getOut().getBody().getContent());
			exchange.getError().getHeader().setContent(res.getError().getHeader().getContent());
			exchange.getError().getBody().setContent(res.getError().getBody().getContent());
			exchange.setStatus(res.getStatus());
			exchange.setRole(res.getRole());
			
		} catch (InterruptedException e) {
			// do nothing
			e.printStackTrace();
		}
		
		return res;
	}



	public Description getDescriptionOfProviderEndpoint(QName providerName) throws ESBException {
		Description desc = null;
		try {
			Exchange exchange = this.createExchange();
			exchange.setSource(this.clientEndpoint.getQName());
			exchange.setDestination(providerName);
			exchange.setOperation(new QName("org.ow2.petals.esb", "description").toString());
			exchange.setPattern(PatternType.IN_OUT);
			Document doc = this.getDocumentBuilderFactory().newDocumentBuilder().newDocument();
			doc.createElement("wsdlDescription");

			exchange.getIn().getBody().setContent(doc);

			Exchange response = this.sendSync(exchange, 0);

//			// The DOM Document needs to be converted into an InputStource
//			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			final StreamResult streamResult = new StreamResult(baos);
//			// FIXME: The Transformer creation is not thread-safe
//			final Transformer transformer = TransformerFactory.newInstance()
//			.newTransformer();
//			transformer.transform(new DOMSource(response.getOut().getBody().getContent()), streamResult);
//
//			final InputSource documentInputSource = new InputSource(
//					new ByteArrayInputStream(baos.toByteArray()));
//			documentInputSource.setSystemId(response.getOut().getBody().getContent().getBaseURI());
//			baos.flush();
//			baos.close();
			desc = getReader().read(response.getOut().getBody().getContent());
		} catch (ParserConfigurationException e) {
			throw new ESBException(e);
		} catch (TransportException e) {
			throw new ESBException(e);
		} catch (WSDLException e) {
			throw new ESBException(e);
		} catch (URISyntaxException e) {
			throw new ESBException(e);
		} catch (ExchangeException e) {
			throw new ESBException(e);
		} 
//		catch (TransformerConfigurationException e) {
//			throw new ESBException(e);
//		} catch (TransformerFactoryConfigurationError e) {
//			throw new ESBException(e);
//		} catch (TransformerException e) {
//			throw new ESBException(e);
//		} catch (IOException e) {
//			throw new ESBException(e);
//		}
		return desc;
	}

	public static WSDL4ComplexWsdlReader getReader() throws WSDLException {
		if(reader == null) {
			reader = WSDL4ComplexWsdlFactory.newInstance().newWSDLReader();
		}
		return reader;
	}

	public DocumentBuilderFactory getDocumentBuilderFactory() {
		if(documentBuilderFactory == null) {
			this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
			this.documentBuilderFactory.setNamespaceAware(true);
		}
		return documentBuilderFactory;
	}
}
