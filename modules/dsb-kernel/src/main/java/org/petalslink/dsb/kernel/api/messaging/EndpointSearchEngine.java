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
package org.petalslink.dsb.kernel.api.messaging;

import java.util.List;

import javax.xml.namespace.QName;

import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;

/**
 * TODO : The search engine must not be JBI dependant!
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface EndpointSearchEngine {

    /**
     * @param givenEndpoint
     * @param linkType
     * @return
     * @throws RoutingException
     */
    ServiceEndpoint getTargetedEndpointFromGivenEndpoint(ServiceEndpoint givenEndpoint,
            String linkType) throws SearchException;

    /**
     * @param givenServiceName
     * @param strategy
     * @param linkType
     * @return
     * @throws RoutingException
     */
    List<ServiceEndpoint> getTargetedEndpointFromGivenServiceName(QName givenServiceName,
            String strategy, String linkType) throws SearchException;

    /**
     * @param givenInterfaceName
     * @param strategy
     * @param linkType
     * @return
     * @throws RoutingException
     */
    List<ServiceEndpoint> getTargetedEndpointFromGivenInterfaceName(QName givenInterfaceName,
            String strategy, String linkType) throws SearchException;

    /**
     * Get all the endpoints
     * 
     * @return
     */
    List<ServiceEndpoint> getAll();

}
