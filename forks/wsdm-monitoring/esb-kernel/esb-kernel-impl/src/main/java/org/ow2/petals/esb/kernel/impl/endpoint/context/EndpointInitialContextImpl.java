package org.ow2.petals.esb.kernel.impl.endpoint.context;

import org.ow2.petals.esb.kernel.api.endpoint.context.EndpointInitialContext;

public class EndpointInitialContextImpl implements EndpointInitialContext {

	int numberOfThread = 0;
	
	public EndpointInitialContextImpl(int numberOfThread) {
		this.setNumberOfThreads(numberOfThread);
	}
	
	public int getNumberOfThreads() {
		return this.numberOfThread;
	}

	public void setNumberOfThreads(int numberOfThread) {
		this.numberOfThread = numberOfThread;
	}



}
