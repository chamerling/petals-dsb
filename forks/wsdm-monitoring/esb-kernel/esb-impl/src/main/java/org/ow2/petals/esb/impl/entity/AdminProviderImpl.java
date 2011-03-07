package org.ow2.petals.esb.impl.entity;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.petals.esb.api.entity.AdminProvider;
import org.ow2.petals.esb.api.service.AdminService;
import org.ow2.petals.esb.impl.service.AdminServiceImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.impl.entity.ClientAndProviderImpl;

@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller="composite")
public class AdminProviderImpl extends ClientAndProviderImpl implements
		AdminProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AdminService adminService = null;
	
	public AdminService createAdminService() throws ESBException {
		if(this.adminService != null) {
			throw new ESBException("Admin service already exist!!!");
		}
		this.adminService = (AdminService) this.createService(new QName(this.getNode().getQName().getNamespaceURI(), "adminService"), "adminService", AdminServiceImpl.class);
		return this.adminService;
	}

}
