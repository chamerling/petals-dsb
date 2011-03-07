package org.ow2.petals.esb.kernel.api.entity;

import java.util.List;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.service.Service;

@Interface(name="service")
public interface Provider extends Endpoint {
	
	<S extends Service> S createService(QName name, String fractalInterfaceName, Class<S> serviceClass) throws ESBException;
	
	List<Service> getServices();
	
	void setQName(QName name);
}
