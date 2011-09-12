/**
 * 
 */
package org.petalslink.dsb.service.client;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;

import org.petalslink.dsb.api.WSAConstants;

/**
 * @author chamerling
 * 
 */
public class WSAMessageImpl extends MessageImpl {

    public static final String NAMESPACE_URI = "http://www.w3.org/2005/08/addressing";

    public static final String PREFIX = "wsa";

    public static final QName TO_QNAME = new QName(NAMESPACE_URI, "To", PREFIX);

    private String to;

    private String protocol;

    /**
     * 
     */
    public WSAMessageImpl(String to) {
        super();
        this.to = to;
        try {
            URI uri = URI.create(to);
            this.protocol = uri.getScheme();
        } catch (Exception e) {
        }
        this.setProperty(TO_QNAME.toString(), this.to);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.service.client.MessageImpl#getEndpoint()
     */
    @Override
    public String getEndpoint() {
        return WSAConstants.ENDPOINT_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.service.client.MessageImpl#getInterface()
     */
    @Override
    public QName getInterface() {
        return new QName(String.format(WSAConstants.NS_TEMPLATE, protocol),
                WSAConstants.INTERFACE_NAME);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.service.client.MessageImpl#getService()
     */
    @Override
    public QName getService() {
        return new QName(String.format(WSAConstants.NS_TEMPLATE, protocol),
                WSAConstants.SERVICE_NAME);
    }

}
