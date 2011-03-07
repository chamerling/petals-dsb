package org.ow2.petals.monitoring.datacollector.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ow2.easywsdl.schema.util.XMLPrettyPrinter;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.AbstractBehaviourImpl;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.BusinessException;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.MarshallerException;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.monitoring.datacollector.api.RawReportClientEndpointBehaviour;
import org.ow2.petals.monitoring.model.rawreport.api.RawReport;
import org.ow2.petals.monitoring.model.rawreport.api.RawReportException;
import org.ow2.petals.monitoring.model.rawreport.impl.RawReportFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ebmwebsourcing.wsstar.notification.definition.WSNotificationFactory;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.Notify;
import com.ebmwebsourcing.wsstar.notification.definition.inout.WSNotificationReader;
import com.ebmwebsourcing.wsstar.notification.definition.utils.WSNotificationException;

public class RawReportClientEndpointBehaviourImpl extends
		AbstractBehaviourImpl<RawReport> implements
		RawReportClientEndpointBehaviour {

	private static Logger log = Logger
			.getLogger(RawReportClientEndpointBehaviourImpl.class.getName());

	private final List<RawReport> rawReports = new ArrayList<RawReport>();

	private WSNotificationReader notificationReader = null;

	public RawReportClientEndpointBehaviourImpl(final Endpoint ep) {
		super(ep);
	}

	public void execute(final Exchange exchange) throws BusinessException {
		try {
			final Notify notification = this.marshallNotification(exchange
					.getIn().getBody().getContent());
			if (notification != null) {
				this.notify(notification);
			}
		} catch (final MarshallerException e) {
			throw new BusinessException(e);
		}

	}

	public WSNotificationReader getNotificationReader()
			throws WSNotificationException {
		if (this.notificationReader == null) {
			this.notificationReader = WSNotificationReader.getInstance();
		}
		return this.notificationReader;
	}

	public List<RawReport> getRawReports() {
		return this.rawReports;
	}

	public RawReport marshall(final Document doc) throws MarshallerException {
		RawReport rawreport = null;
		try {
			rawreport = RawReportFactory.getInstance().newRawReportReader()
					.readRawReport(doc);
		} catch (final RawReportException e) {
			throw new MarshallerException(e);
		}
		return rawreport;
	}

	public Notify marshallNotification(final Document doc)
			throws MarshallerException {
		Notify res = null;
		try {
			if (doc.getDocumentElement().getLocalName().equals("Notify")) {
				res = this.getNotificationReader().readNotify(doc);
			}
		} catch (final WSNotificationException e) {
			throw new MarshallerException(e);
		}
		return res;
	}

	public void notify(final Notify notification) {
		try {
			final Element msg = notification.getNotificationMessage().get(0)
					.getMessage().getContent();

			final DocumentBuilderFactory builderFactory = DocumentBuilderFactory
					.newInstance();
			builderFactory.setNamespaceAware(true);

			final Document doc = builderFactory.newDocumentBuilder()
					.newDocument();
			doc.appendChild(doc.adoptNode(msg));
			final RawReport rawreport = this.marshall(doc);
			this.rawReports.add(rawreport);

			RawReportClientEndpointBehaviourImpl.log.finest("Notification: \n"
					+ XMLPrettyPrinter.prettyPrint(doc));

		} catch (final ParserConfigurationException e) {
			e.printStackTrace();
			RawReportClientEndpointBehaviourImpl.log.severe(e.getMessage());
		} catch (final MarshallerException e) {
			e.printStackTrace();
			RawReportClientEndpointBehaviourImpl.log.severe(e.getMessage());
		} catch (final WSNotificationException e) {
			e.printStackTrace();
			RawReportClientEndpointBehaviourImpl.log.severe(e.getMessage());
		}
	}

	public Document unmarshall(final RawReport data) throws MarshallerException {
		Document doc = null;
		try {
			doc = RawReportFactory.getInstance().newRawReportWriter()
					.getDocument(data);
		} catch (final RawReportException e) {
			throw new MarshallerException(e);
		}
		return doc;
	}

}
