/**
 * 
 */
package org.petalslink.dsb.kernel.cxf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.jbi.messaging.MessageExchange;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.AbstractConduit;
import org.apache.cxf.transport.AbstractDestination;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.MessageObserver;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.wsdl.EndpointReferenceUtils;
import org.petalslink.dsb.kernel.io.Adapter;
import org.petalslink.dsb.kernel.io.server.DSBServiceServer;
import org.petalslink.dsb.kernel.io.server.DSBServiceServerFactory;
import org.petalslink.dsb.kernel.io.server.ServerFactoryRegistry;
import org.petalslink.dsb.service.client.MessageListener;
import org.w3c.dom.Document;

/**
 * The {@link Destination} is used to receive message on the service side ie
 * Petals is listening to incoming messages here.
 * 
 * @author chamerling
 * 
 */
public class DSBDestination extends AbstractDestination implements MessageListener {

    private static final Logger LOG = LogUtils.getL7dLogger(DSBDestination.class);

    public static final String CORRELATION = "DSB-CORRELATION";

    private DSBServiceServer server;

    /**
     * Map to wait for CXF responses
     */
    private Map<String, CountDownLatch> latches;

    private Map<String, org.petalslink.dsb.service.client.Message> outMessages;

    private ExecutorService executor;

    private MessageListener responseListener;

    public DSBDestination(EndpointInfo info) {
        super(getTargetReference(info, null), info);
        this.latches = new ConcurrentHashMap<String, CountDownLatch>();
        this.outMessages = new ConcurrentHashMap<String, org.petalslink.dsb.service.client.Message>();
        this.responseListener = new ResponseListener();
        this.executor = Executors.newFixedThreadPool(10);
    }

    protected Logger getLogger() {
        return LOG;
    }

    /**
     * @param inMessage
     *            the incoming message
     * @return the inbuilt backchannel
     */
    protected Conduit getInbuiltBackChannel(Message inMessage) {
        // get a back channel to send back the response to the client
        return new BackChannelConduit(EndpointReferenceUtils.getAnonymousEndpointReference(),
                inMessage);
    }

    public void shutdown() {
        this.deactivate();
    }

    public void deactivate() {
        this.server.stop();
        this.executor.shutdownNow();
    }

    public void activate() {
        // creates ther server
        if (this.server == null) {
            DSBServiceServerFactory factory = ServerFactoryRegistry.getFactory();
            if (factory == null) {
                throw new RuntimeException("Can not find any factory for server");
            }
            this.server = factory.getServiceServer();
        }
        // As soon as CXF runtime activates endpoint (adds listener, etc)
        // Destination.activate() method is automatically invoked.
        // Implementation of Destination.activate() normally opens network
        // transport connections and listens to incoming requests
        this.server.setListener(this);
        this.server.start();
    }

    /**
     * Send back the response to the client
     * 
     * @author chamerling
     * 
     */
    protected class BackChannelConduit extends AbstractConduit {

        protected Message inMessage;

        protected DSBDestination destination;

        BackChannelConduit(EndpointReferenceType ref, Message message) {
            super(ref);
            inMessage = message;
        }

        /**
         * Register a message observer for incoming messages.
         * 
         * @param observer
         *            the observer to notify on receipt of incoming
         */
        public void setMessageObserver(MessageObserver observer) {
            // shouldn't be called for a back channel conduit
        }

        /**
         * Send an outbound message, assumed to contain all the name-value
         * mappings of the corresponding input message (if any).
         * 
         * @param message
         *            the message to be sent.
         */
        public void prepare(Message message) throws IOException {
            // setup the message to be send back
            message.put(MessageExchange.class, inMessage.get(MessageExchange.class));
            message.setContent(OutputStream.class, new DSBDestinationOutputStream(inMessage,
                    message, responseListener));
        }

        protected Logger getLogger() {
            return LOG;
        }
    }

    /**
     * be able to receive a message from the petals listener.
     */
    public org.petalslink.dsb.service.client.Message onMessage(
            org.petalslink.dsb.service.client.Message message) {
        org.petalslink.dsb.service.client.Message result = null;

        // When a request comes, the destination creates a message, sets the
        // content and notifies message observer. This is done in the current
        // listener which is called by the Petals message server implementation.
        // create the CXF message from the incoming Petals Message
        try {
            final MessageImpl inMessage = new MessageImpl();
            inMessage.put(org.petalslink.dsb.service.client.Message.class, message);

            Document doc = message.getPayload();
            InputStream is = Adapter.getInputStream(doc);
            if (doc != null) {
                inMessage.setContent(InputStream.class, is);
            }

            // TODO : pass the callback to be able to send back the response...

            // dispatch to correct destination in case of multiple endpoint
            // inMessage.setDestination(petalsTransportFactory.getDestination(message.getService()
            // .toString() + message.getInterface().toString()));
            // petalsTransportFactory
            // .getDestination(
            // message.getService().toString() +
            // message.getInterface().toString())
            // .getMessageObserver().onMessage(inMessage);
            inMessage.setDestination(this);

            String correlation = message.getProperties().get(CORRELATION);
            if (correlation == null) {
                correlation = UUID.randomUUID().toString();
                message.getProperties().put(CORRELATION, correlation);
            }
            inMessage.put(CORRELATION, correlation);

            // wait for a response with a latch...
            CountDownLatch latch = new CountDownLatch(1);
            latches.put(correlation, latch);

            // do it in another thread to not block the current one...
            this.executor.submit(new Runnable() {
                public void run() {
                    incomingObserver.onMessage(inMessage);
                }
            });

            latch.await(30, TimeUnit.SECONDS);
            // we have a response, send it back
            LOG.fine("We have a response, send it back to the client");
            result = outMessages.remove(correlation);

        } catch (Exception ex) {
            ex.printStackTrace();
            // TODO
        }
        return result;
    }

    /**
     * Let's be able to notify server that a response is available from the CXF
     * runtime...
     * 
     * @author chamerling
     * 
     */
    private class ResponseListener implements MessageListener {

        public org.petalslink.dsb.service.client.Message onMessage(
                org.petalslink.dsb.service.client.Message message) {
            LOG.fine("Got out message on response listener");
            // push the message to the shared map and notify the initial
            // listener that an out message is available
            String correlation = message.getProperties().get(CORRELATION);
            if (correlation != null && latches.get(correlation) != null) {
                outMessages.put(correlation, message);
                latches.get(correlation).countDown();
            } else {
            }

            // don't care about the response since there is no response...
            return null;
        }
    }
}
