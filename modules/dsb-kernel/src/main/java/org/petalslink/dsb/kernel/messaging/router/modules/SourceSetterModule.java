/**
 * 
 */
package org.petalslink.dsb.kernel.messaging.router.modules;

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
import org.ow2.petals.jbi.messaging.exchange.MessageExchange;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.jbi.messaging.routing.module.SenderModule;
import org.ow2.petals.transport.util.TransportSendContext;
import org.ow2.petals.util.LoggingUtil;

/**
 * Sets some properties concerning the consumer in the message exchnage so that
 * the receiver can use them or not...
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = SenderModule.class) })
public class SourceSetterModule implements SenderModule {

    static final String LOCATION_COMPONENT = "consumer.location.component";

    static final String LOCATION_CONTAINER = "consumer.location.container";

    static final String LOCATION_DOMAIN = "consumer.location.domain";

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.jbi.messaging.routing.module.SenderModule#electEndpoints
     * (java.util.Map, org.ow2.petals.jbi.component.context.ComponentContext,
     * org.ow2.petals.jbi.messaging.exchange.MessageExchange)
     */
    public void electEndpoints(Map<ServiceEndpoint, TransportSendContext> electedEndpoints,
            ComponentContext sourceComponentContext, MessageExchange exchange)
            throws RoutingException {
        
        if (log.isDebugEnabled()) {
            log.debug("Setting the source component location information into the exchange");
        }

        if (exchange.getConsumerEndpoint() != null
                && exchange.getConsumerEndpoint().getLocation() != null) {
            exchange.setProperty(LOCATION_COMPONENT, exchange.getConsumerEndpoint().getLocation()
                    .getComponentName());
            exchange.setProperty(LOCATION_CONTAINER, exchange.getConsumerEndpoint().getLocation()
                    .getContainerName());
            exchange.setProperty(LOCATION_DOMAIN, exchange.getConsumerEndpoint().getLocation()
                    .getSubdomainName());
        }

    }

}
