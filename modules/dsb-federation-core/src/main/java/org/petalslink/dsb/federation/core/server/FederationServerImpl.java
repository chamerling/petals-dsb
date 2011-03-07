/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.petalslink.dsb.federation.core.server;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.api.EndpointQuery;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.federation.api.FederationException;
import org.petalslink.dsb.federation.core.api.ClientManager;
import org.petalslink.dsb.federation.core.api.FederationServer;
import org.petalslink.dsb.federation.core.api.PeerManager;
import org.petalslink.dsb.federation.core.api.PropagationStrategy;
import org.petalslink.dsb.federation.core.api.ServiceManager;

/**
 * The default Federation server implementation. The implementation is
 * customized by inner implementations and is protocol independant.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class FederationServerImpl extends FederationNodeImpl implements FederationServer {

    private ClientManager clientManager;

    private PropagationStrategy propagationStrategy;

    private ServiceManager serviceManager;

    private PeerManager peerManager;

    private static Log logger = LogFactory.getLog(FederationServerImpl.class);

    private boolean started;

    /**
	 * 
	 */
    public FederationServerImpl(String name, String callBackURL) {
        super(name, callBackURL);
        this.setServiceManager(new ServiceManagerImpl());
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        if (this.getClientManager() == null) {
            throw new RuntimeException("The client manager can not be null");
        }

        if (this.getPropagationStrategy() == null) {
            throw new RuntimeException("The propagation strategy can not be null");
        }

        if (this.getServiceManager() != null) {
            this.serviceManager.start();
        }

        this.started = true;
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        if (this.getServiceManager() != null) {
            this.serviceManager.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    public ClientManager getClientManager() {
        return this.clientManager;
    }

    /**
     * @return the serviceManager
     */
    public ServiceManager getServiceManager() {
        return this.serviceManager;
    }

    /**
     * TODO : Synchronize on the Map and not on the method
     * 
     * {@inheritDoc}
     */
    public void invoke(MessageExchange message, String clientId, String id)
            throws FederationException {
        this.checkStarted();
        // check if the client is authorized to invoke
        this.checkClient(clientId);

        this.getPropagationStrategy().invoke(message, clientId, id);
    }

    /**
     * {@inheritDoc}
     */
    public void lookup(EndpointQuery query, String clientId, String id) throws FederationException {
        this.checkStarted();
        this.checkClient(clientId);
        this.propagationStrategy.lookup(query, clientId, id);
    }

    protected void checkClient(String clientId) throws FederationException {
        if (this.getClient(clientId) == null) {
            throw new FederationException("The client '" + clientId
                    + "' is not a registered client within the federation");
        }
    }

    protected void checkStarted() {
        if (!this.started) {
            throw new RuntimeException("The server is not started");
        }
    }

    /**
     * {@inheritDoc}
     */
    public PropagationStrategy getPropagationStrategy() {
        return this.propagationStrategy;
    }

    /**
     * {@inheritDoc}
     */
    public void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    /**
     * {@inheritDoc}
     */
    public void setPropagationStrategy(PropagationStrategy propagationStrategy) {
        this.propagationStrategy = propagationStrategy;
    }

    /**
     * {@inheritDoc}
     */
    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /**
     * {@inheritDoc}
     */
    public void lookupReply(Set<ServiceEndpoint> endpoints, String clientId, String id)
            throws FederationException {
        // FIXME = Must be moved to the propagation strategy, here we just get
        // the first response...
        // got a reply from a lookup...
        this.checkStarted();
        // check if the client is authorized to invoke
        this.checkClient(clientId);
        this.getPropagationStrategy().lookupReply(endpoints, clientId, id);
    }

    /**
     * {@inheritDoc}
     */
    public PeerManager getPeerManager() {
        return this.peerManager;
    }

    /**
     * {@inheritDoc}
     */
    public void setPeerManager(PeerManager peerManager) {
        this.peerManager = peerManager;
    }

}
