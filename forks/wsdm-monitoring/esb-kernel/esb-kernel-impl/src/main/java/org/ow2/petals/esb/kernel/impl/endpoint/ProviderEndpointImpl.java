package org.ow2.petals.esb.kernel.impl.endpoint;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.petals.esb.kernel.api.endpoint.ProviderEndpoint;
import org.ow2.petals.esb.kernel.api.service.Service;

@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller="primitive")
public class ProviderEndpointImpl extends EndpointImpl implements ProviderEndpoint {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private QName name;
	
	private Service service;
	
	public Service getService() {
		return this.service;
	}
	
	public void setService(Service service) {
		this.service = service;
	}

	public void setQName(QName name) {
		this.name = name;
	}

	@Override
	public QName getQName() {
		return this.name;
	}

}
