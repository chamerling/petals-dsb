package org.ow2.petals.monitoring.core.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ow2.petals.esb.kernel.api.endpoint.ClientAndProviderEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.ProviderEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.AbstractBehaviourImpl;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.BusinessException;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.MarshallerException;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.exchange.api.ExchangeException;
import org.ow2.petals.monitoring.core.api.ExchangeTrace;
import org.ow2.petals.monitoring.core.api.InterceptorMonitoringEndpointBehaviour;
import org.ow2.petals.transporter.api.transport.TransportException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import petals.ow2.org.exchange.PatternType;

public class InterceptorMonitoringEndpointBehaviourImpl extends
		AbstractBehaviourImpl<Object> implements
		InterceptorMonitoringEndpointBehaviour {

	private static Logger log = Logger
			.getLogger(InterceptorMonitoringEndpointBehaviourImpl.class
					.getName());

	private QName functionalProviderEndpoint = null;

	private QName monitoringEndpoint = null;

	private DocumentBuilderFactory builderFactory = null;

	public InterceptorMonitoringEndpointBehaviourImpl(
			final ClientAndProviderEndpoint ep) {
		super(ep);
		this.builderFactory = DocumentBuilderFactory.newInstance();
		this.builderFactory.setNamespaceAware(true);
	}

	private Exchange createAddNewExchangeRequestMessage(
			final Exchange exchange, final Date start, final Date end,
			final ExchangeTrace.ResponseType type) throws BusinessException {
		Exchange interceptedExchange = null;
		try {
			interceptedExchange = ((ClientAndProviderEndpoint) this.endpoint)
					.createExchange();
			interceptedExchange.setDestination(this.monitoringEndpoint);
			interceptedExchange.setPattern(PatternType.IN_ONLY);
			interceptedExchange.setOperation(new QName(
					"http://petals.ow2.org/wsdm", "addNewExchange").toString());

			final Document doc = this.builderFactory.newDocumentBuilder()
					.newDocument();
			final Element body = doc.createElementNS(
					"http://schemas.xmlsoap.org/soap/envelope/", "Body");
			body.setPrefix("soap-env");
			doc.appendChild(body);

			final Element addNewExchangeRequest = doc.createElementNS(
					"http://petals.ow2.org/wsdm", "addNewExchangeRequest");
			addNewExchangeRequest.setPrefix("wsdm");
			body.appendChild(addNewExchangeRequest);

			final Element serviceName = doc.createElement("serviceName");
			final ProviderEndpoint pe = (ProviderEndpoint) this.endpoint
					.getNode().getRegistry().getEndpoint(
							this.monitoringEndpoint);
			if (pe == null) {
				throw new BusinessException("Impossible to find endpoint: "
						+ this.monitoringEndpoint);
			}
			serviceName.setTextContent(pe.getService().getQName().toString());
			addNewExchangeRequest.appendChild(serviceName);

			final Element endpointName = doc.createElement("endpointName");
			endpointName.setTextContent(exchange.getDestination().toString());
			addNewExchangeRequest.appendChild(endpointName);

			final Element operationName = doc.createElement("operationName");
			operationName.setTextContent(exchange.getOperation().toString());
			addNewExchangeRequest.appendChild(operationName);

			final Element request = doc.createElement("request");
			request.appendChild(exchange.getIn().getBody().getContent()
					.getDocumentElement().cloneNode(true));
			addNewExchangeRequest.appendChild(request);

			if ((exchange.getOut().getBody() != null)
					&& (exchange.getOut().getBody().getContent() != null)
					&& (exchange.getOut().getBody().getContent()
							.getDocumentElement() != null)) {
				final Element response = doc.createElement("response");
				response.appendChild(exchange.getOut().getBody().getContent()
						.getDocumentElement().cloneNode(true));
				addNewExchangeRequest.appendChild(response);
			}

			final GregorianCalendar gCalendar = new GregorianCalendar();

			final Element startExchange = doc.createElement("startExchange");
			gCalendar.setTime(start);
			XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(gCalendar);
			startExchange.setTextContent(xmlCalendar.toXMLFormat());
			addNewExchangeRequest.appendChild(startExchange);

			final Element endExchange = doc.createElement("endExchange");
			gCalendar.setTime(end);
			xmlCalendar = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(gCalendar);
			endExchange.setTextContent(xmlCalendar.toXMLFormat());
			addNewExchangeRequest.appendChild(endExchange);

			final Element responseType = doc.createElement("responseType");
			responseType.setTextContent(type.toString());
			addNewExchangeRequest.appendChild(responseType);

			interceptedExchange.getIn().getBody().setContent(doc);
		} catch (final ParserConfigurationException e) {
			throw new BusinessException(e);
		} catch (final DatatypeConfigurationException e) {
			throw new BusinessException(e);
		} catch (final ExchangeException e) {
			throw new BusinessException(e);
		}
		return interceptedExchange;
	}

	public void execute(Exchange exchange) throws BusinessException {
		exchange.setDestination(this.functionalProviderEndpoint);
		final Date start = Calendar.getInstance().getTime();
		try {
			// send functional exchange
			exchange = ((ClientAndProviderEndpoint) this.endpoint).sendSync(
					exchange, 0);
			final Date end = Calendar.getInstance().getTime();

			// intercept exchange for monitoring
			this.interceptExchange(exchange, start, end,
					ExchangeTrace.ResponseType.SUCCESS);
		} catch (final TransportException e) {
			final Date end = Calendar.getInstance().getTime();

			// intercept exchange for monitoring
			this.interceptExchange(exchange, start, end,
					ExchangeTrace.ResponseType.FAIL);
			throw new BusinessException(e);
		}
	}

	public QName getFunctionalProviderEndpoint() {
		return this.functionalProviderEndpoint;
	}

	public QName getMonitoringEndpoint() {
		return this.monitoringEndpoint;
	}

	public void interceptExchange(final Exchange exchange, final Date start,
			final Date end, final ExchangeTrace.ResponseType type)
			throws BusinessException {
		try {
			// create exchange
			final Exchange interceptedExchange = this
					.createAddNewExchangeRequestMessage(exchange, start, end,
							type);

			// send exchange
			((ClientAndProviderEndpoint) this.endpoint)
					.send(interceptedExchange);
		} catch (final TransportException e) {
			throw new BusinessException(e);
		}
	}

	public Object marshall(final Document arg0) throws MarshallerException {
		throw new UnsupportedOperationException();
	}

	public void setFunctionalProviderEndpoint(final QName fpe) {
		this.functionalProviderEndpoint = fpe;
	}

	public void setMonitoringEndpoint(final QName name) {
		this.monitoringEndpoint = name;
	}

	public Document unmarshall(final Object arg0) throws MarshallerException {
		throw new UnsupportedOperationException();
	}

}
