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

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.petalslink.dsb.api.EndpointQuery;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.federation.api.FederationException;
import org.petalslink.dsb.federation.api.FederationService;

/**
 * TODO = Set a timeout...
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class CXFFederationClientImpl implements FederationService {

	// CXF stub
    private final FederationService client;

	private static Log logger = LogFactory
			.getLog(CXFFederationClientImpl.class);

	/**
	 * 
	 */
	public CXFFederationClientImpl(String address) {
		if (logger.isInfoEnabled()) {
            logger.info("Creating service client for service at " + address);
		}
		final JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(FederationService.class);
        factory.setAddress(address);
        this.client = (FederationService) factory.create();
	}

	/**
	 * {@inheritDoc}
	 */
    public void invoke(MessageExchange message, String clientId, String id)
			throws FederationException {
		if (logger.isDebugEnabled()) {
			logger.debug("Invoking operation 'invoke'");
		}
        this.client.invoke(message, clientId, id);
	}

    /**
     * {@inheritDoc}
     */
    public void lookup(EndpointQuery query, String clientId, String id) throws FederationException {
        if (logger.isDebugEnabled()) {
            logger.debug("Invoking operation 'lookup'");
        }
        this.client.lookup(query, clientId, id);
    }

    /**
     * {@inheritDoc}
     */
    public void lookupReply(Set<ServiceEndpoint> endpoints, String clientId, String id)
            throws FederationException {
        if (logger.isDebugEnabled()) {
            logger.debug("Invoking operation 'lookupreply'");
        }
        this.client.lookupReply(endpoints, clientId, id);

    }

}
