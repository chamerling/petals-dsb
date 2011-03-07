package org.ow2.petals.esb.kernel.explorer;

import static org.objectweb.fractal.fraclet.types.Step.CREATE;
import static org.objectweb.fractal.fraclet.types.Step.DESTROY;
import static org.objectweb.fractal.fraclet.types.Step.START;
import static org.objectweb.fractal.fraclet.types.Step.STOP;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.fraclet.annotations.Controller;
import org.objectweb.fractal.fraclet.annotations.Lifecycle;
import org.objectweb.fractal.fraclet.annotations.Requires;
import org.objectweb.fractal.fraclet.types.Contingency;
import org.ow2.petals.base.fractal.api.FractalException;
import org.ow2.petals.base.fractal.impl.FractalHelper;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.node.Node;

@org.objectweb.fractal.fraclet.annotations.Component
public class ClientNode {

	@Requires(name = "node", contingency=Contingency.OPTIONAL)
	private Node node;
	
	/**
	 * The component.
	 */
	@Controller
	private Component component;

	/**
	 * Default constructor
	 * @throws MaestroException
	 */
	public ClientNode()  {
		super();
	}

	/**
	 * Create the scope behaviour
	 * @throws MaestroException
	 */
	@Lifecycle(step = CREATE)
	public void create() throws ESBException {
	}

	public void init(Component fractalNode) throws ESBException {
		this.component = fractalNode;
	}

	/**
	 * Start the NodeImpl behaviour
	 * @throws MaestroException
	 */
	@Lifecycle(step = START)
	public void start() throws ESBException {
	}

	/**
	 * Stop the NodeImpl behaviour
	 * @throws MaestroException
	 */
	@Lifecycle(step = STOP)
	public void stop() throws ESBException {
	}

	/**
	 * Destroy the NodeImpl behaviour
	 * @throws MaestroException
	 */
	@Lifecycle(step = DESTROY)
	public void destroy() throws ESBException {
	}

	public Component getComponent() {
		return this.component;
	}


	public String getName() throws FractalException {
		return FractalHelper.getFractalHelper().getName(this.component);
	}

	public void setName(String name) throws FractalException {
		if(name != null)
			FractalHelper.getFractalHelper().changeName(this.component, name);
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

}
