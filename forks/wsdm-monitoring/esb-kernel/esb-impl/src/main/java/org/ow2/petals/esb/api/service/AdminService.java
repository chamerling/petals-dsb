package org.ow2.petals.esb.api.service;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.api.endpoint.AdminEndpoint;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.service.Service;


@Interface(name="adminService")
public interface AdminService extends Service {

	AdminEndpoint createAdminEndpoint() throws ESBException;
}
