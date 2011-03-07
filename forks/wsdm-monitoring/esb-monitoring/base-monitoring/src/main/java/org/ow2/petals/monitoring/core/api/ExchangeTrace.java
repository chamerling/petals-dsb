package org.ow2.petals.monitoring.core.api;

import java.util.Date;

import javax.xml.namespace.QName;

import org.jdom.Document;

public interface ExchangeTrace {

	enum ResponseType {
		SUCCESS, FAIL
	};

	Date getEndExchange();

	String getEndpointName();

	QName getInterfaceName();

	String getOperationName();

	Document getRequest();

	Document getResponse();

	ResponseType getResponseType();

	QName getServiceName();

	Date getStartExchange();

	void setEndExchange(Date endExchange);

	void setEndpointName(String endpointName);

	void setInterfaceName(QName interfaceName);

	void setOperationName(String operationName);

	void setRequest(Document request);

	void setResponse(Document response);

	void setResponseType(ResponseType responseType);

	void setServiceName(QName serviceName);

	void setStartExchange(Date startExchange);
}
