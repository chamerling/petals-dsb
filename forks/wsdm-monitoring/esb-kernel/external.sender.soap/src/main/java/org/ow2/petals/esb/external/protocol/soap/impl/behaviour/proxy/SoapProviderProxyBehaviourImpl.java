package org.ow2.petals.esb.external.protocol.soap.impl.behaviour.proxy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.output.DOMOutputter;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.WSDL4ComplexWsdlFactory;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.Endpoint;
import org.ow2.easywsdl.wsdl.api.Service;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.abstractItf.AbsItfOperation.MEPPatternConstants;
import org.ow2.petals.esb.external.protocol.soap.impl.SOAPSenderImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.ProviderProxyEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.AbstractBehaviourImpl;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.BusinessException;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.MarshallerException;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.proxy.ProviderProxyBehaviour;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.transporter.api.transport.TransportException;
import org.w3c.dom.Document;

import petals.ow2.org.exchange.PatternType;



public class SoapProviderProxyBehaviourImpl extends AbstractBehaviourImpl<Object> implements ProviderProxyBehaviour {


	private DocumentBuilderFactory builder;


	private boolean isAdaptedDescription = false;

	private SOAPSenderImpl sender = null;

	public SoapProviderProxyBehaviourImpl(ProviderProxyEndpoint ep) {
		super(ep);
		this.builder = DocumentBuilderFactory.newInstance();
		this.builder.setNamespaceAware(true);
		this.sender = new SOAPSenderImpl();
	}

	@Override
	public Description getDescription() {
		if(!isAdaptedDescription) {
			Description desc = null;
			try {
				desc = WSDL4ComplexWsdlFactory.newInstance().newWSDLReader().read(((ProviderProxyEndpoint)this.endpoint).getWSDLDescriptionAddress().toURL());
				for(Service s: desc.getServices()) {
					Iterator<Endpoint> it = s.getEndpoints().iterator();
					while(it.hasNext()) {
						Endpoint ep = it.next();
						if(ep.getAddress().equals(((ProviderProxyEndpoint)this.endpoint).getExternalAddress())) {
							ep.setAddress(this.endpoint.getQName().toString());
						} else {
							s.removeEndpoint(ep.getName());
							it = s.getEndpoints().iterator();
						}
					}
				}
			} catch (WSDLException e) {
				desc = null;
			} catch (MalformedURLException e) {
				desc = null;
			} catch (URISyntaxException e) {
				desc = null;
			} catch (IOException e) {
				desc = null;
			}
			if(desc != null) {
				super.setDescription(desc);
			}
			isAdaptedDescription = true;
		}
		return super.getDescription();
	}


	@Override
	public void setDescription(Description desc) {
		super.setDescription(desc);
		isAdaptedDescription = false;
	}


	public void execute(Exchange exchange) throws BusinessException {
		try {
			this.sendExchange2ExternalProviderEndpoint(exchange);
		} catch (TransportException e) {
			throw new BusinessException(e);
		}
	}


	public Exchange sendExchange2ExternalProviderEndpoint(Exchange exchange)
	throws TransportException {
		try {

			Document request = createSOAPMessageRequest(exchange.getIn().getBody().getContent());


			Document response = this.sender.sendSoapRequest(request, ((ProviderProxyEndpoint)this.endpoint).getExternalAddress());

			if(exchange.getPattern().equals(PatternType.IN_OUT)) {
				this.setExchangeFromSoapResponse(exchange, response);
			}
		} catch (UnsupportedOperationException e) {
			throw new TransportException(e);
		} catch (ESBException e) {
			throw new TransportException(e);
		} catch (JDOMException e) {
			throw new TransportException(e);
		}
		return exchange;
	}





	private static Document createSOAPMessageRequest(Document msg) throws JDOMException {
		Document res = null;

		// TODO: create soap1.1 or 1.2 message

		Element env = new Element("Envelope", Namespace.getNamespace("soap-env", "http://schemas.xmlsoap.org/soap/envelope/"));
		env.addNamespaceDeclaration(Namespace.getNamespace("xsd", "http://www.w3.org/1999/XMLSchema"));
		env.addNamespaceDeclaration(Namespace.getNamespace("xsi", "http://www.w3.org/1999/XMLSchema-instance"));
		org.jdom.Document jdom = new org.jdom.Document(env);

		DOMBuilder builder = new DOMBuilder();
		org.jdom.Document jdomDocument = builder.build(msg);

		env.addContent(((Element)jdomDocument.getRootElement()).detach());

		DOMOutputter converter = new DOMOutputter();
		res = converter.output(jdom);

		return res;
	}

	private void setExchangeFromSoapResponse(Exchange exchange, Document soapResponse) throws ESBException {
		try {

			DOMBuilder builder = new DOMBuilder();
			org.jdom.Document soapResp = builder.build(soapResponse);

			// set the header
			Document header = this.builder.newDocumentBuilder().newDocument();
			List<Element> headers = soapResp.getRootElement().getChildren("Header", Namespace.getNamespace("http://schemas.xmlsoap.org/soap/envelope/") );
			if((headers != null)&&(headers.size() == 1)) {
				DOMOutputter converter = new DOMOutputter();
				org.w3c.dom.Document domHeader = converter.output(new org.jdom.Document((Element) headers.get(0).detach()));
				header.appendChild(header.adoptNode(domHeader.getDocumentElement().cloneNode(true)));
			} else {
				headers = soapResp.getRootElement().getChildren("Header");
				if((headers != null)&&(headers.size() == 1)) {
					DOMOutputter converter = new DOMOutputter();
					org.w3c.dom.Document domHeader = converter.output(new org.jdom.Document((Element) headers.get(0).detach()));
					header.appendChild(header.adoptNode(domHeader.getDocumentElement().cloneNode(true)));
				}
			}
			exchange.getOut().getHeader().setContent(header);

			// set the body
			Document body = this.builder.newDocumentBuilder().newDocument();
			List<Element> bodies = soapResp.getRootElement().getChildren("Body", Namespace.getNamespace("http://schemas.xmlsoap.org/soap/envelope/") );
			if((bodies != null)&&(bodies.size() == 1)) {
				DOMOutputter converter = new DOMOutputter();
				org.w3c.dom.Document domBody = converter.output(new org.jdom.Document((Element) bodies.get(0).detach()));
				body.appendChild(body.adoptNode(domBody.getDocumentElement().cloneNode(true)));
			} else {
				bodies = soapResp.getRootElement().getChildren("Body");
				if((bodies != null)&&(bodies.size() == 1)) {
					DOMOutputter converter = new DOMOutputter();
					org.w3c.dom.Document domBody = converter.output(new org.jdom.Document((Element) bodies.get(0).detach()));
					body.appendChild(body.adoptNode(domBody.getDocumentElement().cloneNode(true)));
				} 
			}
			exchange.getOut().getBody().setContent(body);

		} catch (ParserConfigurationException e) {
			throw new ESBException(e);
		} catch (JDOMException e) {
			throw new ESBException(e);
		}
	}

	public Object marshall(Document document) throws MarshallerException  {
		throw new UnsupportedOperationException();
	}

	public Document unmarshall(Object object) throws MarshallerException {
		throw new UnsupportedOperationException();
	}


}
