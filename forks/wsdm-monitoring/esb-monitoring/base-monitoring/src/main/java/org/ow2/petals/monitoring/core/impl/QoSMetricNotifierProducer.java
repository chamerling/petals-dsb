package org.ow2.petals.monitoring.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ow2.easywsdl.schema.util.XMLPrettyPrinter;
import org.ow2.petals.esb.kernel.api.endpoint.ClientEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.endpoint.ProviderEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.BusinessException;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.monitoring.core.api.MonitoringException;
import org.ow2.petals.monitoring.core.api.MonitoringProviderEndpoint;
import org.ow2.petals.monitoring.core.api.MonitoringProviderEndpointBehaviour;
import org.ow2.petals.monitoring.core.util.Util;
import org.ow2.petals.soap.handler.SOAPException;
import org.ow2.petals.soap.handler.SOAPSender;
import org.ow2.petals.transporter.api.transport.TransportException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import petals.ow2.org.exchange.PatternType;

import com.ebmwebsourcing.wsstar.addressing.definition.WSAddressingFactory;
import com.ebmwebsourcing.wsstar.addressing.definition.api.EndpointReferenceType;
import com.ebmwebsourcing.wsstar.addressing.definition.api.ReferenceParametersType;
import com.ebmwebsourcing.wsstar.addressing.definition.api.WSAddressingException;
import com.ebmwebsourcing.wsstar.dm.WSDMFactory;
import com.ebmwebsourcing.wsstar.dm.api.QoSMetrics;
import com.ebmwebsourcing.wsstar.dm.api.WSDMException;
import com.ebmwebsourcing.wsstar.notification.definition.WSNotificationFactory;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.Message;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.NotificationMessageHolderType;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.Notify;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.TopicExpressionType;
import com.ebmwebsourcing.wsstar.notification.definition.inout.WSNotificationWriter;
import com.ebmwebsourcing.wsstar.notification.definition.utils.WSNotificationException;
import com.ebmwebsourcing.wsstar.notification.extension.WSNotificationExtensionFactory;
import com.ebmwebsourcing.wsstar.notification.extension.api.ResourcesUuidType;
import com.ebmwebsourcing.wsstar.notification.extension.utils.WSNotificationExtensionException;
import com.ebmwebsourcing.wsstar.notification.extension.utils.WsnSpecificTypeHelper;
import com.ebmwebsourcing.wsstar.notification.service.basenotification.impl.NotificationProducerMgr;
import com.ebmwebsourcing.wsstar.notification.service.basenotification.impl.SubscriptionManagerMgr;

public class QoSMetricNotifierProducer extends Thread {

	private final Logger log = Logger.getLogger(QoSMetricNotifierProducer.class
			.getName());

	private final MonitoringProviderEndpointBehaviour behaviour;

	private final QoSMetrics metric;

	private final SOAPSender soapSender;

	private DocumentBuilderFactory documentFactory = null;

	public QoSMetricNotifierProducer(
			final MonitoringProviderEndpointBehaviour behaviour,
			final QoSMetrics metric) {
		this.behaviour = behaviour;
		this.metric = metric;
		this.soapSender = new SOAPSender();
		this.documentFactory = DocumentBuilderFactory.newInstance();
		this.documentFactory.setNamespaceAware(true);
	}

	private Exchange createQoSMetricMessageExchange(
			final String endpointAddress, final String uuid,
			final QoSMetrics metrics) throws MonitoringException {
		Exchange message = null;

		// find destination endpoint
		final Endpoint endpoint = this.behaviour.getEndpoint().getNode()
		.getRegistry().getEndpoint(QName.valueOf(endpointAddress));

		if (endpoint == null) {
			throw new MonitoringException("Impossible to find endpoint: "
					+ QName.valueOf(endpointAddress));
		}

		// marshall response
		String msg;
		try {
			final Notify notify = this.createQosMetricNotification(
					endpointAddress, uuid, metrics);
			final Document request = WSNotificationWriter.getInstance().writeNotify(notify);

			msg = XMLPrettyPrinter.prettyPrint(request);
			message = Util
			.createMessageExchange(
					(ClientEndpoint) this.behaviour.getEndpoint(),
					(ProviderEndpoint) endpoint,
					((MonitoringProviderEndpoint) this.behaviour
							.getEndpoint()).getInterfaceName(),
							new QName(
									"http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.xsd",
							"Notify"), PatternType.IN_ONLY, msg);
		} catch (final WSNotificationException e) {
			this.log.severe("Error: " + e.getMessage());
		} catch (final TransportException e) {
			this.log.severe("Error: " + e.getMessage());
		}
		return message;
	}

	private Notify createQosMetricNotification(final String endpointAddress,
			final String uuid, final QoSMetrics metrics)
	throws WSNotificationException {
		Notify notifyPayload = null;
		try {
			notifyPayload = WSNotificationFactory.getInstance().createNotify();
			final NotificationMessageHolderType msg = WSNotificationFactory
			.getInstance().createNotificationMessageHolderType();

			final Document doc = WSDMFactory.newInstance().newWSDMWriter()
			.getDocument(metrics);
			final Message mess = WSNotificationFactory.getInstance()
			.createMessage();
			final Element elmt = doc.getDocumentElement();
			WsnSpecificTypeHelper.setContentToMessage(elmt, mess);
			msg.setMessage(mess);

			final TopicExpressionType notifyTopicExpr = this
			.createQoSMetricTopicExpression();
			msg.setTopic(notifyTopicExpr);

			final EndpointReferenceType registrationRef = WSAddressingFactory
			.getInstance().newEndpointReferenceType();
			registrationRef.setAddress(endpointAddress);
			final ReferenceParametersType ref = registrationRef
			.newReferenceParameters();
			registrationRef.setReferenceParameters(ref);

			final ResourcesUuidType rUuids = WSNotificationExtensionFactory
			.getInstance().createResourcesUuidType();
			rUuids.addUuid(uuid);
			WsnSpecificTypeHelper.setResourcesUuidType(rUuids, ref);

			msg.setSubscriptionReference(registrationRef);

			final EndpointReferenceType producerRef = WSAddressingFactory
			.getInstance().newEndpointReferenceType();
			producerRef.setAddress(((ProviderEndpoint) this.behaviour
					.getEndpoint()).getQName().toString());
			msg.setProducerReference(producerRef);

			notifyPayload.addNotificationMessage(msg);
		} catch (final WSDMException e) {
			throw new WSNotificationException(e);
		} catch (final WSAddressingException e) {
			throw new WSNotificationException(e);
		} catch (final WSNotificationExtensionException e) {
			throw new WSNotificationException(e);
		}
		return notifyPayload;
	}

	private TopicExpressionType createQoSMetricTopicExpression()
	throws WSNotificationException {
		final TopicExpressionType notifyTopicExpr = WSNotificationFactory
		.getInstance().createTopicExpressionType();
		notifyTopicExpr
		.addTopicNameSpace("tns",
		"http://docs.oasis-open.org/wsdm/2004/12/mows/wsdm-mows-events.xml");
		notifyTopicExpr
		.setDialect("http://docs.oasis-open.org/wsn/t-1/TopicExpression/Concrete");
		notifyTopicExpr.setContent("tns:MetricsCapability");
		return notifyTopicExpr;
	}

	private Document createSoapEnvelopeFromPayload(final Document response)
	throws BusinessException {
		Document docResp = null;
		try {
			// create the document
			docResp = this.documentFactory.newDocumentBuilder().newDocument();
			final org.w3c.dom.Element enveloppe = docResp.createElementNS(
					"http://schemas.xmlsoap.org/soap/envelope/", "Enveloppe");
			enveloppe.setPrefix("soapenv");
			final org.w3c.dom.Element body = docResp.createElementNS(
					"http://schemas.xmlsoap.org/soap/envelope/", "Body");
			body.setPrefix("soapenv");

			if (response.getDocumentElement() != null) {
				body.appendChild(docResp.adoptNode(response
						.getDocumentElement().cloneNode(true)));
			}

			enveloppe.appendChild(body);
			docResp.appendChild(enveloppe);
		} catch (final ParserConfigurationException e) {
			throw new BusinessException(e);
		}
		return docResp;
	}

	private List<EndpointReferenceType> getConcernedClients()
	throws WSNotificationException {
		final List<EndpointReferenceType> eprs = new ArrayList<EndpointReferenceType>();
		final TopicExpressionType notifyTopicExpr = this
		.createQoSMetricTopicExpression();

		final List<String> uuids = ((NotificationProducerMgr) ((MonitoringProviderEndpoint) this.behaviour
				.getEndpoint()).getNotificationProducer()).getTopicsMgr()
				.getSubscriptionIdsFromTopicsSet(notifyTopicExpr, true);

		for (final String subscriptionId : uuids) {
			eprs
			.add(((SubscriptionManagerMgr) ((MonitoringProviderEndpoint) this.behaviour
					.getEndpoint()).getSubscriptionManager())
					.getConsumerEdpRefOfSubscription(subscriptionId));
		}
		return eprs;
	}

	@Override
	public void run() {
		String address = null;
		try {
			final List<EndpointReferenceType> eprs = this.getConcernedClients();
			final List<String> uuids = ((NotificationProducerMgr) ((MonitoringProviderEndpoint) this.behaviour
					.getEndpoint()).getNotificationProducer()).getTopicsMgr()
					.getSubscriptionIdsFromTopicsSet(
							this.createQoSMetricTopicExpression(), true);

			final Iterator<EndpointReferenceType> itEpr = eprs.iterator();
			final Iterator<String> itUuid = uuids.iterator();
			while (itEpr.hasNext() && itUuid.hasNext()) {
				final EndpointReferenceType epr = itEpr.next();
				final String uuid = itUuid.next();
				final Endpoint ep = this.behaviour.getEndpoint().getNode()
				.getRegistry().getEndpoint(
						QName.valueOf(epr.getAddress()));
				address = epr.getAddress();
				if (ep != null) {
					final Exchange ex = this.createQoSMetricMessageExchange(epr
							.getAddress(), uuid, this.metric);
					this.log.info("send internal notification to: "
							+ ex.getDestination());
					this.behaviour.getEndpoint().getNode()
					.getTransportersManager().push(ex,
							ep.getNode().getQName());
				} else {
					final Notify notify = this.createQosMetricNotification(epr
							.getAddress(), uuid, this.metric);
					final Document request = WSNotificationWriter
					.getInstance().writeNotify(notify);

					this.soapSender.sendSoapRequest(this
							.createSoapEnvelopeFromPayload(request), epr
							.getAddress());
					this.log.info("Notification sended to " + epr.getAddress());
				}
			}
		} catch (final TransportException e) {
			this.log.severe("Impossible to send notification to " + address + " : " + e.getMessage());
		} catch (final MonitoringException e) {
			this.log.severe("Impossible to send notification to " + address + " : " + e.getMessage());
		} catch (final WSNotificationException e) {
			this.log.severe("Impossible to send notification to " + address + " : " + e.getMessage());
		} catch (final BusinessException e) {
			this.log.severe("Impossible to send notification to " + address + " : " + e.getMessage());
		} catch (final SOAPException e) {
			this.log.severe("Impossible to send notification to " + address + " : " + e.getMessage());
		}
	}
}
