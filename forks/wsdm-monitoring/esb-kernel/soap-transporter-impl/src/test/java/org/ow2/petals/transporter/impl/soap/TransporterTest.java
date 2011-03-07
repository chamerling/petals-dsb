package org.ow2.petals.transporter.impl.soap;

import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ow2.petals.exchange.ExchangeFactory;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.exchange.api.ExchangeException;
import org.ow2.petals.transporter.api.transport.TransportException;
import org.ow2.petals.transporter.api.transport.Transporter;
import org.ow2.petals.transporter.impl.soap.SOAPTransporterImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import petals.ow2.org.exchange.RoleType;
import petals.ow2.org.exchange.StatusType;

import junit.framework.TestCase;



public class TransporterTest extends TestCase {


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
		ex.setUuid(UUID.randomUUID());
		ex.setSource(new QName("http://petals.ow2.org", "mySource"));
		ex.setDestination(new QName("http://petals.ow2.org", "myDestination"));
		ex.setInterface(new QName("http://petals.ow2.org", "myItf"));
		ex.setOperation("myOperation");
		ex.setRole(RoleType.CONSUMER);
		ex.setStatus(StatusType.ACTIVE);

		Document request = this.builderFactory.newDocumentBuilder().newDocument();
		Element text = request.createElementNS("http://petals.ow2.org/", "text");
		text.setTextContent("helloWorld !!!");
		request.appendChild(text);
		ex.getIn().getBody().setContent(request);

		return ex;
	}

	public void testPushPullWithoutId() throws TransportException, ExchangeException, ParserConfigurationException, InterruptedException {

		Transporter transporter1 = new SOAPTransporterImpl(new QName("http://petals.ow2.org/transporter/", "node1"), "http://localhost:9004/transport");
		((SOAPTransporterImpl)transporter1).getListOfTransporters().put(new QName("http://petals.ow2.org/transporter/", "node2"), "http://localhost:9005/transport");

		Transporter transporter2 = new SOAPTransporterImpl(new QName("http://petals.ow2.org/transporter/", "node2"), "http://localhost:9005/transport");
		((SOAPTransporterImpl)transporter2).getListOfTransporters().put(new QName("http://petals.ow2.org/transporter/", "node1"), "http://localhost:9004/transport");


		//	Thread.sleep(1000000000);

		Exchange exchange1 = this.createExchange();
		transporter1.push(exchange1, new QName("http://petals.ow2.org/transporter/", "node2"));

		Exchange exchange2 = transporter1.pull(new QName("http://petals.ow2.org", "myDestination"), new QName("http://petals.ow2.org/transporter/", "node2"));

		assertNotNull(exchange2);
		System.out.println("!!!!!!!!!!!!!!!!!!! exchange2:\n" + exchange2);

		((SOAPTransporterImpl)transporter1).stop();
		((SOAPTransporterImpl)transporter2).stop();
	}

	public void testPushPullWithId() throws TransportException, ExchangeException, ParserConfigurationException, InterruptedException {

		Transporter transporter1 = new SOAPTransporterImpl(new QName("http://petals.ow2.org/transporter/", "node1"), "http://localhost:9004/transport");
		((SOAPTransporterImpl)transporter1).getListOfTransporters().put(new QName("http://petals.ow2.org/transporter/", "node2"), "http://localhost:9005/transport");

		Transporter transporter2 = new SOAPTransporterImpl(new QName("http://petals.ow2.org/transporter/", "node2"), "http://localhost:9005/transport");
		((SOAPTransporterImpl)transporter2).getListOfTransporters().put(new QName("http://petals.ow2.org/transporter/", "node1"), "http://localhost:9004/transport");


		//	Thread.sleep(1000000000);

		Exchange exchange1 = this.createExchange();
		System.out.println("exchange1.getUuid() = " + exchange1.getUuid());
		transporter1.push(exchange1, new QName("http://petals.ow2.org/transporter/", "node2"));

		Exchange exchange2 = transporter1.pull(exchange1.getUuid(), new QName("http://petals.ow2.org", "myDestination"), new QName("http://petals.ow2.org/transporter/", "node2"));

		assertNotNull(exchange2);
		System.out.println("!!!!!!!!!!!!!!!!!!! exchange2:\n" + exchange2);

		((SOAPTransporterImpl)transporter1).stop();
		((SOAPTransporterImpl)transporter2).stop();
	}


	public void testPushOnError() throws ExchangeException, ParserConfigurationException, InterruptedException, TransportException {

		Transporter transporter1 = new SOAPTransporterImpl(new QName("http://petals.ow2.org/transporter/", "node1"), "http://localhost:9004/transport");
		((SOAPTransporterImpl)transporter1).getListOfTransporters().put(new QName("http://petals.ow2.org/transporter/", "node2"), "http://localhost:9005/transport");

		Transporter transporter2 = new SOAPTransporterImpl(new QName("http://petals.ow2.org/transporter/", "node2"), "http://localhost:9005/transport");
		((SOAPTransporterImpl)transporter2).getListOfTransporters().put(new QName("http://petals.ow2.org/transporter/", "node1"), "http://localhost:9004/transport");


		//	Thread.sleep(1000000000);

		Exchange exchange1 = this.createExchange();
		transporter1.push(exchange1, new QName("http://petals.ow2.org/transporter/", "node2"));

		Exchange exchange2 = null;
		try {
			exchange2 = transporter1.pull(exchange1.getUuid(), new QName("http://petals.ow2.org", "myErrorDestination"), new QName("http://petals.ow2.org/transporter/", "node2"));
			fail();
		} catch (TransportException e) {
			System.out.println("ERROR: " + e.getMessage());
			assertTrue(e.getMessage().contains("Impossible to find queue corresponding to this endpoint "));
		} finally {
			((SOAPTransporterImpl)transporter1).stop();
			((SOAPTransporterImpl)transporter2).stop();
		}
	}

}
