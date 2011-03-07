package org.ow2.petals.esb.kernel.api.service;

import java.util.List;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.endpoint.ProviderEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.context.EndpointInitialContext;

@Interface(name="service")
public interface Service extends Endpoint {
	
	<P extends ProviderEndpoint> P createProviderEndpoint(String name, String fractalInterfaceName, Class<P> endpointClass, EndpointInitialContext context) throws ESBException;
	
	List<ProviderEndpoint> getProviderEndpoints();
	
	void setQName(QName name);
}
