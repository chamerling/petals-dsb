package org.ow2.petals.esb.kernel.api.endpoint;

import java.io.Serializable;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.base.fractal.api.FractalComponent;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.Behaviour;
import org.ow2.petals.esb.kernel.api.endpoint.context.EndpointInitialContext;
import org.ow2.petals.esb.kernel.api.node.Node;
import org.ow2.petals.esb.kernel.api.transport.Skeleton;
import org.ow2.petals.esb.kernel.api.transport.listener.ListenersManager;


@Interface(name="service")
public interface Endpoint extends FractalComponent, Skeleton, Serializable, Cloneable {

	QName getQName();
	
	Node getNode();
	
	void setNode(Node node);
	
	EndpointInitialContext getEndpointInitialContext();
	
	void setEndpointInitialContext(EndpointInitialContext context);
	
	Class<? extends Behaviour<?>> getBehaviourClass();
	
	void setBehaviourClass(Class<? extends Behaviour<?>> behaviourClass);
	
	Behaviour<?> getBehaviour() throws ESBException;

	ListenersManager getListenersManager();
	
	void setListenersManager(ListenersManager listenerManager);
}
