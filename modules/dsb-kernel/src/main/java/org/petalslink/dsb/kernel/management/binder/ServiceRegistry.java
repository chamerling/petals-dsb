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
package org.petalslink.dsb.kernel.management.binder;

import java.util.Set;

import org.ow2.petals.registry.api.Endpoint;

/**
 * A registry used to manage services which have been bound.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface ServiceRegistry {

    /**
     * Add a service to the registry
     * 
     * @param protocol
     * @param url
     * @param endpoint
     */
    void addService(String protocol, String url, Endpoint endpoint);

    /**
     * 
     * @param protocol
     * @param url
     */
    void removeService(String protocol, String url);

    /**
     * Get the list of services URL which have been registered for the given
     * protocol
     * 
     * @param protocol
     * @return
     */
    Set<String> getURLs(String protocol);

}
