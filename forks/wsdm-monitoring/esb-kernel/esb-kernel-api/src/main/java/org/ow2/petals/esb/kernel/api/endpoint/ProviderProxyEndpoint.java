package org.ow2.petals.esb.kernel.api.endpoint;

import java.net.URI;
import java.util.Map;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.endpoint.external.ExternalSender;


@Interface(name="proxy-provider-service")
public interface ProviderProxyEndpoint extends ProviderEndpoint {

	Map<String, ExternalSender> getExternalSenders();
	
	void setExternalSenders(Map<String, ExternalSender> senders);
	
	String getExternalAddress();
	
	void setExternalAddress(String address);
	
	URI getWSDLDescriptionAddress();
	
	void setWSDLDescriptionAddress(URI wsdl);
	
}
