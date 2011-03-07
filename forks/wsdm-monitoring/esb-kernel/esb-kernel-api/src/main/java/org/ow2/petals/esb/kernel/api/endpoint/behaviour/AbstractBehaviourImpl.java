package org.ow2.petals.esb.kernel.api.endpoint.behaviour;

import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;

public abstract class AbstractBehaviourImpl<O> implements Behaviour<O> {

	private Description description;
	
	public Endpoint endpoint;
	
	/**
	 * the default constructor is mandatory
	 */
	public AbstractBehaviourImpl(Endpoint ep) {
		this.endpoint = ep;
	}

	public Endpoint getEndpoint() {
		return this.endpoint;
	}

	public Description getDescription() {
		return this.description;
	}

	public void setDescription(Description desc) {
		this.description = desc;
	}
}
