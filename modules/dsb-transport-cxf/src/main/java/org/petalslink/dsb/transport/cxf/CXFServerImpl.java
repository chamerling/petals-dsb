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
package org.petalslink.dsb.transport.cxf;

import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
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
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;
import org.petalslink.dsb.api.TransportService;
import org.petalslink.dsb.transport.api.Receiver;
import org.petalslink.dsb.transport.api.Server;

/**
 * A CXF/JAX-WS implementation of the {@link Server}, receive messages and send
 * them to the {@link Receiver} which is the core transport layer.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = Server.class) })
public class CXFServerImpl implements Server {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "receiver", signature = Receiver.class)
    private Receiver receiver;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configuration;

    /**
     * 
     */
    private org.apache.cxf.endpoint.Server server;

    @LifeCycle(on = LifeCycleType.START)
    public void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
        // not started due to lifecycleexception (dependencies)
        // this.startServer();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    public void stop() {
        this.log.debug("Stopping...");
        this.stopServer();
    }

    /**
     * {@inheritDoc}
     */
    @LifeCycleListener(phase = Phase.START, priority=101)
    public void startServer() {
        JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
        TransportService service = new TransportServiceImpl(this.receiver, this.log);
        ContainerConfiguration localConfiguration = this.configuration.getContainerConfiguration();
        String address = "http://" + localConfiguration.getHost() + ":"
                + localConfiguration.getWebservicePort() + Constants.SUFFIX;
        svrFactory.setAddress(address);
        svrFactory.setServiceBean(service);
        this.server = svrFactory.create();
        this.log.info("The WebService transporter is ready to receive messages at " + address);
    }

    /**
     * {@inheritDoc}
     */
    public void stopServer() {
        if (this.server != null) {
            this.server.stop();
        }
    }

    //

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setLog(LoggingUtil log) {
        this.log = log;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public void setConfiguration(ConfigurationService configuration) {
        this.configuration = configuration;
    }
}
