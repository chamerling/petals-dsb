package org.ow2.petals.monitoring.core.impl;

import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.monitoring.core.api.ExchangeTrace;

public class ExchangeTraceImpl implements ExchangeTrace {

	public static ExchangeTrace parse(final org.w3c.dom.Document document)
			throws ESBException {
		final ExchangeTrace res = new ExchangeTraceImpl();
		try {
			final DOMBuilder builder = new DOMBuilder();
			final org.jdom.Document doc = builder.build(document);

			res.setServiceName(QName.valueOf((doc.getRootElement().getChild(
					"serviceName") != null ? doc.getRootElement().getChild(
					"serviceName").getText() : null)));
			res
					.setEndpointName((doc.getRootElement().getChild(
							"endpointName") != null ? doc.getRootElement()
							.getChild("endpointName").getText() : null));
			res.setInterfaceName(QName.valueOf((doc.getRootElement().getChild(
					"interfaceName") != null ? doc.getRootElement().getChild(
					"interfaceName").getText() : null)));
			res.setOperationName((doc.getRootElement()
					.getChild("operationName") != null ? doc.getRootElement()
					.getChild("operationName").getText() : null));
			final Element request = doc.getRootElement().getChild("request",
					Namespace.getNamespace("http://petals.ow2.org/wsdm"));
			if (request.getChildren().size() > 0) {
				res.setRequest(new Document(((Element) ((Element) request
						.getChildren().get(0)).detach())));
			}
			final Element response = doc.getRootElement().getChild("response",
					Namespace.getNamespace("http://petals.ow2.org/wsdm"));
			if (response.getChildren().size() > 0) {
				res.setResponse(new Document(((Element) ((Element) response
						.getChildren().get(0)).detach())));
			}
			final Element startExchangeElmt = doc.getRootElement().getChild(
					"startExchange");
			if (startExchangeElmt == null) {
				res.setStartExchange(DatatypeFactory.newInstance()
						.newXMLGregorianCalendar(startExchangeElmt.getText())
						.toGregorianCalendar().getTime());
			}

			final Element endExchangeElmt = doc.getRootElement().getChild(
					"endExchange");
			if (endExchangeElmt == null) {
				res.setEndExchange(DatatypeFactory.newInstance()
						.newXMLGregorianCalendar(endExchangeElmt.getText())
						.toGregorianCalendar().getTime());
			}

			final String respType = (doc.getRootElement().getChild(
					"responseType") != null ? doc.getRootElement().getChild(
					"responseType").getText() : null);
			if (respType.equals("SUCCESS")) {
				res.setResponseType(ResponseType.SUCCESS);
			} else {
				res.setResponseType(ResponseType.FAIL);
			}

		} catch (final DatatypeConfigurationException e) {
			throw new ESBException(e);
		}
		return res;
	}

	private QName serviceName;

	private String endpointName;

	private QName interfaceName;

	private String operationName;

	private Document request;

	private Document response;

	private Date startExchange;

	private Date endExchange;

	private ResponseType responseType;

	public ExchangeTraceImpl() {

	}

	public Date getEndExchange() {
		return this.endExchange;
	}

	public String getEndpointName() {
		return this.endpointName;
	}

	public QName getInterfaceName() {
		return this.interfaceName;
	}

	public String getOperationName() {
		return this.operationName;
	}

	public Document getRequest() {
		return this.request;
	}

	public Document getResponse() {
		return this.response;
	}

	public ResponseType getResponseType() {
		return this.responseType;
	}

	public QName getServiceName() {
		return this.serviceName;
	}

	public Date getStartExchange() {
		return this.startExchange;
	}

	public void setEndExchange(final Date endExchange) {
		this.endExchange = endExchange;
	}

	public void setEndpointName(final String endpointName) {
		this.endpointName = endpointName;
	}

	public void setInterfaceName(final QName interfaceName) {
		this.interfaceName = interfaceName;
	}

	public void setOperationName(final String operationName) {
		this.operationName = operationName;
	}

	public void setRequest(final Document request) {
		this.request = request;
	}

	public void setResponse(final Document response) {
		this.response = response;
	}

	public void setResponseType(final ResponseType responseType) {
		this.responseType = responseType;
	}

	public void setServiceName(final QName serviceName) {
		this.serviceName = serviceName;
	}

	public void setStartExchange(final Date startExchange) {
		this.startExchange = startExchange;
	}

}
