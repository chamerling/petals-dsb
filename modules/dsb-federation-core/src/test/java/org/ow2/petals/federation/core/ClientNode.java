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
package org.ow2.petals.federation.core;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.api.EndpointQuery;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.federation.api.FederationException;
import org.petalslink.dsb.federation.core.Constants;
import org.petalslink.dsb.federation.core.api.ClientManager;
import org.petalslink.dsb.federation.core.client.impl.cxf.CXFServiceManagerImpl;
import org.petalslink.dsb.federation.core.commons.ClientManagerImpl;
import org.petalslink.dsb.federation.core.commons.impl.cxf.CXFClientFactory;

/**
 * Fake client node...
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class ClientNode {

    private static Log logger = LogFactory.getLog(ClientNode.class);

    String name;

    String baseAddress;

    private Set<ServiceEndpoint> endpoints;

    private final String federationURL;

    org.petalslink.dsb.federation.core.client.FederationClientImpl client;

    /**
	 * 
	 */
    public ClientNode(String name, String baseAddress, String federationURL) {
        this.name = name;
        this.baseAddress = baseAddress;
        this.federationURL = federationURL;
        this.endpoints = new HashSet<ServiceEndpoint>(0);

        this.client = new org.petalslink.dsb.federation.core.client.FederationClientImpl(name,
                baseAddress, federationURL + "/" + Constants.FEDERATION_SERVICE_NAME, federationURL
                        + "/" + Constants.FEDERATION_MGMT_SERVICE_NAME);
    }

    /**
 * 
 */
    public void start() {
        System.out.println("Set things...");
        this.client
                .setServiceImplementation(new org.petalslink.dsb.federation.api.client.FederationService() {
                    public Set<ServiceEndpoint> lookup(EndpointQuery query)
                            throws FederationException {
                        System.out.println(ClientNode.this.name
                                + "  : Got a lookup call in service");
                        return ClientNode.this.endpoints;
                    }

                    public void invoke(MessageExchange message) throws FederationException {
                        System.out.println(ClientNode.this.name
                                + "  : Got an invoke call in service");
                    }
                });

        ClientManager clientManager = new ClientManagerImpl();
        clientManager.setClientFactory(new CXFClientFactory());
        this.client.setClientManager(clientManager);
        this.client.setServiceManager(new CXFServiceManagerImpl());

        System.out.println("Start client");
        this.client.start();
    }

    /**
	 * 
	 */
    public void joinFederation() {
        try {
            this.client.join();
        } catch (FederationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @return
     * @return
     * 
     */
    public Set<ServiceEndpoint> lookupFederation(EndpointQuery query) {
        try {
            return this.client.lookup(query);
        } catch (FederationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public void invokeFederation(MessageExchange exchange) {
        try {
            this.client.invoke(exchange);
        } catch (FederationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param endpoints
     *            the endpoints to set
     */
    public void setEndpoints(Set<ServiceEndpoint> endpoints) {
        this.endpoints = endpoints;
    }

    /**
	 * 
	 */
    public void leaveFederation() {
        try {
            this.client.leave();
        } catch (FederationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
