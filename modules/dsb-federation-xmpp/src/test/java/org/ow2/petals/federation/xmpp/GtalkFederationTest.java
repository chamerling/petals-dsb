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

import org.petalslink.dsb.federation.core.api.ClientManager;
import org.petalslink.dsb.federation.core.api.FederationServer;
import org.petalslink.dsb.federation.core.api.Service;
import org.petalslink.dsb.federation.core.commons.ClientManagerImpl;
import org.petalslink.dsb.federation.core.server.DefaultPropagationStrategy;
import org.petalslink.dsb.federation.core.server.FederationServerImpl;
import org.petalslink.dsb.federation.core.server.RemoteCheckerServiceImpl;
import org.petalslink.dsb.federation.xmpp.commons.XMPPClientFactory;
import org.petalslink.dsb.federation.xmpp.commons.XMPPPeerChecker;
import org.petalslink.dsb.federation.xmpp.server.XMPPConnectionService;
import org.petalslink.dsb.federation.xmpp.server.XMPPInboundServiceImpl;
import org.petalslink.dsb.federation.xmpp.server.XMPPPeerService;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class GtalkFederationTest {

    public static void main(String[] args) {

        FederationServer server = new FederationServerImpl("GtalklFedServer01",
                "kitov.merlin@gmail.com");
        ClientManager clientManager = new ClientManagerImpl();
        clientManager.setClientFactory(new XMPPClientFactory());
        server.setClientManager(clientManager);
        server.setPropagationStrategy(new DefaultPropagationStrategy(server));
        Service xmppConnectionService = new XMPPConnectionService("kitov.merlin@gmail.com",
                "rockNroll");
        server.getServiceManager().addService(xmppConnectionService);
        Service xmppPresenceService = new XMPPPeerService(server.getPeerManager());
        server.getServiceManager().addService(xmppPresenceService);
        Service xmppServiceManager = new XMPPInboundServiceImpl(server);
        server.getServiceManager().addService(xmppServiceManager);
        Service remoteChecker = new RemoteCheckerServiceImpl(server, new XMPPPeerChecker(server));
        server.getServiceManager().addService(remoteChecker);
        server.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

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
