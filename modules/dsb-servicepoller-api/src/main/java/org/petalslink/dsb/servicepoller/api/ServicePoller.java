/**
 * 
 */
package org.petalslink.dsb.servicepoller.api;

import javax.jws.WebMethod;
import javax.xml.namespace.QName;

import org.w3c.dom.Document;

/**
 * Standard API definition which hides the data handler handling and just
 * provide a DOM based API.
 * 
 * @author chamerling
 * 
 */
public interface ServicePoller {

    /**
     * Start polling the given service with the given information
     * 
     * @param endpointName
     * @param service
     * @param itf
     * @param operation
     * @param inputMessage
     *            an optionall input message (a DOM document serialized in
     *            String for now)
     */
    @WebMethod
    void start(String endpointName, QName service, QName itf, QName operation, Document inputMessage)
            throws ServicePollerException;

    /**
     * Stop polling the given service if it exists...
     * 
     * @param endpointName
     * @param service
     * @param itf
     * @param operation
     */
    @WebMethod
    void stop(String endpointName, QName service, QName itf, QName operation)
            throws ServicePollerException;

}
