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
package org.petalslink.dsb.kernel.messaging.router.modules;

import java.util.Map;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.jbi.component.context.ComponentContext;
import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.exchange.MessageExchange;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.jbi.messaging.routing.module.SenderModule;
import org.ow2.petals.transport.Transporter;
import org.ow2.petals.transport.util.TransportSendContext;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.DSBConfigurationService;


/**
 * This module will change the context if the container to reach is a remote
 * one. It will set the transport to the one defined in the configuration file.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = @Interface(name = "service", signature = SenderModule.class))
public class UpdateRemoteTansportModule implements SenderModule {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "dsbconfiguration", signature = DSBConfigurationService.class)
    private DSBConfigurationService dsbConfigurationService;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public void electEndpoints(Map<ServiceEndpoint, TransportSendContext> electedEndpoints,
            ComponentContext sourceComponentContext, MessageExchange exchange)
            throws RoutingException {
        if ((this.dsbConfigurationService.getRemoteTransport() == null)
                || (this.dsbConfigurationService.getRemoteTransport().length() == 0)) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Remote transport is null or empty, skip this module");
            }
            return;
        }
        for (TransportSendContext transportSendContext : electedEndpoints.values()) {
            // if the transport is the default one, replace by the one specified in confiration file...
            if (transportSendContext.transport.equals(Transporter.TCP_FRACTAL_TRANSPORTER)) {
                transportSendContext.transport = this.dsbConfigurationService.getRemoteTransport();
            }
        }
    }
}
