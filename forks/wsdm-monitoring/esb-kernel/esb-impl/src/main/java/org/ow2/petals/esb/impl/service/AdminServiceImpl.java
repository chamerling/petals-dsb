package org.ow2.petals.esb.impl.service;

import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.petals.esb.api.endpoint.AdminEndpoint;
import org.ow2.petals.esb.api.service.AdminService;
import org.ow2.petals.esb.impl.endpoint.AdminEndpointImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.impl.endpoint.context.EndpointInitialContextImpl;
import org.ow2.petals.esb.kernel.impl.service.ServiceImpl;

@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller="composite")
public class AdminServiceImpl extends ServiceImpl implements AdminService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AdminEndpoint adminEndpoint = null;
	
	public AdminEndpoint createAdminEndpoint() throws ESBException {
		if(this.adminEndpoint != null) {
			throw new ESBException("Admin endpoint already exist!!!");
		}
		this.adminEndpoint = (AdminEndpoint) this.createProviderEndpoint("adminEndpoint", "adminEndpoint", AdminEndpointImpl.class, new EndpointInitialContextImpl(5));
		return this.adminEndpoint;
	}

}
