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
package org.ow2.petals.monitoring.datacollector.test;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.oasis_open.docs.wsn.bw_2.NotificationConsumer_NotifierEndpoint_Server;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.ow2.easywsdl.schema.api.XmlException;
import org.ow2.easywsdl.schema.util.XMLPrettyPrinter;
import org.ow2.petals.esb.external.protocol.soap.impl.server.SoapServer;
import org.ow2.petals.esb.kernel.ESBKernelFactoryImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.ESBKernelFactory;
import org.ow2.petals.esb.kernel.api.node.Node;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.monitoring.core.api.BaseMonitoring;
import org.ow2.petals.monitoring.core.api.MonitoringException;
import org.ow2.petals.monitoring.core.util.Util;
import org.ow2.petals.monitoring.datacollector.ESBDataCollectorFactoryImpl;
import org.ow2.petals.monitoring.datacollector.api.DataCollector;
import org.ow2.petals.monitoring.datacollector.api.MonitoringEngine;
import org.ow2.petals.monitoring.datacollector.api.RawReportClientEndpoint;
import org.ow2.petals.monitoring.datacollector.api.RawReportClientEndpointBehaviour;
import org.ow2.petals.monitoring.datacollector.api.RawReportProviderEndpoint;
import org.ow2.petals.monitoring.datacollector.api.RawReportProviderEndpointBehaviour;
import org.ow2.petals.monitoring.datacollector.api.RawReportService;
import org.ow2.petals.monitoring.datacollector.impl.MonitoringEngineImpl;
import org.ow2.petals.monitoring.datacollector.util.DataCollectorClient;
import org.ow2.petals.monitoring.datacollector.util.NotificationConsumerTest;
import org.ow2.petals.monitoring.model.rawreport.api.RawReport;
import org.ow2.petals.monitoring.model.rawreport.api.RawReportException;
import org.ow2.petals.monitoring.model.rawreport.api.Report;
import org.ow2.petals.monitoring.model.rawreport.impl.RawReportFactory;
import org.ow2.petals.transporter.api.transport.TransportException;
import org.xml.sax.SAXException;

import petals.ow2.org.exchange.PatternType;

import com.ebmwebsourcing.wsstar.addressing.definition.WSAddressingFactory;
import com.ebmwebsourcing.wsstar.notification.definition.WSNotificationFactory;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.SubscribeResponse;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.Unsubscribe;
import com.ebmwebsourcing.wsstar.notification.definition.inout.WSNotificationReader;
import com.ebmwebsourcing.wsstar.notification.definition.inout.WSNotificationWriter;
import com.ebmwebsourcing.wsstar.notification.extension.utils.WsnSpecificTypeHelper;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
public class DataCollectorTest extends TestCase {

	/**
	 * @param name
	 */
	public DataCollectorTest(final String name) {
		super(name);
	}

	private Node createESB(final boolean explorer) throws ESBException {
		final ESBKernelFactory factory = new ESBKernelFactoryImpl();
		final Node petals = factory.createNode(new QName(
				"http://petals.ow2.org", "node0"), explorer);
		return petals;
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDataCollector() throws MonitoringException,
			NoSuchInterfaceException, InterruptedException, XmlException,
			ESBException, TransportException {
		final boolean explorer = false;
		final Node petals = this.createESB(explorer);

		// create provider monitoring component
		final MonitoringEngine monitoringEngine = petals.createComponent(
				new QName("http://petals.ow2.org", "Monitoring"), "dataEngine",
				MonitoringEngineImpl.class);
		final BaseMonitoring baseMonitoring = monitoringEngine
				.createBaseMonitoring();
		final DataCollector dataCollector = monitoringEngine
				.createDataCollector();

		if (explorer) {
			Thread.sleep(10000000);
		}

		petals.getTransportersManager().stop();
		petals.getListenersManager().shutdownAllListeners();
	}

	public void testDataCollectorIntegration() throws Exception {
		System.out
				.println("\n\n************************ START DATA COLLECTOR INTEGRATION TEST ***********************\n\n");

		final boolean explorer = false;

		final ESBDataCollectorFactoryImpl factory = new ESBDataCollectorFactoryImpl();
		final Node petals = factory.createNode(new QName(
				"http://petals.ow2.org", "node0"), explorer);
		(factory).desactivatePEtALSConnexion();

		// Thread.sleep(10000000);

		Thread.sleep(500);

		// create WS admin client
		final JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory
				.newInstance();
		final org.apache.cxf.endpoint.Client adminWSCLient = dcf.createClient(
				"http://localhost:8085/services/adminExternalEndpoint?wsdl",
				Thread.currentThread().getContextClassLoader());

		// create monitoringEndpoint
		final Object[] res = adminWSCLient.invoke("createMonitoringEndpoint",
				"{http://www.webserviceX.NET/}StockQuote",
				"StockQuoteWSDMSoap1_WSDMMonitoring", true);
		System.out.println("createMonitoringEndpoint response: " + res[0]);
		Assert
				.assertEquals(
						"http://localhost:8085/services/StockQuoteWSDMSoap1_WSDMMonitoringClientProxyEndpoint",
						res[0]);

		Thread.sleep(5000);

		// create WS admin client
		// JaxWsDynamicClientFactory dcf =
		// JaxWsDynamicClientFactory.newInstance();
		// org.apache.cxf.endpoint.Client rawReportServiceWSCLient =
		// dcf.createClient("http://localhost:8085/services/rawReportEndpointClientProxyEndpoint?wsdl",
		// Thread.currentThread().getContextClassLoader());

		// start ws notifier service
		final NotificationConsumerTest notifier = new NotificationConsumerTest();
		final NotificationConsumer_NotifierEndpoint_Server notifServer = new NotificationConsumer_NotifierEndpoint_Server(
				notifier, "http://localhost:9002/NotificationEndpoint");

		final DataCollectorClient client = new DataCollectorClient();
		boolean test;

		System.out.println("CALL SUBSCRIBE ON RawReport Service ");
		test = DataCollectorClient
				.subcribe(
						new URI(
								"./src/test/resources/test/subscribe/rawreport/subscribeRequest.xml"),
						"http://localhost:8085/services/rawReportEndpointClientProxyEndpoint",
						new URI(
								"./src/test/resources/test/subscribe/rawreport/subscribeResponse.xml"));
		Assert.assertTrue(test);
		System.out.println("SUBSCRIBE ON RawReport Service => OK");

		System.out.println("CALL SUBSCRIBE ON WSDM endpoint");
		test = DataCollectorClient
				.subcribe(
						new URI(
								"./src/test/resources/test/subscribe/wsdm/subscribeRequest.xml"),
						"http://localhost:8085/services/StockQuoteWSDMSoap1_WSDMMonitoringClientProxyEndpoint",
						new URI(
								"./src/test/resources/test/subscribe/wsdm/subscribeResponse.xml"));
		Assert.assertTrue(test);
		System.out.println("SUBSCRIBE ON WSDM endpoint => OK");

		System.out.println("CALL ADDNEWREPORTLIST");
		test = DataCollectorClient
				.addNewReportList(
						new URI(
								"./src/test/resources/test/addNewReportList/addNewReportListRequest.xml"),
						"http://localhost:8085/services/rawReportEndpointClientProxyEndpoint");
		Assert.assertTrue(test);
		System.out.println("CALL ADDNEWREPORTLIST => OK");

		Thread.sleep(3000);

		Assert.assertEquals(2, notifier.getNotifications().size());

		if (explorer) {
			Thread.sleep(10000000);
		}

		// rawReportServiceWSCLient.destroy();
		SoapServer.getInstance().stop();
		petals.getTransportersManager().stop();
		petals.getListenersManager().shutdownAllListeners();
	}

	public void testRawReportService() throws MonitoringException,
			NoSuchInterfaceException, InterruptedException, XmlException,
			ESBException, TransportException, SAXException, IOException,
			ParserConfigurationException, RawReportException {
		final boolean explorer = false;
		final Node petals = this.createESB(explorer);

		// create provider monitoring component
		final MonitoringEngine monitoringEngine = petals.createComponent(
				new QName("http://petals.ow2.org", "Monitoring"), "dataEngine",
				MonitoringEngineImpl.class);
		final DataCollector dataCollector = monitoringEngine
				.createDataCollector();

		// create technical provider raw report service
		final RawReportService rawReportService = dataCollector
				.createRawReportService(new QName("http://petals.ow2.org",
						"rawReportService"));
		final RawReportProviderEndpoint rawReportProviderEndpoint = rawReportService
				.createRawReportEndpoint("rawReportEndpoint",
						new ArrayList<String>(Arrays
								.asList(new String[] { "RawReportTopic" })));

		// create raw report client endpoint
		final RawReportClientEndpoint ftGrenobleEndpoint = dataCollector
				.createRawReportClientEndpoint(new QName(
						"http://ftgrenoble.com", "ftGrenobleEndpoint"));

		// Test subscribe method
		String msg = "<wsn:Subscribe xmlns:wsn=\"http://docs.oasis-open.org/wsn/b-2\" xmlns:add=\"http://www.w3.org/2005/08/addressing\""
				+ " targetnamespace=\"http://docs.oasis-open.org/wsn/b-2\">"
				+ "<wsn:ConsumerReference xmlns:wsn=\"http://docs.oasis-open.org/wsn/b-2\" xmlns:add=\"http://www.w3.org/2005/08/addressing\">"
				+ "     <add:Address>{http://ftgrenoble.com}ftGrenobleEndpoint</add:Address>"
				+ "</wsn:ConsumerReference>"
				+ "<wsn:Filter>"
				+ "     <wsn:TopicExpression Dialect=\"http://www.w3.org/TR/1999/REC-xpath-19991116\" xmlns:raw-ev=\"http://www.semeuse.org/rawreport-events.xml\">"
				+ "           raw-ev:RawReportTopic"
				+ "     </wsn:TopicExpression>"
				+ "</wsn:Filter>"
				+ "</wsn:Subscribe>";
		Exchange me = Util
				.createMessageExchange(
						ftGrenobleEndpoint,
						rawReportProviderEndpoint,
						new QName("http://petals.ow2.org", "rawReport"),
						new QName(
								"http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd",
								"Subscribe"), PatternType.IN_OUT, msg);

		// send subscription request
		final Exchange response = ftGrenobleEndpoint.sendSync(me, 0);

		SubscribeResponse subcribeResponse = null;
		if (response.getOut().getBody().getContent() != null) {
			subcribeResponse = WSNotificationReader.getInstance().readSubscribeResponse(
							response.getOut().getBody().getContent());
			System.out.println("Subscribe response: "
					+ XMLPrettyPrinter.prettyPrint(WSNotificationWriter
							.getInstance().writeSubscribeResponse(subcribeResponse)));
		}

		final String exchangeId = "uuid:1";

		// add report1 in provider
		final Report report1 = RawReportFactory.getInstance().newReport();
		this.setReport(report1);
		((RawReportProviderEndpointBehaviour) rawReportProviderEndpoint
				.getBehaviour()).addNewReport(exchangeId, report1);

		// add report2 in provider
		final Report report2 = RawReportFactory.getInstance().newReport();
		this.setReport(report2);
		((RawReportProviderEndpointBehaviour) rawReportProviderEndpoint
				.getBehaviour()).addNewReport(exchangeId, report2);

		// add report3 in provider
		final Report report3 = RawReportFactory.getInstance().newReport();
		this.setReport(report3);
		((RawReportProviderEndpointBehaviour) rawReportProviderEndpoint
				.getBehaviour()).addNewReport(exchangeId, report3);

		// add report4 in provider
		final Report report4 = RawReportFactory.getInstance().newReport();
		this.setReport(report4);
		((RawReportProviderEndpointBehaviour) rawReportProviderEndpoint
				.getBehaviour()).addNewReport(exchangeId, report4);

		// wait the notification
		Thread.sleep(5000);

		// assert on the notification
		List<RawReport> rawReports = null;
		rawReports = ((RawReportClientEndpointBehaviour) ftGrenobleEndpoint
				.getBehaviour()).getRawReports();
		System.out.println("+++++%%%%%%%%++++ number of rawreport: "
				+ rawReports.size());

		System.out.println("8 juil. 2009 16:27:11: ");

		Assert.assertEquals(1, rawReports.size());

		for (final RawReport raw : rawReports) {
			System.out.println("raw = " + raw);
			System.out.println(XMLPrettyPrinter.prettyPrint(RawReportFactory
					.getInstance().newRawReportWriter().getDocument(raw)));
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

		// unsubscribe.setResourcesUuid(subcribeResponse
		// .getSubscriptionReference().getReferenceParameters()
		// .getResourcesUuidType());
		msg = XMLPrettyPrinter.prettyPrint(WSNotificationWriter.getInstance().writeUnsubscribe(unsubscribe));
		me = Util
				.createMessageExchange(
						ftGrenobleEndpoint,
						rawReportProviderEndpoint,
						new QName("http://petals.ow2.org", "rawReport"),
						new QName(
								"http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd",
								"UnSubscribe"), PatternType.IN_OUT, msg);

		// send unsubscription request
		final Exchange responseUnsubscribe = ftGrenobleEndpoint.sendSync(me, 0);

		if (responseUnsubscribe.getOut().getBody().getContent() != null) {
			System.out.println("UnSubscribe response: "
					+ XMLPrettyPrinter.prettyPrint(responseUnsubscribe.getOut()
							.getBody().getContent()));
		}

		if (explorer) {
			Thread.sleep(10000000);
		}

		petals.getTransportersManager().stop();
		petals.getListenersManager().shutdownAllListeners();
	}
}
