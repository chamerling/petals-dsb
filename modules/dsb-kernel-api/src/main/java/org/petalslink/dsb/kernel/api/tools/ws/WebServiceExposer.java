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
package org.petalslink.dsb.kernel.api.tools.ws;

import java.util.Set;

/**
 * Expose services as web services
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface WebServiceExposer {

    static final String DEFAULT_PREFIX = "petals/ws";

    static final int DEFAULT_PORT = 9001;

    /**
     * Expose a set of classes as web services. If the
     * {@link WebServiceInformationBean} URL is defined, the service must be
     * exposed at this address. Else let the implementation choose the address
     * of the service.
     * 
     * @param set
     * @return the set of really exposed services with all the updated
     *         information
     */
    Set<WebServiceInformationBean> expose(Set<WebServiceInformationBean> set)
            throws WebServiceException;

    /**
     * Remove an exposed service
     * 
     * @param name
     * @throws WebServiceException
     */
    void remove(String name) throws WebServiceException;

}
