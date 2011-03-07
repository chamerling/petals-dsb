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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.federation.api.FederationException;
import org.petalslink.dsb.federation.api.FederationManagementService;
import org.petalslink.dsb.federation.core.api.FederationServer;

/**
 * The management service implementation for the server. Creates and stores
 * references to the clients.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class FederationManagementServiceImpl implements FederationManagementService {

    private final FederationServer federationServer;

    private static Log logger = LogFactory.getLog(FederationManagementServiceImpl.class);

    /**
	 * 
	 */
    public FederationManagementServiceImpl(final FederationServer federationServer) {
        this.federationServer = federationServer;
    }

    /**
     * {@inheritDoc}
     */
    public void join(String clientId, String callbackURL) throws FederationException {
        if (logger.isInfoEnabled()) {
            logger.info("Federation client '" + clientId + "' joining federation with callback '"
                    + callbackURL + "'");
        }
        this.federationServer.addClient(clientId, callbackURL);
    }

    /**
     * {@inheritDoc}
     */
    public void leave(String clientId) throws FederationException {
        if (logger.isInfoEnabled()) {
            logger.info("Federation client '" + clientId + "' leaving federation");
        }
        this.federationServer.removeClient(clientId);
    }

}
