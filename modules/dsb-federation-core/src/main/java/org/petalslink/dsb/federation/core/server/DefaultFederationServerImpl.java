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

import org.petalslink.dsb.federation.core.api.ClientManager;
import org.petalslink.dsb.federation.core.commons.ClientManagerImpl;
import org.petalslink.dsb.federation.core.commons.impl.cxf.CXFClientFactory;
import org.petalslink.dsb.federation.core.commons.impl.cxf.CXFServiceInboundImpl;

/**
 * A default serveur implementation with a sequential propagation strategy and
 * based on CXF for communication.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class DefaultFederationServerImpl extends FederationServerImpl {

    /**
	 * 
	 */
    public DefaultFederationServerImpl(String name, String callbackURL) {
        super(name, callbackURL);
        ClientManager clientManager = new ClientManagerImpl();
        clientManager.setClientFactory(new CXFClientFactory());
        this.setClientManager(clientManager);
        this.setPropagationStrategy(new DefaultPropagationStrategy(this));
        // add services...
        this.getServiceManager().addService(new CXFServiceInboundImpl(this));
    }

}
