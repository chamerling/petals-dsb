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
package org.petalslink.dsb.transport.xmpp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.transport.api.Receiver;
import org.petalslink.dsb.transport.api.Server;


/**
 * This is not really a server... It is just a facade to discuss with the XMPP
 * server...
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = Server.class) })
public class XMPPServerImpl implements Server {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "receiver", signature = Receiver.class)
    private Receiver receiver;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configuration;

    private ExecutorService executorService;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
        this.executorService = Executors.newFixedThreadPool(5);
        // the server needs to be started when petals is started, not by
        // Fractal...
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
        this.stopServer();
    }

    /**
     * {@inheritDoc}
     */
    public void startServer() {
        // means that we connect to the Jabber server and that we register a
        // listener for the local Petals container
        XMPPConnectionManager xmppConnectionManager = XMPPConnectionManager.getInstance();
        boolean connected = xmppConnectionManager.connect();
        if (connected) {
            // TODO = get the user from the configuration or topology
            this.log.info("Connected to Jabber server, let's login now...");

            // let say that the login is the container name...
            final String user = this.configuration.getContainerConfiguration().getUser()
                    + "@gmail.com";
            final String password = this.configuration.getContainerConfiguration().getPassword();

            if (this.log.isDebugEnabled()) {
                this.log
                        .debug("Login with user = '" + user + "' and password = '" + password + "'");
            }

            xmppConnectionManager.login(user, password);

            // say that we are here, not so useful...
            xmppConnectionManager.setStatus(XMPPConnectionManager.AVAILABLE_STATUS, "Petals ESB '"
                    + this.configuration.getContainerConfiguration().getName()
                    + "' ready to process messages!");

            // add listener for incoming messages...
            xmppConnectionManager.addPacketListener(new XMPPChatMessageListener(this.receiver,
                    this.executorService, this.log), new MessageTypeFilter(Message.Type.chat));

            // add listener for remote contacts...
            if (this.log.isDebugEnabled()) {
                this.displayContacts();
            }

            // add a listener for contacts status
            xmppConnectionManager.getConnection().getRoster().addRosterListener(
                    new XMPPRosterListener(this.log));

        } else {
            this.log.warning("Not connected to jabber server!!!");
        }
    }

    /**
     * 
     */
    private void displayContacts() {
        XMPPConnection connection = XMPPConnectionManager.getInstance().getConnection();
        if ((connection != null) && connection.isConnected()) {
            for (RosterEntry entry : connection.getRoster().getEntries()) {
                this.log.info("Available contact : Name = " + entry.getName() + " , User = "
                        + entry.getUser() + " , Status = " + entry.getStatus());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stopServer() {
        if (this.executorService != null) {
            this.executorService.shutdownNow();
        }

        XMPPConnectionManager.getInstance().setStatus(XMPPConnectionManager.UNAVAILABLE_STATUS,
                "Bye bye!!!");
        XMPPConnectionManager.getInstance().disconnect();
    }

}
