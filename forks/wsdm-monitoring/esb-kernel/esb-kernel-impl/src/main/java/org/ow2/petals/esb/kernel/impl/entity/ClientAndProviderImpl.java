package org.ow2.petals.esb.kernel.impl.entity;

import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.petals.base.fractal.api.FractalException;
import org.ow2.petals.base.fractal.impl.FractalHelper;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.ClientEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.context.EndpointInitialContext;
import org.ow2.petals.esb.kernel.api.entity.Client;
import org.ow2.petals.esb.kernel.api.entity.ClientAndProvider;
import org.ow2.petals.esb.kernel.api.entity.Provider;
import org.ow2.petals.esb.kernel.api.service.Service;
import org.ow2.petals.esb.kernel.impl.endpoint.EndpointImpl;

@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller="composite")
public class ClientAndProviderImpl extends EndpointImpl implements ClientAndProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(ClientAndProviderImpl.class.getName());


	private Client client;

	private Provider provider;


	public <CE extends ClientEndpoint> CE createClientEndpoint(QName name,
			String fractalInterfaceName, Class<CE> clientEndpointClass, EndpointInitialContext context)
	throws ESBException {
		return this.client.createClientEndpoint(name, fractalInterfaceName, clientEndpointClass, context);
	}

	public ClientEndpoint getClientEndpoint() {
		return this.client.getClientEndpoint();
	}

	public void setQName(QName name) {
		if(this.provider != null) {
			this.provider.setQName(name);
		} else {
			log.severe("provider cannot be null=> impossible to affect name: " + name);
		}
	}

	public <S extends Service> S createService(QName name,
			String fractalInterfaceName, Class<S> serviceClass)
	throws ESBException {
		return this.provider.createService(name, fractalInterfaceName, serviceClass);
	}

	public List<Service> getServices() {
		return this.provider.getServices();
	}

	public void setClient(Client client) throws ESBException {
		try {
			if(this.client != null) {
				throw new ESBException("Client already setted");
			}
			this.client = client;

			FractalHelper.getFractalHelper().addComponent(this.client.getComponent(), this.getComponent(), null);
		} catch (FractalException e) {
			throw new ESBException(e);
		}

	}

	public void setProvider(Provider provider) throws ESBException {
		try {
			if(this.provider != null) {
				throw new ESBException("Provider already setted");
			}
			this.provider = provider;

			FractalHelper.getFractalHelper().addComponent(this.provider.getComponent(), this.getComponent(), null);
		} catch (FractalException e) {
			throw new ESBException(e);
		}

	}





}
