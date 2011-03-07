package org.ow2.petals.esb.kernel.impl.endpoint;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.petals.esb.kernel.api.endpoint.ClientProxyEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.external.ExternalListener;

@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller="primitive")
public class ClientProxyEndpointImpl extends ClientEndpointImpl implements ClientProxyEndpoint {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<String, ExternalListener> listeners = new HashMap<String, ExternalListener>();

	private QName providerEndpointName;
	
	private String externalAddress;

	public Map<String, ExternalListener> getExternalListeners() {
		return this.listeners;
	}


	public QName getProviderEndpointName() {
		return this.providerEndpointName;
	}

	public void setProviderEndpointName(QName providerQName) {
		this.providerEndpointName = providerQName;
	}

	public void setExternalListeners(Map<String, ExternalListener> listeners) {
		this.listeners = listeners;
	}


	public String getExternalAddress() {
		return this.externalAddress;
	}


	public void setExternalAddress(String address) {
		this.externalAddress = address;
	}



}
