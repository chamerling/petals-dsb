/**
 * 
 */
package org.petalslink.dsb.jbi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.JBIException;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessageExchangeFactory;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;

import org.ow2.petals.jbi.component.context.ComponentContext;
import org.ow2.petals.kernel.api.service.Location;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.service.client.Client;
import org.petalslink.dsb.service.client.ClientException;
import org.petalslink.dsb.service.client.Constants;
import org.petalslink.dsb.service.client.Message;
import org.petalslink.dsb.service.client.MessageListener;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

import com.ebmwebsourcing.easycommons.xml.DocumentBuilders;

/**
 * Send messages through JBI
 * 
 * @author chamerling
 * 
 */
public class JBISender implements Client {

    public static final String PROTOCOL_HEADERS = "javax.jbi.messaging.protocol.headers";

    public static final String NAMESPACE_URI = "http://www.w3.org/2005/08/addressing";

    public static final String PREFIX = "wsa";

    public static final QName TO_QNAME = new QName(NAMESPACE_URI, "To", PREFIX);

    public static final QName ADDRESS_QNAME = new QName(NAMESPACE_URI, "Address", PREFIX);

    private ComponentContext componentContext;

    private Logger logger;

    private MessageExchangeFactory messageExchangeFactory;

    private ServiceEndpoint serviceEndpoint;

    private String name;

    /**
     * 
     * @param componentContext
     * @param endpoint
     */
    public JBISender(ComponentContext componentContext, ServiceEndpoint endpoint) {
        this.componentContext = componentContext;
        this.serviceEndpoint = endpoint;
        this.name = this.componentContext.getComponentName();
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
        try {
            MessageExchange messageExchange = this.messageExchangeFactory
                    .createInOptionalOutExchange();
            this.send(message, messageExchange, message.getOperation(), false);
        } catch (MessagingException e) {
            throw new ClientException(e);
        }
    }

    public Message sendReceive(Message message) throws ClientException {
        System.out.println("Send Receive");
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("SendReceive message");
        }
        Message result = null;
        // create a internal message from the given one...
        try {
            MessageExchange messageExchange = this.messageExchangeFactory.createInOutExchange();
            result = this.send(message, messageExchange, message.getOperation(), true);
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Messager has been sent, we have a response");
                logger.fine("Out message is " + result);
            }
        } catch (MessagingException e) {
            throw new ClientException(e);
        }
        return result;
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
    private Message send(final Message message, final MessageExchange messageExchange,
            final QName operation, final boolean synchronous) throws MessagingException,
            ClientException {
        NormalizedMessage normalizedMessage = Adapter.transform(message);
        messageExchange.setMessage(normalizedMessage, "in");
        messageExchange.setOperation(operation);

        // set the message properties
        Map<String, String> props = message.getProperties();
        if (props != null) {
            for (String key : props.keySet()) {
                normalizedMessage.setProperty(key, props.get(key));
            }
        }

        // set the endpoint to consume
        if (serviceEndpoint.getInterfaces() != null && serviceEndpoint.getInterfaces().length >= 1) {
            messageExchange.setInterfaceName(serviceEndpoint.getInterfaces()[0]);
        } else {
            // exception to handle
        }

        // set the addressing stuff
        Map<QName, String> addressing = getAddressing(message);
        if (addressing.size() > 0) {
            setInAddressing(normalizedMessage, addressing);
        }

        // FIXME : Check that null check!!!
        // if cleanendpoint is set to true, ask the dsb to check the registry to
        // fiind the endpoint, else set the current one... We also need to set
        // the location...
        if (!Boolean.parseBoolean(message.getProperty(Constants.CLIENT_CLEAN_ENDPOINT))) {
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
                            return EndpointType.EXTERNAL;
                        }

                        public void setType(EndpointType type) {
                            // TODO Auto-generated method stub
                        }

                        public List<QName> getInterfacesName() {
                            if (serviceEndpoint.getInterfaces() != null)
                                return Arrays.asList(serviceEndpoint.getInterfaces());
                            else 
                                return new ArrayList<QName>(0);
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
                        // TODO
                    }
                    // close the sent exchange
                    messageExchange.setStatus(ExchangeStatus.DONE);
                    this.componentContext.getDeliveryChannel().send(messageExchange);

                    // create the response
                    if (normalizedMessage != null) {
                        return Adapter.transform(normalizedMessage);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.service.client.Client#getName()
     */
    public String getName() {
        return this.name;
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

    public ComponentContext getComponentContext() {
        return this.componentContext;
    }

    protected Map<QName, String> getAddressing(Message message) {
        Map<QName, String> result = new HashMap<QName, String>();
        if (message != null && message.getProperties() != null) {
            // get all the addressing parameters from the message properties...
            Map<String, String> props = message.getProperties();
            if (props.get(TO_QNAME.toString()) != null) {
                result.put(TO_QNAME, props.get(TO_QNAME.toString()));
            }
        }
        return result;
    }

    /**
     * @param addressing
     * 
     */
    private void setInAddressing(NormalizedMessage message, Map<QName, String> addressing) {
        // get the protocol headers map
        final Object o = message.getProperty(PROTOCOL_HEADERS);

        // clone: The constructor HashMap(Map) clones each element
        final Map<QName, String> remains = new HashMap<QName, String>(addressing);

        Map<String, DocumentFragment> headers = null;
        // replace data from existing map
        if (o != null && o instanceof Map) {
            headers = (Map<String, DocumentFragment>) o;
            // get the addressing Document Fragment and replace things if
            // exists, else create new fragment and insert...
            for (final Map.Entry<QName, String> entry : addressing.entrySet()) {
                final QName key = entry.getKey();
                final DocumentFragment destFrag = headers.get(key.toString());
                if (destFrag != null && destFrag.getFirstChild() != null
                        && destFrag.getFirstChild() instanceof Element) {
                    destFrag.getFirstChild().setTextContent(entry.getValue());
                    remains.remove(key);
                }
            }
        } else {
            // create map
            headers = new HashMap<String, DocumentFragment>(remains.size());
        }

        // fill map with remaining data
        for (final Map.Entry<QName, String> entry : remains.entrySet()) {
            final QName key = entry.getKey();
            final DocumentFragment df = getElement(key, entry.getValue());
            if (df != null) {
                headers.put(key.toString(), df);
            }
        }

        // fill property
        message.setProperty(PROTOCOL_HEADERS, headers);
    }

    // FIXME : MUST BE IN A JBI COMMONS PROHECT

    public static final DocumentFragment getElement(QName qname, String text) {
        DocumentFragment result = null;
        if (qname.equals(TO_QNAME)) {
            result = getToFragment(text);
        } else {
            // TODO : To be continued
        }
        return result;
    }

    /**
     * 
     * @param text
     * @return
     */
    public static final DocumentFragment getToFragment(String text) {
        DocumentFragment result = createDocumentFragment(TO_QNAME);
        result.getFirstChild().setTextContent(text);
        result.normalize();
        return result;
    }

    /**
     * 
     * @param content
     * @return
     */
    protected static final Element createAddressElement(String content) {
        final DocumentBuilder docBuilder = DocumentBuilders.takeDocumentBuilder();
        final Document doc = docBuilder.newDocument();
        QName address = ADDRESS_QNAME;
        Element e = doc.createElementNS(address.getNamespaceURI(), address.getLocalPart());
        e.setPrefix(address.getPrefix());
        e.setTextContent(content);
        e.normalize();
        DocumentBuilders.releaseDocumentBuilder(docBuilder);
        return e;
    }

    /**
     * 
     * @param documentName
     * @return
     */
    protected static final DocumentFragment createDocumentFragment(QName documentName) {
        DocumentFragment result = null;
        final DocumentBuilder docBuilder = DocumentBuilders.takeDocumentBuilder();
        final Document doc = docBuilder.newDocument();
        final Element elt = doc.createElementNS(documentName.getNamespaceURI(),
                documentName.getLocalPart());
        elt.setPrefix(documentName.getPrefix());
        result = doc.createDocumentFragment();
        result.appendChild(doc.importNode(elt, true));
        result.normalize();
        DocumentBuilders.releaseDocumentBuilder(docBuilder);
        return result;
    }

}
