package org.ow2.petals.esb.kernel.api.endpoint;

import java.util.Map;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.endpoint.external.ExternalListener;


@Interface(name="proxy-client-service")
public interface ClientProxyEndpoint extends ClientEndpoint {

	Map<String, ExternalListener> getExternalListeners();
	
	void setExternalListeners(Map<String, ExternalListener> listeners);
	
	QName getProviderEndpointName();
	
	void setProviderEndpointName(QName providerQName);
	
	String getExternalAddress();
	
	void setExternalAddress(String address);
	
}
