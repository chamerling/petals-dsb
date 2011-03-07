package org.ow2.petals.esb.kernel.impl.endpoint;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.petals.esb.kernel.api.endpoint.ProviderProxyEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.external.ExternalSender;

@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller="primitive")
public class ProviderProxyEndpointImpl extends ProviderEndpointImpl implements ProviderProxyEndpoint {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<String, ExternalSender> senders = new HashMap<String, ExternalSender>();

		
	private String externalAddress;

	private URI wsdl;
	
	
	public String getExternalAddress() {
		return this.externalAddress;
	}


	public void setExternalAddress(String address) {
		this.externalAddress = address;
	}


	public Map<String, ExternalSender> getExternalSenders() {
		return this.senders;
	}


	public void setExternalSenders(Map<String, ExternalSender> senders) {
		this.senders = senders;
	}


	public URI getWSDLDescriptionAddress() {
		return this.wsdl;
	}


	public void setWSDLDescriptionAddress(URI wsdl) {
		this.wsdl = wsdl;
	}
	


}
