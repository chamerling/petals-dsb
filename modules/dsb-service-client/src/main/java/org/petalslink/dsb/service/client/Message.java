/**
 * 
 */
package org.petalslink.dsb.service.client;

import java.util.Map;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public interface Message {

    /**
     * The effective message payload
     * 
     * @return
     */
    Document getPayload();

    /**
     * The message operation
     * 
     * @return
     */
    QName getOperation();

    /**
     * 
     * @return
     */
    Map<String, String> getProperties();

    /**
     * 
     * @return
     */
    String getProperty(String name);

    /**
     * 
     */
    void setProperty(String name, String value);

    /**
     * The message headers
     * 
     * @return
     */
    Map<String, Document> getHeaders();

    /**
     * Targert service
     * 
     * @return
     */
    QName getService();

    /**
     * Target interface
     * 
     * @return
     */
    QName getInterface();

    /**
     * Target endpoint
     * 
     * @return
     */
    String getEndpoint();

    /**
     * @param endpoint
     */
    void setEndpoint(String endpoint);

    /**
     * @param interfaceQName
     */
    void setInterface(QName interfaceQName);

    /**
     * @param service
     */
    void setService(QName service);

    /**
     * @param out
     */
    void setPayload(Document payload);
    
    /**
     * 
     * @param operation
     */
    void setOperation(QName operation);

}
