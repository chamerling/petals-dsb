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
package org.petalslink.dsb.federation.core.api;

import org.petalslink.dsb.federation.api.FederationManagementService;
import org.petalslink.dsb.federation.api.FederationService;
import org.petalslink.dsb.federation.core.commons.ClientFactory;

/**
 * The client manager used to manage server to client communication.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface ClientManager {

    /**
     * Get a client to call the federation client
     * 
     * @param callbackURL
     * @return
     */
    FederationService getClient(String callbackURL);

    /**
     * Get a client to call the management service
     * 
     * @param callbackURL
     * @return
     */
    FederationManagementService getManagementClient(String callbackURL);

    /**
     * Remove a client which is no more used
     * 
     * @param callbackURL
     */
    void removeClient(String callbackURL);

    void removeManagementClient(String callbackURL);

    ClientFactory getClientFactory();

    void setClientFactory(ClientFactory clientFactory);

}
