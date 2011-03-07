package org.ow2.petals.esb.impl.endpoint;

import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.petals.esb.api.endpoint.AdminEndpoint;
import org.ow2.petals.esb.impl.endpoint.behaviour.AdminBehaviourImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.Behaviour;
import org.ow2.petals.esb.kernel.impl.endpoint.ProviderEndpointImpl;

@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller="primitive")
public class AdminEndpointImpl extends ProviderEndpointImpl implements
		AdminEndpoint {
	
	private Logger log = Logger.getLogger(AdminEndpointImpl.class.getName());

	
}
