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
package org.petalslink.dsb.federation.xmpp.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.federation.api.FederationService;
import org.petalslink.dsb.federation.core.client.ServiceManager;
import org.petalslink.dsb.federation.xmpp.commons.OperationFilter;
import org.petalslink.dsb.federation.xmpp.commons.XMPPConnectionManager;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class XMPPServiceManagerImpl implements ServiceManager {

    private final String user;

    private final String password;

    private static Log logger = LogFactory.getLog(XMPPServiceManagerImpl.class);

    /**
     * 
     */
    public XMPPServiceManagerImpl(String user, String password) {
        this.user = user;
        this.password = password;
    }

    /**
     * {@inheritDoc}
     */
    public void expose(String baseURL, FederationService federationService) {
        // add the listeners to be able to receive messages...
        XMPPConnectionManager manager = XMPPConnectionManager.getInstance();

        boolean connected = manager.connect();
        if (connected) {
            manager.login(this.user, this.password);
            manager.addPacketListener(new FederationClientListener(federationService),
                    new OperationFilter(FederationService.class));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        if (logger.isDebugEnabled()) {
            logger.debug("Service manager is stopping");
        }
        XMPPConnectionManager manager = XMPPConnectionManager.getInstance();
        manager.disconnect();
    }

}
