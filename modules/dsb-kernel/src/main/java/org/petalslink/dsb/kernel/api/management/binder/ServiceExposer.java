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
package org.petalslink.dsb.kernel.api.management.binder;

import org.ow2.petals.kernel.api.service.ServiceEndpoint;

/**
 * Expose a service endpoint. One implementation per protocol!
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface ServiceExposer {

    /**
     * 
     */
    String getProtocol();

    /**
     * Bind the service. Note that the endpoint can have properties, it will be
     * nice to inject these properties since they can be used by remote pairs.
     * 
     * @param endpoint
     */
    void expose(ServiceEndpoint endpoint) throws BinderException;

}
