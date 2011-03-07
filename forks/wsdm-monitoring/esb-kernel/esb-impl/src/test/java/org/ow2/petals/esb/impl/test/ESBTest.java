package org.ow2.petals.esb.impl.test;

import java.net.URI;
import java.net.URL;
import java.util.GregorianCalendar;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;

import junit.framework.TestCase;

import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.ow2.petals.esb.api.ESBFactory;
import org.ow2.petals.esb.impl.ESBFactoryImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.node.Node;
import org.petals.ow2.echo.Echo_EchoSOAP_Server;



public class ESBTest extends TestCase {

	
//	Node petals = null;
//	
//	@Override
//	protected void setUp() throws Exception {
//		// TODO Auto-generated method stub
//		super.setUp();
//		
//		petals = createESB();
//	}

	private Node createESB() throws ESBException {
		boolean explorer = false;
		ESBFactory factory = new ESBFactoryImpl();
		Node petals = factory.createNode(new QName("http://petals.ow2.org", "node0"), explorer);

		//Component component = petals.createComponent("SOAPWorld", "service", ComponentImpl.class);
		return petals;
	}

	public void testAdminEndpoint_CreateComponent() throws Exception {
	    Node petals = createESB();

	//	 Thread.sleep(10000000);

		// create WS client
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		org.apache.cxf.endpoint.Client adminWSCLient = dcf.createClient("http://localhost:8085/services/adminExternalEndpoint?wsdl", Thread.currentThread().getContextClassLoader());

		Object[] res = adminWSCLient.invoke("createComponent", "{http://petals.ow2.org}MyComponent", "service", "org.ow2.petals.esb.kernel.impl.component.ComponentImpl");
		System.out.println("createComponent response: " + res[0]);


		
		
		assertEquals("{http://petals.ow2.org}MyComponent", res[0]);
		
		adminWSCLient.destroy();
		petals.getTransportersManager().stop();
	}

	public void testAdminEndpoint_CreateClient() throws Exception {
		Node petals = createESB();

//		 Thread.sleep(10000000);

		// create WS client
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		org.apache.cxf.endpoint.Client adminWSCLient = dcf.createClient("http://localhost:8085/services/adminExternalEndpoint?wsdl", Thread.currentThread().getContextClassLoader());

		// create component
		Object[] res = adminWSCLient.invoke("createComponent", "{http://petals.ow2.org}MyComponent", "service", "org.ow2.petals.esb.kernel.impl.component.ComponentImpl");
		System.out.println("createComponent response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyComponent", res[0]);
		
		
		// create client
		res = adminWSCLient.invoke("createClient", "{http://petals.ow2.org}MyComponent", "{http://petals.ow2.org}MyClient", "service", "org.ow2.petals.esb.kernel.impl.entity.ClientImpl");
		System.out.println("createClient response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyClient", res[0]);
		
		adminWSCLient.destroy();
		petals.getTransportersManager().stop();
	}
	
	
	public void testAdminEndpoint_CreateProvider() throws Exception {
		Node petals = createESB();

//		 Thread.sleep(10000000);

		// create WS client
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		org.apache.cxf.endpoint.Client adminWSCLient = dcf.createClient("http://localhost:8085/services/adminExternalEndpoint?wsdl", Thread.currentThread().getContextClassLoader());

		// create component
		Object[] res = adminWSCLient.invoke("createComponent", "{http://petals.ow2.org}MyComponent", "service", "org.ow2.petals.esb.kernel.impl.component.ComponentImpl");
		System.out.println("createComponent response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyComponent", res[0]);
		
		
		// create provider
		res = adminWSCLient.invoke("createProvider", "{http://petals.ow2.org}MyComponent", "{http://petals.ow2.org}MyProvider", "service", "org.ow2.petals.esb.kernel.impl.entity.ProviderImpl");
		System.out.println("createProvider response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyProvider", res[0]);
		
		adminWSCLient.destroy();
		petals.getTransportersManager().stop();
	}
	
	
	public void testAdminEndpoint_CreateClientAndProvider() throws Exception {
		Node petals = createESB();

//		 Thread.sleep(10000000);

		// create WS client
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		org.apache.cxf.endpoint.Client adminWSCLient = dcf.createClient("http://localhost:8085/services/adminExternalEndpoint?wsdl", Thread.currentThread().getContextClassLoader());

		// create component
		Object[] res = adminWSCLient.invoke("createComponent", "{http://petals.ow2.org}MyComponent", "service", "org.ow2.petals.esb.kernel.impl.component.ComponentImpl");
		System.out.println("createComponent response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyComponent", res[0]);
		
		
		// create clientAndProvider
		res = adminWSCLient.invoke("createClientAndProvider", "{http://petals.ow2.org}MyComponent", "{http://petals.ow2.org}MyClientAndProvider", "service", "org.ow2.petals.esb.kernel.impl.entity.ClientAndProviderImpl", "service", "org.ow2.petals.esb.kernel.impl.entity.ClientImpl", "service", "org.ow2.petals.esb.kernel.impl.entity.ProviderImpl");
		System.out.println("createClientAndProvider response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyClientAndProvider", res[0]);
		
		adminWSCLient.destroy();
		petals.getTransportersManager().stop();
	}

	
	public void testAdminEndpoint_CreateService() throws Exception {
		Node petals = createESB();

//		 Thread.sleep(10000000);

		// create WS client
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		org.apache.cxf.endpoint.Client adminWSCLient = dcf.createClient("http://localhost:8085/services/adminExternalEndpoint?wsdl", Thread.currentThread().getContextClassLoader());

		// create component
		Object[] res = adminWSCLient.invoke("createComponent", "{http://petals.ow2.org}MyComponent", "service", "org.ow2.petals.esb.kernel.impl.component.ComponentImpl");
		System.out.println("createComponent response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyComponent", res[0]);
		
		
		// create provider
		res = adminWSCLient.invoke("createProvider", "{http://petals.ow2.org}MyComponent", "{http://petals.ow2.org}MyProvider", "service", "org.ow2.petals.esb.kernel.impl.entity.ProviderImpl");
		System.out.println("createProvider response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyProvider", res[0]);
		
		
		// create service
		res = adminWSCLient.invoke("createService", "{http://petals.ow2.org}MyProvider", "{http://petals.ow2.org}MyService", "service", "org.ow2.petals.esb.kernel.impl.service.BusinessServiceImpl");
		System.out.println("createService response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyService", res[0]);
		
		adminWSCLient.destroy();
		petals.getTransportersManager().stop();
	}
	
	
	public void testAdminEndpoint_CreateClientEndpoint() throws Exception {
		Node petals = createESB();

//		 Thread.sleep(10000000);

		// create WS client
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		org.apache.cxf.endpoint.Client adminWSCLient = dcf.createClient("http://localhost:8085/services/adminExternalEndpoint?wsdl", Thread.currentThread().getContextClassLoader());

		// create component
		Object[] res = adminWSCLient.invoke("createComponent", "{http://petals.ow2.org}MyComponent", "service", "org.ow2.petals.esb.kernel.impl.component.ComponentImpl");
		System.out.println("createComponent response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyComponent", res[0]);
		
		// create clientAndProvider
		res = adminWSCLient.invoke("createClientAndProvider", "{http://petals.ow2.org}MyComponent", "{http://petals.ow2.org}MyClientAndProvider", "service", "org.ow2.petals.esb.kernel.impl.entity.ClientAndProviderImpl", "service", "org.ow2.petals.esb.kernel.impl.entity.ClientImpl", "service", "org.ow2.petals.esb.kernel.impl.entity.ProviderImpl");
		System.out.println("createClientAndProvider response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyClientAndProvider", res[0]);
		
		
		// create clientEndpoint
		res = adminWSCLient.invoke("createClientEndpoint", "{http://petals.ow2.org}MyClientAndProvider", "{http://petals.ow2.org}MyClientEndpoint", "proxy-client-service", "org.ow2.petals.esb.kernel.impl.endpoint.ClientProxyEndpointImpl", "org.ow2.petals.esb.kernel.impl.endpoint.behaviour.proxy.ClientProxyBehaviourImpl");
		System.out.println("createClientEndpoint response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyClientEndpoint", res[0]);
		
		adminWSCLient.destroy();
		petals.getTransportersManager().stop();
	}
	
	
	public void testAdminEndpoint_CreateProviderEndpoint() throws Exception {
		Node petals = createESB();

//		 Thread.sleep(10000000);

		// create WS client
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		org.apache.cxf.endpoint.Client adminWSCLient = dcf.createClient("http://localhost:8085/services/adminExternalEndpoint?wsdl", Thread.currentThread().getContextClassLoader());

		// create component
		Object[] res = adminWSCLient.invoke("createComponent", "{http://petals.ow2.org}MyComponent", "service", "org.ow2.petals.esb.kernel.impl.component.ComponentImpl");
		System.out.println("createComponent response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyComponent", res[0]);
		
		
		// create provider
		res = adminWSCLient.invoke("createProvider", "{http://petals.ow2.org}MyComponent", "{http://petals.ow2.org}MyProvider", "service", "org.ow2.petals.esb.kernel.impl.entity.ProviderImpl");
		System.out.println("createProvider response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyProvider", res[0]);
		
		
		// create service
		res = adminWSCLient.invoke("createService", "{http://petals.ow2.org}MyProvider", "{http://petals.ow2.org}MyService", "service", "org.ow2.petals.esb.kernel.impl.service.BusinessServiceImpl");
		System.out.println("createService response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyService", res[0]);
		
		// create providerEndpoint
		URL echoUrl = Thread.currentThread().getContextClassLoader().getResource("wsdl/echo.wsdl");
		res = adminWSCLient.invoke("createProviderEndpoint", "{http://petals.ow2.org}MyService", "MyProviderEndpoint", "service", "org.ow2.petals.esb.kernel.impl.endpoint.ProviderEndpointImpl", "org.ow2.petals.esb.impl.test.util.EchoSoapBehaviour", echoUrl.toURI());
		System.out.println("createProviderEndpoint response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyProviderEndpoint", res[0]);
		
		adminWSCLient.destroy();
		petals.getTransportersManager().stop();
	}
	
	
	public void testAdminEndpoint_AddSoapListener() throws Exception {
		Node petals = createESB();

//		 Thread.sleep(10000000);

		// create WS client
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		org.apache.cxf.endpoint.Client adminWSCLient = dcf.createClient("http://localhost:8085/services/adminExternalEndpoint?wsdl", Thread.currentThread().getContextClassLoader());

		// create component
		Object[] res = adminWSCLient.invoke("createComponent", "{http://petals.ow2.org}MyComponent", "service", "org.ow2.petals.esb.kernel.impl.component.ComponentImpl");
		System.out.println("createComponent response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyComponent", res[0]);
		
		// create clientAndProvider
		res = adminWSCLient.invoke("createClientAndProvider", "{http://petals.ow2.org}MyComponent", "{http://petals.ow2.org}MyClientAndProvider", "service", "org.ow2.petals.esb.kernel.impl.entity.ClientAndProviderImpl", "service", "org.ow2.petals.esb.kernel.impl.entity.ClientImpl", "service", "org.ow2.petals.esb.kernel.impl.entity.ProviderImpl");
		System.out.println("createClientAndProvider response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyClientAndProvider", res[0]);
		
		
		// create clientEndpoint
		res = adminWSCLient.invoke("createClientEndpoint", "{http://petals.ow2.org}MyClientAndProvider", "{http://petals.ow2.org}MyClientEndpoint", "proxy-client-service", "org.ow2.petals.esb.kernel.impl.endpoint.ClientProxyEndpointImpl", "org.ow2.petals.esb.kernel.impl.endpoint.behaviour.proxy.ClientProxyBehaviourImpl");
		System.out.println("createClientEndpoint response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyClientEndpoint", res[0]);
		
		// add Soap Listener
		res = adminWSCLient.invoke("addSoapListener", "{http://petals.ow2.org}MyClientEndpoint");
		System.out.println("addSoapListener response: " + res[0]);
		assertEquals("http://localhost:8085/services/MyClientEndpoint", res[0]);
		
		adminWSCLient.destroy();
		petals.getTransportersManager().stop();
	}
	
	
	public void testAdminEndpoint_ExposeInSoapOnClient() throws Exception {
		Node petals = createESB();

//		 Thread.sleep(10000000);

		// create WS client
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		org.apache.cxf.endpoint.Client adminWSCLient = dcf.createClient("http://localhost:8085/services/adminExternalEndpoint?wsdl", Thread.currentThread().getContextClassLoader());

		Thread.sleep(400);
		
		// create component
		Object[] res = adminWSCLient.invoke("createComponent", "{http://petals.ow2.org}MyComponent", "service", "org.ow2.petals.esb.kernel.impl.component.ComponentImpl");
		System.out.println("createComponent response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyComponent", res[0]);
		
		
		// create clientAndProvider
		res = adminWSCLient.invoke("createClientAndProvider", "{http://petals.ow2.org}MyComponent", "{http://petals.ow2.org}MyClientAndProvider", "service", "org.ow2.petals.esb.kernel.impl.entity.ClientAndProviderImpl", "service", "org.ow2.petals.esb.kernel.impl.entity.ClientImpl", "service", "org.ow2.petals.esb.kernel.impl.entity.ProviderImpl");
		System.out.println("createClientAndProvider response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyClientAndProvider", res[0]);
			
		
		// create service
		res = adminWSCLient.invoke("createService", "{http://petals.ow2.org}MyClientAndProvider", "{http://petals.ow2.org}MyService", "service", "org.ow2.petals.esb.kernel.impl.service.BusinessServiceImpl");
		System.out.println("createService response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyService", res[0]);
		
		// create providerEndpoint
		URL echoUrl = Thread.currentThread().getContextClassLoader().getResource("wsdl/echo.wsdl");
		res = adminWSCLient.invoke("createProviderEndpoint", "{http://petals.ow2.org}MyService", "MyProviderEndpoint", "service", "org.ow2.petals.esb.kernel.impl.endpoint.ProviderEndpointImpl", "org.ow2.petals.esb.impl.test.util.EchoSoapBehaviour", echoUrl.toURI());
		System.out.println("createProviderEndpoint response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyProviderEndpoint", res[0]);
		
		
		// expose in soap on client
		res = adminWSCLient.invoke("exposeInSoapOnClient", "{http://petals.ow2.org}MyService", "MyProviderEndpoint", "{http://petals.ow2.org}MyClientAndProvider");
		System.out.println("createProviderEndpoint response: " + res[0]);
		assertEquals("http://localhost:8085/services/MyProviderEndpointClientProxyEndpoint", res[0]);
		
		adminWSCLient.destroy();
		petals.getTransportersManager().stop();
	}
	
	public void testAdminEndpoint_CreateServiceEndpoint() throws Exception {
		Node petals = createESB();

//		 Thread.sleep(10000000);

		// create WS client
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		org.apache.cxf.endpoint.Client adminWSCLient = dcf.createClient("http://localhost:8085/services/adminExternalEndpoint?wsdl", Thread.currentThread().getContextClassLoader());

		Thread.sleep(400);
		
		// create component
		Object[] res = adminWSCLient.invoke("createComponent", "{http://petals.ow2.org}MyComponent", "service", "org.ow2.petals.esb.kernel.impl.component.ComponentImpl");
		System.out.println("createComponent response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyComponent", res[0]);
		
		
		// create clientAndProvider
		res = adminWSCLient.invoke("createClientAndProvider", "{http://petals.ow2.org}MyComponent", "{http://petals.ow2.org}MyClientAndProvider", "service", "org.ow2.petals.esb.kernel.impl.entity.ClientAndProviderImpl", "service", "org.ow2.petals.esb.kernel.impl.entity.ClientImpl", "service", "org.ow2.petals.esb.kernel.impl.entity.ProviderImpl");
		System.out.println("createClientAndProvider response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyClientAndProvider", res[0]);
			
				
		// create serviceEndpoint
		URL echoUrl = Thread.currentThread().getContextClassLoader().getResource("wsdl/echo.wsdl");
		res = adminWSCLient.invoke("createServiceEndpoint", "{http://petals.ow2.org}MyClientAndProvider", "{http://petals.ow2.org}MyService", "service", "org.ow2.petals.esb.kernel.impl.service.BusinessServiceImpl", "MyProviderEndpoint", "service", "org.ow2.petals.esb.kernel.impl.endpoint.ProviderEndpointImpl", "org.ow2.petals.esb.impl.test.util.EchoSoapBehaviour", echoUrl.toURI());
		System.out.println("createServiceEndpoint response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyProviderEndpoint", res[0]);
		
		adminWSCLient.destroy();
		petals.getTransportersManager().stop();
	}
	
	public void testAdminEndpoint_CreateServiceEndpointInSoapClient() throws Exception {
		Node petals = createESB();

//		 Thread.sleep(10000000);

		Thread.sleep(600);
		
		// create WS client
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		org.apache.cxf.endpoint.Client adminWSCLient = dcf.createClient("http://localhost:8085/services/adminExternalEndpoint?wsdl", Thread.currentThread().getContextClassLoader());

		Thread.sleep(600);
		
		// create component
		Object[] res = adminWSCLient.invoke("createComponent", "{http://petals.ow2.org}MyComponent", "service", "org.ow2.petals.esb.kernel.impl.component.ComponentImpl");
		System.out.println("createComponent response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyComponent", res[0]);
		
		
		// create clientAndProvider
		res = adminWSCLient.invoke("createClientAndProvider", "{http://petals.ow2.org}MyComponent", "{http://petals.ow2.org}MyClientAndProvider", "service", "org.ow2.petals.esb.kernel.impl.entity.ClientAndProviderImpl", "service", "org.ow2.petals.esb.kernel.impl.entity.ClientImpl", "service", "org.ow2.petals.esb.kernel.impl.entity.ProviderImpl");
		System.out.println("createClientAndProvider response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyClientAndProvider", res[0]);
			
		
		// create serviceEndpoint
		URL echoUrl = Thread.currentThread().getContextClassLoader().getResource("wsdl/echo.wsdl");
		res = adminWSCLient.invoke("createServiceEndpointOnSoapClient", null, "{http://petals.ow2.org}MyService", null, null, "MyProviderEndpoint", null, null, "org.ow2.petals.esb.impl.test.util.EchoSoapBehaviour", echoUrl.toURI(), null);
		System.out.println("createServiceEndpointOnSoapClient response: " + res[0]);
		assertEquals("http://localhost:8085/services/MyProviderEndpointClientProxyEndpoint", res[0]);
		
		adminWSCLient.destroy();
		petals.getTransportersManager().stop();
	}
	
	public void testAdminEndpoint_UseServiceEndpointInSoapClient() throws Exception {
		Node petals = createESB();

//		 Thread.sleep(10000000);

		// create WS client
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		org.apache.cxf.endpoint.Client adminWSCLient = dcf.createClient("http://localhost:8085/services/adminExternalEndpoint?wsdl", Thread.currentThread().getContextClassLoader());

		Thread.sleep(400);
		
		// create component
		Object[] res = adminWSCLient.invoke("createComponent", "{http://petals.ow2.org}MyComponent", "service", "org.ow2.petals.esb.kernel.impl.component.ComponentImpl");
		System.out.println("createComponent response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyComponent", res[0]);
		
		
		// create clientAndProvider
		res = adminWSCLient.invoke("createClientAndProvider", "{http://petals.ow2.org}MyComponent", "{http://petals.ow2.org}MyClientAndProvider", "service", "org.ow2.petals.esb.kernel.impl.entity.ClientAndProviderImpl", "service", "org.ow2.petals.esb.kernel.impl.entity.ClientImpl", "service", "org.ow2.petals.esb.kernel.impl.entity.ProviderImpl");
		System.out.println("createClientAndProvider response: " + res[0]);
		assertEquals("{http://petals.ow2.org}MyClientAndProvider", res[0]);
			
		
		// create serviceEndpoint
		URL echoUrl = Thread.currentThread().getContextClassLoader().getResource("wsdl/echo.wsdl");
		res = adminWSCLient.invoke("createServiceEndpointOnSoapClient", null, "{http://petals.ow2.org}MyService", null, null, "EchoEndpoint", null, null, "org.ow2.petals.esb.impl.test.util.EchoSoapBehaviour", echoUrl.toURI(), null);
		System.out.println("createServiceEndpointOnSoapClient response: " + res[0]);
		assertEquals("http://localhost:8085/services/EchoEndpointClientProxyEndpoint", res[0]);

		// create WS client
		org.apache.cxf.endpoint.Client echoWSCLient = dcf.createClient("http://localhost:8085/services/EchoEndpointClientProxyEndpoint?wsdl", Thread.currentThread().getContextClassLoader());

		res  = echoWSCLient.invoke("echo", "test echo");
		System.out.println("Echo response: " + res[0]);
		

		assertEquals("test echo", res[0]);
		
		adminWSCLient.destroy();
		echoWSCLient.destroy();
		petals.getTransportersManager().stop();
	}
	
	public void testAdminEndpoint_ImportSoapEndpoint() throws Exception {
		Node petals = createESB();
		
		// start ws echo service
		Echo_EchoSOAP_Server server = new Echo_EchoSOAP_Server();
        System.out.println("Server ready..."); 
		
		// create WS client
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		org.apache.cxf.endpoint.Client adminWSCLient = dcf.createClient("http://localhost:8085/services/adminExternalEndpoint?wsdl", Thread.currentThread().getContextClassLoader());

		Thread.sleep(400);
		
		// import serviceEndpoint
		URL echoUrl = Thread.currentThread().getContextClassLoader().getResource("wsdl/echo.wsdl");
		Object[] res = adminWSCLient.invoke("importSoapEndpoint", null, "http://localhost:9001/echo", echoUrl.toURI());
		System.out.println("importSoapEndpoint response: " + res[0]);
		assertEquals("{http://ow2.petals.org/echo/}echoSOAP", res[0]);
		
		server.shutdown();
		
		adminWSCLient.destroy();
		petals.getTransportersManager().stop();
	}
	
	
	public void testAdminEndpoint_WrapSoapEndpoint() throws Exception {
		Node petals = createESB();
		
		// start ws echo service
		Echo_EchoSOAP_Server server = new Echo_EchoSOAP_Server();
        System.out.println("Server ready..."); 
		
		// create admin client
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		org.apache.cxf.endpoint.Client adminWSCLient = dcf.createClient("http://localhost:8085/services/adminExternalEndpoint?wsdl", Thread.currentThread().getContextClassLoader());

		Thread.sleep(400);
		
		// addProxySoapEndpointOnClient
		URL echoUrl = Thread.currentThread().getContextClassLoader().getResource("wsdl/echo.wsdl");
		long start = GregorianCalendar.getInstance().getTimeInMillis();
		Object[] res = adminWSCLient.invoke("wrapSoapEndpoint", null, "http://localhost:9001/echo", echoUrl.toURI());
		System.out.println("wrapSoapEndpoint response: " + res[0]);
		long end = GregorianCalendar.getInstance().getTimeInMillis();
		long creationTime = end-start;
		
		assertEquals("http://localhost:8085/services/echoSOAPClientProxyEndpoint", res[0]);
		
		
		// create WS client
		start = GregorianCalendar.getInstance().getTimeInMillis();
		org.apache.cxf.endpoint.Client echoWSCLient = dcf.createClient("http://localhost:8085/services/echoSOAPClientProxyEndpoint?wsdl", Thread.currentThread().getContextClassLoader());
		res  = echoWSCLient.invoke("echo", "test echo");
		end = GregorianCalendar.getInstance().getTimeInMillis();
		long invocationTime = end-start;
		System.out.println("TIME of creation: " + creationTime);
		System.out.println("TIME of Invocation: " + invocationTime);
		System.out.println("Echo response: " + res[0]);
		assertEquals("test echo", res[0]);
		
		server.shutdown();
		
		adminWSCLient.destroy();
		petals.getTransportersManager().stop();
	}
}
