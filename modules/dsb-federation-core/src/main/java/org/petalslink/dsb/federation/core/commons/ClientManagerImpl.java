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
package org.petalslink.dsb.federation.core.commons;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.federation.api.FederationManagementService;
import org.petalslink.dsb.federation.api.FederationService;
import org.petalslink.dsb.federation.core.api.ClientManager;

/**
 * The client manager is in charge of managing client instances to federation
 * clients
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class ClientManagerImpl implements ClientManager {

    Map<String, FederationService> clients;

    Map<String, FederationManagementService> managementClients;

    private ClientFactory clientFactory;

    private static Log logger = LogFactory.getLog(ClientManagerImpl.class);

    /**
	 * 
	 */
    public ClientManagerImpl() {
        this.clients = new ConcurrentHashMap<String, FederationService>(5);
        this.managementClients = new ConcurrentHashMap<String, FederationManagementService>(1);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized FederationService getClient(String callbackURL) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get client for federation client " + callbackURL);
        }
        FederationService result = this.clients.get(callbackURL);
        if (result == null) {
            result = this.getClientFactory().createClient(callbackURL);
            this.clients.put(callbackURL, result);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void removeClient(String callbackURL) {
        if (logger.isDebugEnabled()) {
            logger.debug("Removing client for federation client " + callbackURL);
        }
        if (callbackURL == null) {
            return;
        }
        this.clients.remove(callbackURL);
    }

    public ClientFactory getClientFactory() {
        return this.clientFactory;
    }

    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    /**
     * {@inheritDoc}
     */
    public FederationManagementService getManagementClient(String callbackURL) {
        if (logger.isDebugEnabled()) {
            logger.debug("Get management client for federation client " + callbackURL);
        }
        FederationManagementService result = this.managementClients.get(callbackURL);
        if (result == null) {
            result = this.getClientFactory().createManagementClient(callbackURL);
            this.managementClients.put(callbackURL, result);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void removeManagementClient(String callbackURL) {
        if (logger.isDebugEnabled()) {
            logger.debug("Removing management client for federation client " + callbackURL);
        }
        if (callbackURL == null) {
            return;
        }
        this.managementClients.remove(callbackURL);
    }

}
