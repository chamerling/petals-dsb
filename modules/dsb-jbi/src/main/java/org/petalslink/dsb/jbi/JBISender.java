/**
 * 
 */
package org.petalslink.dsb.jbi;

import java.util.Arrays;
import java.util.List;
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
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.ow2.petals.kernel.api.service.Location;
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.service.client.Client;
import org.petalslink.dsb.service.client.ClientException;
import org.petalslink.dsb.service.client.Message;
import org.petalslink.dsb.service.client.MessageListener;
import org.petalslink.dsb.xmlutils.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

/**
 * Send messages through JBI
 * 
 * @author chamerling
 * 
 */
public class JBISender implements Client {

    public static final String CLEAN_ENDPOINT = "jbi.client.CLEAN_ENDPOINT";

    private ComponentContext componentContext;

    private Logger logger;

    private MessageExchangeFactory messageExchangeFactory;

    private ServiceEndpoint serviceEndpoint;

    public JBISender(ComponentContext componentContext, ServiceEndpoint endpoint) {
        this.componentContext = componentContext;
        this.serviceEndpoint = endpoint;
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

    public void fireAndForget(Message message) throws ClientException {
        // create a internal message from the given one...
        // this is a send operation
        try {
            MessageExchange messageExchange = this.messageExchangeFactory
                    .createInOptionalOutExchange();
            this.send(message, messageExchange, message.getOperation(), false);
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
            Document out = this.send(message, messageExchange, message.getOperation(), true);
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Messager has been sent, we have a response");
            }
            result = createOutMessage(out);
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Out message is " + result);
            }
        } catch (MessagingException e) {
            throw new ClientException(e);
        }
        return result;
    }

    protected Message createOutMessage(final Document out) {
        return new Message() {

            public Document getPayload() {
                return out;
            }

            public QName getOperation() {
                return null;
            }

            public Map<String, String> getProperties() {
                return null;
            }

            public Map<String, Document> getHeaders() {
                return null;
            }

            public QName getService() {
                return null;
            }

            public QName getInterface() {
                return null;
            }

            public String getEndpoint() {
                return null;
            }
        };
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
    private Document send(final Message message, final MessageExchange messageExchange,
            final QName operation, final boolean synchronous) throws MessagingException,
            ClientException {
        NormalizedMessage normalizedMessage = messageExchange.createMessage();
        Document payload = message.getPayload();
        payload.normalizeDocument();
        Source source = new DOMSource(payload);
        normalizedMessage.setContent(source);
        Map<String, String> props = message.getProperties();
        for (String key : props.keySet()) {
            normalizedMessage.setProperty(key, props.get(key));
        }

        messageExchange.setMessage(normalizedMessage, "in");
        messageExchange.setOperation(operation);

        // set the endpoint to consume
        if (serviceEndpoint.getInterfaces() != null && serviceEndpoint.getInterfaces().length >= 1) {
            messageExchange.setInterfaceName(serviceEndpoint.getInterfaces()[0]);
        } else {
            // exception to handle
        }

        if (!Boolean.parseBoolean(message.getProperties().get(CLEAN_ENDPOINT))) {
            // do we need to set the endpoit or not? It is up to the caller to
            // set that; For example, the core kernel service client need to set
            // informaiton about the service he wants to call but it is not a
            // service in the registry, so the service is not set in the message
            // exchange to avoid the standard router to query the registry
            messageExchange
                    .setEndpoint(new org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint() {

                        public QName getServiceName() {
                            return serviceEndpoint.getServiceName();
                        }

                        public QName[] getInterfaces() {
                            return serviceEndpoint.getInterfaces();
                        }

                        public String getEndpointName() {
                            return serviceEndpoint.getEndpointName();
                        }

                        public DocumentFragment getAsReference(QName operationName) {
                            return null;
                        }

                        public EndpointType getType() {
                            return EndpointType.INTERNAL;
                        }

                        public void setType(EndpointType type) {
                            // TODO Auto-generated method stub
                        }

                        public List<QName> getInterfacesName() {
                            return Arrays.asList(serviceEndpoint.getInterfaces());
                        }

                        public Document getDescription() {
                            return null;
                        }

                        public Location getLocation() {
                            return new Location();
                        }
                    });
        }

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
            this.componentContext.getDeliveryChannel().send(messageExchange);
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Let's send the message to the delivery channel with sendSync()");
            }
            boolean invoke = this.componentContext.getDeliveryChannel().sendSync(messageExchange);
            if (invoke) {
                if (ExchangeStatus.ERROR.equals(messageExchange.getStatus())) {
                    throw createFromOutMessage(messageExchange);
                } else if (ExchangeStatus.ACTIVE.equals(messageExchange.getStatus())) {
                    normalizedMessage = messageExchange.getFault();
                    if (normalizedMessage == null) {
                        // get out message
                        normalizedMessage = messageExchange.getMessage("out");
                    } else {
                        // fault

                    }
                    // close the sent exchange
                    messageExchange.setStatus(ExchangeStatus.DONE);
                    this.componentContext.getDeliveryChannel().send(messageExchange);
                    if (normalizedMessage != null) {
                        source = normalizedMessage.getContent();
                        if (source != null) {
                            try {
                                return XMLHelper.createDocument(source, false);
                            } catch (DSBException e) {
                                throw new ClientException("Can not create Document from response",
                                        e);
                            }
                        } else {
                            // TODO
                        }
                    } else {
                    }
                }

            } else {
                throw createFromOutMessage(messageExchange);
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

}
