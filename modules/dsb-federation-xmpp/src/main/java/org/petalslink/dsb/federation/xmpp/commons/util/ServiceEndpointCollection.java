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
package org.petalslink.dsb.federation.xmpp.commons.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.petalslink.dsb.api.ServiceEndpoint;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "org.petalslink.dsb.api", name = "ServiceEndpointCollection")
public class ServiceEndpointCollection {

    @XmlElement
    ServiceEndpoint[] endpoints;

    /**
     * 
     */
    public ServiceEndpointCollection() {
    }

    public ServiceEndpoint[] getEndpoints() {
        return this.endpoints;
    }

    public void setEndpoints(ServiceEndpoint[] endpoints) {
        this.endpoints = endpoints;
    }

}
