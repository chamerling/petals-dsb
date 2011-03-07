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
package org.petalslink.dsb.federation.xmppwebserver;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
public class FederationXMPPBotServlet extends HttpServlet {

    private FederationServer server = null;

    /**
     * 
     */
    private static final long serialVersionUID = -8723698373945722435L;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws ServletException {
        this.log("Intilializing connection to server");
        this.startServer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        this.stopServer();
    }

    private void startServer() {
        System.out.println("Starting server");
        String login = this.getServletContext().getInitParameter("federationLogin");
        String password = this.getServletContext().getInitParameter("federationPassword");
        System.out.println("Connecting with " + login + " / " + password);
        this.server = new FederationServerImpl("GtalklFedServer01", login);
        ClientManager clientManager = new ClientManagerImpl();
        clientManager.setClientFactory(new XMPPClientFactory());
        this.server.setClientManager(clientManager);
        this.server.setPropagationStrategy(new DefaultPropagationStrategy(this.server));
        Service xmppConnectionService = new XMPPConnectionService(login, password);
        this.server.getServiceManager().addService(xmppConnectionService);
        Service xmppPresenceService = new XMPPPeerService(this.server.getPeerManager());
        this.server.getServiceManager().addService(xmppPresenceService);
        Service xmppInboundService = new XMPPInboundServiceImpl(this.server);
        this.server.getServiceManager().addService(xmppInboundService);
        Service xmppChecker = new RemoteCheckerServiceImpl(this.server, new XMPPPeerChecker(
                this.server));
        this.server.getServiceManager().addService(xmppChecker);
        this.server.start();
    }

    private void stopServer() {
        System.out.println("Stopping server");
        if (this.server != null) {
            this.server.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        OutputStream os = resp.getOutputStream();
        StringBuffer sb = new StringBuffer();
        sb.append("<html><body><h1>Federation Server</h1>");
        sb.append("<h2>Informations</h2>");
        sb.append("Started at " + this.server.getJoinDate());
        sb.append("<br><br>");
        sb.append("Callback URL : " + this.server.getCallbackURL());
        sb.append("<br><br>");
        sb.append("<h2>Current Clients</h2>");
        for (org.petalslink.dsb.federation.core.api.FederationClient client : this.server.getClients()) {
            sb.append(" - " + client.getName() + ", joined at " + client.getJoinDate());
        }
        sb.append("</body></html>");
        os.write(sb.toString().getBytes());
        os.flush();
    }
}
