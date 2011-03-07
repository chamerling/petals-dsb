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
package org.petalslink.dsb.ws.api;

import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * The Service Information Service is in charge of returning the information
 * about Web services bound to the DSB.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@WebService
public interface ServiceInformation {

    /**
     * Get the list of Web service which are exposed by the DSB
     * 
     * @return
     */
    @WebMethod
    Set<String> getExposedWebServices();

    /**
     * Get the list of Web service which are bound to the DSB
     * 
     * @return
     */
    @WebMethod
    Set<String> getConsumedWebServices();

    /**
     * Get the list of REST services which are exposed by the DSB
     * 
     * @return
     */
    @WebMethod
    Set<String> getExposedRESTServices();

    /**
     * Get the list of REST services which are bound to the DSB
     * 
     * @return
     */
    @WebMethod
    Set<String> getConsumedRESTServices();

}
