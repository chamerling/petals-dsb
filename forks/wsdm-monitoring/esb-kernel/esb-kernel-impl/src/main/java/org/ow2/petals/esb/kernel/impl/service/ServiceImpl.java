package org.ow2.petals.esb.kernel.impl.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.petals.base.fractal.api.FractalException;
import org.ow2.petals.base.fractal.impl.FractalHelper;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.endpoint.ProviderEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.context.EndpointInitialContext;
import org.ow2.petals.esb.kernel.api.service.Service;
import org.ow2.petals.esb.kernel.api.transport.listener.Listener;
import org.ow2.petals.esb.kernel.api.transport.listener.ListenersManager;
import org.ow2.petals.esb.kernel.impl.endpoint.EndpointImpl;
import org.ow2.petals.esb.kernel.impl.transport.listener.ListenersManagerImpl;

@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller="composite")
public class ServiceImpl extends EndpointImpl implements Service {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(ServiceImpl.class.getName());
	
	
	public QName name;
	
	private List<ProviderEndpoint> endpoints = new ArrayList<ProviderEndpoint>();
	
	
	public <PE extends ProviderEndpoint> PE createProviderEndpoint(String name, String fractalInterfaceName, Class<PE> endpointClass, EndpointInitialContext context) throws ESBException {
		PE providerEndpoint = null;
		try {
			org.objectweb.fractal.api.Component providerComponent = FractalHelper.getFractalHelper().createNewComponent(endpointClass.getName(), null);
			FractalHelper.getFractalHelper().startComponent(providerComponent);
			if(name != null) {
				FractalHelper.getFractalHelper().changeName(providerComponent, name.toString());
			}
			providerEndpoint = (PE)providerComponent.getFcInterface(fractalInterfaceName);
			
			
			// init
			providerEndpoint.initFractalComponent(providerComponent);
			providerEndpoint.setQName(new QName(this.getQName().getNamespaceURI(),name));
			providerEndpoint.setNode(this.getNode());
			providerEndpoint.setService(this);
			providerEndpoint.setEndpointInitialContext(context);
			
			// add service in registry
			if((this.getNode() != null)&&(this.getNode().getRegistry() != null)) {
				this.getNode().getRegistry().addEndpoint(providerEndpoint);
			} else {
				throw new ESBException("Registry cannot be null"); 
			}
			
			// add component in list
			if(this.getComponent() != null) {
				FractalHelper.getFractalHelper().addComponent(providerComponent, this.getComponent(), null);
			}
			this.endpoints.add(providerEndpoint);
			
			// create listener or add endpoint in listener node
			if((providerEndpoint.getEndpointInitialContext() != null)&&(providerEndpoint.getEndpointInitialContext().getNumberOfThreads() > 0)) {
				Map<QName, Endpoint> endpoints = Collections.synchronizedMap(new HashMap<QName, Endpoint>());
				endpoints.put(providerEndpoint.getQName(), providerEndpoint);
				providerEndpoint.setListenersManager(new ListenersManagerImpl(providerEndpoint.getEndpointInitialContext().getNumberOfThreads(), endpoints));
			} else {
				this.getNode().getListenedEndpoints().put(providerEndpoint.getQName(), providerEndpoint);
			}
		} catch (NoSuchInterfaceException e) {
			throw new ESBException(e);
		} catch (FractalException e) {
			throw new ESBException(e);
		}
		log.fine("providerEndpoint " + name + " created and started");
		return providerEndpoint;
	}

	public List<ProviderEndpoint> getProviderEndpoints() {
		return this.endpoints;
	}

	public void setQName(QName name) {
		this.name = name;
	}

	@Override
	public QName getQName() {
		return this.name;
	}


}
