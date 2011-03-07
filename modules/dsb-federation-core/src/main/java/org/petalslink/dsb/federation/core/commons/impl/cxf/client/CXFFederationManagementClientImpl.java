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
package org.petalslink.dsb.federation.core.commons.impl.cxf.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.petalslink.dsb.federation.api.FederationException;
import org.petalslink.dsb.federation.api.FederationManagementService;

/**
 * A CXF client to the federation
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class CXFFederationManagementClientImpl implements FederationManagementService {

    private final FederationManagementService client;

    private static Log logger = LogFactory.getLog(CXFFederationManagementClientImpl.class);

    /**
	 * 
	 */
    public CXFFederationManagementClientImpl(String address) {
        if (logger.isInfoEnabled()) {
            logger.info("Creating federation management service client for service at " + address);
        }
        final JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(FederationManagementService.class);
        factory.setAddress(address);
        this.client = (FederationManagementService) factory.create();
    }

    /**
     * {@inheritDoc}
     */
    public void join(String clientId, String callbackURL) throws FederationException {
        if (logger.isDebugEnabled()) {
            logger.debug("Call to join federation");
        }
        this.client.join(clientId, callbackURL);
    }

    /**
     * {@inheritDoc}
     */
    public void leave(String clientId) throws FederationException {
        if (logger.isDebugEnabled()) {
            logger.debug("Call to leave federation");
        }
        this.client.leave(clientId);
    }

}
