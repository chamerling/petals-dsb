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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.transport.cxf.Adapter;


/**
 * Message adapter between federation and JBI world.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class JBIFederationMessageAdapter {

    private JBIFederationMessageAdapter() {
    }

    /**
     * @param eps
     * @return
     */
    public static Set<ServiceEndpoint> transform(
            List<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> endpoints) {
        Set<ServiceEndpoint> result = new HashSet<ServiceEndpoint>();

        for (org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint serviceEndpoint : endpoints) {
            result.add(transform(serviceEndpoint));
        }
        return result;
    }

    public static ServiceEndpoint transform(
            org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint serviceEndpoint) {
        return Adapter.createServiceEndpoint(serviceEndpoint);
    }

    public static org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint transform(
            ServiceEndpoint serviceEndpoint) {
        return Adapter.createServiceEndpoint(serviceEndpoint);
    }

    /**
     * @param endpoints
     */
    public static Set<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> transform(
            Set<ServiceEndpoint> endpoints) {
        Set<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint> result = new HashSet<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint>();
        for (ServiceEndpoint serviceEndpoint : endpoints) {
            result.add(transform(serviceEndpoint));
        }
        return result;
    }

}
