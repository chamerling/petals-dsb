package org.ow2.petals.esb.impl.test;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.ow2.petals.esb.impl.test.util.EchoBehaviour;
import org.ow2.petals.esb.kernel.ESBKernelFactoryImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.ESBKernelFactory;
import org.ow2.petals.esb.kernel.api.component.Component;
import org.ow2.petals.esb.kernel.api.endpoint.ClientEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.ProviderEndpoint;
import org.ow2.petals.esb.kernel.api.entity.Client;
import org.ow2.petals.esb.kernel.api.entity.Provider;
import org.ow2.petals.esb.kernel.api.node.Node;
import org.ow2.petals.esb.kernel.api.service.Service;
import org.ow2.petals.esb.kernel.impl.component.ComponentImpl;
import org.ow2.petals.esb.kernel.impl.endpoint.ClientEndpointImpl;
import org.ow2.petals.esb.kernel.impl.endpoint.ProviderEndpointImpl;
import org.ow2.petals.esb.kernel.impl.endpoint.context.EndpointInitialContextImpl;
import org.ow2.petals.esb.kernel.impl.entity.ClientImpl;
import org.ow2.petals.esb.kernel.impl.entity.ProviderImpl;
import org.ow2.petals.esb.kernel.impl.service.BusinessServiceImpl;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.exchange.api.ExchangeException;
import org.ow2.petals.transporter.api.transport.TransportException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import petals.ow2.org.exchange.PatternType;
import petals.ow2.org.exchange.StatusType;



public class ESBTest extends TestCase {

	private Node createESB() throws ESBException {
		boolean explorer = false;
		ESBKernelFactory factory = new ESBKernelFactoryImpl();
		Node petals = factory.createNode(new QName("http://petals.ow2.org", "node0"), explorer);
		return petals;
	}

	public void testSynchronousInOutSend() throws ESBException, TransportException, ParserConfigurationException, InterruptedException, ExchangeException {


		Node petals = createESB();

		Component component = petals.createComponent(new QName("http://petals.ow2.org","SOAPWorld"), "service", ComponentImpl.class);


		Provider provider = component.createProvider(new QName("http://ibm.com","provider"), "service", ProviderImpl.class);

		Client client = component.createClient(new QName("http://sun.com","consumer"), "service", ClientImpl.class);

		Service service = provider.createService(new QName("http://petals.ow2.org/wheather", "weather"), "service", BusinessServiceImpl.class);

		ProviderEndpoint providerEndpoint = service.createProviderEndpoint("myWeatherEndpoint", "service", ProviderEndpointImpl.class, new EndpointInitialContextImpl(5));
		providerEndpoint.setBehaviourClass(EchoBehaviour.class);

		ClientEndpoint clientEndpoint = client.createClientEndpoint(new QName("http://petals.ow2.org/wheather", "myClientWheatherEndpoint"), "service", ClientEndpointImpl.class,new EndpointInitialContextImpl(5));

		try {
			// create exchange
			Exchange exchange = clientEndpoint.createExchange();
			exchange.setDestination(providerEndpoint.getQName());
			exchange.setPattern(PatternType.IN_OUT);
			exchange.setStatus(StatusType.ACTIVE);
			exchange.setOperation(new QName("http://petals.ow2.org/wheather", "getWeather").toString());
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root = doc.createElement("text");
			root.setTextContent("hello world!!!");
			doc.appendChild(root);
			exchange.getIn().getBody().setContent(doc);



			// send exchange
			Exchange response = clientEndpoint.sendSync(exchange, 0);


			// print response
			System.out.println("response = " + response.getOut().getBody().getContent().getDocumentElement().getTextContent());

			assertEquals("hello world!!!", response.getOut().getBody().getContent().getDocumentElement().getTextContent());

		} finally {
			petals.getTransportersManager().stop();
			petals.getListenersManager().shutdownAllListeners();
		}
		//		Thread.sleep(1000000000);
	}

	public void testASynchronousInOutSend() throws ESBException, TransportException, ParserConfigurationException, InterruptedException, ExchangeException {
		Node petals = createESB();

		Component component = petals.createComponent(new QName("http://petals.ow2.org","SOAPWorld"), "service", ComponentImpl.class);


		Provider provider = component.createProvider(new QName("http://ibm.com","provider"), "service", ProviderImpl.class);

		Client client = component.createClient(new QName("http://sun.com","consumer"), "service", ClientImpl.class);

		Service service = provider.createService(new QName("http://petals.ow2.org/wheather", "weather"), "service", BusinessServiceImpl.class);

		ProviderEndpoint providerEndpoint = service.createProviderEndpoint("myWeatherEndpoint", "service", ProviderEndpointImpl.class, new EndpointInitialContextImpl(5));
		providerEndpoint.setBehaviourClass(EchoBehaviour.class);

		ClientEndpoint clientEndpoint = client.createClientEndpoint(new QName("http://petals.ow2.org/wheather", "myClientWheatherEndpoint"), "service", ClientEndpointImpl.class, new EndpointInitialContextImpl(5));
		try {
			// create exchange
			Exchange exchange = clientEndpoint.createExchange();
			exchange.setDestination(providerEndpoint.getQName());
			exchange.setPattern(PatternType.IN_OUT);
			exchange.setStatus(StatusType.ACTIVE);
			exchange.setOperation(new QName("http://petals.ow2.org/wheather", "getWeather").toString());
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root = doc.createElement("text");
			root.setTextContent("hello world!!!");
			doc.appendChild(root);
			exchange.getIn().getBody().setContent(doc);

			// send exchange
			clientEndpoint.send(exchange);


			// wait and get response by the client
			Exchange response = null;
			while(response == null) {
				try {
					response = clientEndpoint.getNode().getTransportersManager().pull(exchange.getUuid(), clientEndpoint.getQName(), clientEndpoint.getNode().getQName());
				} catch (TransportException e) {
					// do nothing
				}
			}

			// print response
			System.out.println("response = " + response.getOut().getBody().getContent().getDocumentElement().getTextContent());

			assertEquals("hello world!!!", response.getOut().getBody().getContent().getDocumentElement().getTextContent());


		} finally {
			petals.getTransportersManager().stop();
			petals.getListenersManager().shutdownAllListeners();
		}
		//		Thread.sleep(1000000000);
	}

}
