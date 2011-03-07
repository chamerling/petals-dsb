package org.ow2.petals.esb.impl.test.util;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.AbstractBehaviourImpl;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.Behaviour;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.BusinessException;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.MarshallerException;
import org.ow2.petals.exchange.api.Exchange;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class EchoSoapBehaviour extends AbstractBehaviourImpl<String> implements Behaviour<String> {

	public EchoSoapBehaviour(Endpoint ep) {
		super(ep);
	}

	public void execute(Exchange exchange) throws BusinessException {
		try {
			// get request
			Document request = exchange.getIn().getBody().getContent();

			// marshall request
			String text = this.marshall(request);

			// Business: print text
			System.out.println("echo: " + text);
			

			// create response
			Document response = this.unmarshall(text);

			// set response in exchange
			exchange.getOut().getBody().setContent(response);
		} catch (MarshallerException e) {
			throw new BusinessException(e);
		}		
	}

	public String marshall(Document document) throws MarshallerException  {
		String res = null;
		NodeList echos = document.getDocumentElement().getElementsByTagNameNS("http://ow2.petals.org/echo/", "echo");
		if(echos != null && echos.getLength() == 1) {
			NodeList ins = ((Element)echos.item(0)).getElementsByTagName("in");
			if(ins != null && ins.getLength() == 1) {
				res = ((Element)ins.item(0)).getTextContent();
			}
		}
		return res;
	}

	public Document unmarshall(String object) throws MarshallerException {
		Document doc = null;
		try {
			// create the document
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element body = doc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "Body");
			body.setPrefix("soapenv");
			Element echo = doc.createElementNS("http://ow2.petals.org/echo/", "echo");
			echo.setPrefix("echo");
			body.appendChild(echo);
			
			Element out = doc.createElement("out");
			out.setTextContent(object);
			echo.appendChild(out);
			
			doc.appendChild(body);
		} catch (ParserConfigurationException e) {
			throw new MarshallerException(e);
		}
		return doc;
	}



}
