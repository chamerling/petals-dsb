package org.ow2.petals.esb.external.protocol.soap.test;

import java.net.URI;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.ow2.petals.esb.external.protocol.soap.impl.SOAPSenderImpl;
import org.ow2.petals.esb.external.protocol.soap.impl.behaviour.proxy.SoapProviderProxyBehaviourImpl;
import org.ow2.petals.esb.kernel.ESBKernelFactoryImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.ESBKernelFactory;
import org.ow2.petals.esb.kernel.api.component.Component;
import org.ow2.petals.esb.kernel.api.endpoint.ClientEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.ProviderProxyEndpoint;
import org.ow2.petals.esb.kernel.api.entity.Client;
import org.ow2.petals.esb.kernel.api.entity.Provider;
import org.ow2.petals.esb.kernel.api.node.Node;
import org.ow2.petals.esb.kernel.api.service.Service;
import org.ow2.petals.esb.kernel.impl.component.ComponentImpl;
import org.ow2.petals.esb.kernel.impl.endpoint.ClientEndpointImpl;
import org.ow2.petals.esb.kernel.impl.endpoint.ProviderProxyEndpointImpl;
import org.ow2.petals.esb.kernel.impl.endpoint.context.EndpointInitialContextImpl;
import org.ow2.petals.esb.kernel.impl.entity.ClientImpl;
import org.ow2.petals.esb.kernel.impl.entity.ProviderImpl;
import org.ow2.petals.esb.kernel.impl.service.BusinessServiceImpl;
import org.ow2.petals.exchange.api.Exchange;
import org.petals.ow2.echo.Echo_EchoSOAP_Server;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import petals.ow2.org.exchange.PatternType;
import petals.ow2.org.exchange.StatusType;



public class SOAPSenderTest extends TestCase {

	private Component createESB() throws ESBException {
		boolean explorer = false;
		ESBKernelFactory factory = new ESBKernelFactoryImpl();
		Node petals = factory.createNode(new QName("http://petals.ow2.org", "node0"), explorer);
		petals.setHost("localhost");
		
		Component component = petals.createComponent(new QName("http://petals.ow2.org", "SOAPWorld"), "service", ComponentImpl.class);
		return component;
	}

	public void testSoapListener() throws Exception {
		Component component = createESB();

		// start ws echo service
		new Echo_EchoSOAP_Server();
        System.out.println("Server ready..."); 
		
		
		Provider provider = component.createProvider(new QName("http://ibm.com","provider"), "service", ProviderImpl.class);
		Service providerService = provider.createService(new QName("http://ow2.petals.org/echo/", "echo"), "service", BusinessServiceImpl.class);
		ProviderProxyEndpoint providerEndpoint = providerService.createProviderEndpoint("echoSOAP", "proxy-provider-service", ProviderProxyEndpointImpl.class, new EndpointInitialContextImpl(5));
		providerEndpoint.setBehaviourClass(SoapProviderProxyBehaviourImpl.class);
		providerEndpoint.setExternalAddress("http://localhost:9001/echo");
		providerEndpoint.setWSDLDescriptionAddress(new URI("./src/test/resources/wsdl/echo.wsdl"));
		providerEndpoint.getExternalSenders().put("soap", new SOAPSenderImpl());
		

		Client client = component.createClient(new QName("http://sun.com","consumer"), "service", ClientImpl.class);
		ClientEndpoint clientEndpoint = client.createClientEndpoint(new QName("http://petals.ow2.org/echo", "myClientEchoEndpoint"), "service", ClientEndpointImpl.class, new EndpointInitialContextImpl(5));

		
		// create exchange
		Exchange exchange = clientEndpoint.createExchange();
		exchange.setDestination(providerEndpoint.getQName());
		exchange.setPattern(PatternType.IN_OUT);
		exchange.setStatus(StatusType.ACTIVE);
		exchange.setOperation(new QName("http://ow2.petals.org/echo/", "echo").toString());
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element body = doc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "Body");
		body.setPrefix("soap-env");
		Element root = doc.createElementNS("http://ow2.petals.org/echo/", "echo");
		root.setPrefix("echo");
		Element in = doc.createElement("in");
		in.setTextContent("hello world!!!");
		body.appendChild(root);
		root.appendChild(in);
		doc.appendChild(body);
		exchange.getIn().getBody().setContent(doc);

		// send exchange
		Exchange response = clientEndpoint.sendSync(exchange, 0);


		// print response
		//System.out.println("final response = " + XMLPrettyPrinter.prettyPrint(response.getOut().getBody().getContent()));

		assertEquals("hello world!!!", response.getOut().getBody().getContent().getDocumentElement().getTextContent());


		//Thread.sleep(100000000);
		

	}

}
