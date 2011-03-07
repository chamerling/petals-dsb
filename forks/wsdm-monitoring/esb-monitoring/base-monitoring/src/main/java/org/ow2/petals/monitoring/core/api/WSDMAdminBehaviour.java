package org.ow2.petals.monitoring.core.api;

import javax.xml.namespace.QName;

import org.ow2.petals.esb.api.endpoint.behaviour.AdminBehaviour;

public interface WSDMAdminBehaviour extends AdminBehaviour {

	String createMonitoringEndpoint(QName serviceName, String endpointName,
			boolean exposeInSoap);

	ClientMonitoring getWsdmClient();

	BaseMonitoring getWsdmProvider();

	void setWsdmClient(ClientMonitoring wsdmClient);

	void setWsdmProvider(BaseMonitoring wsdmProvider);

}
