package org.ow2.petals.esb.external.protocol.soap.impl;

import java.util.logging.Logger;

import org.ow2.petals.esb.external.protocol.soap.impl.server.SoapServer;
import org.ow2.petals.esb.external.protocol.soap.impl.server.SoapServerConfig;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.ClientProxyEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.external.ExternalListener;

public class SOAPListenerImpl implements ExternalListener {

	/**
	 * The logger
	 */
	private Logger log = Logger.getLogger(SOAPListenerImpl.class.getName());

	private ClientProxyEndpoint endpoint = null;

	
	
	public SOAPListenerImpl(ClientProxyEndpoint endpoint) throws ESBException {
			this.endpoint = endpoint;
	
			SoapServer.getInstance().getWelcomeServlet().getListeners().add(this);
			
			String url = "http://" + SoapServer.getInstance().getConfig().getHost() + ":" + SoapServer.getInstance().getConfig().getPort() + "/services/" + this.getEndpoint().getQName().getLocalPart();
			endpoint.setExternalAddress(url);
			log.info("new service deployed at: " + endpoint.getExternalAddress());
	}

	public ClientProxyEndpoint getEndpoint() {
		return this.endpoint;
	}

	public void setEndpoint(ClientProxyEndpoint endpoint) {
		throw new UnsupportedOperationException();
	}
	
	
	
}
