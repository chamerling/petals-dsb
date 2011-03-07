package org.ow2.petals.esb.kernel.api;

import javax.xml.namespace.QName;

import org.ow2.petals.esb.kernel.api.node.Node;

public interface ESBKernelFactory {

	Node createNode(QName name, boolean explorer) throws ESBException;
}
