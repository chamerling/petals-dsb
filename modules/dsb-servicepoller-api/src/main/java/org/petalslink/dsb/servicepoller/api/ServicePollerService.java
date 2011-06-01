/**
 * 
 */
package org.petalslink.dsb.servicepoller.api;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.namespace.QName;

/**
 * @author chamerling
 * 
 */
@WebService
public interface ServicePollerService {

    /**
     * Start polling the given service with the given information
     * 
     * @param endpointName
     * @param service
     * @param itf
     * @param operation
     * @param inputMessage an optionall input message (a DOM document serialized in String for now)
     */
    @WebMethod
    void start(String endpointName, QName service, QName itf, QName operation, DocumentHandler inputMessage);

    /**
     * Stop polling the given service if it exists...
     * 
     * @param endpointName
     * @param service
     * @param itf
     * @param operation
     */
    @WebMethod
    void stop(String endpointName, QName service, QName itf, QName operation);

}
