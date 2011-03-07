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
package org.petalslink.dsb.kernel.communication;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;
import org.petalslink.dsb.ws.api.RemoteCheckerService;



/**
 * TODO : Get the Webservices names
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class RemoteCheckerClient implements RemoteCheckerService {

    private final String remoteURL;

    private RemoteCheckerService remoteCheckerClient;
    

    /**
     * @param remoteURL
     */
    RemoteCheckerClient(String remoteURL) {
        this.remoteURL = remoteURL;
    }

    /**
     * 
     */
    public void init() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setAddress(this.remoteURL);
        factoryBean.setServiceClass(RemoteCheckerService.class);
        this.remoteCheckerClient = (RemoteCheckerService) factoryBean.create();

        // TODO set the connection timeout!
    }

    /**
     * {@inheritDoc}
     */
    public boolean ping() throws PEtALSWebServiceException {
        return this.remoteCheckerClient.ping();
    }
}
