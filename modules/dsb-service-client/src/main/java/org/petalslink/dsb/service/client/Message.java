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
     * The message headers
     * 
     * @return
     */
    Map<String, Document> getHeaders();

    QName getService();

    QName getInterface();

    String getEndpoint();

}
