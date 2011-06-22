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
package org.petalslink.dsb.transport.soap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.petalslink.dsb.api.TransportService;
import org.petalslink.dsb.cxf.CXFHelper;
import org.petalslink.dsb.transport.api.Client;
import org.petalslink.dsb.transport.api.ClientFactory;
import org.petalslink.dsb.transport.api.Context;

/**
 * CXF/JAX-WS client factory
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class CXFClientFactory implements ClientFactory {

    private static Logger log = Logger.getLogger(CXFClientFactory.class.getName());

    private Map<String, Client> cache = new ConcurrentHashMap<String, Client>();

    public void start() {
    }

    public void stop() {
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Client getClient(Context context) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Getting a transport client for container '" + context + "'");
        }

        if (context.containerName == null) {

        }

        Client client = null;
        if (this.cache.get(context.containerName) == null) {
            if (log.isLoggable(Level.FINE)) {
                log.fine("Creating a new transport client for container '" + context + "'");
            }

            String address = "http://" + context.hostName + ":" + context.port + Constants.SUFFIX;

            if (log.isLoggable(Level.FINE)) {
                log.fine("Creating a CXF client to reach container " + context + " located at "
                        + address);
            }

            String baseURL = "http://" + context.hostName + ":" + context.port;
            TransportService transportServiceClient = CXFHelper.getClient(baseURL,
                    TransportService.class);
            client = new CXFClientImpl(transportServiceClient);
            this.cache.put(context.containerName, client);
        } else {
            client = this.cache.get(context.containerName);
        }

        return client;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.transport.api.ClientFactory#releaseClient(org.petalslink
     * .dsb.transport.api.Context, org.petalslink.dsb.transport.api.Client)
     */
    public void releaseClient(Context context, Client client) {
        // TODO Auto-generated method stub

    }

}
