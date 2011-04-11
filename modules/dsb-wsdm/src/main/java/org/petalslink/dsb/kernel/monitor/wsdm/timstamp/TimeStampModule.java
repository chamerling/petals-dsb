/**
 * PETALS - PETALS Services Platform. Copyright (c) 2009 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */
package org.petalslink.dsb.kernel.monitor.wsdm.timstamp;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.jbi.component.context.ComponentContext;
import org.ow2.petals.jbi.messaging.exchange.MessageExchange;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.jbi.messaging.routing.module.ReceiverModule;
import org.ow2.petals.jbi.messaging.routing.module.SenderModule;
import org.ow2.petals.transport.util.TransportSendContext;
import org.ow2.petals.util.LoggingUtil;

/**
 * Timestamp the messages
 * 
 * @author chamerling, nsalatge
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "timestampSender", signature = SenderModule.class),
        @Interface(name = "timestampReceiver", signature = ReceiverModule.class) })
public class TimeStampModule implements SenderModule, ReceiverModule {

    /**
     * Logger wrapper.
     */
    protected LoggingUtil log;

    /**
     * The logger.
     */
    @Monolog(name = "logger")
    protected Logger logger;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.jbi.messaging.routing.module.SenderModule#electDestinations
     * (java.util.Map,
     * org.ow2.petals.jbi.component.context.ComponentContextImpl,
     * org.ow2.petals.jbi.messaging.exchange.MessageExchangeImpl)
     */
    public void electEndpoints(
            Map<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint, TransportSendContext> electedDestinations,
            ComponentContext sourceComponentContext, MessageExchange exchange)
            throws RoutingException {
        this.log.call();

        this.setTimeStamp(exchange);
    }

    public boolean receiveExchange(MessageExchange exchange, ComponentContext arg1)
            throws RoutingException {
        if (exchange != null) {
            this.setTimeStamp(exchange);
        }
        return true;
    }

    private void setTimeStamp(MessageExchange exchange) {

        Date date = GregorianCalendar.getInstance().getTime();
        String t = null;
        if (MessageExchange.Role.CONSUMER.equals(exchange.getRole())) {
            if (TimeStamperHandler.getInstance().getTimeStamp(exchange).getDateClientIn() == null) {
                TimeStamperHandler.getInstance().getTimeStamp(exchange).setDateClientIn(date);
                t = "t1: ";
            } else if (TimeStamperHandler.getInstance().getTimeStamp(exchange).getDateClientOut() == null) {
                TimeStamperHandler.getInstance().getTimeStamp(exchange).setDateClientOut(date);
                t = "t4: ";
            }
        }

        if (MessageExchange.Role.PROVIDER.equals(exchange.getRole())) {
            if (TimeStamperHandler.getInstance().getTimeStamp(exchange).getDateProviderIn() == null) {
                TimeStamperHandler.getInstance().getTimeStamp(exchange).setDateProviderIn(date);
                t = "t2: ";
            } else if (TimeStamperHandler.getInstance().getTimeStamp(exchange).getDateProviderOut() == null) {
                TimeStamperHandler.getInstance().getTimeStamp(exchange).setDateProviderOut(date);
                t = "t3: ";
            }
        }

        if ((t != null) && this.log.isInfoEnabled()) {
            this.log.info(t + "timestamp the messageExchange (" + exchange.getExchangeId() + "): "
                    + date.getTime());
        }
    }

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

}
