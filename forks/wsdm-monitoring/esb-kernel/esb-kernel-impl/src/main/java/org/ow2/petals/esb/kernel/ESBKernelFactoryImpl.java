package org.ow2.petals.esb.kernel;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.ow2.petals.base.fractal.api.FractalException;
import org.ow2.petals.base.fractal.impl.FractalHelper;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.ESBKernelFactory;
import org.ow2.petals.esb.kernel.api.node.Node;
import org.ow2.petals.esb.kernel.api.registry.Registry;
import org.ow2.petals.esb.kernel.api.transport.TransportersManager;
import org.ow2.petals.esb.kernel.impl.node.NodeImpl;

public class ESBKernelFactoryImpl implements ESBKernelFactory {
	
	private static Logger log = Logger.getLogger(ESBKernelFactoryImpl.class.getName());

	public Node createNode(QName name, boolean explorer) throws ESBException {
		Node node = null;
		
		try {
			Component nodeComponent = this.createNodeComposite(explorer);
			FractalHelper.getFractalHelper().startComponent(nodeComponent);

			if(name != null) {
				FractalHelper.getFractalHelper().changeName(nodeComponent, name.toString());
			}
			
			node = (Node)nodeComponent.getFcInterface("service");
			
			// init
			node.initFractalComponent(nodeComponent);
			node.setNode(node);
			//node.getListenedEndpoints().put(node.getQName(), node);
			
			// create registry
			Registry registry = node.createRegistry(name.getLocalPart() + "_registry");
			
			// create transporterManager
			TransportersManager transportersManager = node.createTransportersManager(name.getLocalPart() + "_transporterManager");
			
			// add node in registry
			node.getRegistry().addEndpoint(node);
			
		} catch (NoSuchInterfaceException e) {
			throw new ESBException(e);
		} catch (FractalException e) {
			throw new ESBException(e);
		}
		log.fine("node " + name + " created and started");
		return node;
	}

	/**
	 * Initialize maestro composite
	 *
	 * @throws MaestroException
	 *
	 */
	private Component createNodeComposite(boolean explorer) throws FractalException {
		Component nodeComponent = null;

		if(!explorer) {
			nodeComponent = FractalHelper.getFractalHelper()
			.createNewComponent(NodeImpl.class.getName(), null);
		}
		else {
			Component explorerComponent = FractalHelper.getFractalHelper()
			.createNewComponent("ESBExplorer", new HashMap<Object, Object>());
			
			FractalHelper.getFractalHelper().startComponent(explorerComponent);
			Component explorerComp = FractalHelper.getFractalHelper().getComponents(explorerComponent).get(0);
			
			Component myExplorerComp = FractalHelper.getFractalHelper().getComponents(explorerComp).get(1);
			
			nodeComponent = FractalHelper.getFractalHelper().getComponents(myExplorerComp).get(1);
		}
		return nodeComponent;
	}	
}
