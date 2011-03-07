/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.ow2.petals.kernel.registry.listener.wsdm;

import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.registry.RegistryListener;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.soap.handler.SOAPException;
import org.ow2.petals.soap.handler.SOAPSender;
import org.ow2.petals.util.LoggingUtil;
import org.petals.ow2.admin.Admin;
import org.petals.ow2.admin.Admin_Service;
import org.w3c.dom.Document;

import com.ebmwebsourcing.wsstar.addressing.definition.WSAddressingFactory;
import com.ebmwebsourcing.wsstar.addressing.definition.api.EndpointReferenceType;
import com.ebmwebsourcing.wsstar.notification.definition.WSNotificationFactory;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.FilterType;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.Subscribe;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.TopicExpressionType;
import com.ebmwebsourcing.wsstar.notification.definition.inout.WSNotificationWriter;

/**
 * Notify the monitoring Bus that a new endpoint has been added in the registry
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = RegistryListener.class) })
public class MonitoringNotifierImpl implements RegistryListener {

	private static final String DEFAULT_MONITORING_REGISTRATION_URL = "http://localhost:8085/services/adminExternalEndpoint";

	private static final String MONITORING_ADMIN_URL_KEY = "user.monitoring.admin";

	private static final String MONITORING_LISTENER_URL_KEY = "user.monitoring.listener";

	private static final String DEFAULT_MONITORING_LISTENER_URL = "http://localhost:8080/soa4all-dsb-monitoring-notifserver-1.0-SNAPSHOT/ws/NotificationListenerService";

	private static final String ENDPOINT_SUFFIX = "_WSDMMonitoring";

	private DocumentBuilderFactory factory;

	private String monitoringServiceRegistrationURL;

	private String monitoringListener;

	private static Admin_Service adminService;

	@Requires(name = "configuration", signature = ConfigurationService.class)
	private ConfigurationService configurationService;

	@Monolog(name = "logger")
	private Logger logger;

	private LoggingUtil log;

	@LifeCycle(on = LifeCycleType.START)
	protected void start() {
		this.log = new LoggingUtil(this.logger);
		this.log.debug("Starting...");

		Map<String, String> userConf = this.configurationService
				.getContainerConfiguration().getUserConfiguration();
		if (userConf != null) {
			this.monitoringServiceRegistrationURL = userConf
					.get(MONITORING_ADMIN_URL_KEY);

			// to automatically register a notification listener
			this.monitoringListener = userConf.get(MONITORING_LISTENER_URL_KEY);
		}

		if (this.monitoringServiceRegistrationURL == null) {
			this.monitoringServiceRegistrationURL = DEFAULT_MONITORING_REGISTRATION_URL;
		}

		if (this.log.isDebugEnabled()) {
			this.log.debug("Registration Notification will be sent to : "
					+ this.monitoringServiceRegistrationURL);
		}

		this.factory = DocumentBuilderFactory.newInstance();
	}

	@LifeCycle(on = LifeCycleType.STOP)
	protected void stop() {
		this.log.debug("Stopping...");
	}

	/**
	 * {@inheritDoc}
	 */
	public void onRegister(ServiceEndpoint endpoint) {
		// let's say to the monitoring platform that there is something new...
		if (this.log.isInfoEnabled()) {
			this.log
					.info("Notifying monitoring Bus that endpoint has been registered : "
							+ endpoint);
		}
		Thread t = new AddTask(endpoint);
		t.start();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onUnregister(ServiceEndpoint endpoint) {

	}

	private class AddTask extends Thread {

		private final ServiceEndpoint endpoint;

		/**
		 * 
		 */
		public AddTask(ServiceEndpoint serviceEndpoint) {
			this.endpoint = serviceEndpoint;
		}

		@Override
		public void run() {
			URL wsdl = null;
			try {
				wsdl = new URL(
						MonitoringNotifierImpl.this.monitoringServiceRegistrationURL
								+ "?wsdl");
			} catch (MalformedURLException e) {
				MonitoringNotifierImpl.this.log.warning(e.getMessage());
			}

			Admin_Service client = getClient(wsdl);
			if (client != null) {
				Admin port = client.getAdminSOAP();
				javax.xml.namespace.QName _createMonitoringEndpoint_wsdmServiceName = this.endpoint
						.getServiceName();
				java.lang.String _createMonitoringEndpoint_wsdmProviderEndpointName = this.endpoint
						.getEndpointName()
						+ ENDPOINT_SUFFIX;
				boolean _createMonitoringEndpoint_exposeInSoap = true;
				java.lang.String _createMonitoringEndpoint__return = port
						.createMonitoringEndpoint(
								_createMonitoringEndpoint_wsdmServiceName,
								_createMonitoringEndpoint_wsdmProviderEndpointName,
								_createMonitoringEndpoint_exposeInSoap);
				MonitoringNotifierImpl.this.log
						.info("createMonitoringEndpoint.result="
								+ _createMonitoringEndpoint__return);

				// send the subscribe to the default listener if any...
				// if (MonitoringNotifierImpl.this.monitoringListener != null) {
				// this.defaultSubscriber(_createMonitoringEndpoint__return);
				// } else {
				// System.out.println("No subscriber!!!");
				// }

			} else {
				MonitoringNotifierImpl.this.log.error("Can not get client...");
			}
		}

		/**
		 * @param _createMonitoringEndpoint__return
		 */
		private void defaultSubscriber(
				java.lang.String _createMonitoringEndpoint__return) {
			try {
				Document request = MonitoringNotifierImpl.this
						.createSubscribeRequest(MonitoringNotifierImpl.this.monitoringListener);

				writeDocument(request, System.out);

				SOAPSender sender = new SOAPSender();
				Document response = sender.sendSoapRequest(
						MonitoringNotifierImpl.this
								.createSoapEnvelopeFromPayload(request),
						_createMonitoringEndpoint__return);
				System.out.println("RESPONSE : ");
				writeDocument(response, System.out);
			} catch (SOAPException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static synchronized Admin_Service getClient(URL wsdlURL) {
		if (adminService == null) {
			try {
				adminService = new Admin_Service(wsdlURL, new QName(
						"http://ow2.petals.org/Admin/", "Admin"));
			} catch (Exception e) {
			}
		}
		return adminService;
	}

	private Document createSubscribeRequest(String address) throws Exception {
		Subscribe subscribePayload = WSNotificationFactory.getInstance()
				.createSubscribe();

		EndpointReferenceType consumerEdpRef = WSAddressingFactory
				.getInstance().newEndpointReferenceType();
		consumerEdpRef.setAddress(address);

		subscribePayload.setConsumerReference(consumerEdpRef);

		TopicExpressionType topic = WSNotificationFactory.getInstance()
				.createTopicExpressionType();
		topic
				.addTopicNameSpace("mows-ev",
						"http://docs.oasis-open.org/wsdm/2004/12/mows/wsdm-mows-events.xml");
		topic.setDialect("http://www.w3.org/TR/1999/REC-xpath-19991116");
		topic.setContent("mows-ev:MetricsCapability");

		FilterType filter = WSNotificationFactory.getInstance()
				.createFiltertype();
		filter.setTopicExpression(topic);

		subscribePayload.setFilter(filter);

		return WSNotificationWriter.getInstance().writeSubscribe(
				subscribePayload);

	}

	private Document createSoapEnvelopeFromPayload(final Document bodyElement)
			throws Exception {
		Document docResp = null;
		// create the document
		docResp = this.factory.newDocumentBuilder().newDocument();
		final org.w3c.dom.Element enveloppe = docResp.createElementNS(
				"http://schemas.xmlsoap.org/soap/envelope/", "Enveloppe");
		enveloppe.setPrefix("soapenv");
		final org.w3c.dom.Element body = docResp.createElementNS(
				"http://schemas.xmlsoap.org/soap/envelope/", "Body");
		body.setPrefix("soapenv");

		if (bodyElement.getDocumentElement() != null) {
			body.appendChild(docResp.adoptNode(bodyElement.getDocumentElement()
					.cloneNode(true)));
		}
		enveloppe.appendChild(body);
		docResp.appendChild(enveloppe);
		return docResp;
	}

	public static void writeDocument(final Document document,
			final OutputStream outputStream) throws Exception {

		if ((document != null) && (outputStream != null)) {
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();

			transformer.transform(new DOMSource(document), new StreamResult(
					outputStream));
		} else {
			throw new Exception("Can not write document to output stream");
		}
	}
}
