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
import java.util.logging.Logger;

import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.TransportException;
import org.petalslink.dsb.transport.api.Client;
import org.petalslink.dsb.transport.api.ClientException;
import org.petalslink.dsb.transport.api.ClientFactory;
import org.petalslink.dsb.transport.api.Constants;
import org.petalslink.dsb.transport.api.Context;
import org.petalslink.dsb.transport.api.ReceiveInterceptor;
import org.petalslink.dsb.transport.api.Receiver;
import org.petalslink.dsb.transport.api.SendInterceptor;
import org.petalslink.dsb.transport.api.Server;
import org.petalslink.dsb.transport.api.Transporter;

/**
 * A generic transport layer inspired from Petals ESB NIO transporter.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class TransporterImpl implements Transporter {
    
    private static Logger log = Logger.getLogger(TransporterImpl.class.getName());

    /**
     * Map of exchanges that are blocked during a synchronous send
     */
    private Map<String, MessageExchange> pendingSyncExchanges;

    private boolean stopTraffic;

    /**
     * The Transporter listener component will be notified when a message is
     * received. It is generally an upper component such as routing for
     * example...
     */
    private Receiver transportListener;

    /**
     * The client factory is in charge of creating clients to send messages to
     */
    private ClientFactory clientFactory;

    /**
     * The send interceptor are optional. Note that the implementation can
     * contains more than one interceptor (deported for better control)
     */
    private SendInterceptor sendInterceptor;

    /**
     * The receive interceptor are optional. Note that the implementation can
     * contains more than one interceptor (deported for better control)
     */
    private ReceiveInterceptor receiveInterceptor;

    private Server server;

    public void start() {
        // this.log = new LoggingUtil(this.logger);
        // this.log.debug("Starting...");
        this.pendingSyncExchanges = new ConcurrentHashMap<String, MessageExchange>(100);
        if (this.server != null) {
            this.server.startServer();
        }
    }

    public void stop() {
        if (this.server != null) {
            this.server.startServer();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void send(MessageExchange exchange, Context context) throws TransportException {
        // this.log.start("Send exchange to destination '" +
        // transportContext.componentName + "'");
        if (exchange == null) {
            throw new TransportException("Can not send a null message");
        }

        if (this.clientFactory == null) {
            throw new TransportException("Can not find any Client factory, check configuration");
        }

        boolean intercept = this.interceptSend(exchange, context);
        if (!intercept) {
            // return
            return;
        }

        this.checkTransporterState();

        try {
            Client client = this.clientFactory.getClient(context);
            if (client == null) {
                throw new TransportException("Can not find a client to reach remote container");
            }

            try {
                client.send(exchange, context.timeout);
                // In case of error during send, the clientConnection is
                // automatically invalidate.
            } finally {
                // release the client
                this.clientFactory.releaseClient(context, client);
                // this.clientConnections.returnObject(
                // transportContext.destination.getContainerName(),
                // clientConnection);
            }

        } catch (final Exception e) {
            // catch all
            throw new TransportException(e);
        }
        // this.log.end();
    }

    /**
     * @param exchange
     * @param transportContext
     * @return
     */
    private boolean interceptSend(MessageExchange exchange, Context transportContext) {
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
    public MessageExchange sendSync(MessageExchange exchange, Context transportContext)
            throws TransportException {
        if (exchange == null) {
            throw new TransportException("Can not send a null message");
        }

        if (this.clientFactory == null) {
            throw new TransportException("Can not find any Client factory, check configuration");
        }
        // this.log.start("Send synchronous exchange to destination : "
        // + transportContext.componentName);
        MessageExchange responseExchange = null;

        this.checkTransporterState();
        setSynchronous(exchange, true);
        this.pendingSyncExchanges.put(exchange.getId(), exchange);

        try {
            synchronized (exchange) {
                this.send(exchange, transportContext);
                // wait for a response (from the listener at #onMessage) or a
                // timeout reached
                exchange.wait(transportContext.timeout);
            }
            responseExchange = this.pendingSyncExchanges.remove(exchange.getId());
        } catch (final InterruptedException e) {
            this.pendingSyncExchanges.remove(exchange);
            throw new TransportException(
                    "Failed to receive the response message of a synchronous send", e);
        }

        // If responseExchange is the exchange,
        // it means that the timeout has been reached or the transporter is
        // stopping
        if (responseExchange == exchange) {
            this.checkTransporterState();

            // this.log.warning("Failed to send synchronously the exchange: " +
            // exchange.getId()
            // + ". Timeout occured");

            // this.log.end();
            return null;
        }

        // this.log.end();

        return responseExchange;
    }

    /**
     * @param exchange
     */
    protected void setSynchronous(MessageExchange exchange, boolean sync) {
        TransporterUtils.setProperty(exchange, Constants.SYNC, Boolean.toString(sync));
    }

    /**
     * 
     */
    protected boolean isSynchronous(MessageExchange exchange) {
        String value = TransporterUtils.getPropertyValue(exchange, Constants.SYNC);
        return Boolean.parseBoolean(value);
    }

    /**
     * {@inheritDoc}
     */
    public void stopTraffic() {
        // this.log.start();
        this.stopTraffic = true;

        // Awake all the pending 'SYNC_SEND' issuer threads to abort the
        // invocation
        boolean redo = true;
        while (redo) {
            try {
                for (final MessageExchange messageExchange : this.pendingSyncExchanges.values()) {
                    synchronized (messageExchange) {
                        messageExchange.notify();
                    }
                }
                redo = false;
            } catch (final ConcurrentModificationException e) {
                // Try again
            }
        }

        // this.log.end();
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
    public void onMessage(org.petalslink.dsb.api.MessageExchange messageExchange) {
        // this.log.start();

        boolean intercept = interceptReceive(messageExchange);
        if (!intercept) {
            // return
            return;
        }

        // an async message is directly sent to the listener, while for a sync
        // one, we have someone already waiting for the response...
        boolean sync = this.isSynchronous(messageExchange);
        if (sync) {
            MessageExchange exchange = this.pendingSyncExchanges.put(messageExchange.getId(),
                    messageExchange);
            synchronized (exchange) {
                exchange.notify();
            }
        } else {
            // if (this.log.isDebugEnabled()) {
            // this.log.debug("The message " + messageExchange.getId()
            // + " is not a synchronized response");
            // }
            if (this.transportListener != null) {
                this.transportListener.onMessage(messageExchange);
            } else {
                // NOP for now...
            }
        }
    }

    /**
     * @param messageExchange
     * @return
     */
    private boolean interceptReceive(org.petalslink.dsb.api.MessageExchange messageExchange) {
        boolean result = true;
        if (this.receiveInterceptor == null) {
            return true;
        }

        Constants.STATUS status = this.receiveInterceptor.receive(messageExchange);
        result = status == Constants.STATUS.CONTINUE;
        return result;
    }

    public void setTransportListener(Receiver transportListener) {
        this.transportListener = transportListener;
    }

    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public void setSendInterceptor(SendInterceptor sendInterceptor) {
        this.sendInterceptor = sendInterceptor;
    }

    public void setReceiveInterceptor(ReceiveInterceptor receiveInterceptor) {
        this.receiveInterceptor = receiveInterceptor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.transport.api.Sender#sendAsync(org.petalslink.dsb.
     * api.MessageExchange, org.petalslink.dsb.transport.api.Context,
     * org.petalslink.dsb.transport.api.Receiver)
     */
    public void sendAsync(MessageExchange exchange, Context context, Receiver listener)
            throws TransportException {
        throw new TransportException("Not implemented");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.transport.api.Transporter#setServer(org.petalslink
     * .dsb.transport.api.Server)
     */
    public void setServer(Server server) {
        this.server = server;
    }
}
