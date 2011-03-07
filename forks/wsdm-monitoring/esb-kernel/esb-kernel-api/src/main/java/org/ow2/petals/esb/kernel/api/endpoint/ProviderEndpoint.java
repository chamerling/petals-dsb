package org.ow2.petals.esb.kernel.api.endpoint;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.service.Service;

@Interface(name="service")
public interface ProviderEndpoint extends Endpoint {

	Service getService();
	
	void setService(Service service);
	
	void setQName(QName name);
}
