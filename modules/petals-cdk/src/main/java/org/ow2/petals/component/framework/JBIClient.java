/**
 * 
 */
package org.ow2.petals.component.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.JBIException;
import javax.jbi.component.ComponentContext;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessageExchangeFactory;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;

import org.ow2.petals.component.framework.message.ExchangeImpl;
import org.petalslink.dsb.service.client.Client;
import org.petalslink.dsb.service.client.ClientException;
import org.petalslink.dsb.service.client.Message;
import org.petalslink.dsb.service.client.MessageImpl;
import org.petalslink.dsb.service.client.MessageListener;
import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public class JBIClient implements Client {

    private ComponentContext componentContext;

    private Logger logger;

    private MessageExchangeFactory messageExchangeFactory;

    public JBIClient(ComponentContext componentContext) {
        this.componentContext = componentContext;
        // TODO : initialize somewhere else
        try {
            this.messageExchangeFactory = this.componentContext.getDeliveryChannel()
                    .createExchangeFactory();
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
    
    /* (non-Javadoc)
     * @see org.petalslink.dsb.service.client.Client#getName()
     */
    public String getName() {
        return "client-" + componentContext.getComponentName();
    }

    public void fireAndForget(Message message) throws ClientException {
        // create a internal message from the given one...
        // this is a send operation
        try {
            MessageExchange messageExchange = this.messageExchangeFactory
                    .createInOptionalOutExchange();
            this.send(message, new ExchangeImpl(messageExchange), message.getOperation(), false);
        } catch (MessagingException e) {
            throw new ClientException(e);
        }
    }

    public Message sendReceive(Message message) throws ClientException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("SendReceive message");
        }
        Message result = null;
        // create a internal message from the given one...
        try {
            MessageExchange messageExchange = this.messageExchangeFactory.createInOutExchange();
            Document out = this.send(message, new ExchangeImpl(messageExchange),
                    message.getOperation(), true);
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Messager has been sent, we have a response");
            }
            result = createOutMessage(out);
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Out message is " + result);
            }
        } catch (MessagingException e) {
            throw new ClientException(e);
        } finally {
        }
        return result;
    }

    protected Message createOutMessage(final Document out) {
        Message message = new MessageImpl();
        message.setPayload(out);
        return message;
    }

    /**
     * Send a DOM document and get a result as DOM document too...
     * 
     * @param payload
     * @param messageExchange
     * @param operation
     * @param synchronous
     * @return
     * @throws MessagingException
     */
    private Document send(final Message message, final ExchangeImpl messageExchange,
            final QName operation, final boolean synchronous) throws MessagingException,
            ClientException {
        NormalizedMessage normalizedMessage = messageExchange.getInMessage();
        messageExchange.setInMessageContent(message.getPayload());
        Map<String, String> props = message.getProperties();
        if (props != null) {
            for (String key : props.keySet()) {
                normalizedMessage.setProperty(key, props.get(key));
            }
        }

        // addressing stuff
        Map<QName, String> addressing = getAddressing(message);
        if (addressing.size() > 0) {
            messageExchange.setInAddressing(addressing);
        }

        messageExchange.setOperation(operation);

        // set the endpoint to consume
        if (message.getInterface() != null) {
            messageExchange.setInterfaceName(message.getInterface());
        } else {
            // exception to handle
        }
        
        messageExchange.setService(message.getService());

        /*
         * WHY? javax.jbi.servicedesc.ServiceEndpoint toCall = null; if
         * (serviceEndpoint.getServiceName() != null) {
         * messageExchange.setService(serviceEndpoint.getServiceName()); if
         * (serviceEndpoint.getEndpointName() != null) { toCall =
         * componentContext.getEndpoint(serviceEndpoint.getServiceName(),
         * serviceEndpoint.getEndpointName()); }
         * messageExchange.setEndpoint(toCall); }
         */
        if (!synchronous) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Let's send the message to the delivery channel with send()...");
            }
            this.componentContext.getDeliveryChannel().send(messageExchange.getMessageExchange());
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Let's send the message to the delivery channel with sendSync()");
            }
            boolean invoke = this.componentContext.getDeliveryChannel().sendSync(
                    messageExchange.getMessageExchange());
            if (invoke) {
                if (ExchangeStatus.ERROR.equals(messageExchange.getStatus())) {
                    throw createFromOutMessage(messageExchange.getMessageExchange());
                } else if (ExchangeStatus.ACTIVE.equals(messageExchange.getStatus())) {
                    normalizedMessage = messageExchange.getFault();
                    if (messageExchange.isOutMessage()) {
                        normalizedMessage = messageExchange.getOutMessage();
                    } else {
                        // fault

                    }

                    // close the sent exchange
                    messageExchange.setDoneStatus();
                    this.componentContext.getDeliveryChannel().send(
                            messageExchange.getMessageExchange());
                    if (normalizedMessage != null) {
                        return messageExchange.getOutMessageContentAsDocument();
                    } else {
                        //
                        System.out.println("No OUT... message");
                    }
                }

            } else {
                throw createFromOutMessage(messageExchange.getMessageExchange());
            }
        }
        return null;
    }

    public void sendAsync(Message message, MessageListener listener) throws ClientException {
        // dummy implementation, do it in a completely separated thread!
        Message out = sendReceive(message);
        if (listener != null && out != null) {
            listener.onMessage(out);
        }

    }

    protected ClientException createFromOutMessage(MessageExchange messageExchange) {
        ClientException result = null;
        String message = null;
        if (ExchangeStatus.ERROR.equals(messageExchange.getStatus())) {
            message = "Got an ERROR while trying to send message to service, cause : TODO serialize ERROR";
            result = new ClientException(message);
        } else if (messageExchange.getFault() != null) {
            message = "Got a FAULT while invoking service, cause : TODO serialize Fault";
            // TODO : Create Document from Fault
            result = new ClientException(message);
        } else {
            message = "Got something wrong while invoking service...";
            result = new ClientException(message);
        }
        return result;
    }

    protected Map<QName, String> getAddressing(Message message) {
        Map<QName, String> result = new HashMap<QName, String>();
        if (message != null && message.getProperties() != null) {
            // get all the addressing parameters from the message properties...
            Map<String, String> props = message.getProperties();
            if (props
                    .get(org.ow2.petals.component.framework.api.Constants.WSStar.Addressing.TO_QNAME
                            .toString()) != null) {
                result.put(
                        org.ow2.petals.component.framework.api.Constants.WSStar.Addressing.TO_QNAME,
                        props.get(org.ow2.petals.component.framework.api.Constants.WSStar.Addressing.TO_QNAME
                                .toString()));
            }
        }
        return result;
    }
}
