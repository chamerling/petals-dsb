package org.ow2.petals.esb.api;

import javax.xml.namespace.QName;

import org.ow2.petals.esb.impl.endpoint.behaviour.AdminBehaviourImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.ESBKernelFactory;
import org.ow2.petals.esb.kernel.api.node.Node;

public interface ESBFactory extends ESBKernelFactory {

	public Node createNode(QName name, Class<? extends AdminBehaviourImpl> adminBehaviourClass, boolean explorer) throws ESBException;
}
