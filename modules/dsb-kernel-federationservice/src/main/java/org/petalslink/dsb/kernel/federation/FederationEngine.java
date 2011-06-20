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
package org.petalslink.dsb.kernel.federation;

import org.petalslink.dsb.federation.core.client.FederationClientWithCallback;

/**
 * The federation engine is the communication channel between the current DSB
 * node and the federation server ie to invoke the server with the federation
 * client instance.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface FederationEngine {

    /**
     * The federation feature is activated or not???
     * 
     * @return
     */
    boolean isActivated();

    /**
     * Get the client to call the federation server.
     * 
     * @return
     */
    FederationClientWithCallback getFederationClient();

    /**
     * Connect to the federation (ie call a management operation on the server)
     */
    void connectFederation();

    /**
     * Disconnect from the federation
     */
    void disconnectFromFederation();

}
