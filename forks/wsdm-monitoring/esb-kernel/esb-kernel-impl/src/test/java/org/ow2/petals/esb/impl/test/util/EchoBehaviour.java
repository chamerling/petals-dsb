package org.ow2.petals.esb.impl.test.util;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.AbstractBehaviourImpl;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.BusinessException;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.MarshallerException;
import org.ow2.petals.exchange.api.Exchange;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EchoBehaviour extends AbstractBehaviourImpl<String> {

	public EchoBehaviour(Endpoint ep) {
		super(ep);
	}

	public void execute(Exchange exchange) throws BusinessException {
		try {
			// get request
			Document request = exchange.getIn().getBody().getContent();

			// marshall request
			String text = this.marshall(request);

			// Business: print text
			System.out.println(text);

			// create response
			Document response = this.unmarshall(text);

			// set response in exchange
			exchange.getOut().getBody().setContent(response);
		} catch (MarshallerException e) {
			throw new BusinessException(e);
		}		
	}

	public String marshall(Document document) throws MarshallerException  {
		return document.getDocumentElement().getTextContent();
	}

	public Document unmarshall(String object) throws MarshallerException {
		Document doc = null;
		try {
			// create the document
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root = doc.createElement("text");
			root.setTextContent(object);
			doc.appendChild(root);
		} catch (ParserConfigurationException e) {
			throw new MarshallerException(e);
		}
		return doc;
	}



}
