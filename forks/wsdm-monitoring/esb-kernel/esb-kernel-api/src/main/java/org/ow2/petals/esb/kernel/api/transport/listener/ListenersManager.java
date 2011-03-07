package org.ow2.petals.esb.kernel.api.transport.listener;

import java.util.Map;

import javax.xml.namespace.QName;

import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;

public interface ListenersManager {

//	Map<QName, Endpoint> getEndpoints();
	
	void addListener(Listener listener);
	
	Listener removeListener(Listener listener);

	void shutdownAllListeners();
}
