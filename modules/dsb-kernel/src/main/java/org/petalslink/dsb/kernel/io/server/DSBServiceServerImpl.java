/**
 * 
 */
package org.petalslink.dsb.kernel.io.server;

import java.util.MissingResourceException;
import java.util.logging.Logger;

import javax.jbi.JBIException;
import javax.jbi.component.ComponentContext;
import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;

import org.petalslink.dsb.jbi.Adapter;
import org.petalslink.dsb.service.client.Message;
import org.petalslink.dsb.service.client.MessageListener;

/**
 * A JBI {@link DSBServiceServer} implementation
 * 
 * @author chamerling
 * 
 */
public class DSBServiceServerImpl implements DSBServiceServer {

    /**
     * Do something when a message is received...
     */
    private MessageListener listener;

    private ComponentContext componentContext;

    private boolean running;

    int activeEndpoints;
    
    private Logger logger;

    private DeliveryChannel channel;

    public DSBServiceServerImpl(ComponentContext componentContext) {
        this.componentContext = componentContext;
        try {
            this.channel = this.componentContext.getDeliveryChannel();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        try {
            this.logger = this.componentContext.getLogger("", null);
        } catch (MissingResourceException e) {
            e.printStackTrace();
        } catch (JBIException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.io.server.DSBServiceServer#start()
     */
    public void start() {
        activeEndpoints++;
        // start a thread which listen to the delivery channel
        if (!running && channel != null) {
            new Thread(new ChannelListenner()).start();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.io.server.DSBServiceServer#stop()
     */
    public void stop() {
        // stop listening to the delivery channel
        activeEndpoints--;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.io.server.DSBServiceServer#setListener(org.
     * petalslink.dsb.kernel.io.server.DSBServiceListener)
     */
    public void setListener(MessageListener listener) {
        if (listener == null)
            return;

        this.listener = listener;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.io.server.DSBServiceServer#getListener()
     */
    public MessageListener getListener() {
        return this.listener;
    }

    private class ChannelListenner implements Runnable {

        public final void run() {

            try {
                synchronized (channel) {
                    running = true;
                }

                do {
                    MessageExchange exchange = null;
                    synchronized (channel) {
                        try {
                            logger.fine("Waiting for a message on kernel service server");
                            exchange = channel.accept();
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                    
                    logger.fine("Got a message on the listener");
                    
                    if (exchange != null && exchange.getStatus() == ExchangeStatus.ACTIVE) {

                        try {
                            logger.fine("Dispatch message...");
                            dispatch(exchange);
                            logger.fine("Send back the message");
                            if (exchange.getMessage("out") != null) {
                                // FIXME : Where to do it?
                                //exchange.setStatus(ExchangeStatus.DONE);
                                channel.send(exchange);
                            }

                        } finally {
                            //
                        }
                    } else {
                        logger.fine("Do not care about this message status : " + exchange.getStatus());
                    }
                } while (activeEndpoints > 0);
                synchronized (channel) {
                    running = false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void dispatch(MessageExchange exchange) {
        // create a Message from the message exhange and send it to the
        // listener...
        if (getListener() == null) {
            return;
        }

        // TODO : Check faults and errors...
        if (exchange != null && exchange.getMessage("in") != null) {
            Message message = Adapter.transform(exchange.getMessage("in"));
            Message out = getListener().onMessage(message);
            if (out != null) {
                // set the out message in the exchange
                try {
                    exchange.setMessage(Adapter.transform(out), "out");
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
