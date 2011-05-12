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

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;

/**
 * This is just a ping service used by remote containers to check if the local
 * container is able to receive messages. This is used by client and server to
 * bypass basic JMX connection used in Petals ESB.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@WebService
public interface RemoteCheckerService {

    /**
     * This is just a simple operation called by remote containers to see if I am alive...
     * 
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    boolean ping() throws PEtALSWebServiceException;
}
