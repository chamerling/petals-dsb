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

import java.util.HashMap;
import java.util.Map;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.kernel.configuration.ContainerConfiguration;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.transport.api.Client;
import org.petalslink.dsb.transport.api.ClientFactory;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = ClientFactory.class) })
public class XMPPClientFactory implements ClientFactory {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private Map<String, Client> clients;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
        this.clients = new HashMap<String, Client>();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public Client getClient(String container) {
        // for now the container name is also the Google Talk Login... Here a
        // client to the container is in fact a message to a jabber ID...
        Client client = null;
        ContainerConfiguration cc = this.configurationService.getContainerConfiguration(container);
        if (cc != null) {
            String jid = cc.getUser() + "@gmail.com";
            client = this.clients.get(jid);
            if (client == null) {
                client = new XMPPClientImpl(jid);
                this.clients.put(jid, client);
            }
        } else {
            this.log.warning("Can not find a container with name '" + container + "'");
        }
        return client;
    }

    /**
     * {@inheritDoc}
     */
    public void releaseClient(String container, Client client) {

    }

}
