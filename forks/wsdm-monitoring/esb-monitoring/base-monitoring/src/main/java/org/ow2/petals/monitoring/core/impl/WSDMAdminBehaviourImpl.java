package org.ow2.petals.monitoring.core.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.petals.esb.impl.endpoint.behaviour.AdminBehaviourImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.BusinessException;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.monitoring.core.api.BaseMonitoring;
import org.ow2.petals.monitoring.core.api.ClientMonitoring;
import org.ow2.petals.monitoring.core.api.MonitoringException;
import org.ow2.petals.monitoring.core.api.MonitoringProviderEndpoint;
import org.ow2.petals.monitoring.core.api.MonitoringService;
import org.ow2.petals.monitoring.core.api.WSDMAdminBehaviour;
import org.w3c.dom.Document;

public class WSDMAdminBehaviourImpl extends AdminBehaviourImpl implements
		WSDMAdminBehaviour {

	private final Logger log = Logger.getLogger(WSDMAdminBehaviourImpl.class
			.getName());

	private boolean firstReading = true;

	private BaseMonitoring wsdmProvider;

	private ClientMonitoring wsdmClient;

	public WSDMAdminBehaviourImpl(final Endpoint ep) {
		super(ep);
	}

	public String createMonitoringEndpoint(final QName wsdmServiceName,
			final String wsdmEndpointName, final boolean exposeInSoap) {
		String res = null;
		try {
			final MonitoringService wsdmService = this.wsdmProvider
					.createMonitoringService(wsdmServiceName);
			final MonitoringProviderEndpoint wsdmEndpoint = wsdmService
					.createMonitoringEndpoint(
							wsdmEndpointName,
							new ArrayList<String>(
									Arrays
											.asList(new String[] { "MetricsCapability" })));

			if (exposeInSoap) {
				res = this.exposeInSoapOnClient(wsdmServiceName,
						wsdmEndpointName, this.wsdmClient.getQName());
			} else {
				res = wsdmEndpoint.getQName().toString();
			}
		} catch (final ESBException e) {
			this.log.severe(e.getMessage());
			e.printStackTrace();
		} catch (final MonitoringException e) {
			this.log.severe(e.getMessage());
			e.printStackTrace();
		}
		return res;
	}

	private Document createResponseFromQName(final String res,
			final String messageResponse, final String return_)
			throws BusinessException {
		Document docResp = null;
		try {
			// create the document
			docResp = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
			final org.w3c.dom.Element body = docResp.createElementNS(
					"http://schemas.xmlsoap.org/soap/envelope/", "Body");
			body.setPrefix("soapenv");
			final org.w3c.dom.Element createComponentResponse = docResp
					.createElementNS("http://ow2.petals.org/Admin/",
							messageResponse);
			createComponentResponse.setPrefix("adm");
			body.appendChild(createComponentResponse);

			final org.w3c.dom.Element endpoint = docResp.createElement(return_);
			endpoint.setTextContent(res.toString());
			createComponentResponse.appendChild(endpoint);

			docResp.appendChild(body);
		} catch (final ParserConfigurationException e) {
			throw new BusinessException(e);
		}
		return docResp;
	}

	@Override
	public void execute(final Exchange exchange) throws BusinessException {
		super.execute(exchange);

		// convert dom to jdom
		final DOMBuilder builder = new DOMBuilder();
		final org.jdom.Document doc = builder.build(exchange.getIn().getBody()
				.getContent());

		if ((doc.getRootElement() != null)
				&& (doc.getRootElement().getChild("createMonitoringEndpoint",
						Namespace.getNamespace("http://ow2.petals.org/Admin/")) != null)) {
			this.log.finest("CREATE MONITORING ENDPOINT METHOD");
			final Element createComponent = doc.getRootElement().getChild(
					"createMonitoringEndpoint",
					Namespace.getNamespace("http://ow2.petals.org/Admin/"));
			final QName wsdmServiceName = QName.valueOf(createComponent
					.getChild("wsdmServiceName").getValue());
			final String wsdmProviderEndpointName = createComponent.getChild(
					"wsdmProviderEndpointName").getValue();
			final boolean exposeInSoap = Boolean.valueOf(createComponent
					.getChild("exposeInSoap").getValue());
			final String res = this.createMonitoringEndpoint(wsdmServiceName,
					wsdmProviderEndpointName, exposeInSoap);

			final Document docResp = this.createResponseFromQName(res,
					"createMonitoringEndpointResponse", "wsdmEndpointName");
			exchange.getOut().getBody().setContent(docResp);
		}

	}

	@Override
	public Description getDescription() {
		if (this.firstReading) {
			try {

				final URL wsdlUrl = Thread.currentThread()
						.getContextClassLoader().getResource(
								"wsdl/WSDMMonitoringAdmin.wsdl");
				final Description desc = AdminBehaviourImpl.getReader().read(
						wsdlUrl);
				super.setDescription(desc);
			} catch (final WSDLException e) {
				// do nothing
				this.log.severe(e.getMessage());
			} catch (final URISyntaxException e) {
				// do nothing
				this.log.severe(e.getMessage());
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				this.log.severe(e.getMessage());
			}
			this.firstReading = false;
		}
		return super.getDescription();
	}

	public ClientMonitoring getWsdmClient() {
		return this.wsdmClient;
	}

	public BaseMonitoring getWsdmProvider() {
		return this.wsdmProvider;
	}

	public void setWsdmClient(final ClientMonitoring wsdmClient) {
		this.wsdmClient = wsdmClient;
	}

	public void setWsdmProvider(final BaseMonitoring wsdmProvider) {
		this.wsdmProvider = wsdmProvider;
	}

}
