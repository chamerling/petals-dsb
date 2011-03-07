package org.ow2.petals.esb.kernel.impl.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.petals.base.fractal.api.FractalException;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.node.Node;
import org.ow2.petals.esb.kernel.api.registry.Registry;
import org.ow2.petals.esb.kernel.api.service.BusinessService;
import org.ow2.petals.esb.kernel.api.service.TechnicalService;
import org.ow2.petals.esb.kernel.impl.endpoint.EndpointImpl;

@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller="primitive")
public class RegistryImpl extends EndpointImpl implements Registry {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger log = Logger.getLogger(RegistryImpl.class.getName());
	
	private Map<QName, Endpoint> endpoints = new HashMap<QName, Endpoint>();

	public void addEndpoint(Endpoint endpoint) {
		log.fine(endpoint.getQName() + " is stored in registry");
		this.endpoints.put(endpoint.getQName(), endpoint);
	}
	
	public List<BusinessService> getBusinessServices() {
		List<BusinessService> services = new ArrayList<BusinessService>();
		for(Endpoint ep: this.endpoints.values()) {
			if(ep instanceof BusinessService) {
				services.add((BusinessService) ep);
			}
		}
		return services;
	}

	public List<TechnicalService> getTechnicalServices() {
		List<TechnicalService> services = new ArrayList<TechnicalService>();
		for(Endpoint ep: this.endpoints.values()) {
			if(ep instanceof TechnicalService) {
				services.add((TechnicalService) ep);
			}
		}
		return services;
	}

	public Endpoint getEndpoint(QName name) {
		return this.endpoints.get(name);
	}

	public List<Endpoint> getLocalEndpoints() {
		return new ArrayList<Endpoint>(this.endpoints.values());
	}


	public void setQName(QName name) {
		try {
			super.setName(name.toString());
		} catch (FractalException e) {
			// do nothing
		}
	}




}
