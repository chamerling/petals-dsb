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
import org.petalslink.dsb.federation.api.FederationService;
import org.petalslink.dsb.federation.core.api.FederationServer;

/**
 * This is the service exposed by the federation server and which is accessed by
 * clients. IT is generally wrapped by protocol dependent service ie a web
 * service implementation will delegate calls to the current class instance...
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class FederationServiceImpl implements FederationService {

    private final FederationServer federationServer;

    private static Log logger = LogFactory.getLog(FederationServiceImpl.class);

    /**
	 * 
	 */
    public FederationServiceImpl(final FederationServer federationServer) {
        this.federationServer = federationServer;
    }

    /**
     * {@inheritDoc}
     */
    public void invoke(MessageExchange message, String clientId, String id)
            throws FederationException {
        if (logger.isInfoEnabled()) {
            logger.info("Receive 'invoke' from clientID = '" + clientId + "'");
        }
        this.federationServer.invoke(message, clientId, id);
    }

    /**
     * {@inheritDoc}
     */
    public void lookup(EndpointQuery query, String clientId, String id) throws FederationException {
        if (logger.isInfoEnabled()) {
            logger.info("Receive 'lookup' from clientID = '" + clientId + "'");
        }
        this.federationServer.lookup(query, clientId, id);
    }

    /**
     * {@inheritDoc}
     */
    public void lookupReply(Set<ServiceEndpoint> endpoints, String clientId, String id)
            throws FederationException {
        if (logger.isInfoEnabled()) {
            logger.info("Receive 'lookupReply' from clientID = '" + clientId + "'");
        }
        this.federationServer.lookupReply(endpoints, clientId, id);
    }

}
