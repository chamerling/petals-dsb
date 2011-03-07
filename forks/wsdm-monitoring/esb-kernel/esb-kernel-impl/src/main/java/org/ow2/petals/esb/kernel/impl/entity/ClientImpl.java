package org.ow2.petals.esb.kernel.impl.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.petals.base.fractal.api.FractalException;
import org.ow2.petals.base.fractal.impl.FractalHelper;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.ClientEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.endpoint.context.EndpointInitialContext;
import org.ow2.petals.esb.kernel.api.entity.Client;
import org.ow2.petals.esb.kernel.impl.endpoint.EndpointImpl;
import org.ow2.petals.esb.kernel.impl.transport.listener.ListenersManagerImpl;

@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller="composite")
public class ClientImpl extends EndpointImpl implements Client {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(ClientImpl.class.getName());

	private QName name;

	private ClientEndpoint clientEndpoint;

	public ClientEndpoint getClientEndpoint() {
		return this.clientEndpoint;
	}

	public void setQName(QName name) {
		this.name = name;
	}

	@Override
	public QName getQName() {
		return this.name;
	}

	public <CE extends ClientEndpoint> CE createClientEndpoint(QName name, String fractalInterfaceName, Class<CE> clientEndpointClass, EndpointInitialContext context) throws ESBException {
		CE clientEndpoint = null;
		try {
			org.objectweb.fractal.api.Component clientComponent = FractalHelper.getFractalHelper().createNewComponent(clientEndpointClass.getName(), null);
			FractalHelper.getFractalHelper().startComponent(clientComponent);

			if(name != null) {
				FractalHelper.getFractalHelper().changeName(clientComponent, name.toString());
			}

			clientEndpoint = (CE)clientComponent.getFcInterface(fractalInterfaceName);

			// init
			clientEndpoint.initFractalComponent(clientComponent);
			clientEndpoint.setQName(name);
			clientEndpoint.setNode(this.getNode());
			clientEndpoint.setEndpointInitialContext(context);

			//clientEndpoint.setStub(new StubImpl(clientEndpoint));

			// add consumerEndpoint in registry
			this.getNode().getRegistry().addEndpoint(clientEndpoint);

			// set listener manager
			//clientEndpoint.setListenersManager(new ListenersManagerImpl(clientEndpoint));

			// add component in list
			FractalHelper.getFractalHelper().addComponent(clientComponent, this.getComponent(), null);
			this.clientEndpoint = clientEndpoint;

			// create listener or add endpoint in listener node
			if((clientEndpoint.getEndpointInitialContext() != null)&&(clientEndpoint.getEndpointInitialContext().getNumberOfThreads() > 0)) {
				Map<QName, Endpoint> endpoints = Collections.synchronizedMap(new HashMap<QName, Endpoint>());
				endpoints.put(clientEndpoint.getQName(), clientEndpoint);
				clientEndpoint.setListenersManager(new ListenersManagerImpl(clientEndpoint.getEndpointInitialContext().getNumberOfThreads(), endpoints));
			} else {
				this.getNode().getListenedEndpoints().put(clientEndpoint.getQName(), clientEndpoint);
			}
		} catch (NoSuchInterfaceException e) {
			throw new ESBException(e);
		} catch (FractalException e) {
			throw new ESBException(e);
		}
		log.fine("clientEndpoint " + name + " created and started");
		return clientEndpoint;
	}

}
