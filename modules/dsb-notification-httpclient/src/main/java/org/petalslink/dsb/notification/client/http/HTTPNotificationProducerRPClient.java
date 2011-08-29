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
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.abstraction.GetResourcePropertyResponse;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.abstraction.UpdateResourceProperties;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.abstraction.UpdateResourcePropertiesResponse;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.utils.WsrfrpException;
import com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducerRP;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;
import com.ebmwebsourcing.wsstar.wsrfbf.services.faults.AbsWSStarFault;

/**
 * @author chamerling
 * 
 */
public class HTTPNotificationProducerRPClient implements INotificationProducerRP {

    static Logger logger = Logger.getLogger(HTTPNotificationProducerRPClient.class.getName());

    private String endpoint;

    /**
     * 
     */
    public HTTPNotificationProducerRPClient(String endpoint) {
        this.endpoint = endpoint;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducerRP#
     * getResourceProperty(javax.xml.namespace.QName)
     */
    public GetResourcePropertyResponse getResourceProperty(QName getResourceProperty)
            throws WsnbException, AbsWSStarFault {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Sending getResourceProperty message to " + endpoint);
        }

        if (getResourceProperty == null) {
            throw new WsnbException("getResourceProperty message can not be null");
        }

        Document payload = null;
        try {
            payload = Wsnb4ServUtils.getWsrfrpWriter().writeGetResourcePropertyAsDOM(
                    getResourceProperty);
        } catch (WsrfrpException e1) {
            throw new WsnbException(e1.getMessage());
        }

        final Document p = payload;
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
                    return p;
                }

                public QName getOperation() {
                    return new QName(
                            com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.WsrfrpConstants.WS_RESOURCE_PROPERTIES_NAMESPACE_URI,
                            "GetResourceProperty");
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
            });
        } catch (ClientException e) {
            e.printStackTrace();
            throw new WsnbException(e.getMessage());
        }
        if (response == null || response.getPayload() == null) {
            throw new WsnbException("Can not get any response from service");
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("getResourceProperty response : ");
            try {
                logger.fine(XMLHelper.createStringFromDOMDocument(response.getPayload()));
            } catch (TransformerException e) {
            }
        }
        try {
            return Wsnb4ServUtils.getWsrfrpReader().readGetResourcePropertyResponse(
                    response.getPayload());
        } catch (WsrfrpException e) {
            throw new WsnbException(e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ebmwebsourcing.wsstar.wsnb.services.INotificationProducerRP#
     * updateResourceProperties
     * (com.ebmwebsourcing.wsstar.resourceproperties.datatypes
     * .api.abstraction.UpdateResourceProperties)
     */
    public UpdateResourcePropertiesResponse updateResourceProperties(
            UpdateResourceProperties updateResourceProperties) throws WsnbException, AbsWSStarFault {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Sending updateResourceProperties message to " + endpoint);
        }

        if (updateResourceProperties == null) {
            throw new WsnbException("updateResourceProperties message can not be null");
        }

        Document payload = null;
        try {
            payload = Wsnb4ServUtils.getWsrfrpWriter().writeUpdateResourcePropertiesAsDOM(
                    updateResourceProperties);
        } catch (WsrfrpException e1) {
            throw new WsnbException(e1.getMessage());
        }

        final Document p = payload;
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
                    return p;
                }

                public QName getOperation() {
                    return com.ebmwebsourcing.wsstar.resourceproperties.datatypes.api.WsrfrpConstants.UPDATE_RESOURCE_PROPERTIES_QNAME;
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
            });
        } catch (ClientException e) {
            e.printStackTrace();
            throw new WsnbException(e.getMessage());
        }
        if (response == null || response.getPayload() == null) {
            throw new WsnbException("Can not get any response from service");
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("updateResourceProperties response : ");
            try {
                logger.fine(XMLHelper.createStringFromDOMDocument(response.getPayload()));
            } catch (TransformerException e) {
            }
        }
        try {
            return Wsnb4ServUtils.getWsrfrpReader().readUpdateResourcePropertiesResponse(
                    response.getPayload());
        } catch (WsrfrpException e) {
            throw new WsnbException(e.getMessage());
        }
    }
}
