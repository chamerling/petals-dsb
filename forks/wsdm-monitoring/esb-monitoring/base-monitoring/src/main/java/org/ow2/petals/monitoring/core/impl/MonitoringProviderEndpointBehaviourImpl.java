package org.ow2.petals.monitoring.core.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.lang.NotImplementedException;
import org.jdom.output.XMLOutputter;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.WSDL4ComplexWsdlFactory;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.WSDL4ComplexWsdlReader;
import org.ow2.easywsdl.schema.SchemaFactory;
import org.ow2.easywsdl.schema.api.Schema;
import org.ow2.easywsdl.schema.api.SchemaException;
import org.ow2.easywsdl.schema.api.SchemaReader;
import org.ow2.easywsdl.schema.api.XmlException;
import org.ow2.easywsdl.schema.api.absItf.AbsItfSchema;
import org.ow2.easywsdl.schema.util.DOMUtil;
import org.ow2.easywsdl.schema.util.SourceHelper;
import org.ow2.easywsdl.schema.util.XMLPrettyPrinter;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.abstractItf.AbsItfDescription;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.endpoint.ProviderEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.AbstractBehaviourImpl;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.BusinessException;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.MarshallerException;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.monitoring.core.api.ExchangeTrace;
import org.ow2.petals.monitoring.core.api.MonitoringException;
import org.ow2.petals.monitoring.core.api.MonitoringProviderEndpoint;
import org.ow2.petals.monitoring.core.api.MonitoringProviderEndpointBehaviour;
import org.ow2.petals.monitoring.model.rawreport.api.RawReportException;
import org.ow2.petals.monitoring.model.rawreport.api.RawReportReader;
import org.ow2.petals.monitoring.model.rawreport.api.Report;
import org.ow2.petals.monitoring.model.rawreport.api.ReportList;
import org.ow2.petals.monitoring.model.rawreport.impl.RawReportFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ebmwebsourcing.wsstar.dm.WSDMFactory;
import com.ebmwebsourcing.wsstar.dm.api.QoSMetrics;
import com.ebmwebsourcing.wsstar.dm.api.WSDMException;
import com.ebmwebsourcing.wsstar.dm.api.WSDMWriter;
import com.ebmwebsourcing.wsstar.dm.api.expression.QoSDuration;
import com.ebmwebsourcing.wsstar.dm.api.expression.QoSInteger;
import com.ebmwebsourcing.wsstar.notification.definition.WSNotificationFactory;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.GetCurrentMessage;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.GetCurrentMessageResponse;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.PauseSubscription;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.PauseSubscriptionResponse;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.Renew;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.RenewResponse;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.ResumeSubscription;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.ResumeSubscriptionResponse;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.Subscribe;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.SubscribeResponse;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.Unsubscribe;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.UnsubscribeResponse;
import com.ebmwebsourcing.wsstar.notification.definition.inout.WSNotificationReader;
import com.ebmwebsourcing.wsstar.notification.definition.inout.WSNotificationWriter;
import com.ebmwebsourcing.wsstar.notification.definition.utils.WSNotificationException;
import com.ebmwebsourcing.wsstar.notification.extension.utils.WSNotificationExtensionException;
import com.ebmwebsourcing.wsstar.notification.service.basenotification.impl.SubscriptionManagerMgr;
import com.ebmwebsourcing.wsstar.notification.service.fault.WSNotificationFault;

public class MonitoringProviderEndpointBehaviourImpl extends
		AbstractBehaviourImpl<Object> implements
		MonitoringProviderEndpointBehaviour {

	private static Logger log = Logger
			.getLogger(MonitoringProviderEndpointBehaviourImpl.class.getName());

	private WSNotificationReader notificationReader = null;

	private WSNotificationWriter notificationWriter = null;

	private final Map<QName, QoSMetrics> qosmetrics = Collections
			.synchronizedMap(new HashMap<QName, QoSMetrics>());

	private boolean firstReading = true;

	private WSDMFactory wsdmFactory = null;

	private WSDMWriter wsdmWriter = null;

	private static WSDL4ComplexWsdlReader wsdlReader = null;

	private static RawReportReader reportReader = null;

	private DocumentBuilderFactory documentFactory = null;

	private static Map<URI, AbsItfDescription> wsdlImports = null;
	private static Map<URI, AbsItfSchema> schemaImports = null;

	static {
		try {
			final SchemaReader schemaReader = SchemaFactory.newInstance()
					.newSchemaReader();
			MonitoringProviderEndpointBehaviourImpl.schemaImports = new HashMap<URI, AbsItfSchema>();

			// add report
			final URL reporturl = Thread.currentThread()
					.getContextClassLoader().getResource(
							"imports/rawreport/rawReport.xsd");
			final Schema report = schemaReader.read(reporturl);
			MonitoringProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"imports/rawreport/rawReport.xsd"), report);
			MonitoringProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"rawReport.xsd"), report);

			// add addressing
			final URL wsaddrurl = Thread.currentThread()
					.getContextClassLoader().getResource("imports/ws-addr.xsd");
			final Schema addressing = schemaReader.read(wsaddrurl);
			MonitoringProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"imports/ws-addr.xsd"), addressing);
			MonitoringProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"ws-addr.xsd"), addressing);

			// add resources
			final URL rp2url = Thread.currentThread().getContextClassLoader()
					.getResource("imports/rp-2.xsd");
			final Schema rp_2 = schemaReader.read(rp2url);
			MonitoringProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"imports/rp-2.xsd"), rp_2);
			MonitoringProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"rp-2.xsd"), rp_2);

			// add resources
			final URL r2url = Thread.currentThread().getContextClassLoader()
					.getResource("imports/r-2.xsd");
			final Schema r_2 = schemaReader.read(r2url);
			MonitoringProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"imports/r-2.xsd"), r_2);
			MonitoringProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"r-2.xsd"), r_2);

			// add addressing
			final URL bw2url = Thread.currentThread().getContextClassLoader()
					.getResource("imports/b-2.xsd");
			final Schema b_2 = schemaReader.read(bw2url);
			MonitoringProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"imports/b-2.xsd"), b_2);
			MonitoringProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"b-2.xsd"), b_2);

			// add muws-part1-2
			final URL muws12url = Thread.currentThread()
					.getContextClassLoader().getResource("imports/muws1-2.xsd");
			final Schema muws12 = schemaReader.read(muws12url);
			MonitoringProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"imports/muws1-2.xsd"), muws12);
			MonitoringProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"muws1-2.xsd"), muws12);

			// add muws-part2-2
			final URL muws22url = Thread.currentThread()
					.getContextClassLoader().getResource("imports/muws2-2.xsd");
			final Schema muws22 = schemaReader.read(muws22url);
			MonitoringProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"imports/muws2-2.xsd"), muws22);
			MonitoringProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"muws2-2.xsd"), muws22);

			// add mows-2
			final URL mows2url = Thread.currentThread().getContextClassLoader()
					.getResource("imports/mows-2.xsd");
			final Schema mows2 = schemaReader.read(mows2url);
			MonitoringProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"imports/mows-2.xsd"), mows2);
			MonitoringProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"mows-2.xsd"), mows2);

		} catch (final WSDLException e) {
			e.printStackTrace();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final SchemaException e) {
			e.printStackTrace();
		}
	}

	static {
		try {
			final WSDL4ComplexWsdlReader wsdlReader = MonitoringProviderEndpointBehaviourImpl
					.getWsdlReader();
			MonitoringProviderEndpointBehaviourImpl.wsdlImports = new HashMap<URI, AbsItfDescription>();

			// add rw-2.wsdl
			final URL rw2url = Thread.currentThread().getContextClassLoader()
					.getResource("imports/rw-2.wsdl");
			final AbsItfDescription rw_2 = wsdlReader.read(rw2url,
					MonitoringProviderEndpointBehaviourImpl.wsdlImports,
					MonitoringProviderEndpointBehaviourImpl.schemaImports);
			MonitoringProviderEndpointBehaviourImpl.wsdlImports.put(new URI(
					"imports/rw-2.wsdl"), rw_2);
			MonitoringProviderEndpointBehaviourImpl.wsdlImports.put(new URI(
					"rw-2.wsdl"), rw_2);

			// add rpw-2.wsdl
			final URL rpw2url = Thread.currentThread().getContextClassLoader()
					.getResource("imports/rpw-2.wsdl");
			final AbsItfDescription rpw_2 = wsdlReader.read(rpw2url,
					MonitoringProviderEndpointBehaviourImpl.wsdlImports,
					MonitoringProviderEndpointBehaviourImpl.schemaImports);
			MonitoringProviderEndpointBehaviourImpl.wsdlImports.put(new URI(
					"imports/rpw-2.wsdl"), rpw_2);
			MonitoringProviderEndpointBehaviourImpl.wsdlImports.put(new URI(
					"rpw-2.wsdl"), rpw_2);

			// add bw-2.wsdl
			final URL bw2url = Thread.currentThread().getContextClassLoader()
					.getResource("imports/bw-2.wsdl");
			final AbsItfDescription bw_2 = wsdlReader.read(bw2url,
					MonitoringProviderEndpointBehaviourImpl.wsdlImports,
					MonitoringProviderEndpointBehaviourImpl.schemaImports);
			MonitoringProviderEndpointBehaviourImpl.wsdlImports.put(new URI(
					"imports/bw-2.wsdl"), bw_2);
			MonitoringProviderEndpointBehaviourImpl.wsdlImports.put(new URI(
					"bw-2.wsdl"), bw_2);

			// add wsdm
			final URL wsdmurl = Thread.currentThread().getContextClassLoader()
					.getResource("wsdm.wsdl");
			final AbsItfDescription wsdm = wsdlReader.read(wsdmurl,
					MonitoringProviderEndpointBehaviourImpl.wsdlImports,
					MonitoringProviderEndpointBehaviourImpl.schemaImports);
			MonitoringProviderEndpointBehaviourImpl.wsdlImports.put(new URI(
					"wsdm.wsdl"), wsdm);

		} catch (final URISyntaxException e) {
			e.printStackTrace();
		} catch (final SchemaException e) {
			e.printStackTrace();
		}
	}

	public static RawReportReader getReportReader() throws RawReportException {
		if (MonitoringProviderEndpointBehaviourImpl.reportReader == null) {
			MonitoringProviderEndpointBehaviourImpl.reportReader = RawReportFactory
					.getInstance().newRawReportReader();
		}
		return MonitoringProviderEndpointBehaviourImpl.reportReader;
	}

	public static WSDL4ComplexWsdlReader getWsdlReader() throws WSDLException {
		if (MonitoringProviderEndpointBehaviourImpl.wsdlReader == null) {
			MonitoringProviderEndpointBehaviourImpl.wsdlReader = WSDL4ComplexWsdlFactory
					.newInstance().newWSDLReader();
		}
		return MonitoringProviderEndpointBehaviourImpl.wsdlReader;
	}

	public MonitoringProviderEndpointBehaviourImpl(final Endpoint ep) {
		super(ep);
		this.documentFactory = DocumentBuilderFactory.newInstance();
		this.documentFactory.setNamespaceAware(true);
	}

	public void addNewExchange(final ExchangeTrace trace)
			throws MonitoringException {
		try {
			QoSMetrics metric = this.qosmetrics.get(new QName(trace
					.getInterfaceName().getNamespaceURI(), trace
					.getOperationName()));
			if (metric == null) {
				// create new metric
				metric = this.createQosMetrics(trace.getOperationName(), trace
						.getInterfaceName());
			}

			this.updateMetric(metric, new XMLOutputter().outputString(
					trace.getRequest()).length(), new XMLOutputter()
					.outputString(trace.getResponse()).length(), trace
					.getStartExchange(), trace.getEndExchange(), trace
					.getResponseType() == ExchangeTrace.ResponseType.SUCCESS,
					trace.getResponseType() == ExchangeTrace.ResponseType.FAIL);

			final QoSMetricNotifierProducer notifier = new QoSMetricNotifierProducer(
					this, metric);
			notifier.start();

		} catch (final WSDMException e) {
			throw new MonitoringException(e);
		}
	}

	public void addNewReportList(final ReportList reports)
			throws MonitoringException {
		if (reports.getReports().size() == 4) {
			final Report reportProviderIn = reports.getReports().get(1);
			final Report reportProviderOut = reports.getReports().get(2);

			try {
				QoSMetrics metric = this.qosmetrics
						.get(new QName(QName.valueOf(
								reportProviderIn.getServiceName())
								.getNamespaceURI(), reportProviderIn
								.getOperationName()));
				if (metric == null) {
					// create new metric
					QName itfName = null;
					if (reportProviderIn.getInterfaceName() != null) {
						itfName = QName.valueOf(reportProviderIn
								.getInterfaceName());
					}
					metric = this.createQosMetrics(reportProviderIn
							.getOperationName(), itfName);

					this.qosmetrics.put(new QName(QName.valueOf(
							reportProviderIn.getServiceName())
							.getNamespaceURI(), reportProviderIn
							.getOperationName()), metric);
					MonitoringProviderEndpointBehaviourImpl.log
							.info("metric created for operation: "
									+ reportProviderIn.getOperationName());
				}

				this.updateMetric(metric, new Long(reportProviderIn
						.getContentLength()).intValue(), new Long(
						reportProviderOut.getContentLength()).intValue(),
						reportProviderIn.getDateInGMT(), reportProviderOut
								.getDateInGMT(), reportProviderOut
								.getDoesThisRequestInIsAnException(),
						reportProviderOut.getDoesThisRequestInIsAnException());

				final QoSMetricNotifierProducer notifier = new QoSMetricNotifierProducer(
						this, metric);
				notifier.start();

			} catch (final WSDMException e) {
				throw new MonitoringException(e);
			}
		} else {
			throw new MonitoringException("Incorrect number of report");
		}
	}

	private Document convertFirstElementIntoDocument(final Document in)
			throws ParserConfigurationException {
		final Document res = this.documentFactory.newDocumentBuilder()
				.newDocument();
		final Element first = DOMUtil.getFirstElement(in.getDocumentElement());
		if (first != null) {
			res.appendChild(res.adoptNode(first.cloneNode(true)));
		}
		return res;
	}

	private QoSMetrics createQosMetrics(final String operationName,
			final QName itfName) throws WSDMException {
		final QoSMetrics metrics = WSDMFactory.newInstance().newQoSMetrics();

		// serviceTime
		final QoSDuration serviceTime = WSDMFactory.newInstance()
				.newQoSDuration();

		// maxResponseTime
		final QoSDuration maxResponseTime = WSDMFactory.newInstance()
				.newQoSDuration();

		// lastResponseTime
		final QoSDuration lastResponseTime = WSDMFactory.newInstance()
				.newQoSDuration();

		// numberOfSuccessfulRequests
		final QoSInteger numberOfSuccessfulRequests = WSDMFactory.newInstance()
				.newQoSInteger();

		// numberOfRequests
		final QoSInteger numberOfRequests = WSDMFactory.newInstance()
				.newQoSInteger();

		// numberOfFailedRequests
		final QoSInteger numberOfFailedRequests = WSDMFactory.newInstance()
				.newQoSInteger();

		// maxRequestSize
		final QoSInteger maxRequestSize = WSDMFactory.newInstance()
				.newQoSInteger();

		// maxResponseSize
		final QoSInteger maxResponseSize = WSDMFactory.newInstance()
				.newQoSInteger();

		// lastRequestSize
		final QoSInteger lastRequestSize = WSDMFactory.newInstance()
				.newQoSInteger();

		// lastResponseSize
		final QoSInteger lastResponseSize = WSDMFactory.newInstance()
				.newQoSInteger();

		// set values in metrics
		metrics.setOperation(operationName);
		metrics.setPortType(itfName);
		metrics.setServiceTime(serviceTime);
		metrics.setMaxResponseTime(maxResponseTime);
		metrics.setLastResponseTime(lastResponseTime);
		metrics.setNumberOfSuccessfulRequests(numberOfSuccessfulRequests);
		metrics.setNumberOfRequests(numberOfRequests);
		metrics.setNumberOfFailedRequests(numberOfFailedRequests);
		metrics.setMaxRequestSize(maxRequestSize);
		metrics.setMaxResponseSize(maxResponseSize);
		metrics.setLastRequestSize(lastRequestSize);
		metrics.setLastResponseSize(lastResponseSize);
		return metrics;
	}

	public void execute(final Exchange exchange) throws BusinessException {
		try {
			if (QName.valueOf(exchange.getOperation()).getLocalPart().equals(
					"GetResourceProperty")
					&& (exchange.getIn().getBody().getContent() instanceof Document)) {
				throw new BusinessException(new NotImplementedException(
						"GetResourceProperty not implemented"));
			} else if (QName.valueOf(exchange.getOperation()).getLocalPart()
					.equals("GetCurrentMessage")
					&& (exchange.getIn().getBody().getContent() instanceof Document)) {
				throw new BusinessException(new NotImplementedException(
						"GetCurrentMessage not implemented"));
			} else if (QName.valueOf(exchange.getOperation()).getLocalPart()
					.equals("Subscribe")
					&& (exchange.getIn().getBody().getContent() instanceof Document)) {
				log.info("Add subscription");
				// unmarshall request
				final Document in = exchange.getIn().getBody().getContent();

				// log: print request
				MonitoringProviderEndpointBehaviourImpl.log.finest("request:\n"
						+ XMLPrettyPrinter.prettyPrint(in));

				// unmarshall request
				Subscribe subscribe = null;
				if (in.getDocumentElement().getLocalName().equals("Body")) {
					subscribe = this.getNotificationReader().readSubscribe(
							this.convertFirstElementIntoDocument(in));
				} else {
					subscribe = this.getNotificationReader().readSubscribe(in);
				}
				// call business method
				final SubscribeResponse subcribeResponse = this
						.subscribe(subscribe);

				// marshall response
				final Document respDoc = this.getNotificationWriter()
						.writeSubscribeResponse(subcribeResponse);

				// log: print response
				MonitoringProviderEndpointBehaviourImpl.log
						.finest("response:\n"
								+ XMLPrettyPrinter.prettyPrint(respDoc));

				// set the response
				if (in.getDocumentElement().getLocalName().equals("Body")) {
					exchange.getOut().getBody().setContent(
							this.wrapResponseByBody(respDoc));
				} else {
					exchange.getOut().getBody().setContent(respDoc);
				}
				log.info("Subscription added");
			} else if (QName.valueOf(exchange.getOperation()).getLocalPart()
					.equals("UnSubscribe")
					&& (exchange.getIn().getBody().getContent() instanceof Document)) {

				// unmarshall request
				final Document in = exchange.getIn().getBody().getContent();

				// unmarshall request
				Unsubscribe unSubscribe = null;
				if (in.getDocumentElement().getLocalName().equals("Body")) {
					unSubscribe = this.getNotificationReader().readUnsubscribe(
							this.convertFirstElementIntoDocument(in));
				} else {
					unSubscribe = this.getNotificationReader().readUnsubscribe(
							in);
				}

				// log: print request
				MonitoringProviderEndpointBehaviourImpl.log.finest("request:\n"
						+ XMLPrettyPrinter.prettyPrint(in));

				// call business method
				final UnsubscribeResponse unSubcribeResponse = this
						.unsubscribe(unSubscribe);

				// marshall response
				final Document respDoc = this.getNotificationWriter()
						.writeUnsubscribeResponse(unSubcribeResponse);

				// log: print response
				MonitoringProviderEndpointBehaviourImpl.log
						.finest("response:\n"
								+ XMLPrettyPrinter.prettyPrint(respDoc));

				// set the response
				if (in.getDocumentElement().getLocalName().equals("Body")) {
					exchange.getOut().getBody().setContent(
							this.wrapResponseByBody(respDoc));
				} else {
					exchange.getOut().getBody().setContent(respDoc);
				}
			} else if (QName.valueOf(exchange.getOperation()).getLocalPart()
					.equals("addNewExchange")
					&& (exchange.getIn().getBody().getContent() instanceof Document)) {

				// unmarshall request
				final Document in = exchange.getIn().getBody().getContent();

				// unmarshall request
				final ExchangeTrace trace = ExchangeTraceImpl.parse(this
						.convertFirstElementIntoDocument(in));

				// log: print request
				MonitoringProviderEndpointBehaviourImpl.log.finest("request:\n"
						+ XMLPrettyPrinter.prettyPrint(in));

				// call business method
				this.addNewExchange(trace);

			} else if (QName.valueOf(exchange.getOperation()).getLocalPart()
					.equals("addNewReportList")
					&& (exchange.getIn().getBody().getContent() instanceof Document)) {

				// unmarshall request
				final Document in = exchange.getIn().getBody().getContent();

				// unmarshall request
				final ReportList reports = MonitoringProviderEndpointBehaviourImpl
						.getReportReader().readReportList(
								this.convertFirstElementIntoDocument(in));

				// log: print request
				MonitoringProviderEndpointBehaviourImpl.log.finest("request:\n"
						+ XMLPrettyPrinter.prettyPrint(in));

				// call business method
				this.addNewReportList(reports);

			} else {
				throw new BusinessException("Unknown operation "
						+ exchange.getOperation() + " on endpoint "
						+ this.endpoint.getQName());
			}
		} catch (final WSNotificationException e) {
			throw new BusinessException(e);
		} catch (final WSNotificationFault e) {
			throw new BusinessException(e);
		} catch (final ParserConfigurationException e) {
			throw new BusinessException(e);
		} catch (final ESBException e) {
			throw new BusinessException(e);
		} catch (final MonitoringException e) {
			throw new BusinessException(e);
		} catch (final RawReportException e) {
			throw new BusinessException(e);
		}
	}

	private QName extractOperationQNameInRequest(final Element in) {
		final QName res = QName.valueOf(in.getTextContent());
		return res;
	}

	public GetCurrentMessageResponse getCurrentMessage(
			final GetCurrentMessage arg0) throws WSNotificationException,
			WSNotificationFault {
		GetCurrentMessageResponse res = null;
		if (this.endpoint instanceof MonitoringProviderEndpoint) {
			res = ((MonitoringProviderEndpoint) this.endpoint)
					.getNotificationProducer().getCurrentMessage(arg0);
		} else {
			MonitoringProviderEndpointBehaviourImpl.log
					.warning("endpoint is not a MonitoringProviderEndpoint");
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Description getDescription() {
		if (this.firstReading) {
			try {
				if ((((ProviderEndpoint) this.endpoint).getService() != null)
						&& (((ProviderEndpoint) this.endpoint).getQName() != null)) {
					final Document doc = this.getWsdmFactory().createWSDMWSDL(
							((ProviderEndpoint) this.endpoint).getService()
									.getQName(),
							((ProviderEndpoint) this.endpoint).getQName()
									.getLocalPart());
					super
							.setDescription(MonitoringProviderEndpointBehaviourImpl
									.getWsdlReader()
									.read(
											SourceHelper
													.convertDOMSource2InputSource(new DOMSource(
															doc)),
											MonitoringProviderEndpointBehaviourImpl.wsdlImports,
											MonitoringProviderEndpointBehaviourImpl.schemaImports));

					final Description d = super.getDescription();
					d.addImportedDocumentsInWsdl();

				} else {
					MonitoringProviderEndpointBehaviourImpl.log
							.warning("Error: the service or the interface or the endpoint is null");
				}
			} catch (final WSDLException e) {
				MonitoringProviderEndpointBehaviourImpl.log
						.warning("Impossible to generate the description");
				e.printStackTrace();
			} catch (final WSDMException e) {
				MonitoringProviderEndpointBehaviourImpl.log
						.warning("Impossible to generate the description");
				e.printStackTrace();
			} catch (final SchemaException e) {
				MonitoringProviderEndpointBehaviourImpl.log
						.warning("Impossible to generate the description");
				e.printStackTrace();
			} catch (final XmlException e) {
				MonitoringProviderEndpointBehaviourImpl.log
						.warning("Impossible to generate the description");
				e.printStackTrace();
			}
			this.firstReading = false;
		}
		return super.getDescription();
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

	public QoSMetrics getResourceProperties(final QName operation)
			throws MonitoringException {
		final QoSMetrics metrics = this.qosmetrics.get(operation);
		return metrics;
	}

	public WSDMFactory getWsdmFactory() throws WSDMException {
		if (this.wsdmFactory == null) {
			this.wsdmFactory = WSDMFactory.newInstance();
		}
		return this.wsdmFactory;
	}

	public WSDMWriter getWsdmWriter() throws WSDMException {
		if (this.wsdmWriter == null) {
			this.wsdmWriter = this.getWsdmFactory().newWSDMWriter();
		}
		return this.wsdmWriter;
	}

	public Object marshall(final Document document) throws MarshallerException {
		throw new UnsupportedOperationException();
	}

	public PauseSubscriptionResponse pauseSubscription(
			final PauseSubscription request) throws WSNotificationException,
			WSNotificationFault {
		throw new UnsupportedOperationException();
	}

	public RenewResponse renew(final Renew request)
			throws WSNotificationException, WSNotificationFault {
		throw new UnsupportedOperationException();
	}

	public ResumeSubscriptionResponse resumeSubscription(
			final ResumeSubscription request) throws WSNotificationException,
			WSNotificationFault {
		throw new UnsupportedOperationException();
	}

	public void setNotificationReader(
			final WSNotificationReader notificationReader) {
		this.notificationReader = notificationReader;
	}

	public void setNotificationWriter(
			final WSNotificationWriter notificationWriter) {
		this.notificationWriter = notificationWriter;
	}

	public SubscribeResponse subscribe(final Subscribe arg0)
			throws WSNotificationException, WSNotificationFault {
		SubscribeResponse res = null;
		if (this.endpoint instanceof MonitoringProviderEndpoint) {
			((SubscriptionManagerMgr) ((MonitoringProviderEndpoint) this.endpoint)
					.getSubscriptionManager()).setSubscriptionsManagerEdp(this
					.getEndpoint().getQName().getLocalPart());
			((SubscriptionManagerMgr) ((MonitoringProviderEndpoint) this.endpoint)
					.getSubscriptionManager())
					.setSubscriptionsManagerInterface(((MonitoringProviderEndpoint) this
							.getEndpoint()).getInterfaceName());
			((SubscriptionManagerMgr) ((MonitoringProviderEndpoint) this.endpoint)
					.getSubscriptionManager())
					.setSubscriptionsManagerService(((MonitoringProviderEndpoint) this
							.getEndpoint()).getService().getQName());
			try {
				res = ((MonitoringProviderEndpoint) this.endpoint)
						.getNotificationProducer().subscribe(arg0);
			} catch (final WSNotificationExtensionException e) {
				throw new WSNotificationException(e);
			}
		} else {
			throw new WSNotificationException(
					"endpoint is not a MonitoringProviderEndpoint");
		}
		return res;
	}

	public Document unmarshall(final Object object) throws MarshallerException {
		throw new UnsupportedOperationException();
	}

	public UnsubscribeResponse unsubscribe(final Unsubscribe request)
			throws WSNotificationException, WSNotificationFault {
		UnsubscribeResponse res = null;
		if (this.endpoint instanceof MonitoringProviderEndpoint) {
			((SubscriptionManagerMgr) ((MonitoringProviderEndpoint) this.endpoint)
					.getSubscriptionManager()).setSubscriptionsManagerEdp(this
					.getEndpoint().getQName().getLocalPart());
			((SubscriptionManagerMgr) ((MonitoringProviderEndpoint) this.endpoint)
					.getSubscriptionManager())
					.setSubscriptionsManagerInterface(((MonitoringProviderEndpoint) this
							.getEndpoint()).getInterfaceName());
			((SubscriptionManagerMgr) ((MonitoringProviderEndpoint) this.endpoint)
					.getSubscriptionManager())
					.setSubscriptionsManagerService(((MonitoringProviderEndpoint) this
							.getEndpoint()).getService().getQName());
			try {
				res = ((SubscriptionManagerMgr) ((MonitoringProviderEndpoint) this.endpoint)
						.getSubscriptionManager()).unsubscribe(request);
			} catch (final WSNotificationExtensionException e) {
				throw new WSNotificationException(e);
			}
		} else {
			throw new WSNotificationException(
					"endpoint is not a MonitoringProviderEndpoint");
		}
		return res;
	}

	private void updateMetric(final QoSMetrics metric,
			final int intLastRequestSize, final int intLastResponseSize,
			final Date startDate, final Date endDate,
			final boolean responseTypeSuccess, final boolean responseTypeFail)
			throws WSDMException {
		try {
			// lastRequestSize
			final QoSInteger lastRequestSize = metric.getLastRequestSize();
			lastRequestSize.setValue(intLastRequestSize);
			lastRequestSize.setLastUpdated(Calendar.getInstance().getTime());
			if (lastRequestSize.getResetAt() == null) {
				lastRequestSize.setResetAt(Calendar.getInstance().getTime());
			}
			metric.setLastRequestSize(lastRequestSize);

			// lastResponseSize
			final QoSInteger lastResponseSize = metric.getLastResponseSize();
			lastResponseSize.setValue(intLastResponseSize);
			lastResponseSize.setLastUpdated(Calendar.getInstance().getTime());
			if (lastResponseSize.getResetAt() == null) {
				lastResponseSize.setResetAt(Calendar.getInstance().getTime());
			}
			metric.setLastResponseSize(lastResponseSize);

			// maxRequestSize
			final QoSInteger maxRequestSize = metric.getMaxRequestSize();
			if (maxRequestSize.getValue() == null) {
				maxRequestSize.setValue(metric.getLastRequestSize().getValue()
						.intValue());
				maxRequestSize.setLastUpdated(Calendar.getInstance().getTime());
				if (maxRequestSize.getResetAt() == null) {
					maxRequestSize.setResetAt(Calendar.getInstance().getTime());
				}
			} else if ((maxRequestSize.getValue() != null)
					&& (maxRequestSize.getValue().intValue() < metric
							.getLastRequestSize().getValue().intValue())) {
				maxRequestSize.setValue(metric.getLastRequestSize().getValue()
						.intValue());
				maxRequestSize.setLastUpdated(Calendar.getInstance().getTime());
				if (maxRequestSize.getResetAt() == null) {
					maxRequestSize.setResetAt(Calendar.getInstance().getTime());
				}
			}

			// maxResponseSize
			final QoSInteger maxResponseSize = metric.getMaxResponseSize();
			if (maxResponseSize.getValue() == null) {
				maxResponseSize.setValue(metric.getLastResponseSize()
						.getValue().intValue());
				maxResponseSize
						.setLastUpdated(Calendar.getInstance().getTime());
				if (maxResponseSize.getResetAt() == null) {
					maxResponseSize
							.setResetAt(Calendar.getInstance().getTime());
				}
			} else if ((maxResponseSize.getValue() != null)
					&& (maxResponseSize.getValue().intValue() < metric
							.getLastResponseSize().getValue().intValue())) {
				maxResponseSize.setValue(metric.getLastResponseSize()
						.getValue().intValue());
				maxResponseSize
						.setLastUpdated(Calendar.getInstance().getTime());
				if (maxResponseSize.getResetAt() == null) {
					maxResponseSize
							.setResetAt(Calendar.getInstance().getTime());
				}
			}

			// lastResponseTime
			final QoSDuration lastResponseTime = metric.getLastResponseTime();
			if ((startDate != null) && (endDate != null)) {
				final long time = endDate.getTime() - startDate.getTime();
				lastResponseTime.setValue(DatatypeFactory.newInstance()
						.newDuration(time));
				lastResponseTime.setLastUpdated(Calendar.getInstance()
						.getTime());
				if (lastResponseTime.getResetAt() == null) {
					lastResponseTime.setResetAt(Calendar.getInstance()
							.getTime());
				}
			}

			// maxResponseTime
			final QoSDuration maxResponseTime = metric.getMaxResponseTime();
			if (maxResponseTime.getValue() == null) {
				maxResponseTime.setValue(metric.getLastResponseTime()
						.getValue());
				maxResponseTime
						.setLastUpdated(Calendar.getInstance().getTime());
				if (maxResponseTime.getResetAt() == null) {
					maxResponseTime
							.setResetAt(Calendar.getInstance().getTime());
				}
			} else if ((maxResponseTime.getValue() != null)
					&& (maxResponseTime.getValue().compare(
							metric.getLastResponseTime().getValue()) < DatatypeConstants.LESSER)) {
				maxResponseTime.setValue(metric.getLastResponseTime()
						.getValue());
				maxResponseTime
						.setLastUpdated(Calendar.getInstance().getTime());
				if (maxResponseTime.getResetAt() == null) {
					maxResponseTime
							.setResetAt(Calendar.getInstance().getTime());
				}
			}

			// serviceTime
			final QoSDuration serviceTime = metric.getServiceTime();
			if (serviceTime.getValue() == null) {
				serviceTime.setValue(lastResponseTime.getValue());
			} else {
				serviceTime.getValue().add(lastResponseTime.getValue());
				serviceTime.getValue().multiply(new BigDecimal(0.5));
			}
			if (serviceTime.getResetAt() == null) {
				serviceTime.setResetAt(Calendar.getInstance().getTime());
			}
			serviceTime.setLastUpdated(Calendar.getInstance().getTime());

			// numberOfRequests
			final QoSInteger numberOfRequests = metric.getNumberOfRequests();
			if (numberOfRequests.getValue() == null) {
				numberOfRequests.setValue(0);
			}
			numberOfRequests.setValue(numberOfRequests.getValue() + 1);
			if (numberOfRequests.getResetAt() == null) {
				numberOfRequests.setResetAt(Calendar.getInstance().getTime());
			}
			numberOfRequests.setLastUpdated(Calendar.getInstance().getTime());

			// numberOfSuccessfulRequests
			final QoSInteger numberOfSuccessfulRequests = metric
					.getNumberOfSuccessfulRequests();
			if (numberOfSuccessfulRequests.getValue() == null) {
				numberOfSuccessfulRequests.setValue(0);
			}
			if (responseTypeSuccess == false) {
				numberOfSuccessfulRequests.setValue(numberOfSuccessfulRequests
						.getValue() + 1);
			}
			if (numberOfSuccessfulRequests.getResetAt() == null) {
				numberOfSuccessfulRequests.setResetAt(Calendar.getInstance()
						.getTime());
			}
			numberOfSuccessfulRequests.setLastUpdated(Calendar.getInstance()
					.getTime());

			// numberOfFailedRequests
			final QoSInteger numberOfFailedRequests = metric
					.getNumberOfFailedRequests();
			if (numberOfFailedRequests.getValue() == null) {
				numberOfFailedRequests.setValue(0);
			}
			if (responseTypeFail == true) {
				numberOfFailedRequests.setValue(numberOfFailedRequests
						.getValue() + 1);
			}
			if (numberOfFailedRequests.getResetAt() == null) {
				numberOfFailedRequests.setResetAt(Calendar.getInstance()
						.getTime());
			}
			numberOfFailedRequests.setLastUpdated(GregorianCalendar
					.getInstance().getTime());

		} catch (final DatatypeConfigurationException e) {
			throw new WSDMException(e);
		}

	}

	private Document wrapResponseByBody(final Document response)
			throws BusinessException {
		Document docResp = null;
		try {
			// create the document
			docResp = this.documentFactory.newDocumentBuilder().newDocument();
			final org.w3c.dom.Element body = docResp.createElementNS(
					"http://schemas.xmlsoap.org/soap/envelope/", "Body");
			body.setPrefix("soapenv");

			if (response.getDocumentElement() != null) {
				body.appendChild(docResp.adoptNode(response
						.getDocumentElement().cloneNode(true)));
			}

			docResp.appendChild(body);
		} catch (final ParserConfigurationException e) {
			throw new BusinessException(e);
		}
		return docResp;
	}

}
