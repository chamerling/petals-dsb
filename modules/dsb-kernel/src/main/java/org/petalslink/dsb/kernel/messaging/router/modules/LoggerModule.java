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

import java.util.Iterator;
import java.util.Map;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.jbi.component.context.ComponentContext;
import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.exchange.MessageExchangeWrapper;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.jbi.messaging.routing.module.SenderModule;
import org.ow2.petals.transport.util.TransportSendContext;
import org.ow2.petals.util.oldies.LoggingUtil;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = SenderModule.class) })
public class LoggerModule implements SenderModule {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

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
            ComponentContext sourceComponentContext, MessageExchangeWrapper exchange)
            throws RoutingException {
        this.log.debug("******************************* Router logs *******************************");

        if (this.log.isDebugEnabled()) {
            
            log.debug("- Component context : ");
            log.debug(sourceComponentContext.getAddress()); 
            log.debug("");

            log.debug("- Consumer endpoint : ");
            if (exchange.getConsumerEndpoint() != null) {
                log.debug("  - Endpoint name : " + exchange.getConsumerEndpoint().getEndpointName());
                log.debug("  - Service name : " + exchange.getConsumerEndpoint().getServiceName());
                // CHA 2012 : !
                // log.debug("  - Location : " + exchange.getConsumerEndpoint().getLocation());
                // log.debug("  - Interfaces : " + exchange.getConsumerEndpoint().getInterfacesName());
            } else {
                log.debug("  - None");
            }
            log.debug("");

            log.debug("- Exchange values : ");
            log.debug("  - Endpoint name : " + exchange.getEndpoint());
            log.debug("  - ID : " + exchange.getExchangeId());
            log.debug("  - Interface name : " + exchange.getInterfaceName());
            log.debug("  - Operation name : " + exchange.getOperation());
            log.debug("  - Pattern : " + exchange.getPattern());
            log.debug("  - Role : " + exchange.getRole());
            log.debug("  - Service name : " + exchange.getService());
            log.debug("  - Status : " + exchange.getStatus());
            log.debug("");

            Iterator<ServiceEndpoint> iter = electedEndpoints.keySet().iterator();
            if (iter.hasNext()) {
                this.log.debug("- Endpoints found :");
            } else {
                this.log.debug("No endpoints found!");
            }

            while (iter.hasNext()) {
                ServiceEndpoint serviceEndpoint = iter.next();
                this.log.debug("  - Service Endpoint : " + serviceEndpoint);
                TransportSendContext context = electedEndpoints.get(serviceEndpoint);
                if (context != null) {
                    this.log.debug("  - Transport Context : " + context.transport + ", "
                            + context.destination);
                } else {
                    this.log.debug("Transport Context is null");
                }
                this.log.debug("--");
            }
            this.log.debug("***************************************************************************");

        }
    }

}
