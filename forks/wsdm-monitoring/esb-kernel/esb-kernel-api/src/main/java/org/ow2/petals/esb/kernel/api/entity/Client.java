package org.ow2.petals.esb.kernel.api.entity;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.ClientEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.endpoint.context.EndpointInitialContext;

@Interface(name="service")
public interface Client extends Endpoint {
	
	void setQName(QName name);
	
	<CE extends ClientEndpoint> CE createClientEndpoint(QName name, String fractalInterfaceName, Class<CE> clientEndpointClass, EndpointInitialContext context) throws ESBException;
	
	ClientEndpoint getClientEndpoint();
	
}
