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
package org.ow2.petals.messaging.framework.servicebus.service;

import java.util.Map;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.ow2.petals.messaging.framework.servicebus.Endpoint;

/**
 * This is the platform independant registry service definition
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@WebService
public interface Registry {

    /**
     * Register an endpoint in the registry
     * 
     * @param endpoint
     */
    @WebMethod
    boolean register(Endpoint endpoint);

    /**
     *Remove an endpoint from the registry
     */
    @WebMethod
    boolean remove(Endpoint endpoint);

    /**
     * Retrieve a list of endpoints from the query. It is up to the
     * implementation to deal with the query...
     * 
     * @param query
     * @return
     */
    @WebMethod
    Set<Endpoint> lookup(Map<String, String> query);
}
