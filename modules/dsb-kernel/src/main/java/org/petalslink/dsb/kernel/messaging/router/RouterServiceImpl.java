/**
 * PETALS - PETALS Services Platform. Copyright (c) 2007 EBM Websourcing,
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
 * -------------------------------------------------------------------------
 * $Id: RouterService.java,v 1.2 2005/07/22 10:24:27 alouis Exp $
 * -------------------------------------------------------------------------
 */

package org.petalslink.dsb.kernel.messaging.router;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.jbi.messaging.MessageExchange.Role;
import javax.jbi.messaging.MessagingException;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.Cardinality;
import org.objectweb.fractal.fraclet.annotation.annotations.type.Contingency;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.container.lifecycle.ServiceUnitLifeCycle;
import org.ow2.petals.jbi.component.context.ComponentContext;
import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.exchange.MessageExchangeWrapper;
import org.ow2.petals.jbi.messaging.routing.RouterService;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.jbi.messaging.routing.module.InstallModule;
import org.ow2.petals.jbi.messaging.routing.module.ReceiverModule;
import org.ow2.petals.jbi.messaging.routing.module.SenderModule;
import org.ow2.petals.jbi.messaging.routing.monitoring.RouterMonitorService;
import org.ow2.petals.jbi.messaging.routing.util.SourcesForkerUtil;
import org.ow2.petals.transport.TransportException;
import org.ow2.petals.transport.TransportListener;
import org.ow2.petals.transport.Transporter;
import org.ow2.petals.transport.util.TransportSendContext;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;

import static javax.jbi.management.LifeCycleMBean.SHUTDOWN;
import static javax.jbi.management.LifeCycleMBean.STARTED;
import static javax.jbi.management.LifeCycleMBean.STOPPED;
import static javax.jbi.messaging.MessageExchange.Role.CONSUMER;
import static javax.jbi.messaging.MessageExchange.Role.PROVIDER;

/**
 * CHA 2012 : Update to add module management.
 * 
 * Routes messages to their destinations. Performs an itinerary resolution,
 * adding for example a transformation service before delivering the message to
 * its recipient, and perform an address resolution, finding the endpoint
 * satisfying the the destination information (finding an endpoint for a
 * specified service). Resolutions are based on defined rules.
 * 
 * @author alouis - EBM WebSourcing
 * @author chamerling - EBM WebSourcing
 * @author rnaudin - EBM WebSourcing
 */
@FractalComponent
@Provides(interfaces = {
        @Interface(name = "service", signature = org.ow2.petals.jbi.messaging.routing.RouterService.class),
        @Interface(name = "transportlistener", signature = TransportListener.class),
        
        // CHA 2012
        @Interface(name = "routermodulemanager", signature = RouterModuleManager.class) })
public class RouterServiceImpl implements RouterService, TransportListener, RouterModuleManager {
    
    // CHA 2012
    private LoggingUtil log;

    @Monolog(name = "logger")
    private Logger logger;
    
    private RouterModuleManager routerModuleManager;
    
    // -CHA 2012

    private static final String INSTALLMODULE_FRACTAL_PREFIX = "installmodule";

    private static final String PROVIDER_SUFFIX = "-provider";

    /**
     * The maximum number of messages exchange waiting to be received
     */
    private static final int QUEUE_SIZE = 10000;

    private static final String RECEIVERMODULE_FRACTAL_PREFIX = "receivermodule";

    private static final String SENDERMODULE_FRACTAL_PREFIX = "sendermodule";

    // private static final String CONSUMER_SUFFIX = "-consumer";

    /**
     * The timeout of the stop of the traffic
     */
    private static final int STOP_TRAFFIC_TIMEOUT = 10000;

    private static final String TRANSPORTER_FRACTAL_PREFIX = "transporter";

    /**
     * The map of the exchanges queues. One queue per installed component.
     */
    private Map<String, BlockingQueue<MessageExchangeWrapper>> exchangeQueues;

    /**
     * The Router Monitor Fractal component
     */
    // CHA 2012 : set to optional while it is not required in the DSB
    // This need to be part of a module and not directly in the router itself...
    @Requires(name = "routermonitor", signature = RouterMonitorService.class, contingency=Contingency.OPTIONAL)
    private RouterMonitorService routerMonitorService;

    /**
     * The list of installModule Fractal components
     */
    @Requires(name = INSTALLMODULE_FRACTAL_PREFIX, signature = InstallModule.class, cardinality = Cardinality.COLLECTION, contingency = Contingency.OPTIONAL)
    private final Map<String, Object> installModules = new Hashtable<String, Object>();

    /**
     * The map of the pending exchanges. One List per Provides of SU stopped or
     * shut down.
     */
    private Map<String, List<MessageExchangeWrapper>> pendingMessageExchanges;

    /**
     * The list of ReceiverModule Fractal components
     */
    @Requires(name = RECEIVERMODULE_FRACTAL_PREFIX, signature = ReceiverModule.class, cardinality = Cardinality.COLLECTION, contingency = Contingency.OPTIONAL)
    private final Map<String, Object> receiverModules = new Hashtable<String, Object>();

    /**
     * The list of SenderModule Fractal components
     */
    @Requires(name = SENDERMODULE_FRACTAL_PREFIX, signature = SenderModule.class, cardinality = Cardinality.COLLECTION, contingency = Contingency.OPTIONAL)
    private final Map<String, Object> senderModules = new Hashtable<String, Object>();

    /**
     * Traffic flag to handle stop of traffic
     */
    private boolean stopTraffic;

    /**
     * The list of the threads which are blocking on a poll
     */
    private List<Thread> threadsList;

    /**
     * the Transporter Fractal components
     */
    @Requires(name = TRANSPORTER_FRACTAL_PREFIX, signature = Transporter.class, cardinality = Cardinality.COLLECTION, contingency = Contingency.OPTIONAL)
    private final Map<String, Object> transporters = new Hashtable<String, Object>();

    /**
     * Check if the message exchange to send is marked to be bypassed.
     * 
     * @param exchange
     *            The exchange to check
     */
    private static final boolean checkBypassMessageExchange(final MessageExchangeWrapper exchange) {
        boolean bypass = false;
        Object noAck = null;

        if (exchange.isTerminated()) {
            if (CONSUMER.equals(exchange.getRole())) {
                noAck = exchange.getProperty(RouterService.PROPERTY_ROUTER_PROVIDER_NOACK);
            } else {
                noAck = exchange.getProperty(RouterService.PROPERTY_ROUTER_CONSUMER_NOACK);
            }
            bypass = noAck != null && noAck.toString().toLowerCase().equals("true");
        }

        return bypass;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.jbi.messaging.routing.Router#addComponent(org.ow2.petals
     * .jbi.component.context.ComponentContextImpl)
     */
    public void addComponent(final ComponentContext componentContext) throws RoutingException {
        this.log.start();

        for (final Object installModule : this.installModules.values()) {
            ((InstallModule) installModule).addComponent(componentContext);
        }

        this.exchangeQueues.put(componentContext.getComponentName(),
                new ArrayBlockingQueue<MessageExchangeWrapper>(QUEUE_SIZE));

        this.log.end();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.transport.TransportListener#exchangeSent(org.ow2.petals
     * .jbi.messaging.exchange.MessageExchangeDecorator)
     */
    public void exchangeSent(MessageExchangeWrapper exchangeDecorator) {
        if (this.routerMonitorService != null) {
            this.routerMonitorService.exchangeSent(exchangeDecorator);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.jbi.messaging.routing.RouterService#modifiedSALifeCycle
     * (java.util.List, java.lang.String)
     */
    public void modifiedSALifeCycle(final List<ServiceUnitLifeCycle> serviceUnitLifeCycles) {
        this.log.start();

        synchronized (this.pendingMessageExchanges) {
            for (final ServiceUnitLifeCycle serviceUnitLifeCycle : serviceUnitLifeCycles) {
                final List<org.ow2.petals.jbi.descriptor.original.generated.Provides> providesList = serviceUnitLifeCycle
                        .getServiceUnitDescriptor().getServices().getProvides();
                if (providesList != null) {
                    final String suState = serviceUnitLifeCycle.getCurrentState();
                    for (final org.ow2.petals.jbi.descriptor.original.generated.Provides provides : providesList) {
                        final String uniqueId = provides.getEndpointName()
                                + provides.getServiceName() + PROVIDER_SUFFIX;
                        if (STOPPED.equals(suState) || SHUTDOWN.equals(suState)) {
                            if (!this.pendingMessageExchanges.containsKey(uniqueId)) {
                                this.pendingMessageExchanges.put(uniqueId,
                                        new Vector<MessageExchangeWrapper>(100));
                            }
                        } else if (STARTED.equals(suState)) {
                            final BlockingQueue<MessageExchangeWrapper> componentQueue = this.exchangeQueues
                                    .get(serviceUnitLifeCycle.getTargetComponentName());
                            componentQueue.addAll(this.pendingMessageExchanges.remove(uniqueId));
                        }
                    }
                }
                // TODO: find a way to handle consumes when they are shut down
            }
        }

        this.log.end();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.transport.TransportListener#onExchange(org.ow2.petals.
     * jbi.messaging.exchange.MessageExchangeDecorator)
     */
    public void onExchange(final MessageExchangeWrapper exchangeDecorator) {
        this.log.start();

        String componentName = null;

        if (this.routerMonitorService != null) {
            this.routerMonitorService.exchangeReceived(exchangeDecorator);
        }

        synchronized (this.pendingMessageExchanges) {
            if (exchangeDecorator.getRole().equals(CONSUMER)) {
                // TODO: handle the List of shut down consumes
                componentName = ((ServiceEndpoint)exchangeDecorator.getConsumerEndpoint()).getLocation()
                        .getComponentName();
            } else if (exchangeDecorator.getRole().equals(PROVIDER)) {
                final org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint endpoint = (org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint) exchangeDecorator
                        .getEndpoint();
                final String uniqueId = endpoint.getEndpointName() + endpoint.getServiceName()
                        + PROVIDER_SUFFIX;
                if (this.pendingMessageExchanges.containsKey(uniqueId)) {
                    this.log.debug("SU not started, store the exchange");
                    this.pendingMessageExchanges.get(uniqueId).add(exchangeDecorator);
                } else {
                    componentName = ((org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint) exchangeDecorator
                            .getEndpoint()).getLocation().getComponentName();
                }
            }
        }

        if (componentName != null) {
            this.exchangeQueues.get(componentName).add(exchangeDecorator);
        }

        this.log.end();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.jbi.messaging.routing.Router#receive(org.ow2.petals.jbi
     * .component.context.ComponentContextImpl, long)
     */
    public MessageExchangeWrapper receive(final ComponentContext componentContext,
            final long timeoutMS) throws RoutingException {

        MessageExchangeWrapper exchangeDecorator;
        final String componentName = componentContext.getComponentName();

        this.log.start("Component : " + componentName + " - Timeout : " + timeoutMS);

        this.checkStopTraffic();

        if (timeoutMS == 0) {
            // non-blocking call
            exchangeDecorator = this.exchangeQueues.get(componentName).poll();
        } else {
            this.threadsList.add(Thread.currentThread());
            try {
                if (timeoutMS > 0) {
                    // blocking call with timeout
                    exchangeDecorator = this.exchangeQueues.get(componentName).poll(timeoutMS,
                            TimeUnit.MILLISECONDS);
                } else {
                    // blocking call without timeout
                    exchangeDecorator = this.exchangeQueues.get(componentName).take();
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RoutingException("The pending receipt for the component '"
                        + componentName + "' is interrupted", e);
            } finally {
                this.threadsList.remove(Thread.currentThread());
            }
        }

        // CHA 2012
        if (exchangeDecorator != null) {
            for (org.petalslink.dsb.kernel.messaging.router.ReceiverModule receiver : this.routerModuleManager
                    .getReceivers()) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Receiver module %s is called", receiver.getName()));
                }
                receiver.receiveExchange(exchangeDecorator, componentContext);
            }
            this.log.end("Exchange Id : " + exchangeDecorator.getExchangeId() + " - Component : "
                    + componentName);
        } else {
            this.log.end("No exchange - Component : " + componentName);
        }

        return exchangeDecorator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.jbi.messaging.routing.Router#removeComponent(org.ow2.petals
     * .jbi.component.context.ComponentContextImpl)
     */
    public void removeComponent(final ComponentContext componentContext) throws RoutingException {
        this.log.start();

        for (final Object installModule : this.installModules.values()) {
            ((InstallModule) installModule).removeComponent(componentContext);
        }

        this.exchangeQueues.remove(componentContext.getComponentName());

        this.log.end();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.jbi.messaging.routing.Router#send(org.ow2.petals.jbi.component
     * .context.ComponentContextImpl,
     * org.ow2.petals.jbi.messaging.exchange.MessageExchangeImpl, long)
     */
    public void send(final ComponentContext source, final MessageExchangeWrapper exchangeDecorator)
            throws RoutingException {
        this.log.call();

        this.checkStopTraffic();

        if (!checkBypassMessageExchange(exchangeDecorator)) {

            // clean unused messages
            exchangeDecorator.getMessageExchange().cleanMessages();

            // Get the list of elected endpoints from the sender modules
            final Map<ServiceEndpoint, TransportSendContext> electedDestinations = new LinkedHashMap<ServiceEndpoint, TransportSendContext>();
            for (org.petalslink.dsb.kernel.messaging.router.SenderModule senderModule : routerModuleManager
                    .getSenders()) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Sender module %s is called", senderModule.getName()));
                }
                senderModule.electEndpoints(electedDestinations, source, exchangeDecorator);
            }

            boolean isLocalSend = false;
            try {
                exchangeDecorator.setObserverRole(null);
                if (CONSUMER.equals(exchangeDecorator.getRole())) {
                    exchangeDecorator.setRole(PROVIDER);
                    isLocalSend = this.sendToProvider(electedDestinations, source,
                            exchangeDecorator, false, 0);
                } else {
                    exchangeDecorator.setRole(CONSUMER);
                    // only one destination for a consumer
                    final TransportSendContext transportSendContext = electedDestinations.values()
                            .iterator().next();
                    isLocalSend = this.sendToConsumer(transportSendContext, exchangeDecorator,
                            false, 0);
                }
                // if local, the observer role is set in the onExchange() method
                if (!isLocalSend) {
                    exchangeDecorator.setObserverRole(exchangeDecorator.getRole());
                }
            } catch (RoutingException e) {
                // reset the roles
                if (CONSUMER.equals(exchangeDecorator.getRole())) {
                    exchangeDecorator.setRole(PROVIDER);
                    exchangeDecorator.setObserverRole(PROVIDER);
                } else {
                    exchangeDecorator.setRole(CONSUMER);
                    exchangeDecorator.setObserverRole(CONSUMER);
                }
                throw e;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.jbi.messaging.routing.RouterService#sendSync(org.ow2.petals
     * .jbi.component.context.ComponentContext,
     * org.ow2.petals.jbi.messaging.exchange.MessageExchangeDecorator, long)
     */
    public void sendSync(final ComponentContext source,
            final MessageExchangeWrapper exchangeDecorator, final long timeout)
            throws RoutingException {
        this.log.call();

        this.checkStopTraffic();

        // clean unused messages
        exchangeDecorator.getMessageExchange().cleanMessages();

        this.removeBypassMessageExchange(exchangeDecorator);

        long currentTime = 0;
        if (timeout > 0) {
            currentTime = System.currentTimeMillis();

            exchangeDecorator.setProperty(PROPERTY_ROUTER_TIMETOLIVE, currentTime + timeout);
        }

        final Map<ServiceEndpoint, TransportSendContext> electedDestinations = new LinkedHashMap<ServiceEndpoint, TransportSendContext>();
        for (org.petalslink.dsb.kernel.messaging.router.SenderModule senderModule : routerModuleManager
                .getSenders()) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Sender module %s is called", senderModule.getName()));
            }
            senderModule.electEndpoints(electedDestinations, source, exchangeDecorator);
        }

        long currentTimeout = timeout;
        if (currentTimeout > 0) {
            currentTimeout -= System.currentTimeMillis() - currentTime;
            if (currentTimeout == 0) {
                currentTimeout = -1;
            }
        }

        if (currentTimeout >= 0) {
            final Role originalRole = exchangeDecorator.getRole();
            try {
                exchangeDecorator.setObserverRole(null);
                if (CONSUMER.equals(exchangeDecorator.getRole())) {
                    exchangeDecorator.setRole(PROVIDER);
                    this.sendToProvider(electedDestinations, source, exchangeDecorator, true,
                            timeout);
                } else {
                    exchangeDecorator.setRole(CONSUMER);
                    // only one destination for a consumer
                    final TransportSendContext transportSendContext = electedDestinations.values()
                            .iterator().next();
                    this.sendToConsumer(transportSendContext, exchangeDecorator, true, timeout);
                }

                if (!exchangeDecorator.isTimeout()) {
                    if (this.routerMonitorService != null) {
                        this.routerMonitorService.exchangeReceived(exchangeDecorator);
                    }
                    for (org.petalslink.dsb.kernel.messaging.router.ReceiverModule receiverModule : routerModuleManager
                            .getReceivers()) {
                        if (log.isDebugEnabled()) {
                            log.debug(String.format("Receiver module %s is called", receiverModule.getName()));
                        }
                        receiverModule.receiveExchange(exchangeDecorator, source);
                    }
                }
            } finally {
                exchangeDecorator.setRole(originalRole);
                exchangeDecorator.setObserverRole(originalRole);
            }
        } else {
            exchangeDecorator.setTimeout(true);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.jbi.messaging.routing.RouterService#stopTraffic()
     */
    public void stopTraffic() {
        this.log.start();

        // Wait that all the pending exchanges are received or the timeout is
        // reached
        final long timeout = STOP_TRAFFIC_TIMEOUT + System.currentTimeMillis();
        boolean done = false;
        try {
            while (System.currentTimeMillis() < timeout && !done) {
                for (final BlockingQueue<MessageExchangeWrapper> queue : this.exchangeQueues
                        .values()) {
                    if (queue.size() > 0) {
                        // sleep for 1 second
                        Thread.sleep(1000);
                        break;
                    }
                }
                done = true;
            }
        } catch (final InterruptedException e) {
            // do nothing
        }

        if (!done) {
            this.log.warning("The timeout for stopping the Router traffic has been reached."
                    + " Some message have not beed accepted by the component, they are lost.");
        }

        this.stopTraffic = true;

        boolean redo = true;
        // Interrupt all the pending receive threads
        while (redo) {
            try {
                for (final Thread thread : this.threadsList) {
                    if (thread.isAlive()) {
                        thread.interrupt();
                    }
                }
                redo = false;
            } catch (final ConcurrentModificationException e) {
                // Try again
            }
        }

        this.log.end();
    }

    /**
     * start the router.
     * 
     * @throws RoutingException
     *             impossible to start the router
     */
    @LifeCycle(on = LifeCycleType.START)
    protected void start() throws RoutingException {
        // CHA 2012
        this.log = new LoggingUtil(logger);
        this.routerModuleManager = new RouterModuleManagerImpl();

        // -CHA 2012
        
        this.log.call();
        this.exchangeQueues = new ConcurrentHashMap<String, BlockingQueue<MessageExchangeWrapper>>();
        this.pendingMessageExchanges = new ConcurrentHashMap<String, List<MessageExchangeWrapper>>();
        this.threadsList = new Vector<Thread>(100);
    }

    /**
     * stop the router.
     */
    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() throws RoutingException {
        this.log.call();
    }

    /**
     * Check if the traffic is off. If so, raise an exception with the
     * interrupted flag set.
     * 
     * @throws TransportException
     */
    private void checkStopTraffic() throws RoutingException {
        if (this.stopTraffic) {
            Thread.currentThread().interrupt();
            throw new RoutingException("The Router traffic is stopped", new InterruptedException());
        }
    }

    /**
     * If the message exchange to send is marked to be bypassed, remove the mark
     * and log a warning message.
     * 
     * @param exchangeDecorator
     *            The exchange to check
     */
    private boolean removeBypassMessageExchange(final MessageExchangeWrapper exchangeDecorator) {
        this.log.call();

        final boolean bypass = false;

        if (exchangeDecorator.isTerminated()) {
            if (CONSUMER.equals(exchangeDecorator.getRole())) {
                if (exchangeDecorator.getProperty(RouterService.PROPERTY_ROUTER_CONSUMER_NOACK) != null) {
                    exchangeDecorator.setProperty(RouterService.PROPERTY_ROUTER_CONSUMER_NOACK,
                            "false");
                    this.log.warning("Property '" + RouterService.PROPERTY_ROUTER_CONSUMER_NOACK
                            + "' is not supported in synchronous send mode");
                }
            } else {
                if (exchangeDecorator.getProperty(RouterService.PROPERTY_ROUTER_PROVIDER_NOACK) != null) {
                    exchangeDecorator.setProperty(RouterService.PROPERTY_ROUTER_PROVIDER_NOACK,
                            "false");
                    this.log.warning("Property '" + RouterService.PROPERTY_ROUTER_PROVIDER_NOACK
                            + "' is not supported in synchronous send mode");
                }
            }
        }
        return bypass;
    }

    /**
     * Sends the specified message, sent by the specified component.
     * 
     * @param source
     *            the source
     * @param exchange
     *            the exchange
     * @param sync
     *            {@code true} is the send is synchronous
     * @param timeout
     *            timeout of the message (in ms). 0 for no timeout
     * @return {@code true} if the send is not synchronous and local
     * @throws RoutingException
     */
    private boolean sendToConsumer(final TransportSendContext transportSendContext,
            final MessageExchangeWrapper exchangeDecorator, final boolean sync, final long timeout)
            throws RoutingException {
        this.log.start();

        boolean result = false;

        final long startTime = System.currentTimeMillis();
        boolean retry = true;
        int attemptDelay = 0;

        while (retry) {
            retry = false;
            if (attemptDelay != 0) {
                try {
                    Thread.sleep(transportSendContext.delay);
                } catch (final InterruptedException e) {
                    throw new RoutingException(e);
                }
            }
            try {
                if (transportSendContext.attempt > 1
                        || exchangeDecorator.getMessageExchange().isPersisted()) {
                    // fork the exchange if it contains stream source(s)
                    try {
                        SourcesForkerUtil.forkExchangeSources(exchangeDecorator);
                    } catch (final MessagingException e) {
                        throw new RoutingException(e);
                    }
                }

                if (sync) {
                    if (timeout > 0) {
                        final long elapseTime = System.currentTimeMillis() - startTime;
                        if (elapseTime > timeout) {
                            // The timeout is reached
                            exchangeDecorator.setTimeout(true);
                            break;
                        }
                        transportSendContext.timeout = timeout - elapseTime;
                    }
                    ((Transporter) this.transporters.get(TRANSPORTER_FRACTAL_PREFIX + "-"
                            + transportSendContext.transport)).sendSync(exchangeDecorator,
                            transportSendContext);
                } else {
                    ((Transporter) this.transporters.get(TRANSPORTER_FRACTAL_PREFIX + "-"
                            + transportSendContext.transport)).send(exchangeDecorator,
                            transportSendContext);
                    if (Transporter.LOCAL_FRACTAL_TRANSPORTER
                            .equals(transportSendContext.transport)) {
                        result = true;
                    }
                }
                retry = false;
            } catch (final TransportException e) {
                transportSendContext.attempt -= 1;
                if (transportSendContext.attempt > 0) {
                    this.log.warning("The send attempt to the " + transportSendContext.destination
                            + " failed", e);
                    retry = true;
                    attemptDelay = transportSendContext.delay;
                } else {
                    throw new RoutingException(e);
                }
            } finally {
                try {
                    SourcesForkerUtil.cleanExchangeSources(exchangeDecorator);
                } catch (IOException e) {
                    // do nothing
                }
            }
        }

        this.log.end();

        return result;
    }

    /**
     * Sends the specified message to the provider of the service.
     * 
     * @param source
     *            the source
     * @param exchange
     *            the exchange decorator
     * @param sync
     *            {@code true} is the send is synchronous
     * @param timeout
     *            timeout of the message (in ms). 0 for no timeout one
     * @return {@code true} if the send is not synchronous and local
     * @throws RoutingException
     */
    private boolean sendToProvider(
            final Map<ServiceEndpoint, TransportSendContext> endpointDestinations,
            final ComponentContext source, final MessageExchangeWrapper exchangeDecorator,
            final boolean sync, final long timeout) throws RoutingException {
        this.log.start();

        boolean result = false;

        TransportSendContext transportSendContext = null;
        final long startTime = System.currentTimeMillis();
        boolean retry = true;
        int attemptDelay = 0;

        while (retry) {
            if (attemptDelay != 0) {
                this.log
                        .debug("Wait " + attemptDelay + " millisecond before the next send attempt");
                try {
                    Thread.sleep(attemptDelay);
                } catch (final InterruptedException e) {
                    throw new RoutingException(e);
                }
            }
            retry = false;
            final Iterator<ServiceEndpoint> iterator = endpointDestinations.keySet().iterator();
            try {
                while (iterator.hasNext()) {
                    final ServiceEndpoint electedEndpoint = iterator.next();
                    transportSendContext = endpointDestinations.get(electedEndpoint);
                    if (transportSendContext.attempt > 0) {
                        this.log.debug("Send attempt to endpoint '"
                                + electedEndpoint.getEndpointName() + "'");
                        try {
                            exchangeDecorator.setEndpoint(electedEndpoint);
                            exchangeDecorator.setService(electedEndpoint.getServiceName());
                            // the interface list contains 1 interface
                            exchangeDecorator.setInterfaceName(electedEndpoint.getInterfacesName().get(0));
                            if (iterator.hasNext() || attemptDelay > 0
                                    || transportSendContext.attempt > 1
                                    || exchangeDecorator.getMessageExchange().isPersisted()) {
                                // fork the exchange if it contains stream
                                // source(s)
                                try {
                                    SourcesForkerUtil.forkExchangeSources(exchangeDecorator);
                                } catch (final MessagingException e) {
                                    throw new RoutingException(e);
                                }
                            }

                            if (sync) {
                                if (timeout > 0) {
                                    final long elapseTime = System.currentTimeMillis() - startTime;
                                    if (elapseTime > timeout) {
                                        this.log.debug("Timeout reached!");
                                        exchangeDecorator.setTimeout(true);
                                        // The timeout is reached
                                        retry = false;
                                        break;
                                    }
                                    transportSendContext.timeout = timeout - elapseTime;
                                }
                                ((Transporter) this.transporters.get(TRANSPORTER_FRACTAL_PREFIX
                                        + "-" + transportSendContext.transport)).sendSync(
                                        exchangeDecorator, transportSendContext);
                            } else {
                                ((Transporter) this.transporters.get(TRANSPORTER_FRACTAL_PREFIX
                                        + "-" + transportSendContext.transport)).send(
                                        exchangeDecorator, transportSendContext);
                                if (Transporter.LOCAL_FRACTAL_TRANSPORTER
                                        .equals(transportSendContext.transport)) {
                                    result = true;
                                }
                            }
                            retry = false;
                            break;
                        } catch (final TransportException e) {
                            transportSendContext.attempt -= 1;
                            if (transportSendContext.attempt > 0) {
                                retry = true;
                                // get the max delay
                                if (transportSendContext.delay > attemptDelay) {
                                    attemptDelay = transportSendContext.delay;
                                }
                            }

                            if (iterator.hasNext() || retry) {
                                this.log.warning("The send attempt to the endpoint '"
                                        + electedEndpoint.getEndpointName() + "' with destination "
                                        + transportSendContext.destination + " failed", e);
                            } else {
                                throw new RoutingException(e);
                            }
                        }
                    }
                }
            } finally {
                try {
                    SourcesForkerUtil.cleanExchangeSources(exchangeDecorator);
                } catch (IOException e) {
                    // do nothing
                }
            }
        }

        this.log.end();
        return result;
    }
    
    // CHA 2012
    // Router Module ACK
    /**
     * Load the modules into the manager
     */
    @LifeCycleListener(phase = Phase.START)
    public void loadModules() {
        if (log.isDebugEnabled()) {
            log.debug("Ack to add modules to manager");
        }
        for (String key : senderModules.keySet()) {
            Object o = senderModules.get(key);
            
            if (o instanceof Component) {
                System.out.println("############## COMPONENT = " + o);
            }
            
            if (o != null && o instanceof SenderModule) {
                addModule(key, (SenderModule) o);
            }
        }
        for (String key : receiverModules.keySet()) {
            Object o = receiverModules.get(key);
            if (o != null && o instanceof ReceiverModule) {
                addModule(key, (ReceiverModule) o);
            }
        }
    }

    void addModule(final String name, final SenderModule sender) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Adding sender module %s to manager", name));
        }
        this.routerModuleManager.add(new org.petalslink.dsb.kernel.messaging.router.SenderModule() {
            public void electEndpoints(Map<ServiceEndpoint, TransportSendContext> electedEndpoints,
                    ComponentContext sourceComponentContext,
                    org.ow2.petals.jbi.messaging.exchange.MessageExchangeWrapper exchange)
                    throws RoutingException {
                sender.electEndpoints(electedEndpoints, sourceComponentContext, exchange);
            }

            public String getName() {
                return name;
            }
            
            /* (non-Javadoc)
             * @see org.petalslink.dsb.kernel.messaging.router.SenderModule#getDescription()
             */
            public String getDescription() {
                return sender.getClass().getName();
            }
        });
    }

    void addModule(final String name, final ReceiverModule receiver) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Adding receiver module %s to manager", name));
        }
        
        this.routerModuleManager
                .add(new org.petalslink.dsb.kernel.messaging.router.ReceiverModule() {

                    public boolean receiveExchange(
                            org.ow2.petals.jbi.messaging.exchange.MessageExchangeWrapper exchange,
                            ComponentContext sourceComponentContext) throws RoutingException {
                        return receiver.receiveExchange(exchange, sourceComponentContext);
                    }

                    public String getName() {
                        return name;
                    }
                    
                    /* (non-Javadoc)
                     * @see org.petalslink.dsb.kernel.messaging.router.ReceiverModule#getDescription()
                     */
                    public String getDescription() {
                        return receiver.getClass().getName();
                    }
                });
    }

    // Try to fix some issue...

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#add(org
     * .petalslink.dsb.kernel.messaging.router.SenderModule)
     */
    public void add(org.petalslink.dsb.kernel.messaging.router.SenderModule module) {
        this.routerModuleManager.add(module);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#add(org
     * .petalslink.dsb.kernel.messaging.router.ReceiverModule)
     */
    public void add(org.petalslink.dsb.kernel.messaging.router.ReceiverModule module) {
        this.routerModuleManager.add(module);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#getSenders
     * ()
     */
    public List<org.petalslink.dsb.kernel.messaging.router.SenderModule> getSenders() {
        return this.routerModuleManager.getSenders();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#getReceivers
     * ()
     */
    public List<org.petalslink.dsb.kernel.messaging.router.ReceiverModule> getReceivers() {
        return this.routerModuleManager.getReceivers();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#setSenderState
     * (java.lang.String, boolean)
     */
    public void setSenderState(String name, boolean onoff) {
        this.routerModuleManager.setSenderState(name, onoff);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#
     * setReceiverState(java.lang.String, boolean)
     */
    public void setReceiverState(String name, boolean onoff) {
        this.routerModuleManager.setReceiverState(name, onoff);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#
     * getReceiverState(java.lang.String)
     */
    public boolean getReceiverState(String name) {
        return this.routerModuleManager.getReceiverState(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#getSenderState
     * (java.lang.String)
     */
    public boolean getSenderState(String name) {
        return this.routerModuleManager.getSenderState(name);
    }
}
