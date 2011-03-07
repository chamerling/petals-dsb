package org.ow2.petals.esb.kernel.api.endpoint.external;

import org.ow2.petals.esb.kernel.api.endpoint.ClientProxyEndpoint;

public interface ExternalListener {

	ClientProxyEndpoint getEndpoint();
	
	void setEndpoint(ClientProxyEndpoint endpoint);
}
