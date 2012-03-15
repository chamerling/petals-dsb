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
package org.petalslink.dsb.transport;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jbi.messaging.MessagingException;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.Contingency;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.jbi.messaging.exchange.MessageExchangeWrapper;
import org.ow2.petals.kernel.configuration.ConfigurationService;
import org.ow2.petals.transport.TransportException;
import org.ow2.petals.transport.TransportListener;
import org.ow2.petals.transport.Transporter;
import org.ow2.petals.transport.util.TransportSendContext;
import org.ow2.petals.transport.util.TransporterUtil;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.transport.api.Client;
import org.petalslink.dsb.transport.api.ClientException;
import org.petalslink.dsb.transport.api.ClientFactory;
import org.petalslink.dsb.transport.api.Constants;
import org.petalslink.dsb.transport.api.ReceiveInterceptor;
import org.petalslink.dsb.transport.api.Receiver;
import org.petalslink.dsb.transport.api.SendInterceptor;

/**
 * A generic transport layer inspired from Petals NIO transporter.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = Transporter.class),
        @Interface(name = "receiver", signature = Receiver.class) })
public class TransporterImpl implements Transporter, Receiver {

    /**
     * Map of exchanges that are blocked during a synchronous send
     */
    private Map<String, MessageExchangeWrapper> pendingSyncExchanges;

    private boolean stopTraffic;

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "configuration", signature = ConfigurationService.class)
    private ConfigurationService configurationService;

    /**
     * The Transporter listener component will be notified when a message is
     * received
     */
    @Requires(name = "transportlistener", signature = TransportListener.class)
    private TransportListener transportListener;

    /**
     * The client factory is in charge of creating clients to send messages to
     */
    @Requires(name = "clientfactory", signature = ClientFactory.class)
    private ClientFactory clientFactory;

    /**
     * The send interceptor are optional. Note that the implementation can
     * contains more than one interceptor (deported for better control)
     */
    @Requires(name = "sendinterceptor", signature = SendInterceptor.class, contingency = Contingency.OPTIONAL)
    private SendInterceptor sendInterceptor;

    /**
     * The receive interceptor are optional. Note that the implementation can
     * contains more than one interceptor (deported for better control)
     */
    @Requires(name = "receiveinterceptor", signature = ReceiveInterceptor.class, contingency = Contingency.OPTIONAL)
    private ReceiveInterceptor receiveInterceptor;

    /**
     * Timeout to send a message exchange.
     */
    private long sendTimeout;

    @LifeCycle(on = LifeCycleType.START)
    public void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
        this.sendTimeout = this.configurationService.getContainerConfiguration()
                .getTCPSendTimeout();
        this.pendingSyncExchanges = new ConcurrentHashMap<String, MessageExchangeWrapper>(100);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    public void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public void send(MessageExchangeWrapper exchange, TransportSendContext transportContext)
            throws TransportException {
        this.log.start("Send exchange to destination '" + transportContext.destination + "'");

        boolean intercept = this.interceptSend(exchange, transportContext);
        if (!intercept) {
            // return
            return;
        }

        this.checkTransporterState();

        try {
            Client client = this.clientFactory.getClient(transportContext.destination
                    .getContainerName());
            if (client == null) {
                throw new TransportException("Can not find a client to reach remote container");
            }

            try {
                client.send(exchange, this.sendTimeout);
                // In case of error during send, the clientConnection is
                // automatically invalidate.
            } finally {
                // release the client
                this.clientFactory.releaseClient(transportContext.destination.getContainerName(),
                        client);
                // this.clientConnections.returnObject(
                // transportContext.destination.getContainerName(),
                // clientConnection);
            }

        } catch (final Exception e) {
            // catch all
            throw new TransportException(e);
        }
        this.log.end();
    }

    /**
     * @param exchange
     * @param transportContext
     * @return
     */
    private boolean interceptSend(MessageExchangeWrapper exchange, TransportSendContext transportContext) {
        boolean result = true;

        if (this.sendInterceptor == null) {
            return true;
        }

        try {
            Constants.STATUS status = this.sendInterceptor.send(exchange, transportContext);
            result = status == Constants.STATUS.CONTINUE;
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void sendSync(MessageExchangeWrapper exchange, TransportSendContext transportContext)
            throws TransportException {
        this.log
                .start("Send synchronous exchange to destination : " + transportContext.destination);
        MessageExchangeWrapper responseExchange;

        this.checkTransporterState();

        // set the relevant properties to mark the message as a
        // synchronous send
        try {
            TransporterUtil.setSendSyncProperties(exchange);
        } catch (final MessagingException e) {
            throw new TransportException(e);
        }

        this.pendingSyncExchanges.put(exchange.getExchangeId(), exchange);

        try {
            synchronized (exchange) {
                this.send(exchange, transportContext);
                // wait for a response or a timeout reached
                exchange.wait(transportContext.timeout);
            }
            responseExchange = this.pendingSyncExchanges.remove(exchange.getExchangeId());
        } catch (final InterruptedException e) {
            TransporterUtil.updateSyncProperties(exchange);
            this.pendingSyncExchanges.remove(exchange);
            throw new TransportException(
                    "Failed to receive the response message of a synchronous send", e);
        }

        // If responseExchange is the exchange,
        // it means that the timeout has been reached or the transporter is
        // stopping
        if (responseExchange == exchange) {
            this.checkTransporterState();
            exchange.setTimeout(true);

            this.log.warning("Failed to send synchronously the exchange: "
                    + exchange.getExchangeId() + ". Timeout occured");

            this.log.end();
        } else {
            try {
                exchange.setMessageExchange(responseExchange.getMessageExchange());
            } catch (MessagingException e) {
                throw new TransportException(e);
            }
        }

        this.log.end();
    }

    /**
     * {@inheritDoc}
     */
    public void stopTraffic() {
        this.log.start();
        this.stopTraffic = true;

        // Awake all the pending 'SYNC_SEND' issuer threads to abort the
        // invocation
        boolean redo = true;
        while (redo) {
            try {
                for (final MessageExchangeWrapper messageExchangeImpl : this.pendingSyncExchanges.values()) {
                    synchronized (messageExchangeImpl) {
                        messageExchangeImpl.notify();
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
     * Check if the transporter is running, if not, raise an exception.
     * 
     * @throws ClientException
     */
    private void checkTransporterState() throws TransportException {
        if (this.stopTraffic) {
            Thread.currentThread().interrupt();
            throw new TransportException("The Transporter traffic is stopped",
                    new InterruptedException());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onMessage(MessageExchangeWrapper messageExchange) {
        this.log.start();

        boolean intercept = interceptReceive(messageExchange);
        if (!intercept) {
            // return
            return;
        }

        boolean sync = this.getSyncMode(messageExchange, true);
        if (sync) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("The message " + messageExchange.getExchangeId()
                        + " is a synchronized response");
            }
            TransporterUtil.updateSyncProperties(messageExchange);
            MessageExchangeWrapper exchange = this.pendingSyncExchanges.put(messageExchange
                    .getExchangeId(), messageExchange);
            synchronized (exchange) {
                exchange.notify();
            }
        } else {
            if (this.log.isDebugEnabled()) {
                this.log.debug("The message " + messageExchange.getExchangeId()
                        + " is not a synchronized response");
            }
            this.transportListener.onExchange(messageExchange);
        }
        this.log.end();
    }

    /**
     * @param messageExchange
     * @return
     */
    private boolean interceptReceive(MessageExchangeWrapper messageExchange) {
        boolean result = true;
        if (this.receiveInterceptor == null) {
            return true;
        }

        Constants.STATUS status = this.receiveInterceptor.receive(messageExchange);
        result = status == Constants.STATUS.CONTINUE;
        return result;
    }

    /**
     * @param messageExchange
     * @param b
     * @return
     */
    private boolean getSyncMode(MessageExchangeWrapper messageExchange, boolean isResponse) {
        final boolean syncMode;
        if ((MessageExchange.Role.CONSUMER.equals(messageExchange.getRole()) && isResponse)
                || (MessageExchange.Role.PROVIDER.equals(messageExchange.getRole()) && !isResponse)) {
            syncMode = Boolean.parseBoolean((String) messageExchange
                    .getProperty(TransporterUtil.PROPERTY_SENDSYNC_CONSUMER));
        } else {
            syncMode = Boolean.parseBoolean((String) messageExchange
                    .getProperty(TransporterUtil.PROPERTY_SENDSYNC_PROVIDER));
        }
        return syncMode;
    }

    // For test only while waiting a fractal based solution without ADLs

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setTransportListener(TransportListener transportListener) {
        this.transportListener = transportListener;
    }

    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setSendInterceptor(SendInterceptor sendInterceptor) {
        this.sendInterceptor = sendInterceptor;
    }

    public void setReceiveInterceptor(ReceiveInterceptor receiveInterceptor) {
        this.receiveInterceptor = receiveInterceptor;
    }
}
