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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.petalslink.dsb.api.EndpointQuery;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.federation.core.Constants;
import org.petalslink.dsb.federation.core.api.FederationServer;
import org.petalslink.dsb.federation.core.server.DefaultFederationServerImpl;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class StandaloneTest {

    public static void main(String[] args) {

        System.out.println("SERVER");
        String fedURL = "http://localhost:7777/petals/federation";
        FederationServer server = new DefaultFederationServerImpl("Federation01", fedURL);
        server.start();

        System.out.println("CLIENT 01");

        // create the federation clients
        ClientNode node01 = new ClientNode("01",
                "http://localhost:7778/petals/federation/callback/"
                        + Constants.FEDERATION_SERVICE_NAME, fedURL);
        Set<ServiceEndpoint> endpoints = new HashSet<ServiceEndpoint>();
        ServiceEndpoint se = new ServiceEndpoint();
        se.setEndpointName("EP1");
        se.setContainerLocation("01");
        endpoints.add(se);
        node01.setEndpoints(endpoints);
        node01.start();
        node01.joinFederation();

        System.out.println("CLIENT 02");

        ClientNode node02 = new ClientNode("02",
                "http://localhost:7779/petals/federation/callback/"
                        + Constants.FEDERATION_SERVICE_NAME, fedURL);
        endpoints = new HashSet<ServiceEndpoint>();
        se = new ServiceEndpoint();
        se.setEndpointName("EP2");
        se.setContainerLocation("02");
        endpoints.add(se);
        node02.setEndpoints(endpoints);
        node02.start();
        node02.joinFederation();

        System.out.println("SLEEP");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // query...
        System.out.println("Client 1 query...");
        System.out.println(node01.lookupFederation(new EndpointQuery()));

        System.out.println("Client 2 query...");
        System.out.println(node02.lookupFederation(new EndpointQuery()));

        System.out.println("Client 1 invokes");
        MessageExchange exchange = new MessageExchange();
        exchange.setId("123");
        ServiceEndpoint endpoint = new ServiceEndpoint();
        endpoint.setContainerLocation("02");
        exchange.setEndpoint(endpoint);

        for (int i = 0; i < 10; i++) {
            System.out.println("INVOKE " + i);
            node01.invokeFederation(exchange);
        }

        // wait for exit...
        System.out.println("Press enter to stop all\n");
        try {
            System.in.read();
        } catch (IOException ex) {
            // ex.printStackTrace();
        }

        server.stop();
        System.exit(-1);
    }

}
