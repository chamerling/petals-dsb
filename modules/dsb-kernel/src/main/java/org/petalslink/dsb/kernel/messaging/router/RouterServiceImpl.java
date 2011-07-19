/**
 * 
 */
package org.petalslink.dsb.kernel.messaging.router;

import java.io.IOException;
import java.io.InputStream;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
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

import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

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
import org.ow2.petals.commons.stream.InputStreamForker;
import org.ow2.petals.commons.stream.ReaderInputStream;
import org.ow2.petals.container.lifecycle.ServiceUnitLifeCycle;
import org.ow2.petals.jbi.component.context.ComponentContext;
import org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint;
import org.ow2.petals.jbi.messaging.routing.RouterService;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.jbi.messaging.routing.module.InstallModule;
import org.ow2.petals.jbi.messaging.routing.module.ReceiverModule;
import org.ow2.petals.jbi.messaging.routing.module.SenderModule;
import org.ow2.petals.transport.TransportException;
import org.ow2.petals.transport.TransportListener;
import org.ow2.petals.transport.Transporter;
import org.ow2.petals.transport.util.TransportSendContext;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;

import static javax.jbi.management.LifeCycleMBean.SHUTDOWN;
import static javax.jbi.management.LifeCycleMBean.STARTED;
import static javax.jbi.management.LifeCycleMBean.STOPPED;

/**
 * FIXME : THis is a port from petals-kernel-3.0.4 to introduce
 * RouterModuleManager
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
        @Interface(name = "routermodulemanager", signature = RouterModuleManager.class) })
public class RouterServiceImpl implements RouterService, TransportListener, RouterModuleManager {

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
     * a map of Stream caches to send to multiple destination a stream
     */
    private Map<String, Map<String, InputStreamForker>> exchangeForkedStreamCache;

    /**
     * The map of the exchanges queues. One queue per installed component.
     */
    private Map<String, BlockingQueue<org.ow2.petals.jbi.messaging.exchange.MessageExchange>> exchangeQueues;

    /**
     * The list of installModule Fractal components
     */
    @Requires(name = INSTALLMODULE_FRACTAL_PREFIX, signature = InstallModule.class, cardinality = Cardinality.COLLECTION, contingency = Contingency.OPTIONAL)
    private final Map<String, Object> installModules = new Hashtable<String, Object>();

    /**
     * Logger wrapper.
     */
    private LoggingUtil log;

    /**
     * The logger.
     */
    @Monolog(name = "logger")
    private Logger logger;

    /**
     * The map of the pending exchanges. One List per Provides of SU stopped or
     * shut down.
     */
    private Map<String, List<org.ow2.petals.jbi.messaging.exchange.MessageExchange>> pendingMessageExchanges;

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

    // @Requires(name = "modulemanager", signature = RouterModuleManager.class)
    /*
     * For now it is not a require since I have an exception at startup...
     * IllegalBindingException: Mandatory client interface unbound (client
     * interface = /Petals/JBI-Messaging/DSBRouterServiceImpl.modulemanager)
     */
    private RouterModuleManager routerModuleManager;

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
                new ArrayBlockingQueue<org.ow2.petals.jbi.messaging.exchange.MessageExchange>(
                        QUEUE_SIZE));

        this.log.end();
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
                                this.pendingMessageExchanges
                                        .put(uniqueId,
                                                new Vector<org.ow2.petals.jbi.messaging.exchange.MessageExchange>(
                                                        100));
                            }
                        } else if (STARTED.equals(suState)) {
                            final BlockingQueue<org.ow2.petals.jbi.messaging.exchange.MessageExchange> componentQueue = this.exchangeQueues
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
     * jbi.messaging.exchange.MessageExchangeImpl)
     */
    public void onExchange(final org.ow2.petals.jbi.messaging.exchange.MessageExchange exchange) {
        this.log.start();

        String componentName = null;

        synchronized (this.pendingMessageExchanges) {
            if (exchange.getRole().equals(MessageExchange.Role.CONSUMER)) {
                // TODO: handle the List of shut down consumes
                componentName = exchange.getConsumerEndpoint().getLocation().getComponentName();
            } else if (exchange.getRole().equals(MessageExchange.Role.PROVIDER)) {
                final org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint endpoint = (org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint) exchange
                        .getEndpoint();
                final String uniqueId = endpoint.getEndpointName() + endpoint.getServiceName()
                        + PROVIDER_SUFFIX;
                if (this.pendingMessageExchanges.containsKey(uniqueId)) {
                    this.log.debug("SU not started, store the exchange");
                    this.pendingMessageExchanges.get(uniqueId).add(exchange);
                } else {
                    componentName = ((org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint) exchange
                            .getEndpoint()).getLocation().getComponentName();
                }
            }
        }

        if (componentName != null) {
            this.exchangeQueues.get(componentName).add(exchange);
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
    public org.ow2.petals.jbi.messaging.exchange.MessageExchange receive(
            final ComponentContext source, final long timeoutMS) throws RoutingException {

        org.ow2.petals.jbi.messaging.exchange.MessageExchange exchange;
        final String componentName = source.getComponentName();

        this.log.start("Component : " + componentName + " - Timeout : " + timeoutMS);

        this.checkStopTraffic();

        if (timeoutMS == 0) {
            // non-blocking call
            exchange = this.exchangeQueues.get(componentName).poll();
        } else {
            this.threadsList.add(Thread.currentThread());
            try {
                if (timeoutMS > 0) {
                    // blocking call with timeout
                    exchange = this.exchangeQueues.get(componentName).poll(timeoutMS,
                            TimeUnit.MILLISECONDS);
                } else {
                    // blocking call without timeout
                    exchange = this.exchangeQueues.get(componentName).take();
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RoutingException("The pending receipt for the component '"
                        + source.getComponentName() + "' is interrupted", e);
            } finally {
                this.threadsList.remove(Thread.currentThread());
            }
        }

        if (exchange != null) {
            for (org.petalslink.dsb.kernel.messaging.router.ReceiverModule receiver : this.routerModuleManager
                    .getReceivers()) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Receiver module %s is called", receiver.getName()));
                }
                receiver.receiveExchange(exchange, source);
            }
            this.log.end("Exchange Id : " + exchange.getExchangeId() + " - Component : "
                    + componentName);
        } else {
            this.log.end("No exchange - Component : " + componentName);
        }

        return exchange;
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
    public void send(final ComponentContext source,
            final org.ow2.petals.jbi.messaging.exchange.MessageExchange exchange)
            throws RoutingException {
        this.log.call();

        this.checkStopTraffic();

        if (!checkBypassMessageExchange(exchange)) {

            // clean unused messages
            exchange.cleanMessages();

            // Get the list of elected endpoints from the sender modules
            final Map<ServiceEndpoint, TransportSendContext> electedDestinations = new LinkedHashMap<ServiceEndpoint, TransportSendContext>();
            for (org.petalslink.dsb.kernel.messaging.router.SenderModule senderModule : routerModuleManager
                    .getSenders()) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Sender module %s is called", senderModule.getName()));
                }
                senderModule.electEndpoints(electedDestinations, source, exchange);
            }

            if (MessageExchange.Role.CONSUMER.equals(exchange.getRole())) {
                exchange.setRole(MessageExchange.Role.PROVIDER);
            } else {
                exchange.setRole(MessageExchange.Role.CONSUMER);
            }

            if (MessageExchange.Role.PROVIDER.equals(exchange.getRole())) {
                this.sendToProvider(electedDestinations, source, exchange, false, 0);
            } else {
                // only one destination for a consumer
                final TransportSendContext transportSendContext = electedDestinations.values()
                        .iterator().next();
                this.sendToConsumer(transportSendContext, exchange, false, 0);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.jbi.messaging.routing.Router#sendSync(org.ow2.petals.jbi
     * .component.context.ComponentContextImpl,
     * org.ow2.petals.jbi.messaging.exchange.MessageExchangeImpl, long)
     */
    public org.ow2.petals.jbi.messaging.exchange.MessageExchange sendSync(
            final ComponentContext source,
            final org.ow2.petals.jbi.messaging.exchange.MessageExchange exchange, final long timeout)
            throws RoutingException {
        this.log.call();

        this.checkStopTraffic();

        // clean unused messages
        exchange.cleanMessages();

        this.removeBypassMessageExchange(exchange);

        // Get the list of elected endpoints from the sender modules
        final Map<ServiceEndpoint, TransportSendContext> electedDestinations = new LinkedHashMap<ServiceEndpoint, TransportSendContext>();
        for (org.petalslink.dsb.kernel.messaging.router.SenderModule senderModule : routerModuleManager
                .getSenders()) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Sender module %s is called", senderModule.getName()));
            }
            senderModule.electEndpoints(electedDestinations, source, exchange);
        }

        if (MessageExchange.Role.CONSUMER.equals(exchange.getRole())) {
            exchange.setRole(MessageExchange.Role.PROVIDER);
        } else {
            exchange.setRole(MessageExchange.Role.CONSUMER);
        }

        org.ow2.petals.jbi.messaging.exchange.MessageExchange responseExchange = null;
        if (MessageExchange.Role.PROVIDER.equals(exchange.getRole())) {
            responseExchange = this.sendToProvider(electedDestinations, source, exchange, true,
                    timeout);
        } else {
            // only one destination for a consumer
            final TransportSendContext transportSendContext = electedDestinations.values()
                    .iterator().next();
            responseExchange = this.sendToConsumer(transportSendContext, exchange, true, timeout);
        }

        for (org.petalslink.dsb.kernel.messaging.router.ReceiverModule receiverModule : routerModuleManager
                .getReceivers()) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Receiver module %s is called", receiverModule.getName()));
            }
            receiverModule.receiveExchange(responseExchange, source);
        }

        return responseExchange;
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
                for (final BlockingQueue<org.ow2.petals.jbi.messaging.exchange.MessageExchange> queue : this.exchangeQueues
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
        this.log = new LoggingUtil(this.logger);
        this.log.call();

        this.routerModuleManager = new RouterModuleManagerImpl();

        this.exchangeQueues = new ConcurrentHashMap<String, BlockingQueue<org.ow2.petals.jbi.messaging.exchange.MessageExchange>>();
        this.pendingMessageExchanges = new ConcurrentHashMap<String, List<org.ow2.petals.jbi.messaging.exchange.MessageExchange>>();
        this.exchangeForkedStreamCache = new ConcurrentHashMap<String, Map<String, InputStreamForker>>();
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
     * Clean the potential duplicated Sources.
     * 
     * @param exchange
     * @throws MessagingException
     */
    private void cleanExchangeSources(
            final org.ow2.petals.jbi.messaging.exchange.MessageExchange exchange) {
        final Map<String, InputStreamForker> exchangeStreamForked = this.exchangeForkedStreamCache
                .remove(exchange.getExchangeId());
        if (exchangeStreamForked != null) {
            for (final InputStreamForker streamForker : exchangeStreamForked.values()) {
                try {
                    streamForker.getInputStreamTwo().close();
                } catch (final IOException e) {
                    this.log.warning("Failed to clean a forked Source", e);
                }
            }
        }
    }

    /**
     * Handle streaming issue. Duplicate sources if they are StreamSource. TODO
     * : extend the mechanism to the attachments.
     * 
     * @param exchange
     * @throws MessagingException
     */
    private void forkExchangeSources(
            final org.ow2.petals.jbi.messaging.exchange.MessageExchange exchange)
            throws MessagingException {

        Map<String, InputStreamForker> exchangeStreamForked = this.exchangeForkedStreamCache
                .get(exchange.getExchangeId());
        if (exchangeStreamForked == null) {
            exchangeStreamForked = new HashMap<String, InputStreamForker>();
            this.exchangeForkedStreamCache.put(exchange.getExchangeId(), exchangeStreamForked);
        }

        final Map<String, NormalizedMessage> messages = exchange.getMessages();
        for (final String messageName : messages.keySet()) {
            // TODO Handle the Streaming issue for the Attachments??
            final Source content = messages.get(messageName).getContent();
            if (content instanceof StreamSource) {
                InputStreamForker streamForker = exchangeStreamForked.get(messageName);
                if (streamForker != null) {
                    streamForker = new InputStreamForker(streamForker.getInputStreamTwo());
                } else {
                    final StreamSource streamContent = (StreamSource) content;
                    final InputStream isContent = streamContent.getInputStream();
                    if (isContent != null) {
                        // The StreamSource was created from an InputStream
                        streamForker = new InputStreamForker(isContent);
                    } else {
                        // The StreamSource was created from a Reader
                        // we wrap it as an InputStream
                        streamForker = new InputStreamForker(new ReaderInputStream(
                                streamContent.getReader()));
                    }
                }
                exchangeStreamForked.put(messageName, streamForker);
                final Source source = new StreamSource(streamForker.getInputStreamOne());
                messages.get(messageName).setContent(source);
            }
        }
    }

    /**
     * If the message exchange to send is marked to be bypassed, remove the mark
     * and log a warning message.
     * 
     * @param exchange
     *            The exchange to check
     */
    private boolean removeBypassMessageExchange(
            final org.ow2.petals.jbi.messaging.exchange.MessageExchange exchange) {
        this.log.call();

        final boolean bypass = false;

        if (exchange.isTerminated()) {
            if (MessageExchange.Role.CONSUMER.equals(exchange.getRole())) {
                if (exchange.getProperty(RouterService.PROPERTY_ROUTER_CONSUMER_NOACK) != null) {
                    exchange.setProperty(RouterService.PROPERTY_ROUTER_CONSUMER_NOACK, "false");
                    this.log.warning("Property '" + RouterService.PROPERTY_ROUTER_CONSUMER_NOACK
                            + "' is not supported by a synchronous sending");
                }
            } else {
                if (exchange.getProperty(RouterService.PROPERTY_ROUTER_PROVIDER_NOACK) != null) {
                    exchange.setProperty(RouterService.PROPERTY_ROUTER_PROVIDER_NOACK, "false");
                    this.log.warning("Property '" + RouterService.PROPERTY_ROUTER_PROVIDER_NOACK
                            + "' is not supported by a synchronous sending");
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
     * @return the response exchange if a synchronous send is processed
     * @throws RoutingException
     *             impossible to send the message to the consumer
     */
    private org.ow2.petals.jbi.messaging.exchange.MessageExchange sendToConsumer(
            final TransportSendContext transportSendContext,
            final org.ow2.petals.jbi.messaging.exchange.MessageExchange exchange,
            final boolean sync, final long timeout) throws RoutingException {
        this.log.start();

        org.ow2.petals.jbi.messaging.exchange.MessageExchange responseExchange = null;

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
                if (transportSendContext.attempt > 1) {
                    // fork the exchange if it contains stream source(s)
                    try {
                        this.forkExchangeSources(exchange);
                    } catch (final MessagingException e) {
                        throw new RoutingException(e);
                    }
                }

                if (sync) {
                    if (timeout > 0) {
                        final long elapseTime = System.currentTimeMillis() - startTime;
                        if (elapseTime > timeout) {
                            // The timeout is reached
                            exchange.setRole(MessageExchange.Role.PROVIDER);
                            retry = false;
                            break;
                        }
                        transportSendContext.timeout = timeout - elapseTime;
                    }
                    responseExchange = ((Transporter) this.transporters
                            .get(TRANSPORTER_FRACTAL_PREFIX + "-" + transportSendContext.transport))
                            .sendSync(exchange, transportSendContext);
                    if (responseExchange == null) {
                        // The timeout is reached
                        exchange.setRole(MessageExchange.Role.PROVIDER);
                    }
                } else {
                    ((Transporter) this.transporters.get(TRANSPORTER_FRACTAL_PREFIX + "-"
                            + transportSendContext.transport)).send(exchange, transportSendContext);
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
                    // Restore the Role if the message has not been sent
                    exchange.setRole(MessageExchange.Role.PROVIDER);
                    throw new RoutingException(e);
                }
            }
        }

        this.cleanExchangeSources(exchange);

        this.log.end();
        return responseExchange;
    }

    /**
     * Sends the specified message to the provider of the service.
     * 
     * @param source
     *            the source
     * @param exchange
     *            the exchange
     * @param sync
     *            {@code true} is the send is synchronous
     * @param timeout
     *            timeout of the message (in ms). 0 for no timeout one
     * @return the response exchange if a synchronous send is processed,
     *         {@code null} if the timeout is reached
     * @throws RoutingException
     *             impossible to send the message to the provider
     * @throws
     */
    private org.ow2.petals.jbi.messaging.exchange.MessageExchange sendToProvider(
            final Map<ServiceEndpoint, TransportSendContext> endpointDestinations,
            final ComponentContext source,
            final org.ow2.petals.jbi.messaging.exchange.MessageExchange exchange,
            final boolean sync, final long timeout) throws RoutingException {
        this.log.start();

        org.ow2.petals.jbi.messaging.exchange.MessageExchange responseExchange = null;

        TransportSendContext transportSendContext = null;
        final long startTime = System.currentTimeMillis();
        boolean retry = true;
        int attemptDelay = 0;

        while (retry) {
            if (attemptDelay != 0) {
                this.log.debug("Wait " + attemptDelay + " millisecond before the next send attempt");
                try {
                    Thread.sleep(attemptDelay);
                } catch (final InterruptedException e) {
                    throw new RoutingException(e);
                }
            }
            retry = false;
            final Iterator<ServiceEndpoint> iterator = endpointDestinations.keySet().iterator();
            while (iterator.hasNext()) {
                final ServiceEndpoint electedEndpoint = iterator.next();
                transportSendContext = endpointDestinations.get(electedEndpoint);
                if (transportSendContext.attempt > 0) {
                    this.log.debug("Send attempt to endpoint '" + electedEndpoint.getEndpointName()
                            + "'");
                    try {
                        exchange.setEndpoint(electedEndpoint);
                        exchange.setService(electedEndpoint.getServiceName());
                        // the interface list contains 1 interface
                        exchange.setInterfaceName(electedEndpoint.getInterfaces()[0]);
                        if (iterator.hasNext() || attemptDelay > 0
                                || transportSendContext.attempt > 1) {
                            // fork the exchange if it contains stream source(s)
                            try {
                                this.forkExchangeSources(exchange);
                            } catch (final MessagingException e) {
                                throw new RoutingException(e);
                            }
                        }

                        if (sync) {
                            if (timeout > 0) {
                                final long elapseTime = System.currentTimeMillis() - startTime;
                                if (elapseTime > timeout) {
                                    this.log.debug("Timeout reached!");
                                    // The timeout is reached
                                    exchange.setRole(MessageExchange.Role.CONSUMER);
                                    retry = false;
                                    break;
                                }
                                transportSendContext.timeout = timeout - elapseTime;
                            }
                            responseExchange = ((Transporter) this.transporters
                                    .get(TRANSPORTER_FRACTAL_PREFIX + "-"
                                            + transportSendContext.transport)).sendSync(exchange,
                                    transportSendContext);
                            if (responseExchange == null) {
                                // The timeout is reached
                                exchange.setRole(MessageExchange.Role.CONSUMER);
                            }
                        } else {
                            ((Transporter) this.transporters.get(TRANSPORTER_FRACTAL_PREFIX + "-"
                                    + transportSendContext.transport)).send(exchange,
                                    transportSendContext);
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
                            this.log.warning(
                                    "The send attempt to the endpoint '"
                                            + electedEndpoint.getEndpointName()
                                            + "' with destination "
                                            + transportSendContext.destination + " failed", e);
                        } else {
                            // restore the Role if the message has not been sent
                            exchange.setRole(MessageExchange.Role.CONSUMER);
                            throw new RoutingException(e);
                        }
                    }
                }
            }
        }

        this.cleanExchangeSources(exchange);

        this.log.end();
        return responseExchange;
    }

    /**
     * Check if the message exchange to send is marked to be bypassed.
     * 
     * @param exchange
     *            The exchange to check
     */
    private static final boolean checkBypassMessageExchange(
            final org.ow2.petals.jbi.messaging.exchange.MessageExchange exchange) {

        boolean bypass = false;
        Object noAck = null;

        if (exchange.isTerminated()) {
            if (MessageExchange.Role.CONSUMER.equals(exchange.getRole())) {
                noAck = exchange.getProperty(RouterService.PROPERTY_ROUTER_PROVIDER_NOACK);
            } else {
                noAck = exchange.getProperty(RouterService.PROPERTY_ROUTER_CONSUMER_NOACK);
            }
            bypass = noAck != null && noAck.toString().toLowerCase().equals("true");
        }

        return bypass;
    }

    // Router Module ACK
    /**
     * Load the modules into the manager
     */
    @LifeCycleListener(phase=Phase.START)
    public void loadModules() {
        System.out.println("LOADING MODULES");
        if (log.isDebugEnabled()) {
            log.debug("Ack to add modules to manager");
        }
        for (String key : senderModules.keySet()) {
            Object o = senderModules.get(key);
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
                    org.ow2.petals.jbi.messaging.exchange.MessageExchange exchange)
                    throws RoutingException {
                sender.electEndpoints(electedEndpoints, sourceComponentContext, exchange);
            }

            public String getName() {
                return name;
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
                            org.ow2.petals.jbi.messaging.exchange.MessageExchange exchange,
                            ComponentContext sourceComponentContext) throws RoutingException {
                        return receiver.receiveExchange(exchange, sourceComponentContext);
                    }

                    public String getName() {
                        return name;
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
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#setState
     * (java.lang.String, boolean)
     */
    public void setState(String name, boolean onoff) {
        this.routerModuleManager.setState(name, onoff);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.messaging.router.RouterModuleManager#getState
     * (java.lang.String)
     */
    public boolean getState(String name) {
        return this.routerModuleManager.getState(name);
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
}
