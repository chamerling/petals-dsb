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
package org.petalslink.dsb.federation.xmpp.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.federation.api.FederationManagementService;
import org.petalslink.dsb.federation.api.FederationService;
import org.petalslink.dsb.federation.core.api.FederationServer;
import org.petalslink.dsb.federation.core.api.Service;
import org.petalslink.dsb.federation.core.server.FederationManagementServiceImpl;
import org.petalslink.dsb.federation.core.server.FederationServiceImpl;
import org.petalslink.dsb.federation.xmpp.commons.OperationFilter;
import org.petalslink.dsb.federation.xmpp.commons.XMPPConnectionManager;

/**
 * The XMPP Service manager is used to receive XMPP messages from other peers of
 * the federation.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class XMPPInboundServiceImpl implements Service {

    private ExecutorService executorService;

    private final FederationManagementService managementService;

    private final FederationService federationService;

    private final FederationServer federationServer;

    private static Log logger = LogFactory.getLog(XMPPInboundServiceImpl.class);

    /**
     * 
     */
    public XMPPInboundServiceImpl(FederationServer federationServer) {
        this.federationServer = federationServer;
        this.managementService = new FederationManagementServiceImpl(this.federationServer);
        this.federationService = new FederationServiceImpl(this.federationServer);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        this.executorService = Executors.newFixedThreadPool(5);

        // connect to the XMPP server
        if (logger.isInfoEnabled()) {
            logger.info("Connecting to XMPP server");
        }
        XMPPConnectionManager xmppConnectionManager = XMPPConnectionManager.getInstance();

        // add listeners to expose the federation services
        xmppConnectionManager.addPacketListener(new FederationServiceListener(
                this.federationService, this.executorService), new OperationFilter(
                FederationService.class));

        xmppConnectionManager.addPacketListener(new FederationManagementServiceListener(
                this.managementService, this.executorService), new OperationFilter(
                FederationManagementService.class));
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        if (logger.isInfoEnabled()) {
            logger.info("Stopping executors");
        }
        if (this.executorService != null) {
            this.executorService.shutdownNow();
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "XMPPServiceInbound";
    }

    /**
     * {@inheritDoc}
     */
    public TYPE getType() {
        return TYPE.INBOUND;
    }

    /**
     * {@inheritDoc}
     */
    public int getPriority() {
        return 0;
    }

}
