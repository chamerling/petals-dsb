/**
 * 
 */
package org.ow2.petals.exchange;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ow2.easywsdl.schema.util.XMLPrettyPrinter;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.exchange.api.ExchangeException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import petals.ow2.org.exchange.RoleType;
import petals.ow2.org.exchange.StatusType;

import junit.framework.TestCase;

/**
 * @author Nico
 *
 */
public class TestExchange extends TestCase {

	private DocumentBuilderFactory builderFactory;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		this.builderFactory = DocumentBuilderFactory.newInstance();
		this.builderFactory.setNamespaceAware(true);
	}

	
	public Exchange createExchange() throws ExchangeException, ParserConfigurationException {
		Exchange ex = ExchangeFactory.getInstance().newExchange();
		ex.setSource(new QName("http://petals.ow2.org", "mySource"));
		ex.setDestination(new QName("http://petals.ow2.org", "myDestination"));
		ex.setInterface(new QName("http://petals.ow2.org", "myItf"));
		ex.setOperation("myOperation");
		ex.setRole(RoleType.CONSUMER);
		ex.setStatus(StatusType.DONE);
		
		Document request = this.builderFactory.newDocumentBuilder().newDocument();
		Element text = request.createElementNS("http://petals.ow2.org/", "text");
		text.setTextContent("helloWorld !!!");
		request.appendChild(text);
		ex.getIn().getBody().setContent(request);
		
		return ex;
	}
	
	public void test_writeExchange() throws ExchangeException, ParserConfigurationException {
		Exchange ex = this.createExchange();
		
		Document doc = ExchangeFactory.getInstance().newExchangeWriter().getDocument(ex);
		assertNotNull(doc);
		
		System.out.println(XMLPrettyPrinter.prettyPrint(doc));
	}
	
	public void test_readExchange() throws ExchangeException, ParserConfigurationException {
		Exchange ex = this.createExchange();
		
		Document doc = ExchangeFactory.getInstance().newExchangeWriter().getDocument(ex);
		assertNotNull(doc);
		
		ex = ExchangeFactory.getInstance().newExchangeReader().readExchange(doc);
		assertNotNull(ex);
		
		
		System.out.println(XMLPrettyPrinter.prettyPrint(doc));
	}
}
