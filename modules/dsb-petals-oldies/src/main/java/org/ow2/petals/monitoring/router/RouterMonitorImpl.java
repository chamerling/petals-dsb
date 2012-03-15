/*
 * PETALS: PETALS Services Platform Copyright (C) 2007 EBM WebSourcing
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
 * Initial developer(s): Adrien LOUIS
 * --------------------------------------------------------------------------
 * $Id: R
 * --------------------------------------------------------------------------
 */

package org.ow2.petals.monitoring.router;

import java.util.List;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.container.lifecycle.ServiceUnitLifeCycle;
import org.ow2.petals.jbi.component.context.ComponentContext;
import org.ow2.petals.jbi.messaging.exchange.MessageExchange;
import org.ow2.petals.jbi.messaging.routing.RouterService;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.util.oldies.LoggingUtil;

/**
 * @version $Rev: 617 $ $Date: 2006-06-19 17:28:41 +0200 (lun, 19 jun 2006) $
 * @since Petals 1.1
 * @author alouis
 */
@FractalComponent
@Provides(interfaces = @Interface(name = "service", signature = org.ow2.petals.jbi.messaging.routing.RouterService.class))
// @Interface(name = "monitoring-service", signature =
// org.ow2.petals.monitoring.router.RouterMonitor.class)
public class RouterMonitorImpl implements RouterService, RouterMonitor {

    protected LoggingUtil log;

    @Monolog(name = "logger")
    protected Logger logger;

    private boolean monitoring = false;

    private Monitoring monitoring_util;

    @Requires(name = "router", signature = org.ow2.petals.jbi.messaging.routing.RouterService.class)
    private RouterService router;

    private boolean showMessageContent = false;

    // //////////////////////////////////////////
    // Router implementation
    // //////////////////////////////////////////

    /**
     * starts the monitoring with or without monitoring content of messages. If
     * monitoring is already started, just set the monitoring of messages
     * content.
     * 
     * @param showMessageContent
     *            indicates if content of messages have to be monitored, true
     *            for monitor the content, false for ignoring it
     */
    public void activateMonitoring(boolean showMessageContent) {
        this.monitoring = true;
        this.showMessageContent = showMessageContent;
    }

    public void addComponent(ComponentContext componentContext) throws RoutingException {
        this.router.addComponent(componentContext);
    }

    // //////////////////////////////////////////
    // Monitor implementation
    // //////////////////////////////////////////

    /**
     * Stops the monitoring of messages and their content.
     */
    public void deactivateMonitoring() {
        this.monitoring = false;
        this.showMessageContent = false;
    }

    /**
     * Indicates if content of messages have to be set in the messages monitored
     * 
     * @return true if content of messages have to be in the messages, false if
     *         not
     */
    public boolean isMessageContentShown() {
        return this.showMessageContent;
    }

    public void showMessageContent(boolean showMessageContent) {
        this.showMessageContent = showMessageContent;
    }

    /**
     * Indicates if monitoring is started or not
     * 
     * @return true if monitoring is started, false if not
     */
    public boolean isMonitoring() {
        return this.monitoring;
    }

    public MessageExchange receive(ComponentContext source, long timeoutMS)
            throws RoutingException {

        MessageExchange exchange = this.router.receive(source, timeoutMS);

        if (this.monitoring && (exchange != null)) {
            this.report(exchange);
        }

        return exchange;
    }

    // ----------------------------------------------------------------------
    // Fractal LifecycleController implementation
    // ----------------------------------------------------------------------

    public void removeComponent(ComponentContext componentContext) throws RoutingException {
        this.router.removeComponent(componentContext);
    }

    public void send(ComponentContext source, MessageExchange exchange)
            throws RoutingException {

        if (this.monitoring && (exchange != null)) {
            this.report(exchange);
        }

        this.router.send(source, exchange);
    }

    public MessageExchange sendSync(ComponentContext source, MessageExchange exchange,
            long timeOut) throws RoutingException {

        if (this.monitoring && (exchange != null)) {
            this.report(exchange);
        }

        final MessageExchange responseExchange = this.router.sendSync(source, exchange, timeOut);

        // report the response exchange
        if (this.monitoring && (exchange != null)) {
            this.report(responseExchange);
        }

        return responseExchange;
    }

    public void modifiedSALifeCycle(List<ServiceUnitLifeCycle> serviceUnitLifes) {
        this.router.modifiedSALifeCycle(serviceUnitLifes);

    }

    public void stopTraffic() {
        this.router.stopTraffic();
    }

    /**
     * Sets the monitoring object to a new Monitoring
     * 
     * @param monitoring_util
     *            the Monitoring object to use
     */
    public void setMonitoring_util(Monitoring monitoring_util) {
        this.monitoring_util = monitoring_util;
    }

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.call();
    }

    /**
     * catch information on the specified exchange
     * 
     * @param exchange
     *            must be non null
     */
    private void report(MessageExchange exchange) {
        this.monitoring_util.addMessage(exchange.getExchangeId(), exchange, this.showMessageContent);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() throws Exception {
        this.log.call();
    }
}
