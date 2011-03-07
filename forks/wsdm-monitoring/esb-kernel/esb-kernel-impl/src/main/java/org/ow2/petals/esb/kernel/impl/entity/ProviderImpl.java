package org.ow2.petals.esb.kernel.impl.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.petals.base.fractal.api.FractalException;
import org.ow2.petals.base.fractal.impl.FractalHelper;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.entity.Provider;
import org.ow2.petals.esb.kernel.api.service.Service;
import org.ow2.petals.esb.kernel.impl.endpoint.EndpointImpl;

@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller="composite")
public class ProviderImpl extends EndpointImpl implements Provider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger log = Logger.getLogger(ProviderImpl.class.getName());
	
	private List<Service> services = new ArrayList<Service>();

	private QName name;
	
	public List<Service> getServices() {
		return this.services;
	}
	
	public <S extends Service> S createService(QName name, String fractalInterfaceName, Class<S> serviceClass) throws ESBException {
		S service = null;
		try {
			org.objectweb.fractal.api.Component serviceComponent = FractalHelper.getFractalHelper().createNewComponent(serviceClass.getName(), null);
			FractalHelper.getFractalHelper().startComponent(serviceComponent);

			if(name != null) {
				FractalHelper.getFractalHelper().changeName(serviceComponent, name.toString());
			}

			service = (S)serviceComponent.getFcInterface(fractalInterfaceName);
			
			// init
			service.initFractalComponent(serviceComponent);
			service.setQName(name);
			service.setNode(this.getNode());
			
			
			// add component in list
			FractalHelper.getFractalHelper().addComponent(serviceComponent, this.getComponent(), null);
			this.services.add(service);
			
			// add service in registry
			this.getNode().getRegistry().addEndpoint(service);
			
			// add endpoint in listener
			//this.getNode().getListenedEndpoints().put(service.getQName(), service);

			
		} catch (NoSuchInterfaceException e) {
			throw new ESBException(e);
		} catch (FractalException e) {
			throw new ESBException(e);
		}
		log.fine("service " + name + " created and started");
		return service;
	}

	public void setQName(QName name) {
		this.name = name;
	}

	@Override
	public QName getQName() {
		return this.name;
	}

	
}
