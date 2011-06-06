/**
 * 
 */
package org.petalslink.dsb.servicepoller.api;

import javax.jws.WebMethod;
import javax.jws.WebService;

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
     * @param inputMessage
     *            an optionall input message (a DOM document serialized in
     *            String for now)
     */
    @WebMethod
    void start(ServicePollerInformation toPoll, DocumentHandler inputMessage,
            String cronExpression, ServicePollerInformation replyTo) throws ServicePollerException;

    /**
     * Stop polling the given service if it exists...
     * 
     * @param endpointName
     * @param service
     * @param itf
     * @param operation
     */
    @WebMethod
    void stop(ServicePollerInformation toPoll, ServicePollerInformation replyTo)
            throws ServicePollerException;

}
