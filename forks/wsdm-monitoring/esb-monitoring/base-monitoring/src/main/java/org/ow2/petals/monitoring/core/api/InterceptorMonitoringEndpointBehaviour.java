package org.ow2.petals.monitoring.core.api;

import java.util.Date;

import javax.xml.namespace.QName;

import org.ow2.petals.esb.kernel.api.endpoint.behaviour.Behaviour;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.BusinessException;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.monitoring.core.api.ExchangeTrace.ResponseType;

public interface InterceptorMonitoringEndpointBehaviour extends
		Behaviour<Object> {

	QName getFunctionalProviderEndpoint();

	QName getMonitoringEndpoint();

	void interceptExchange(Exchange exchange, Date start, Date end,
			ResponseType type) throws BusinessException;

	void setFunctionalProviderEndpoint(QName fpe);

	void setMonitoringEndpoint(QName name);

}
