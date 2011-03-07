package org.ow2.petals.esb.external.protocol.soap.test;

import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.WSDL4ComplexWsdlFactory;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.petals.esb.external.protocol.soap.impl.SOAPListenerImpl;
import org.ow2.petals.esb.external.protocol.soap.test.util.EchoSoapBehaviour;
import org.ow2.petals.esb.kernel.ESBKernelFactoryImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.ESBKernelFactory;
import org.ow2.petals.esb.kernel.api.component.Component;
import org.ow2.petals.esb.kernel.api.endpoint.ClientProxyEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.ProviderEndpoint;
import org.ow2.petals.esb.kernel.api.entity.Client;
import org.ow2.petals.esb.kernel.api.entity.Provider;
import org.ow2.petals.esb.kernel.api.node.Node;
import org.ow2.petals.esb.kernel.api.service.Service;
import org.ow2.petals.esb.kernel.impl.component.ComponentImpl;
import org.ow2.petals.esb.kernel.impl.endpoint.ClientProxyEndpointImpl;
import org.ow2.petals.esb.kernel.impl.endpoint.ProviderEndpointImpl;
import org.ow2.petals.esb.kernel.impl.entity.ClientImpl;
import org.ow2.petals.esb.kernel.impl.entity.ProviderImpl;
import org.ow2.petals.esb.kernel.impl.service.BusinessServiceImpl;
import org.ow2.petals.esb.kernel.impl.endpoint.behaviour.proxy.ClientProxyBehaviourImpl;
import org.ow2.petals.esb.kernel.impl.endpoint.context.EndpointInitialContextImpl;



public class SOAPListenerTest extends TestCase {

	private Node createESB() throws ESBException {
		boolean explorer = false;
		ESBKernelFactory factory = new ESBKernelFactoryImpl();
		Node petals = factory.createNode(new QName("http://petals.ow2.org", "node0"), explorer);
		petals.setHost("localhost");
		
		return petals;
//		Component component = petals.createComponent(new QName("http://petals.ow2.org", "SOAPWorld"), "service", ComponentImpl.class);
//		return component;
	}

	public void testSoapListener() throws Exception {
		Node petals  = createESB();
		Component component = petals.createComponent(new QName("http://petals.ow2.org", "SOAPWorld"), "service", ComponentImpl.class);
		
		Provider provider = component.createProvider(new QName("http://ibm.com","provider"), "service", ProviderImpl.class);
		Service providerService = provider.createService(new QName("http://ow2.petals.org/echo/", "echo"), "service", BusinessServiceImpl.class);
		ProviderEndpoint providerEndpoint = providerService.createProviderEndpoint("echoSOAP", "service", ProviderEndpointImpl.class, new EndpointInitialContextImpl(5));
		providerEndpoint.setBehaviourClass(EchoSoapBehaviour.class);
		URL wsdlURL = Thread.currentThread().getContextClassLoader().getResource("wsdl/echo.wsdl");
		providerEndpoint.getBehaviour().setDescription(WSDL4ComplexWsdlFactory.newInstance().newWSDLReader().read(wsdlURL));

		Client client = component.createClient(new QName("http://sun.com","consumer"), "service", ClientImpl.class);
		ClientProxyEndpoint clientEndpoint = client.createClientEndpoint(new QName("http://ow2.petals.org/echo/", "echoSOAPExternal"), "proxy-client-service", ClientProxyEndpointImpl.class, new EndpointInitialContextImpl(5));
		clientEndpoint.setBehaviourClass(ClientProxyBehaviourImpl.class);
		clientEndpoint.setProviderEndpointName(providerEndpoint.getQName());
		SOAPListenerImpl soapListener = new SOAPListenerImpl(clientEndpoint); 
		clientEndpoint.getExternalListeners().put("SOAP", soapListener);


		//Thread.sleep(100000000);
		
		Thread.sleep(2000);
		
		// create WS client
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		org.apache.cxf.endpoint.Client echoWSCLient = dcf.createClient("http://localhost:8085/services/echoSOAPExternal?wsdl", Thread.currentThread().getContextClassLoader());

		Object[] res = echoWSCLient.invoke("echo", "test echo");
		System.out.println("Echo response: " + res[0]);
		

		assertEquals("test echo", res[0]);
		
		petals.getTransportersManager().stop();
		petals.getListenersManager().shutdownAllListeners();
//		clientEndpoint.getListenersManager().shutdownAllListeners();
//		providerEndpoint.getListenersManager().shutdownAllListeners();
	}

}
