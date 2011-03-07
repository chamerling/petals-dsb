package org.ow2.petals.esb.api.entity;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.api.service.AdminService;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.entity.ClientAndProvider;

@Interface(name="adminProvider")
public interface AdminProvider extends ClientAndProvider {

	AdminService createAdminService() throws ESBException;
	
}
