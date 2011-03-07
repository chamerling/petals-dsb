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
package org.ow2.petals.federation.xmpp;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.petalslink.dsb.api.EndpointQuery;
import org.petalslink.dsb.api.ServiceEndpoint;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class ClientNode03 {
    public static void main(String[] args) {
        XMPPClient client = new XMPPClient("chamerling.petalslink@gmail.com", "rockNroll", null,
                "kitov.merlin@gmail.com");
        Set<ServiceEndpoint> endpoints = new HashSet<ServiceEndpoint>();
        ServiceEndpoint se = new ServiceEndpoint();
        se.setEndpointName("EP1");
        se.setContainerLocation("03");
        endpoints.add(se);
        se = new ServiceEndpoint();
        se.setEndpointName("EP2");
        se.setContainerLocation("03");
        endpoints.add(se);
        se = new ServiceEndpoint();
        se.setEndpointName("EP3");
        se.setContainerLocation("03");
        endpoints.add(se);
        client.setEndpoints(endpoints);
        client.start();

        System.out.println("JOIN");
        client.joinFederation();

        // System.out.println(client.lookupFederation(new EndpointQuery()));

        System.out.println("LOOKUP!!!");
        EndpointQuery query = new EndpointQuery();
        query.setInterface(QName.valueOf("{http://petals.ow2.org}SOAPPROXYInterface"));
        query.setLinkType("standard");
        Set<ServiceEndpoint> result = client.lookupFederation(query);
        System.out.println("SIZE = " + result.size());
        System.out.println(result);

        System.out.println("Client : Press enter to stop all\n");
        try {
            System.in.read();
        } catch (IOException ex) {
            // ex.printStackTrace();
        }

        System.out.println("LEAVE");
        client.leaveFederation();

    }
}
