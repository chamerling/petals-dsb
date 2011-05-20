/**
 * 
 */
package org.petalslink.dsb.service.poller.api;

import org.w3c.dom.Document;

/**
 * Transport used to send message to services
 * 
 * @author chamerling
 * 
 */
public interface PollingTransport {
    
    /**
     * Send the input message to the given service
     * 
     * @param inputMessage
     * @param service
     * @return
     */
    Document send(Document inputMessage, ServiceInformation service);

}
