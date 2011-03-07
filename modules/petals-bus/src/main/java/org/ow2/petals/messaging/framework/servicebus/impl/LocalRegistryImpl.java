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
package org.ow2.petals.messaging.framework.servicebus.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.ow2.petals.messaging.framework.lifecycle.NullLifeCycle;
import org.ow2.petals.messaging.framework.servicebus.Endpoint;
import org.ow2.petals.messaging.framework.servicebus.service.Registry;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class LocalRegistryImpl extends NullLifeCycle implements Registry {

    private static final String ENDPOINT_NAME = "endpoint-name";

    private static final String INTERFACE_NAME = "interface-name";

    Map<String, Endpoint> map;

    /**
     * 
     */
    public LocalRegistryImpl() {
        this.map = new ConcurrentHashMap<String, Endpoint>();
    }

    /**
     * {@inheritDoc}
     */
    public Set<Endpoint> lookup(Map<String, String> query) {
        // TODO : Add a list of endpoint resolvers which are going through the
        // registry. With this feature, you can add resolvers by configuration
        // for specific needs...vVc =ççgùkvuù,n h
        Set<Endpoint> result = new HashSet<Endpoint>();
        String key = query.get(ENDPOINT_NAME);
        if (key != null) {
            result.add(this.map.get(key));
        }

        if (query.get(INTERFACE_NAME) != null) {
            // lookup interfaces
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean register(Endpoint endpoint) {
        this.map.put(endpoint.getName().toString(), endpoint);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove(Endpoint endpoint) {
        this.map.remove(endpoint.getName().toString());
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public QName getName() {
        return QName.valueOf(Registry.class.getCanonicalName());
    }

}
