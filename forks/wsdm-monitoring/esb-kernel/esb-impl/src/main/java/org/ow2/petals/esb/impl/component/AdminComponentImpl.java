package org.ow2.petals.esb.impl.component;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.petals.esb.api.component.AdminComponent;
import org.ow2.petals.esb.api.entity.AdminProvider;
import org.ow2.petals.esb.impl.entity.AdminProviderImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.impl.component.ComponentImpl;
import org.ow2.petals.esb.kernel.impl.entity.ClientImpl;
import org.ow2.petals.esb.kernel.impl.entity.ProviderImpl;

@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller="composite")
public class AdminComponentImpl extends ComponentImpl implements AdminComponent {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private AdminProvider adminProvider = null;

	public AdminProvider createAdminProvider() throws ESBException {
		if(this.adminProvider != null) {
			throw new ESBException("Admin provider already exist!!!");
		}
		this.adminProvider = (AdminProvider) this.createClientAndProvider(new QName(this.getNode().getQName().getNamespaceURI(), "adminProvider"), "adminProvider", AdminProviderImpl.class, "service", ClientImpl.class, "service", ProviderImpl.class);
		return this.adminProvider;
	}



}
