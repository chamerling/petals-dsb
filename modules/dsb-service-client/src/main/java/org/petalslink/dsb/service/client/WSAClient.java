/**
 * 
 */
package org.petalslink.dsb.service.client;

import javax.xml.namespace.QName;

import org.petalslink.dsb.api.WSAConstants;

/**
 * @author chamerling
 * 
 */
public abstract class WSAClient implements Client {

    // FIXME : THis comes from the CDK and should me in an API somewhere...

    public static final String NAMESPACE_URI = "http://www.w3.org/2005/08/addressing";

    public static final String PREFIX = "wsa";

    public static final QName TO_QNAME = new QName(NAMESPACE_URI, "To", PREFIX);

    private String protocol;

    private String to;

    /**
     * 
     */
    public WSAClient(String protocol, String to) {
        this.protocol = protocol;
        this.to = to;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.service.client.Client#fireAndForget(org.petalslink
     * .dsb.service.client.Message)
     */
    public void fireAndForget(Message message) throws ClientException {
        updateMessage(message);
        doFireAndForget(message);
    }

    /**
     * @param message
     */
    protected abstract void doFireAndForget(Message message) throws ClientException;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.service.client.Client#sendReceive(org.petalslink.dsb
     * .service.client.Message)
     */
    public Message sendReceive(Message message) throws ClientException {
        updateMessage(message);
        return doSendReceive(message);
    }

    /**
     * @param message
     * @return
     */
    protected abstract Message doSendReceive(Message message) throws ClientException;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.service.client.Client#sendAsync(org.petalslink.dsb
     * .service.client.Message,
     * org.petalslink.dsb.service.client.MessageListener)
     */
    public void sendAsync(Message message, MessageListener listener) throws ClientException {
        updateMessage(message);
        doSendAsync(message, listener);
    }

    /**
     * Set the right data for WSA calls...
     * 
     * @param message
     */
    private void updateMessage(Message message) {
        String ns = String.format(WSAConstants.NS_TEMPLATE, protocol);
        String serviceName = WSAConstants.SERVICE_NAME;
        String itfName = WSAConstants.INTERFACE_NAME;
        String ep = WSAConstants.ENDPOINT_NAME;
        final QName service = new QName(ns, serviceName);
        final QName interfaceQName = new QName(ns, itfName);
        final String endpoint = ep;
        
        message.setService(service);
        message.setEndpoint(endpoint);
        message.setInterface(interfaceQName);
        message.setProperty(TO_QNAME.toString(), to);
    }

    /**
     * @param message
     * @param listener
     */
    protected abstract void doSendAsync(Message message, MessageListener listener)
            throws ClientException;

}
