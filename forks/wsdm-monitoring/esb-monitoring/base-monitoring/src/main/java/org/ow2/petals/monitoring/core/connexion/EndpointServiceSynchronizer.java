package org.ow2.petals.monitoring.core.connexion;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ow2.easywsdl.schema.util.DOMUtil;
import org.ow2.easywsdl.wsdl.util.Util;
import org.ow2.petals.esb.external.protocol.soap.impl.SOAPSenderImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.monitoring.core.api.WSDMAdminBehaviour;
import org.ow2.petals.transporter.api.transport.TransportException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class EndpointServiceSynchronizer extends Thread {

	private static Logger log = Logger
			.getLogger(EndpointServiceSynchronizer.class.getName());

	private String address = null;

	private final SOAPSenderImpl sender = new SOAPSenderImpl();

	private List<Endpoint> functionnalEndpoints = null;

	private final WSDMAdminBehaviour wsdmAdminBehaviour;

	private static Document soapRequest = null;
	private static ESBException exception;

	static {
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			EndpointServiceSynchronizer.soapRequest = factory
					.newDocumentBuilder().newDocument();
			final Element enveloppe = EndpointServiceSynchronizer.soapRequest
					.createElementNS(
							"http://schemas.xmlsoap.org/soap/envelope/",
							"Envelope");
			enveloppe.setPrefix("soapenv");
			final Element body = EndpointServiceSynchronizer.soapRequest
					.createElementNS(
							"http://schemas.xmlsoap.org/soap/envelope/", "Body");
			body.setPrefix("soap-env");
			enveloppe.appendChild(body);

			final Element getEndpoints = EndpointServiceSynchronizer.soapRequest
					.createElementNS("http://api.ws.kernel.petals.ow2.org/",
							"getEndpoints");
			getEndpoints.setPrefix("api");
			body.appendChild(getEndpoints);

			EndpointServiceSynchronizer.soapRequest.appendChild(enveloppe);
		} catch (final ParserConfigurationException e) {
			EndpointServiceSynchronizer.exception = new ESBException(e);
		}

	}

	public static Document getSoapRequest() throws ESBException {
		if (EndpointServiceSynchronizer.exception != null) {
			throw EndpointServiceSynchronizer.exception;
		}
		return EndpointServiceSynchronizer.soapRequest;
	}

	public EndpointServiceSynchronizer(
			final EndpointServiceSynchronizerManager epsm) throws ESBException {
		this.address = epsm.getAddress();
		this.functionnalEndpoints = epsm.getFunctionnalEndpoints();
		this.wsdmAdminBehaviour = epsm.getWsdmAdminBehaviour();
		EndpointServiceSynchronizer.getSoapRequest();

	}

	private void addMonitoringEndpointFromThisFunctionalEndpoint(
			final Endpoint ep) {

		this.wsdmAdminBehaviour.createMonitoringEndpoint(ep.getService(), ep
				.getName()
				+ "_WSDMMonitoring", true);
	}

	private Endpoint createEndpointFromElement(final Element epElmt) {
		final Endpoint ep = new Endpoint();
		ep.setName(epElmt.getAttribute("name"));
		NodeList list = epElmt.getElementsByTagName("service");
		if ((list != null) && (list.getLength() > 0)
				&& (list.item(0).getTextContent() != null)) {
			final String localName = Util.getLocalPartWithoutPrefix(list
					.item(0).getTextContent());
			final String prefix = Util.getPrefix(list.item(0).getTextContent());
			String uri = null;
			if ((prefix != null) && (prefix.trim().length() > 0)) {
				uri = epElmt.lookupNamespaceURI(prefix);
			}
			QName service = new QName(localName);
			if (uri != null) {
				service = new QName(uri, localName);
			}
			ep.setService(service);
		}
		list = epElmt.getElementsByTagName("container");
		if ((list != null) && (list.getLength() > 0)
				&& (list.item(0).getTextContent() != null)) {
			ep.setContainer(list.item(0).getTextContent());
		}
		list = epElmt.getElementsByTagName("component");
		if ((list != null) && (list.getLength() > 0)
				&& (list.item(0).getTextContent() != null)) {
			ep.setComponent(list.item(0).getTextContent());
		}
		list = epElmt.getElementsByTagName("subdomain");
		if ((list != null) && (list.getLength() > 0)
				&& (list.item(0).getTextContent() != null)) {
			ep.setSubdomain(list.item(0).getTextContent());
		}
		list = epElmt.getElementsByTagName("description");
		if ((list != null) && (list.getLength() > 0)
				&& (list.item(0).getTextContent() != null)) {
			ep.setDescription(list.item(0).getTextContent());
		}
		return ep;
	}

	@Override
	public void run() {
		try {

			final Document response = this.sender.sendSoapRequest(
					EndpointServiceSynchronizer.getSoapRequest(), this.address);

			final List<Endpoint> currentEndpoints = new ArrayList<Endpoint>();

			// get Result
			if (response != null) {
				final Element env = response.getDocumentElement();
				final NodeList list = env.getElementsByTagNameNS(
						"http://schemas.xmlsoap.org/soap/envelope/", "Body");
				Element body = null;
				if ((list != null) && (list.getLength() > 0)) {
					body = (Element) list.item(0);
				}
				final Element endpoints = DOMUtil.getFirstElement(body);

				if ((endpoints == null)
						|| (DOMUtil.getFirstElement(endpoints) == null)) {
					EndpointServiceSynchronizer.log
							.finest("No functional endpoint exist in petals");
				} else {
					final NodeList endpointsList = endpoints
							.getElementsByTagName("endpoints");
					for (int i = 0; i < endpointsList.getLength(); i++) {
						final Element epElmt = (Element) endpointsList.item(i);
						final Endpoint ep = this
								.createEndpointFromElement(epElmt);
						currentEndpoints.add(ep);
					}
				}
			}

			// verify current endpoints with functional endpoint list
			synchronized (this.functionnalEndpoints) {
				for (final Endpoint ep : currentEndpoints) {
					if (!this.functionnalEndpoints.contains(ep)) {
						this.functionnalEndpoints.add(ep);
						this
								.addMonitoringEndpointFromThisFunctionalEndpoint(ep);
					}
				}
			}

		} catch (final TransportException e) {
			EndpointServiceSynchronizer.log
					.warning("Impossible to connect to Petals Service at "
							+ this.address);
		} catch (final ESBException e) {
			EndpointServiceSynchronizer.log
					.warning("Impossible to connect to Petals Service at "
							+ this.address);
		}
	}
}
