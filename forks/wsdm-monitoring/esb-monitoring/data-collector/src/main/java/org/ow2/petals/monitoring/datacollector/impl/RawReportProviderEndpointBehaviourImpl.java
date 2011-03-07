package org.ow2.petals.monitoring.datacollector.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

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
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Import;
import org.ow2.easywsdl.wsdl.api.Service;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.WSDLImportException;
import org.ow2.easywsdl.wsdl.api.abstractItf.AbsItfDescription;
import org.ow2.easywsdl.wsdl.api.abstractItf.AbsItfDescription.WSDLVersionConstants;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.endpoint.ProviderEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.AbstractBehaviourImpl;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.BusinessException;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.MarshallerException;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.monitoring.core.api.MonitoringException;
import org.ow2.petals.monitoring.core.api.MonitoringProviderEndpointBehaviour;
import org.ow2.petals.monitoring.datacollector.api.RawReportProviderEndpoint;
import org.ow2.petals.monitoring.datacollector.api.RawReportProviderEndpointBehaviour;
import org.ow2.petals.monitoring.model.rawreport.api.RawReportException;
import org.ow2.petals.monitoring.model.rawreport.api.RawReportReader;
import org.ow2.petals.monitoring.model.rawreport.api.Report;
import org.ow2.petals.monitoring.model.rawreport.api.ReportList;
import org.ow2.petals.monitoring.model.rawreport.impl.RawReportFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

public class RawReportProviderEndpointBehaviourImpl extends
		AbstractBehaviourImpl<Object> implements
		RawReportProviderEndpointBehaviour {

	private static Logger log = Logger
			.getLogger(RawReportProviderEndpointBehaviourImpl.class.getName());

	public static RawReportReader getReportReader() throws RawReportException {
		if (RawReportProviderEndpointBehaviourImpl.reportReader == null) {
			RawReportProviderEndpointBehaviourImpl.reportReader = RawReportFactory
					.getInstance().newRawReportReader();
		}
		return RawReportProviderEndpointBehaviourImpl.reportReader;
	}

	private WSNotificationReader notificationReader = null;

	private WSNotificationWriter notificationWriter = null;

	private DocumentBuilderFactory documentFactory = null;

	private boolean firstReading = true;

	private final Map<String, List<Report>> reports = Collections
			.synchronizedMap(new HashMap<String, List<Report>>());

	private static WSDL4ComplexWsdlReader wsdlReader = null;

	private static RawReportReader reportReader = null;
	private static Map<URI, AbsItfDescription> wsdlImports = null;

	private static Map<URI, AbsItfSchema> schemaImports = null;

	static {
		try {
			final SchemaReader schemaReader = SchemaFactory.newInstance()
					.newSchemaReader();
			RawReportProviderEndpointBehaviourImpl.schemaImports = new HashMap<URI, AbsItfSchema>();

			// add report
			final URL reporturl = Thread.currentThread()
					.getContextClassLoader().getResource(
							"imports/rawreport/rawReport.xsd");
			final Schema report = schemaReader.read(reporturl);
			RawReportProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"imports/rawreport/rawReport.xsd"), report);
			RawReportProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"rawReport.xsd"), report);

			// add addressing
			final URL wsaddrurl = Thread.currentThread()
					.getContextClassLoader().getResource("imports/ws-addr.xsd");
			final Schema addressing = schemaReader.read(wsaddrurl);
			RawReportProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"imports/ws-addr.xsd"), addressing);
			RawReportProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"ws-addr.xsd"), addressing);

			// add resources
			final URL rp2url = Thread.currentThread().getContextClassLoader()
					.getResource("imports/rp-2.xsd");
			final Schema rp_2 = schemaReader.read(rp2url);
			RawReportProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"imports/rp-2.xsd"), rp_2);
			RawReportProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"rp-2.xsd"), rp_2);

			// add resources
			final URL r2url = Thread.currentThread().getContextClassLoader()
					.getResource("imports/r-2.xsd");
			final Schema r_2 = schemaReader.read(r2url);
			RawReportProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"imports/r-2.xsd"), r_2);
			RawReportProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"r-2.xsd"), r_2);

			// add addressing
			final URL bw2url = Thread.currentThread().getContextClassLoader()
					.getResource("imports/b-2.xsd");
			final Schema b_2 = schemaReader.read(bw2url);
			RawReportProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"imports/b-2.xsd"), b_2);
			RawReportProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"b-2.xsd"), b_2);

			// add muws-part1-2
			final URL muws12url = Thread.currentThread()
					.getContextClassLoader().getResource("imports/muws1-2.xsd");
			final Schema muws12 = schemaReader.read(muws12url);
			RawReportProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"imports/muws1-2.xsd"), muws12);
			RawReportProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"muws1-2.xsd"), muws12);

			// add muws-part2-2
			final URL muws22url = Thread.currentThread()
					.getContextClassLoader().getResource("imports/muws2-2.xsd");
			final Schema muws22 = schemaReader.read(muws22url);
			RawReportProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"imports/muws2-2.xsd"), muws22);
			RawReportProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"muws2-2.xsd"), muws22);

			// add mows-2
			final URL mows2url = Thread.currentThread().getContextClassLoader()
					.getResource("imports/mows-2.xsd");
			final Schema mows2 = schemaReader.read(mows2url);
			RawReportProviderEndpointBehaviourImpl.schemaImports.put(new URI(
					"imports/mows-2.xsd"), mows2);
			RawReportProviderEndpointBehaviourImpl.schemaImports.put(new URI(
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
			final WSDL4ComplexWsdlReader wsdlReader = RawReportProviderEndpointBehaviourImpl
					.getWsdlReader();
			RawReportProviderEndpointBehaviourImpl.wsdlImports = new HashMap<URI, AbsItfDescription>();

			// add rw-2.wsdl
			final URL rw2url = Thread.currentThread().getContextClassLoader()
					.getResource("imports/rw-2.wsdl");
			final AbsItfDescription rw_2 = wsdlReader.read(rw2url,
					RawReportProviderEndpointBehaviourImpl.wsdlImports,
					RawReportProviderEndpointBehaviourImpl.schemaImports);
			RawReportProviderEndpointBehaviourImpl.wsdlImports.put(new URI(
					"imports/rw-2.wsdl"), rw_2);
			RawReportProviderEndpointBehaviourImpl.wsdlImports.put(new URI(
					"rw-2.wsdl"), rw_2);

			// add rpw-2.wsdl
			final URL rpw2url = Thread.currentThread().getContextClassLoader()
					.getResource("imports/rpw-2.wsdl");
			final AbsItfDescription rpw_2 = wsdlReader.read(rpw2url,
					RawReportProviderEndpointBehaviourImpl.wsdlImports,
					RawReportProviderEndpointBehaviourImpl.schemaImports);
			RawReportProviderEndpointBehaviourImpl.wsdlImports.put(new URI(
					"imports/rpw-2.wsdl"), rpw_2);
			RawReportProviderEndpointBehaviourImpl.wsdlImports.put(new URI(
					"rpw-2.wsdl"), rpw_2);

			// add bw-2.wsdl
			final URL bw2url = Thread.currentThread().getContextClassLoader()
					.getResource("imports/bw-2.wsdl");
			final AbsItfDescription bw_2 = wsdlReader.read(bw2url,
					RawReportProviderEndpointBehaviourImpl.wsdlImports,
					RawReportProviderEndpointBehaviourImpl.schemaImports);
			RawReportProviderEndpointBehaviourImpl.wsdlImports.put(new URI(
					"imports/bw-2.wsdl"), bw_2);
			RawReportProviderEndpointBehaviourImpl.wsdlImports.put(new URI(
					"bw-2.wsdl"), bw_2);

			// add wsdm
			final URL rawurl = Thread.currentThread().getContextClassLoader()
					.getResource("rawReport.wsdl");
			final AbsItfDescription raw = wsdlReader.read(rawurl,
					RawReportProviderEndpointBehaviourImpl.wsdlImports,
					RawReportProviderEndpointBehaviourImpl.schemaImports);
			RawReportProviderEndpointBehaviourImpl.wsdlImports.put(new URI(
					"rawReport.wsdl"), raw);

		} catch (final URISyntaxException e) {
			e.printStackTrace();
		} catch (final SchemaException e) {
			e.printStackTrace();
		}
	}

	public static WSDL4ComplexWsdlReader getWsdlReader() throws WSDLException {
		if (RawReportProviderEndpointBehaviourImpl.wsdlReader == null) {
			RawReportProviderEndpointBehaviourImpl.wsdlReader = WSDL4ComplexWsdlFactory
					.newInstance().newWSDLReader();
		}
		return RawReportProviderEndpointBehaviourImpl.wsdlReader;
	}

	public RawReportProviderEndpointBehaviourImpl(final Endpoint ep) {
		super(ep);
		this.documentFactory = DocumentBuilderFactory.newInstance();
		this.documentFactory.setNamespaceAware(true);
	}

	public void addNewReport(final String messageId, final Report report)
			throws MonitoringException {
		try {
			if (this.reports.containsKey(messageId)) {
				final List<Report> reports = this.reports.get(messageId);
				reports.add(report);

				final ReportList list = RawReportFactory.getInstance()
						.newReportList();
				for (final Report r : reports) {
					list.addReport(r);
				}
				this.addNewReportList(list);
			} else {
				final List<Report> reports = new ArrayList<Report>();
				reports.add(report);
				this.reports.put(messageId, reports);
			}
		} catch (final RawReportException e) {
			throw new MonitoringException(e);
		}
	}

	public void addNewReportList(final ReportList reports)
			throws MonitoringException {
		try {
			if (reports.getReports().size() == 4) {

				// send notification
				final RawReportNotifierProducer notifier = new RawReportNotifierProducer(
						this, reports.getReports().get(0).getExchangeId(),
						reports.getReports());
				notifier.start();

				// find WSDMEndpoint
				final QName serviceName = QName.valueOf(reports.getReports()
						.get(0).getServiceName());
				final String endpoint = reports.getReports().get(0)
						.getEndPoint();
				final ProviderEndpoint wsdmEndpoint = (ProviderEndpoint) this.endpoint
						.getNode().getRegistry().getEndpoint(
								new QName(serviceName.getNamespaceURI(),
										endpoint + "_WSDMMonitoring"));
				if (wsdmEndpoint != null) {
					if (wsdmEndpoint.getBehaviour() instanceof MonitoringProviderEndpointBehaviour) {
						RawReportProviderEndpointBehaviourImpl.log
								.finest("WSDM Endpoint found");
						((MonitoringProviderEndpointBehaviour) wsdmEndpoint
								.getBehaviour()).addNewReportList(reports);
					} else {
						throw new MonitoringException(
								"Error: the wsdm endpoint must have a monitoring behaviour");
					}
				}
			}
		} catch (final ESBException e) {
			throw new MonitoringException(e);
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

	private Document createRawReportWSDL(final QName serviceName,
			final String endpointName) throws ESBException {
		Document res = null;
		try {
			final org.ow2.easywsdl.wsdl.api.Description desc = WSDLFactory
					.newInstance().newDescription(WSDLVersionConstants.WSDL11);
			desc.setTargetNamespace(serviceName.getNamespaceURI());

			// Read wsdm desc
			final URL wsdlUrl = Thread.currentThread().getContextClassLoader()
					.getResource("rawReport.wsdl");
			final org.ow2.easywsdl.wsdl.api.Description rawReportDescription = WSDLFactory
					.newInstance().newWSDLReader().read(wsdlUrl);

			// set name
			desc.setQName(new QName(serviceName.getNamespaceURI(), serviceName
					.getLocalPart()
					+ "_Description"));

			// create wsdm import:
			final Import impt = desc.createImport();
			impt.setLocationURI(new URI("rawReport.wsdl"));
			impt.setNamespaceURI(rawReportDescription.getTargetNamespace());
			desc.addImport(impt);

			// create service
			final Service service = desc.createService();
			service.setQName(serviceName);

			// create WSDM endpoint
			final org.ow2.easywsdl.wsdl.api.Endpoint rawReportEndpoint = service
					.createEndpoint();
			rawReportEndpoint.setName(endpointName);
			rawReportEndpoint.setAddress(endpointName);
			rawReportEndpoint.setBinding(rawReportDescription
					.getBinding(new QName(rawReportDescription
							.getTargetNamespace(), "RawReportBinding")));

			// create Interceptor endpoint
			final org.ow2.easywsdl.wsdl.api.Endpoint interceptorEndpoint = service
					.createEndpoint();
			interceptorEndpoint.setName(endpointName + "_Interceptor");
			interceptorEndpoint.setAddress(endpointName);
			interceptorEndpoint.setBinding(rawReportDescription
					.getBinding(new QName(rawReportDescription
							.getTargetNamespace(), "InterceptorBinding")));

			// add endpoint to service
			service.addEndpoint(rawReportEndpoint);
			service.addEndpoint(interceptorEndpoint);

			// add service to description
			desc.addService(service);

			// create document
			res = WSDLFactory.newInstance().newWSDLWriter().getDocument(desc);
		} catch (final WSDLException e) {
			throw new ESBException(e);
		} catch (final XmlException e) {
			throw new ESBException(e);
		} catch (final WSDLImportException e) {
			throw new ESBException(e);
		} catch (final URISyntaxException e) {
			throw new ESBException(e);
		} catch (final IOException e) {
			throw new ESBException(e);
		}
		return res;
	}

	public void execute(final Exchange exchange) throws BusinessException {
		try {
			if (QName.valueOf(exchange.getOperation()).getLocalPart().equals(
					"Subscribe")
					&& (exchange.getIn().getBody().getContent() instanceof Document)) {
				// unmarshall request
				final Document in = exchange.getIn().getBody().getContent();

				// unmarshall request
				Subscribe subscribe = null;
				if (in.getDocumentElement().getLocalName().equals("Body")) {
					subscribe = this.getNotificationReader().readSubscribe(
							this.convertFirstElementIntoDocument(in));
				} else {
					subscribe = this.getNotificationReader().readSubscribe(in);
				}

				// log: print request
				RawReportProviderEndpointBehaviourImpl.log.finest("request:\n"
						+ XMLPrettyPrinter.prettyPrint(in));

				// call business method
				final SubscribeResponse subcribeResponse = this
						.subscribe(subscribe);

				// marshall response
				final Document respDoc = this.getNotificationWriter()
						.writeSubscribeResponse(subcribeResponse);

				// log: print response
				RawReportProviderEndpointBehaviourImpl.log.finest("response:\n"
						+ XMLPrettyPrinter.prettyPrint(respDoc));

				// set the response
				if (in.getDocumentElement().getLocalName().equals("Body")) {
					exchange.getOut().getBody().setContent(
							this.wrapResponseByBody(respDoc));
				} else {
					exchange.getOut().getBody().setContent(respDoc);
				}
			} else if (QName.valueOf(exchange.getOperation()).getLocalPart()
					.equals("UnSubscribe")
					&& (exchange.getIn().getBody().getContent() instanceof Document)) {

				// unmarshall request
				final Document in = exchange.getIn().getBody().getContent();

				// log: print request
				// log.finest("request avant:\n" +
				// XMLPrettyPrinter.prettyPrint(in));

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
				RawReportProviderEndpointBehaviourImpl.log.finest("request:\n"
						+ XMLPrettyPrinter.prettyPrint(in));

				// call business method
				final UnsubscribeResponse unSubcribeResponse = this
						.unsubscribe(unSubscribe);

				// marshall response
				final Document respDoc = this.getNotificationWriter()
						.writeUnsubscribeResponse(unSubcribeResponse);

				// log: print response
				RawReportProviderEndpointBehaviourImpl.log.finest("response:\n"
						+ XMLPrettyPrinter.prettyPrint(respDoc));

				// set the response
				if (in.getDocumentElement().getLocalName().equals("Body")) {
					exchange.getOut().getBody().setContent(
							this.wrapResponseByBody(respDoc));
				} else {
					exchange.getOut().getBody().setContent(respDoc);
				}
			} else if (QName.valueOf(exchange.getOperation()).getLocalPart()
					.equals("addNewReportList")
					&& (exchange.getIn().getBody().getContent() instanceof Document)) {

				// unmarshall request
				final Document in = exchange.getIn().getBody().getContent();

				// unmarshall request
				final ReportList reports = RawReportProviderEndpointBehaviourImpl
						.getReportReader().readReportList(
								this.convertFirstElementIntoDocument(in));

				// log: print request
				RawReportProviderEndpointBehaviourImpl.log.finest("request:\n"
						+ XMLPrettyPrinter.prettyPrint(in));

				// call business method
				this.addNewReportList(reports);

			} else {
				RawReportProviderEndpointBehaviourImpl.log
						.warning("Unknown operation " + exchange.getOperation()
								+ " on endpoint " + this.endpoint.getQName());
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
		} catch (final RawReportException e) {
			throw new BusinessException(e);
		} catch (final MonitoringException e) {
			throw new BusinessException(e);
		}
	}

	public GetCurrentMessageResponse getCurrentMessage(
			final GetCurrentMessage arg0) throws WSNotificationException,
			WSNotificationFault {
		GetCurrentMessageResponse res = null;
		if (this.endpoint instanceof RawReportProviderEndpoint) {
			res = ((RawReportProviderEndpoint) this.endpoint)
					.getNotificationProducer().getCurrentMessage(arg0);
		} else {
			throw new WSNotificationException(
					"endpoint is not a RawReportProviderEndpoint");
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
					final Document doc = this.createRawReportWSDL(
							((ProviderEndpoint) this.endpoint).getService()
									.getQName(),
							((ProviderEndpoint) this.endpoint).getQName()
									.getLocalPart());
					super
							.setDescription(RawReportProviderEndpointBehaviourImpl
									.getWsdlReader()
									.read(
											SourceHelper
													.convertDOMSource2InputSource(new DOMSource(
															doc)),
											RawReportProviderEndpointBehaviourImpl.wsdlImports,
											RawReportProviderEndpointBehaviourImpl.schemaImports));

					final Description d = super.getDescription();
					d.addImportedDocumentsInWsdl();

				} else {
					RawReportProviderEndpointBehaviourImpl.log
							.warning("Error: the service or the interface or the endpoint is null");
				}
			} catch (final WSDLException e) {
				RawReportProviderEndpointBehaviourImpl.log
						.warning("Impossible to generate the description");
				e.printStackTrace();
			} catch (final ESBException e) {
				RawReportProviderEndpointBehaviourImpl.log
						.warning("Impossible to generate the description");
				e.printStackTrace();
			} catch (final SchemaException e) {
				RawReportProviderEndpointBehaviourImpl.log
						.warning("Impossible to generate the description");
				e.printStackTrace();
			} catch (final XmlException e) {
				RawReportProviderEndpointBehaviourImpl.log
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

	public Map<String, List<Report>> getReports() {
		return this.reports;
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
		if (this.endpoint instanceof RawReportProviderEndpoint) {
			((SubscriptionManagerMgr) ((RawReportProviderEndpoint) this.endpoint)
					.getSubscriptionManager()).setSubscriptionsManagerEdp(this
					.getEndpoint().getQName().getLocalPart());
			((SubscriptionManagerMgr) ((RawReportProviderEndpoint) this.endpoint)
					.getSubscriptionManager())
					.setSubscriptionsManagerInterface(((RawReportProviderEndpoint) this
							.getEndpoint()).getInterfaceName());
			((SubscriptionManagerMgr) ((RawReportProviderEndpoint) this.endpoint)
					.getSubscriptionManager())
					.setSubscriptionsManagerService(((RawReportProviderEndpoint) this
							.getEndpoint()).getService().getQName());
			try {
				res = ((RawReportProviderEndpoint) this.endpoint)
						.getNotificationProducer().subscribe(arg0);
			} catch (final WSNotificationExtensionException e) {
				throw new WSNotificationException(e);
			}
		} else {
			throw new WSNotificationException(
					"endpoint is not a RawReportProviderEndpoint");
		}
		return res;
	}

	public Document unmarshall(final Object object) throws MarshallerException {
		throw new UnsupportedOperationException();
	}

	public UnsubscribeResponse unsubscribe(final Unsubscribe request)
			throws WSNotificationException, WSNotificationFault {
		UnsubscribeResponse res = null;
		if (this.endpoint instanceof RawReportProviderEndpoint) {
			((SubscriptionManagerMgr) ((RawReportProviderEndpoint) this.endpoint)
					.getSubscriptionManager()).setSubscriptionsManagerEdp(this
					.getEndpoint().getQName().getLocalPart());
			((SubscriptionManagerMgr) ((RawReportProviderEndpoint) this.endpoint)
					.getSubscriptionManager())
					.setSubscriptionsManagerInterface(((RawReportProviderEndpoint) this
							.getEndpoint()).getInterfaceName());
			((SubscriptionManagerMgr) ((RawReportProviderEndpoint) this.endpoint)
					.getSubscriptionManager())
					.setSubscriptionsManagerService(((RawReportProviderEndpoint) this
							.getEndpoint()).getService().getQName());
			try {
				res = ((SubscriptionManagerMgr) ((RawReportProviderEndpoint) this.endpoint)
						.getSubscriptionManager()).unsubscribe(request);
			} catch (final WSNotificationExtensionException e) {
				throw new WSNotificationException(e);
			}
		} else {
			throw new WSNotificationException(
					"endpoint is not a RawReportProviderEndpoint");
		}
		return res;
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
