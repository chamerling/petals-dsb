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
package org.petalslink.dsb.client.servicebinder;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.petalslink.dsb.ws.api.ServiceBinder;


/**
 * A factory for getting ServiceBinder clients
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class ClientFactory {

    private static ClientFactory INSTANCE;

    private final Map<String, ServiceBinder> clients;

    private ClientFactory() {
        this.clients = new HashMap<String, ServiceBinder>();
    }

    /**
     * @return the iNSTANCE
     */
    public static ClientFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientFactory();
        }
        return INSTANCE;
    }

    /**
     * 
     */
    public synchronized final ServiceBinder getServiceBinderClient(String address) {
        ServiceBinder result = this.clients.get(address);
        if (result == null) {
            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
            factory.setServiceClass(ServiceBinder.class);
            factory.setAddress(address);
            result = (ServiceBinder) factory.create();
            this.clients.put(address, result);
        }
        return result;
    }
}
