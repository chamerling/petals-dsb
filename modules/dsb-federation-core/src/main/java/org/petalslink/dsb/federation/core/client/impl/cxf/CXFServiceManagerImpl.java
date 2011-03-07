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
package org.petalslink.dsb.federation.core.client.impl.cxf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.petalslink.dsb.federation.api.FederationService;
import org.petalslink.dsb.federation.core.client.ServiceManager;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class CXFServiceManagerImpl implements ServiceManager {

    private static Log logger = LogFactory.getLog(CXFServiceManagerImpl.class);

    private org.apache.cxf.endpoint.Server server;

    /**
     * {@inheritDoc}
     */
    public void expose(String url, FederationService federationService) {
        JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
        svrFactory.setAddress(url);
        svrFactory.setServiceBean(federationService);
        this.server = svrFactory.create();

        if (logger.isInfoEnabled()) {
            logger.info("Client Service callback is started and available at " + url);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        if (this.server != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Stopping service callback");
            }
            this.server.stop();
        }
    }
}
