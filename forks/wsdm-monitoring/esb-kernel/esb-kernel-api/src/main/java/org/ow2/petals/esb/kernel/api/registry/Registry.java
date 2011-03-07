package org.ow2.petals.esb.kernel.api.registry;

import java.util.List;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.service.BusinessService;
import org.ow2.petals.esb.kernel.api.service.TechnicalService;

@Interface(name="service")
public interface Registry extends Endpoint {

	void setQName(QName name);
	
	void addEndpoint(Endpoint endpoint);
	
	Endpoint getEndpoint(QName name);
	
	List<Endpoint> getLocalEndpoints();
	
	List<TechnicalService> getTechnicalServices();
	
	List<BusinessService> getBusinessServices();
	
}
