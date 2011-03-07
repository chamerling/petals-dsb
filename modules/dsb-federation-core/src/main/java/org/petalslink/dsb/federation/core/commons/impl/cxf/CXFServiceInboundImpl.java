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
package org.petalslink.dsb.federation.core.commons.impl.cxf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.petalslink.dsb.federation.api.FederationManagementService;
import org.petalslink.dsb.federation.api.FederationService;
import org.petalslink.dsb.federation.core.Constants;
import org.petalslink.dsb.federation.core.api.FederationServer;
import org.petalslink.dsb.federation.core.api.Service;
import org.petalslink.dsb.federation.core.server.FederationManagementServiceImpl;
import org.petalslink.dsb.federation.core.server.FederationServiceImpl;

/**
 * Launch all the required web services
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class CXFServiceInboundImpl implements Service {

    private static Log logger = LogFactory.getLog(CXFServiceInboundImpl.class);

    private final FederationServer federationServer;

    private final FederationManagementService managementService;

    private Server managementServiceServer;

    private final FederationService federationService;

    private Server federationServiceServer;

    /**
	 * 
	 */
    public CXFServiceInboundImpl(final FederationServer federationServer) {
        this.federationServer = federationServer;

        // FIXME : get the list of service and instanciate them from somewhere
        // else...
        this.managementService = new FederationManagementServiceImpl(federationServer);
        this.federationService = new FederationServiceImpl(federationServer);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        // management
        JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
        String url = this.federationServer.getCallbackURL() + "/"
                + Constants.FEDERATION_MGMT_SERVICE_NAME;
        svrFactory.setAddress(this.federationServer.getCallbackURL());
        svrFactory.setServiceBean(this.managementService);
        this.managementServiceServer = svrFactory.create();

        if (logger.isInfoEnabled()) {
            logger.info("FederationManagementService service is started and available at " + url);
        }

        // federation
        svrFactory = new JaxWsServerFactoryBean();
        url = this.federationServer.getCallbackURL() + "/" + Constants.FEDERATION_SERVICE_NAME;
        svrFactory.setAddress(url);
        svrFactory.setServiceBean(this.federationService);
        this.federationServiceServer = svrFactory.create();

        if (logger.isInfoEnabled()) {
            logger.info("FederationService service is started and available at " + url);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        if (this.federationServiceServer != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Stopping FederationService service...");
            }
            this.federationServiceServer.stop();
        }

        if (this.managementServiceServer != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Stopping FederationManagementService service...");
            }
            this.managementServiceServer.stop();
        }

    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "CXFServiceInbound";
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
