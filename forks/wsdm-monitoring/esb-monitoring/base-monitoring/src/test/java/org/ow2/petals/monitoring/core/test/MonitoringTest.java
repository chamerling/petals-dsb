/**
 * MonitoringEngine-Core - SOA Tools Platform.
 * Copyright (c) 2008 EBM Websourcing, http://www.ebmwebsourcing.com/
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * -------------------------------------------------------------------------
 * $id.java
 * -------------------------------------------------------------------------
 */
package org.ow2.petals.monitoring.core.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.LogManager;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.webservicex.StockQuoteSoap_StockQuoteSoap1_Server;

import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.jdom.input.DOMBuilder;
import org.oasis_open.docs.wsn.bw_2.NotificationConsumer_NotifierEndpoint_Server;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.ow2.easywsdl.schema.api.XmlException;
import org.ow2.easywsdl.schema.util.XMLPrettyPrinter;
import org.ow2.petals.esb.api.ESBFactory;
import org.ow2.petals.esb.external.protocol.soap.impl.server.SoapServer;
import org.ow2.petals.esb.impl.ESBFactoryImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.ClientAndProviderEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.ClientProxyEndpoint;
import org.ow2.petals.esb.kernel.api.node.Node;
import org.ow2.petals.esb.kernel.impl.endpoint.ClientProxyEndpointImpl;
import org.ow2.petals.esb.kernel.impl.endpoint.behaviour.proxy.ClientProxyBehaviourImpl;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.kernel.ws.api.EndpointService_EndpointServicePort_Server;
import org.ow2.petals.monitoring.core.ESBWSDMFactoryImpl;
import org.ow2.petals.monitoring.core.api.BaseMonitoring;
import org.ow2.petals.monitoring.core.api.ClientMonitoring;
import org.ow2.petals.monitoring.core.api.ExchangeTrace;
import org.ow2.petals.monitoring.core.api.MonitoringClientEndpointBehaviour;
import org.ow2.petals.monitoring.core.api.MonitoringEngine;
import org.ow2.petals.monitoring.core.api.MonitoringException;
import org.ow2.petals.monitoring.core.api.MonitoringProviderEndpoint;
import org.ow2.petals.monitoring.core.api.MonitoringProviderEndpointBehaviour;
import org.ow2.petals.monitoring.core.api.MonitoringService;
import org.ow2.petals.monitoring.core.impl.ExchangeTraceImpl;
import org.ow2.petals.monitoring.core.impl.MonitoringClientEndpointBehaviourImpl;
import org.ow2.petals.monitoring.core.impl.MonitoringEngineImpl;
import org.ow2.petals.monitoring.core.test.util.EndpointServiceTest;
import org.ow2.petals.monitoring.core.test.util.NotificationConsumerTest;
import org.ow2.petals.monitoring.core.test.util.WSDMClient;
import org.ow2.petals.monitoring.core.util.Util;
import org.ow2.petals.monitoring.model.rawreport.api.RawReportException;
import org.ow2.petals.monitoring.model.rawreport.api.Report;
import org.ow2.petals.monitoring.model.rawreport.api.ReportList;
import org.ow2.petals.monitoring.model.rawreport.impl.RawReportFactory;
import org.ow2.petals.transporter.api.transport.TransportException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import petals.ow2.org.exchange.PatternType;

import com.ebmwebsourcing.wsstar.addressing.definition.WSAddressingFactory;
import com.ebmwebsourcing.wsstar.dm.WSDMFactory;
import com.ebmwebsourcing.wsstar.dm.api.QoSMetrics;
import com.ebmwebsourcing.wsstar.dm.api.WSDMException;
import com.ebmwebsourcing.wsstar.notification.definition.WSNotificationFactory;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.SubscribeResponse;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.Unsubscribe;
import com.ebmwebsourcing.wsstar.notification.definition.inout.WSNotificationReader;
import com.ebmwebsourcing.wsstar.notification.definition.inout.WSNotificationWriter;
import com.ebmwebsourcing.wsstar.notification.extension.utils.WsnSpecificTypeHelper;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
public class MonitoringTest extends TestCase {

	// private Node petals = null;

	static {
		try {
			final InputStream inputStream = MonitoringTest.class.getClassLoader().getResourceAsStream("logger.properties");
			if (inputStream != null) {
				LogManager.getLogManager().readConfiguration(inputStream);
			}
		} catch (final Exception e) {
			throw new RuntimeException("couldn't initialize logging properly", e);
		}
	}
	
	/**
	 * @param name
	 */
	public MonitoringTest(final String name) {
		super(name);
	}

	private void setReport(final Report report) throws RawReportException {
		report.setConsumerName("consumerClientEndpoint");
		report.setContentLength(new Random().nextLong());
		report.setDateInGMT(Calendar.getInstance().getTime());
		report.setDoesThisRequestInIsAnException(false);
		report.setEndPoint("providerEndpoint");
		report.setOperationName("operationNameOfService");
		report.setServiceName("serviceNameOfProvider");
		report.setServiceProviderName("ProviderName");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System
		.setProperty("javax.xml.transform.TransformerFactory",
		"com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
		// petals = createESB(false);

	}

	// public void testMonitoringUsingWSClassicalInvocation() throws
	// MonitoringException, NoSuchInterfaceException, InterruptedException,
	// XmlException, ESBException, MessageException, TransportException,
	// URISyntaxException, SAXException, IOException,
	// ParserConfigurationException {
	// boolean explorer = false;
	// Node petals = this.createESB(explorer);
	//
	//
	// // create provider monitoring component
	// MonitoringEngine monitoringEngine = petals.createComponent(new
	// QName("http://petals.ow2.org", "Monitoring"), "monitoringEngine",
	// MonitoringEngineImpl.class);
	// BaseMonitoring baseMonitoring = monitoringEngine.createBaseMonitoring();
	//
	// // create technical provider monitoring service
	// MonitoringService monitoringService =
	// baseMonitoring.createMonitoringService(new QName("http://petals.ow2.org",
	// "stockquoteServiceMonitoring"));
	// MonitoringProviderEndpoint monitoringProviderEndpoint =
	// monitoringService.createMonitoringEndpoint("stockquoteEndpointMonitoring",
	// new ArrayList<String>(Arrays
	// .asList(new String[] {
	// "MetricsCapability"})));
	//
	//
	// // create client Component
	// ClientMonitoring clientComponent =
	// monitoringEngine.createClientMonitoring(new
	// QName("http://ftparis.com","ftParisClient"));
	//
	//
	// // create client endpoint
	// ClientAndProviderEndpoint ftParisEndpoint =
	// clientComponent.createClientMonitoringEndpoint(new
	// QName("http://ftparis.com", "ftParisEndpoint"));
	// ftParisEndpoint.setBehaviourClass(MonitoringClientEndpointBehaviourImpl.class);
	//
	// // update metrics in provider
	// String req = "" +
	// "<web:GetQuote xmlns:web=\"http://www.webserviceX.NET/\">" +
	// "    <web:symbol>ebm</web:symbol>" +
	// "</web:GetQuote>";
	// Document requestDoc =
	// DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new
	// ByteArrayInputStream(req.getBytes()));
	// String resp = "" +
	// "<web:GetQuoteResponse>" +
	// "         <web:GetQuoteResult>1000000000</web:GetQuoteResult>" +
	// "</web:GetQuoteResponse>";
	// Document responseDoc =
	// DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new
	// ByteArrayInputStream(resp.getBytes()));
	// DOMBuilder builder = new DOMBuilder();
	// org.jdom.Document requestjDoc = builder.build(requestDoc);
	// org.jdom.Document responsejDoc = builder.build(responseDoc);
	// ExchangeTrace trace1 = new ExchangeTraceImpl();
	// trace1.setServiceName(new QName("http://www.webserviceX.NET/",
	// "StockQuote"));
	// trace1.setEndpointName("StockQuoteSoap1");
	// trace1.setInterfaceName(new QName("http://www.webserviceX.NET/",
	// "StockQuote"));
	// trace1.setOperationName("GetQuote");
	// trace1.setRequest(requestjDoc);
	// trace1.setResponse(responsejDoc);
	// trace1.setStartExchange(Calendar.getInstance().getTime());
	// trace1.setEndExchange(Calendar.getInstance().getTime());
	// trace1.setResponseType(ExchangeTrace.ResponseType.SUCCESS);
	//		
	// ((MonitoringProviderEndpointBehaviour)monitoringProviderEndpoint.getBehaviour()).addNewExchange(trace1);
	//
	//
	//
	//
	// // Test getResourceProperties method
	// String msg =
	// "<wsrf:GetResourceProperty xmlns:wsrf=\"http://docs.oasis-open.org/wsrf/rp-2\" >"
	// + new QName("http://www.webserviceX.NET/", "GetQuote")
	// + "</wsrf:GetResourceProperty>";
	// Exchange me = Util.createMessageExchange(ftParisEndpoint,
	// monitoringProviderEndpoint, new QName("http://petals.ow2.org",
	// "weatherInterfaceMonitoring"), new
	// QName("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceProperties-1.2-draft-01.xsd",
	// "GetResourceProperty"), MEPPatternConstants.IN_OUT, msg);
	//
	//		
	// // send message
	// ftParisEndpoint.sendSync(me, 0);
	//
	//
	// // verif
	// if(me.getOut().getBody().getContent() != null) {
	// System.out.println("GetResourceProperties response: " +
	// XMLPrettyPrinter.prettyPrint(me.getOut().getBody().getContent()));
	// }
	//
	//
	// if(explorer)
	// Thread.sleep(10000000);
	//
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}



	public void testWSDMIntegration() throws Exception {
		System.out
		.println("\n\n************************ START WSDM INTEGRATION TEST ***********************\n\n");

		final boolean explorer = false;
		// ESBFactory factory = new ESBWSDMFactoryImpl();
		// Node petals = factory.createNode(new QName("http://petals.ow2.org",
		// "node0"), explorer);
		// Node petals = createESB(false);
		final ESBWSDMFactoryImpl factory = new ESBWSDMFactoryImpl();
		final Node petals = factory.createNode(new QName(
				"http://petals.ow2.org", "node0"), explorer);
		((ESBWSDMFactoryImpl) factory).desactivatePEtALSConnexion();

		// Thread.sleep(10000000);

		// create WS admin client
		final JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory
		.newInstance();
		final org.apache.cxf.endpoint.Client adminWSCLient = dcf.createClient(
				"http://localhost:8085/services/adminExternalEndpoint?wsdl",
				Thread.currentThread().getContextClassLoader());

		// start ws echo service
		final StockQuoteSoap_StockQuoteSoap1_Server server = new StockQuoteSoap_StockQuoteSoap1_Server();

		// start ws notifier service
		final NotificationConsumerTest notifier = new NotificationConsumerTest();
		final NotificationConsumer_NotifierEndpoint_Server notifServer = new NotificationConsumer_NotifierEndpoint_Server(
				notifier, "http://localhost:9002/NotificationEndpoint");

		try {
			Thread.sleep(1000);

			// import serviceEndpoint
			final URL wsdlURL = Thread.currentThread().getContextClassLoader()
			.getResource("wsdl/stockquote.wsdl");
			Object[] res = adminWSCLient.invoke("importSoapEndpoint", null,
					"http://localhost:9001/stockquote1", wsdlURL.toURI());
			System.out.println("importSoapEndpoint response: " + res[0]);
			Assert.assertEquals("{http://www.webserviceX.NET/}StockQuoteSoap1",
					res[0]);

			Thread.sleep(500);

			// create monitoringEndpoint
			res = adminWSCLient.invoke("createMonitoringEndpoint",
					"{http://www.webserviceX.NET/}StockQuoteWSDMService",
					"StockQuoteWSDMSoap1", true);
			System.out.println("createMonitoringEndpoint response: " + res[0]);
			Assert
			.assertEquals(
					"http://localhost:8085/services/StockQuoteWSDMSoap1ClientProxyEndpoint",
					res[0]);

			// Thread.sleep(50000000);


			Thread.sleep(500);

			// create subscribe
			// org.apache.cxf.endpoint.Client wsdmClient =
			// dcf.createClient("http://localhost:8085/services/StockQuoteWSDMSoap1ClientProxyEndpoint?wsdl",
			// Thread.currentThread().getContextClassLoader());
			// res = wsdmClient.invoke("Subscribe", "MyEndpointAddress");
			final WSDMClient client = new WSDMClient();
			boolean test;

			System.out.println("CALL SUBSCRIBE");
			test = client
			.subcribe(
					new URI(
					"./src/test/resources/test/subscribe/subscribeRequest.xml"),
					"http://localhost:8085/services/StockQuoteWSDMSoap1ClientProxyEndpoint",
					new URI(
					"./src/test/resources/test/subscribe/subscribeResponse.xml"));
			Assert.assertTrue(test);
			System.out.println("SUBSCRIBE => OK");

			Thread.sleep(500);

			System.out.println("CALL ADDNEWEXCHANGE");
			test = client
			.addNewExchange(
					new URI(
					"./src/test/resources/test/addNewExchange/addNewExchangeRequest.xml"),
			"http://localhost:8085/services/StockQuoteWSDMSoap1ClientProxyEndpoint");
			Assert.assertTrue(test);
			System.out.println("CALL ADDNEWEXCHANGE => OK");

			Thread.sleep(5000);

			Assert.assertEquals(1, notifier.getNotifications().size());

			System.out.println("CALL ADDNEWREPORTLIST");
			test = client
			.addNewReportList(
					new URI(
					"./src/test/resources/test/addNewReportList/addNewReportListRequest.xml"),
			"http://localhost:8085/services/StockQuoteWSDMSoap1ClientProxyEndpoint");
			Assert.assertTrue(test);
			System.out.println("CALL ADDNEWREPORTLIST => OK");

			Thread.sleep(5000);

			Assert.assertEquals(2, notifier.getNotifications().size());

			if (explorer) {
				Thread.sleep(10000000);
			}
		} finally {
			server.shutdown();
			notifServer.shutdown();
			adminWSCLient.destroy();
			SoapServer.getInstance().stop();
			petals.getTransportersManager().stop();
			petals.getListenersManager().shutdownAllListeners();
		}
	}


	public void testConnexionWithPEtALS() throws Exception {
		System.out
		.println("\n\n************************ START CONNEXION WITH PETALS TEST ***********************\n\n");

		final boolean explorer = false;
		// ESBFactory factory = new ESBWSDMFactoryImpl();
		// Node petals = ((ESBWSDMFactoryImpl)factory).createNode(new
		// QName("http://petals.ow2.org", "node0"), explorer, true);
		final ESBWSDMFactoryImpl factory = new ESBWSDMFactoryImpl();
		final Node petals = factory.createNode(new QName(
				"http://petals.ow2.org", "node0"), explorer);

		// Thread.sleep(10000000);

		// create WS admin client
		final JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory
		.newInstance();
		final org.apache.cxf.endpoint.Client adminWSCLient = dcf.createClient(
				"http://localhost:8085/services/adminExternalEndpoint?wsdl",
				Thread.currentThread().getContextClassLoader());

		// start ws petals endpoint service
		final EndpointServiceTest epPetalsService = new EndpointServiceTest();
		final EndpointService_EndpointServicePort_Server petalsServer = new EndpointService_EndpointServicePort_Server(
				epPetalsService,
		"http://localhost:7600/petals/ws/EndpointService");

		try {
			epPetalsService.setNumberOfEndpointsInList(0);

			Thread.sleep(5000);

			epPetalsService.setNumberOfEndpointsInList(1);

			Thread.sleep(5000);

			epPetalsService.setNumberOfEndpointsInList(2);

			Thread.sleep(5000);

			epPetalsService.setNumberOfEndpointsInList(3);

			Thread.sleep(5000);

			epPetalsService.setNumberOfEndpointsInList(1);

			Thread.sleep(5000);

			epPetalsService.setNumberOfEndpointsInList(3);

			Thread.sleep(5000);

			final WSDMClient client = new WSDMClient();
			boolean test;

			System.out.println("CALL SUBSCRIBE on endpoint 1");
			test = client
			.subcribe(
					new URI(
					"./src/test/resources/test/subscribe/subscribeRequest.xml"),
					"http://localhost:8085/services/endpoint1_WSDMMonitoringClientProxyEndpoint",
					new URI(
					"./src/test/resources/test/subscribe/subscribeResponse.xml"));
			Assert.assertTrue(test);
			System.out.println("SUBSCRIBE => OK");

			// System.out.println("CALL SUBSCRIBE on endpoint 2");
			// test = client.subcribe(new
			// URI("./src/test/resources/test/subscribe/subscribeRequest.xml"),
			// "http://localhost:8085/services/endpoint2_WSDMMonitoringClientProxyEndpoint",
			// new
			// URI("./src/test/resources/test/subscribe/subscribeResponse.xml"));
			// assertTrue(test);
			// System.out.println("SUBSCRIBE => OK");

			// System.out.println("CALL SUBSCRIBE on endpoint 3");
			// test = client.subcribe(new
			// URI("./src/test/resources/test/subscribe/subscribeRequest.xml"),
			// "http://localhost:8085/services/endpoint3_WSDMMonitoringClientProxyEndpoint",
			// new
			// URI("./src/test/resources/test/subscribe/subscribeResponse.xml"));
			// assertTrue(test);
			// System.out.println("SUBSCRIBE => OK");
		} finally {
			petalsServer.shutdown();
			adminWSCLient.destroy();
			SoapServer.getInstance().stop();
			petals.getTransportersManager().stop();
			petals.getListenersManager().shutdownAllListeners();
		}
	}

	public void testMonitoringDescription() throws MonitoringException,
	NoSuchInterfaceException, InterruptedException, XmlException,
	ESBException, TransportException, URISyntaxException, SAXException,
	IOException, ParserConfigurationException, WSDMException {
		final boolean explorer = false;
		// ESBFactory factory = new ESBFactoryImpl();
		// Node petals = factory.createNode(new QName("http://petals.ow2.org",
		// "node0"), explorer);
		final ESBWSDMFactoryImpl factory = new ESBWSDMFactoryImpl();
		final Node petals = factory.createNode(new QName(
				"http://petals.ow2.org", "node0"), explorer);
		((ESBWSDMFactoryImpl) factory).desactivatePEtALSConnexion();

		try {
			// create provider monitoring component
			// MonitoringEngine monitoringEngine = petals.createComponent(new
			// QName("http://petals.ow2.org", "Monitoring"), "monitoringEngine",
			// MonitoringEngineImpl.class);
			final MonitoringEngine monitoringEngine = (MonitoringEngine) petals
			.getRegistry().getEndpoint(
					new QName("http://petals.ow2.org", "Monitoring"));
			final BaseMonitoring baseMonitoring = monitoringEngine
			.getBaseMonitoring();

			// create technical provider monitoring service
			final MonitoringService monitoringService = baseMonitoring
			.createMonitoringService(new QName("http://petals.ow2.org",
			"stockquoteServiceMonitoring"));
			final MonitoringProviderEndpoint monitoringProviderEndpoint = monitoringService
			.createMonitoringEndpoint("stockquoteEndpointMonitoring",
					new ArrayList<String>(Arrays
							.asList(new String[] { "MetricsCapability" })));

			// create client Component
			final ClientMonitoring clientComponent = monitoringEngine
			.createClientMonitoring(new QName("http://ftparis.com",
			"ftParisClient"));

			final ClientProxyEndpoint clientEndpoint = clientComponent
			.createClientEndpoint(new QName("http://ow2.petals.org/echo/",
			"echoSOAPExternal"), "proxy-client-service",
			ClientProxyEndpointImpl.class, null);
			clientEndpoint.setExternalAddress("http://localhost:8085/");

			clientEndpoint.setBehaviourClass(ClientProxyBehaviourImpl.class);
			clientEndpoint.setProviderEndpointName(monitoringProviderEndpoint
					.getQName());

			final org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description desc = clientEndpoint
			.getBehaviour().getDescription();
			// desc.deleteImportedDocumentsInWsdl(new
			// URI("http://localhost:8085/"));

			// System.out.println("Document:\n" +
			// XMLPrettyPrinter.prettyPrint(WSDL4ComplexWsdlFactory.newInstance().newWSDLWriter().getDocument(desc)));

			System.out.println("desc.getImports().size() = "
					+ desc.getImports().size());
			System.out.println("desc.getBindings().size() = "
					+ desc.getBindings().size());
			System.out.println("desc.get(0).getNamespaceURI() = "
					+ desc.getImports().get(0).getNamespaceURI());
			System.out.println("desc.get(0).getDescription() = "
					+ desc.getImports().get(0).getDescription());
			System.out
			.println("desc.getImports().get(0).getDescription().getBindings().size() = "
					+ desc.getImports().get(0).getDescription()
					.getBindings().size());
			System.out
			.println("desc.getImports().get(0).getDescription().getImports().size() = "
					+ desc.getImports().get(0).getDescription()
					.getImports().size());
			System.out
			.println("desc.getImports().get(0).getDescription().getImports().get(0).getLocationURI() = "
					+ desc.getImports().get(0).getDescription()
					.getImports().get(0).getLocationURI());
			System.out
			.println("desc.getImports().get(0).getDescription().getImports().get(0).getDescription() = "
					+ desc.getImports().get(0).getDescription()
					.getImports().get(0).getDescription());
			Assert.assertNotNull(desc.getImports().get(0).getDescription()
					.getImports().get(0).getDescription());
		} finally {
			SoapServer.getInstance().stop();
			petals.getTransportersManager().stop();
			petals.getListenersManager().shutdownAllListeners();
		}
	}

	public void testMonitoringUsingNotification() throws MonitoringException,
	NoSuchInterfaceException, InterruptedException, XmlException,
	ESBException, TransportException, URISyntaxException, SAXException,
	IOException, ParserConfigurationException, WSDMException,
	RawReportException {
		final boolean explorer = false;
		final ESBFactory factory = new ESBFactoryImpl();
		final Node petals = factory.createNode(new QName(
				"http://petals.ow2.org", "node0"), explorer);

		try {
			// create provider monitoring component
			final MonitoringEngine monitoringEngine = petals.createComponent(
					new QName("http://petals.ow2.org", "Monitoring"),
					"monitoringEngine", MonitoringEngineImpl.class);
			final BaseMonitoring baseMonitoring = monitoringEngine
			.createBaseMonitoring();

			// create technical provider monitoring service
			final MonitoringService monitoringService = baseMonitoring
			.createMonitoringService(new QName("http://petals.ow2.org",
			"stockquoteServiceMonitoring"));
			final MonitoringProviderEndpoint monitoringProviderEndpoint = monitoringService
			.createMonitoringEndpoint("stockquoteEndpointMonitoring",
					new ArrayList<String>(Arrays
							.asList(new String[] { "MetricsCapability" })));

			// create client Component
			final ClientMonitoring clientComponent = monitoringEngine
			.createClientMonitoring(new QName("http://ftparis.com",
			"ftParisClient"));

			// create client endpoint
			final ClientAndProviderEndpoint ftParisEndpoint = clientComponent
			.createClientMonitoringEndpoint(new QName("http://ftparis.com",
			"ftParisEndpoint"));
			ftParisEndpoint
			.setBehaviourClass(MonitoringClientEndpointBehaviourImpl.class);

			// Test subscribe method
			String msg = "<wsn:Subscribe xmlns:wsn=\"http://docs.oasis-open.org/wsn/b-2\" xmlns:add=\"http://www.w3.org/2005/08/addressing\""
				+ " targetnamespace=\"http://docs.oasis-open.org/wsn/b-2\">"
				+ "<wsn:ConsumerReference xmlns:wsn=\"http://docs.oasis-open.org/wsn/b-2\" xmlns:add=\"http://www.w3.org/2005/08/addressing\">"
				+ "     <add:Address>{http://ftparis.com}ftParisEndpoint</add:Address>"
				+ "</wsn:ConsumerReference>"
				+ "<wsn:Filter>"
				+ "     <wsn:TopicExpression Dialect=\"http://www.w3.org/TR/1999/REC-xpath-19991116\" xmlns:mows-ev=\"http://docs.oasis-open.org/wsdm/2004/12/mows/wsdm-mows-events.xml\">"
				+ "           mows-ev:MetricsCapability"
				+ "     </wsn:TopicExpression>"
				+ "</wsn:Filter>"
				+ "</wsn:Subscribe>";
			Exchange me = Util
			.createMessageExchange(
					ftParisEndpoint,
					monitoringProviderEndpoint,
					new QName("http://petals.ow2.org",
					"weatherInterfaceMonitoring"),
					new QName(
							"http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd",
					"Subscribe"), PatternType.IN_OUT, msg);

			// send subscription request
			final Exchange response = ftParisEndpoint.sendSync(me, 0);

			SubscribeResponse subcribeResponse = null;
			if (response.getOut().getBody().getContent() != null) {
				subcribeResponse = WSNotificationReader.getInstance().readSubscribeResponse(
						response.getOut().getBody().getContent());
				System.out.println("Subscribe response: "
						+ XMLPrettyPrinter.prettyPrint(WSNotificationWriter
								.getInstance().writeSubscribeResponse(subcribeResponse)));
			}

			// update metrics in provider using exchangeTrace
			final String req = ""
				+ "<web:GetQuote xmlns:web=\"http://www.webserviceX.NET/\">"
				+ "    <web:symbol>ebm</web:symbol>" + "</web:GetQuote>";
			final Document requestDoc = DocumentBuilderFactory.newInstance()
			.newDocumentBuilder().parse(
					new ByteArrayInputStream(req.getBytes()));
			final String resp = ""
				+ "<web:GetQuoteResponse>"
				+ "         <web:GetQuoteResult>1000000000</web:GetQuoteResult>"
				+ "</web:GetQuoteResponse>";
			final Document responseDoc = DocumentBuilderFactory.newInstance()
			.newDocumentBuilder().parse(
					new ByteArrayInputStream(resp.getBytes()));
			final DOMBuilder builder = new DOMBuilder();
			final org.jdom.Document requestjDoc = builder.build(requestDoc);
			final org.jdom.Document responsejDoc = builder.build(responseDoc);
			final ExchangeTrace trace1 = new ExchangeTraceImpl();
			trace1.setServiceName(new QName("http://www.webserviceX.NET/",
			"StockQuote"));
			trace1.setEndpointName("StockQuoteSoap1");
			trace1.setInterfaceName(new QName("http://www.webserviceX.NET/",
			"StockQuote"));
			trace1.setOperationName("GetQuote");
			trace1.setRequest(requestjDoc);
			trace1.setResponse(responsejDoc);
			trace1.setStartExchange(Calendar.getInstance().getTime());
			trace1.setEndExchange(Calendar.getInstance().getTime());
			trace1.setResponseType(ExchangeTrace.ResponseType.SUCCESS);

			((MonitoringProviderEndpointBehaviour) monitoringProviderEndpoint
					.getBehaviour()).addNewExchange(trace1);

			// wait the notification
			while (((MonitoringClientEndpointBehaviour) ftParisEndpoint
					.getBehaviour()).getMetrics().size() == 0) {
				System.out.println("Wait the first notification");
				Thread.sleep(100);
			}

			// update metrics in provider using reports
			final String exchangeId = "uuid:1";

			// add report1 in provider
			final Report report1 = RawReportFactory.getInstance().newReport();
			this.setReport(report1);

			// add report2 in provider
			final Report report2 = RawReportFactory.getInstance().newReport();
			this.setReport(report2);

			// add report3 in provider
			final Report report3 = RawReportFactory.getInstance().newReport();
			this.setReport(report3);

			// add report4 in provider
			final Report report4 = RawReportFactory.getInstance().newReport();
			this.setReport(report4);

			final ReportList reports = RawReportFactory.getInstance()
			.newReportList();
			reports.addReport(report1);
			reports.addReport(report2);
			reports.addReport(report3);
			reports.addReport(report4);

			((MonitoringProviderEndpointBehaviour) monitoringProviderEndpoint
					.getBehaviour()).addNewReportList(reports);

			// wait the notification
			while (((MonitoringClientEndpointBehaviour) ftParisEndpoint
					.getBehaviour()).getMetrics().size() == 1) {
				System.out.println("Wait the second notification");
				Thread.sleep(100);
			}

			// assert on the notification
			System.out.println("+++++++++ number of metrics: "
					+ ((MonitoringClientEndpointBehaviour) ftParisEndpoint
							.getBehaviour()).getMetrics().size());
			Assert.assertEquals(2,
					((MonitoringClientEndpointBehaviour) ftParisEndpoint
							.getBehaviour()).getMetrics().size());
			for (final QoSMetrics metric : ((MonitoringClientEndpointBehaviour) ftParisEndpoint
					.getBehaviour()).getMetrics()) {
				System.out.println("+++++++++ metrics: ");
				System.out.println(XMLPrettyPrinter.prettyPrint(WSDMFactory
						.newInstance().newWSDMWriter().getDocument(metric)));
			}

			// Test unsubscribe method
			final Unsubscribe unsubscribe = WSNotificationFactory.getInstance()
			.createUnsubscribe();
			final com.ebmwebsourcing.wsstar.addressing.definition.api.EndpointReferenceType epr = WSAddressingFactory
			.getInstance().newEndpointReferenceType();
			epr
			.setAddress(subcribeResponse.getSubscriptionReference()
					.getAddress());
			WsnSpecificTypeHelper.setEndpointReference(epr, unsubscribe);
			// unsubscribe.setEndpointReference(epr);
			WsnSpecificTypeHelper.setResourcesUuidType(WsnSpecificTypeHelper
					.getResourcesUuidType(subcribeResponse
							.getSubscriptionReference().getReferenceParameters()),
							unsubscribe);
			// unsubscribe.setResourcesUuid(subcribeResponse.getSubscriptionReference().getReferenceParameters().getResourcesUuidType());
			msg = XMLPrettyPrinter.prettyPrint(WSNotificationWriter.getInstance().writeUnsubscribe(unsubscribe));
			me = Util
			.createMessageExchange(
					ftParisEndpoint,
					monitoringProviderEndpoint,
					new QName("http://petals.ow2.org",
					"weatherInterfaceMonitoring"),
					new QName(
							"http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd",
					"UnSubscribe"), PatternType.IN_OUT, msg);

			// send unsubscription request
			final Exchange responseUnsubscribe = ftParisEndpoint.sendSync(me, 0);

			if (responseUnsubscribe.getOut().getBody().getContent() != null) {
				System.out.println("UnSubscribe response: "
						+ XMLPrettyPrinter.prettyPrint(responseUnsubscribe.getOut()
								.getBody().getContent()));
			}

			if (explorer) {
				Thread.sleep(10000000);
			}
		} finally {
			petals.getTransportersManager().stop();
			petals.getListenersManager().shutdownAllListeners();
		}
	}
}
