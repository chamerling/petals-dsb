package org.ow2.petals.esb.kernel.api.endpoint.context;


public interface EndpointInitialContext {

	void setNumberOfThreads(int number);
	
	int getNumberOfThreads();
}
