/**
 * 
 */
package org.petalslink.dsb.notification.client.http;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import org.petalslink.dsb.service.client.Client;
import org.petalslink.dsb.service.client.ClientException;
import org.petalslink.dsb.service.client.Message;
import org.w3c.dom.Document;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.WsnbConstants;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.GetCurrentMessage;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.GetCurrentMessageResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.SubscribeResponse;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducer;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;
import com.ebmwebsourcing.wsstar.wsrfbf.services.faults.AbsWSStarFault;

/**
 * @author chamerling
 * 
 */
public class HTTPNotificationProducerClient implements INotificationProducer {

    static Logger logger = Logger.getLogger(HTTPNotificationProducerClient.class.getName());

    private String endpoint;

    /**
     * 
     */
    public HTTPNotificationProducerClient(String endpoint) {
        this.endpoint = endpoint;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducer#
     * getCurrentMessage
     * (com.ebmwebsourcing.wsstar.basenotification.datatypes.api
     * .abstraction.GetCurrentMessage)
     */
    public GetCurrentMessageResponse getCurrentMessage(GetCurrentMessage getCurrentMessage)
            throws WsnbException, AbsWSStarFault {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Sending getCurrentMessage message to " + endpoint);
        }

        if (getCurrentMessage == null) {
            throw new WsnbException("getCurrentMessage message can not be null");
        }

        final Document payload = Wsnb4ServUtils.getWsnbWriter().writeGetCurrentMessageAsDOM(
                getCurrentMessage);

        Client client = new org.petalslink.dsb.service.client.saaj.Client();
        Message response = null;
        try {
            response = client.sendReceive(new Message() {

                public QName getService() {
                    return null;
                }

                public Map<String, String> getProperties() {
                    return null;
                }

                public Document getPayload() {
                    return payload;
                }

                public QName getOperation() {
                    return WsnbConstants.GET_CURRENT_MESSAGE_QNAME;
                }

                public QName getInterface() {
                    return null;
                }

                public Map<String, Document> getHeaders() {
                    return null;
                }

                public String getEndpoint() {
                    return endpoint;
                }

                public String getProperty(String name) {
                    // TODO Auto-generated method stub
                    return null;
                }

                public void setProperty(String name, String value) {
                    // TODO Auto-generated method stub
                    
                }
            });
        } catch (ClientException e) {
            e.printStackTrace();
            throw new WsnbException(e.getMessage());
        }
        if (response == null || response.getPayload() == null) {
            throw new WsnbException("Can not get any response from service");
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("getCurrentMessage response : ");
            try {
                logger.fine(XMLHelper.createStringFromDOMDocument(response.getPayload()));
            } catch (TransformerException e) {
            }
        }
        return Wsnb4ServUtils.getWsnbReader().readGetCurrentMessageResponse(response.getPayload());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducer#subscribe
     * (com
     * .ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe
     * )
     */
    public SubscribeResponse subscribe(Subscribe subscribe) throws WsnbException, AbsWSStarFault {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Sending subscribe message to " + endpoint);
        }

        if (subscribe == null) {
            throw new WsnbException("Subscribe message can not be null");
        }

        final Document payload = Wsnb4ServUtils.getWsnbWriter().writeSubscribeAsDOM(subscribe);

        Client client = new org.petalslink.dsb.service.client.saaj.Client();
        Message response = null;
        try {
            response = client.sendReceive(new Message() {

                public QName getService() {
                    return null;
                }

                public Map<String, String> getProperties() {
                    return null;
                }

                public Document getPayload() {
                    return payload;
                }

                public QName getOperation() {
                    return WsnbConstants.SUBSCRIBE_QNAME;
                }

                public QName getInterface() {
                    return null;
                }

                public Map<String, Document> getHeaders() {
                    return null;
                }

                public String getEndpoint() {
                    return endpoint;
                }

                public String getProperty(String name) {
                    // TODO Auto-generated method stub
                    return null;
                }

                public void setProperty(String name, String value) {
                    // TODO Auto-generated method stub
                    
                }
            });
        } catch (ClientException e) {
            e.printStackTrace();
            throw new WsnbException(e.getMessage());
        }
        if (response == null || response.getPayload() == null) {
            throw new WsnbException("Can not ger any response from service");
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Subscribre response : ");
            try {
                logger.fine(XMLHelper.createStringFromDOMDocument(response.getPayload()));
            } catch (TransformerException e) {
            }
        }
        return Wsnb4ServUtils.getWsnbReader().readSubscribeResponse(response.getPayload());
    }
}
