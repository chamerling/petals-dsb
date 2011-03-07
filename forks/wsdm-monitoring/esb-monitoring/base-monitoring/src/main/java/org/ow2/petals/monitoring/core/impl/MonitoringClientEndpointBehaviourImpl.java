package org.ow2.petals.monitoring.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.ow2.easywsdl.schema.util.XMLPrettyPrinter;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.AbstractBehaviourImpl;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.BusinessException;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.MarshallerException;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.monitoring.core.api.MonitoringClientEndpoint;
import org.ow2.petals.monitoring.core.api.MonitoringClientEndpointBehaviour;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ebmwebsourcing.wsstar.dm.WSDMFactory;
import com.ebmwebsourcing.wsstar.dm.api.QoSMetrics;
import com.ebmwebsourcing.wsstar.dm.api.WSDMException;
import com.ebmwebsourcing.wsstar.dm.api.WSDMReader;
import com.ebmwebsourcing.wsstar.notification.definition.WSNotificationFactory;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.Notify;
import com.ebmwebsourcing.wsstar.notification.definition.inout.WSNotificationReader;
import com.ebmwebsourcing.wsstar.notification.definition.inout.WSNotificationWriter;
import com.ebmwebsourcing.wsstar.notification.definition.utils.WSNotificationException;
import com.ebmwebsourcing.wsstar.notification.extension.utils.WsnSpecificTypeHelper;

public class MonitoringClientEndpointBehaviourImpl extends
		AbstractBehaviourImpl<Notify> implements
		MonitoringClientEndpointBehaviour {

	private static Logger log = Logger
			.getLogger(MonitoringClientEndpointBehaviourImpl.class.getName());

	private WSNotificationReader notificationReader = null;

	private WSNotificationWriter notificationWriter = null;

	private final List<QoSMetrics> metrics = new ArrayList<QoSMetrics>();

	private WSDMReader reader = null;

	public MonitoringClientEndpointBehaviourImpl(final Endpoint ep) {
		super(ep);
		if (this.endpoint instanceof MonitoringClientEndpoint) {
			((MonitoringClientEndpoint) this.endpoint)
					.setNotificationConsumer(this);
		}
	}

	public void execute(final Exchange exchange) throws BusinessException {
		try {
			System.out.println("NOTIF: \n"
					+ XMLPrettyPrinter.prettyPrint(exchange.getIn().getBody()
							.getContent()));
			final Notify notification = this.marshall(exchange.getIn()
					.getBody().getContent());
			if (notification != null) {
				this.notify(notification);
			}

		} catch (final MarshallerException e) {
			throw new BusinessException(e);
		}
	}

	public List<QoSMetrics> getMetrics() {
		return this.metrics;
	}

	public WSNotificationReader getNotificationReader()
			throws WSNotificationException {
		if (this.notificationReader == null) {
			this.notificationReader = WSNotificationReader.getInstance();
		}
		return this.notificationReader;
	}

	public WSNotificationWriter getNotificationWriter()
			throws WSNotificationException {
		if (this.notificationWriter == null) {
			this.notificationWriter = WSNotificationWriter.getInstance();
		}
		return this.notificationWriter;
	}

	public WSDMReader getReader() throws WSDMException {
		if (this.reader == null) {
			this.reader = WSDMFactory.newInstance().newWSDMReader();
		}
		return this.reader;
	}

	public Notify marshall(final Document doc) throws MarshallerException {
		Notify res = null;
		try {
			
			if (doc.getDocumentElement().getLocalName().equals("Notify")) {
				res = this.getNotificationReader().readNotify(doc);
			}
		} catch (final WSNotificationException e) {
			e.printStackTrace();
			throw new MarshallerException(e);
		}
		return res;
	}

	public void notify(final Notify notification) {
		try {
			final Element msg = WsnSpecificTypeHelper
					.getContentFromMessage(notification
							.getNotificationMessage().get(0).getMessage());

			final DocumentBuilderFactory builderFactory = DocumentBuilderFactory
					.newInstance();
			builderFactory.setNamespaceAware(true);

			final QoSMetrics metric = this.getReader().readOperationMetric(
					msg.getOwnerDocument());
			this.metrics.add(metric);

			MonitoringClientEndpointBehaviourImpl.log.finest("Notification: \n"
					+ XMLPrettyPrinter.prettyPrint(this
							.unmarshall(notification)));
		} catch (final WSDMException e) {
			e.printStackTrace();
			MonitoringClientEndpointBehaviourImpl.log.severe(e.getMessage());
		} catch (final DOMException e) {
			e.printStackTrace();
			MonitoringClientEndpointBehaviourImpl.log.severe(e.getMessage());
		} catch (final MarshallerException e) {
			e.printStackTrace();
			MonitoringClientEndpointBehaviourImpl.log.severe(e.getMessage());
		} catch (final WSNotificationException e) {
			e.printStackTrace();
			MonitoringClientEndpointBehaviourImpl.log.severe(e.getMessage());
		}
	}

	public void setNotificationReader(
			final WSNotificationReader notificationReader) {
		this.notificationReader = notificationReader;
	}

	public void setNotificationWriter(
			final WSNotificationWriter notificationWriter) {
		this.notificationWriter = notificationWriter;
	}

	public Document unmarshall(final Notify notify) throws MarshallerException {
		Document res = null;
		try {
			res = this.getNotificationWriter().writeNotify(notify);
		} catch (final WSNotificationException e) {
			throw new MarshallerException(e);
		}
		return res;
	}

}
