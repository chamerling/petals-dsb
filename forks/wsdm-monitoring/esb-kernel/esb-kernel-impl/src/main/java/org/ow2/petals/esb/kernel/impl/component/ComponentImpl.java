package org.ow2.petals.esb.kernel.impl.component;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.petals.base.fractal.api.FractalException;
import org.ow2.petals.base.fractal.impl.FractalHelper;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.component.Component;
import org.ow2.petals.esb.kernel.api.entity.Client;
import org.ow2.petals.esb.kernel.api.entity.ClientAndProvider;
import org.ow2.petals.esb.kernel.api.entity.Provider;
import org.ow2.petals.esb.kernel.impl.endpoint.EndpointImpl;

@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller="composite")
public class ComponentImpl extends EndpointImpl implements Component {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger log = Logger.getLogger(ComponentImpl.class.getName());
	
	private List<Client> clients = new ArrayList<Client>();
	
	private List<Provider> providers = new ArrayList<Provider>();
	
	private List<ClientAndProvider> clientAndProviders = new ArrayList<ClientAndProvider>();

	
	public List<Client> getClients() {
		return this.clients;
	}

	public List<Provider> getProviders() {
		return this.providers;
	}

	public List<ClientAndProvider> getClientAndProviders() {
		return this.clientAndProviders;
	}

	public <C extends Client> C createClient(QName name, String fractalInterfaceName, Class<C> clientClassName) throws ESBException {
		C client = null;
		try {
			org.objectweb.fractal.api.Component clientComponent = FractalHelper.getFractalHelper().createNewComponent(clientClassName.getName(), null);
			FractalHelper.getFractalHelper().startComponent(clientComponent);

			if(name != null) {
				FractalHelper.getFractalHelper().changeName(clientComponent, name.toString());
			}

			client = (C)clientComponent.getFcInterface(fractalInterfaceName);
			
			// init
			client.initFractalComponent(clientComponent);
			client.setQName(name);
			client.setNode(this.getNode());
			
			
			// add component in list
			FractalHelper.getFractalHelper().addComponent(clientComponent, this.getComponent(), null);
			this.clients.add(client);
			
			// add consumer in registry
			this.getNode().getRegistry().addEndpoint(client);
			
			// add endpoint in listener
			//this.getNode().getListenedEndpoints().put(client.getQName(), client);

		} catch (NoSuchInterfaceException e) {
			throw new ESBException(e);
		} catch (FractalException e) {
			throw new ESBException(e);
		}
		log.fine("client " + name + " created and started");
		return client;
	}
	
	
	public <P extends Provider> P createProvider(QName name, String fractalInterfaceName, Class<P> providerClassName) throws ESBException {
		P provider = null;
		try {
			org.objectweb.fractal.api.Component providerComponent = FractalHelper.getFractalHelper().createNewComponent(providerClassName.getName(), null);
			FractalHelper.getFractalHelper().startComponent(providerComponent);

			if(name != null) {
				FractalHelper.getFractalHelper().changeName(providerComponent, name.toString());
			}

			provider = (P)providerComponent.getFcInterface(fractalInterfaceName);
			
			// init
			provider.initFractalComponent(providerComponent);
			provider.setQName(name);
			provider.setNode(this.getNode());
			
			
			// add component in list
			FractalHelper.getFractalHelper().addComponent(providerComponent, this.getComponent(), null);
			this.providers.add(provider);
			
			// add provider in registry
			this.getNode().getRegistry().addEndpoint(provider);
			
			// add endpoint in listener
			//this.getNode().getListenedEndpoints().put(provider.getQName(), provider);

		} catch (NoSuchInterfaceException e) {
			throw new ESBException(e);
		} catch (FractalException e) {
			throw new ESBException(e);
		}
		log.fine("provider " + name + " created and started");
		return provider;
	}

	public <CP extends ClientAndProvider, C extends Client, P extends Provider> CP createClientAndProvider(
			QName name, String fractalInterfaceName, Class<CP> className, String fractalClientInterfaceName, Class<C> clientClassName, String fractalProviderInterfaceName,
			Class<P> providerClassName) throws ESBException {
		CP clientAndProvider = null;
		try {
			org.objectweb.fractal.api.Component providerComponent = FractalHelper.getFractalHelper().createNewComponent(className.getName(), null);
			FractalHelper.getFractalHelper().startComponent(providerComponent);

			if(name != null) {
				FractalHelper.getFractalHelper().changeName(providerComponent, name.toString());
			}

			clientAndProvider = (CP)providerComponent.getFcInterface(fractalInterfaceName);
			
			// init
			clientAndProvider.initFractalComponent(providerComponent);
			clientAndProvider.setNode(this.getNode());
			clientAndProvider.setClient(this.createClient(name, fractalClientInterfaceName, clientClassName));
			clientAndProvider.setProvider(this.createProvider(name, fractalProviderInterfaceName, providerClassName));
			clientAndProvider.setQName(name);
			
			// add component in list
			FractalHelper.getFractalHelper().addComponent(providerComponent, this.getComponent(), null);
			this.clientAndProviders.add(clientAndProvider);
			
			// add provider in registry
			this.getNode().getRegistry().addEndpoint(clientAndProvider);
			
			// add endpoint in listener
			//this.getNode().getListenedEndpoints().put(clientAndProvider.getQName(), clientAndProvider);

			
		} catch (NoSuchInterfaceException e) {
			throw new ESBException(e);
		} catch (FractalException e) {
			throw new ESBException(e);
		}
		log.fine("provider " + name + " created and started");
		return clientAndProvider;
	}
	
}
