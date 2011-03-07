package org.ow2.petals.esb.kernel.impl.endpoint;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.petals.base.fractal.impl.FractalComponentImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.Behaviour;
import org.ow2.petals.esb.kernel.api.endpoint.context.EndpointInitialContext;
import org.ow2.petals.esb.kernel.api.node.Node;
import org.ow2.petals.esb.kernel.api.transport.Skeleton;
import org.ow2.petals.esb.kernel.api.transport.TransportersManager;
import org.ow2.petals.esb.kernel.api.transport.listener.ListenersManager;
import org.ow2.petals.esb.kernel.impl.transport.SkeletonImpl;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.transporter.api.transport.TransportException;


@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller="primitive")
public class EndpointImpl extends FractalComponentImpl implements Endpoint {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(EndpointImpl.class.getName());

	private Node node;

	private Behaviour<?> behaviour;

	private Class<? extends Behaviour<?>> behaviourClass;
	
	private EndpointInitialContext initialContext;

	private Skeleton skeleton = null;
	
	private ListenersManager listenersManager = null;

	public EndpointImpl() {
		this.skeleton = new SkeletonImpl(this);
	}

	public QName getQName() {
		QName res = null;
		if(this instanceof Node) {
			res = QName.valueOf(this.getName());
		} else {
			QName qname = QName.valueOf(this.getName());
			res = qname;
		}
		return res;
	}

	public Node getNode() {
		return this.node;
	}

	public void accept(Exchange exchange) throws TransportException {
		this.skeleton.accept(exchange);
	}

	public TransportersManager getTransportersManager() {
		return this.getNode().getTransportersManager();
	}

	public void setNode(Node node) {
		this.node = node;

	}

	public Class<? extends Behaviour<?>> getBehaviourClass() {
		return this.behaviourClass;
	}


//	public ListenersManager getListenersManager() {
//		ListenersManager res = null;
//		if(this.node != null) {
//			if(this instanceof Node) {
//				res = ((Node)this).getListenersManager();
//			} else {
//				res = this.node.getListenersManager();
//			}
//		}
//		return res;
//	}
//
//	public void setListenersManager(ListenersManager manager) {
//		throw new UnsupportedOperationException();
//	}

	@SuppressWarnings("unchecked")
	public Behaviour<?> getBehaviour() throws ESBException {
		if(this.behaviour == null) {
			if(this.behaviourClass != null) {
				log.fine("create behaviour: " + this.behaviourClass);
				Constructor<?> constructor = this.behaviourClass.getConstructors()[0];
				try {
					this.behaviour = (Behaviour) constructor.newInstance(this);
				} catch (IllegalArgumentException e) {
					throw new ESBException(e);
				} catch (InstantiationException e) {
					throw new ESBException(e);
				} catch (IllegalAccessException e) {
					throw new ESBException(e);
				} catch (InvocationTargetException e) {
					throw new ESBException(e);
				}
			}
		}
		return this.behaviour;
	}

	public void setBehaviourClass(Class<? extends Behaviour<?>> behaviourClass) {
		this.behaviourClass = behaviourClass;
		this.behaviour = null;
	}

	public EndpointInitialContext getEndpointInitialContext() {
		return this.initialContext;
	}

	public void setEndpointInitialContext(EndpointInitialContext context) {
		this.initialContext = context;
	}

	public ListenersManager getListenersManager() {
		return this.listenersManager;
	}

	public void setListenersManager(ListenersManager listenersManager) {
		this.listenersManager = listenersManager;
	}

}
