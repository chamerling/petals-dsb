/**
 * 
 */
package org.petalslink.dsb.servicepoller.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
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
     * @return an ID which have to be used in other operations...
     */
    @WebMethod
    String start(@WebParam(name = "toPoll") ServicePollerInformation toPoll,
            @WebParam(name = "inputMessage") DocumentHandler inputMessage,
            @WebParam(name = "cronExpression") String cronExpression,
            @WebParam(name = "sendReplyTo") ServicePollerInformation replyTo)
            throws ServicePollerException;

    /**
     * 
     * @param id
     * @return
     * @throws ServicePollerException
     */
    @WebMethod
    boolean stop(String id) throws ServicePollerException;

    /**
     * 
     * @param id
     * @return
     * @throws ServicePollerException
     */
    @WebMethod
    boolean pause(String id) throws ServicePollerException;

    /**
     * 
     * @param id
     * @return
     * @throws ServicePollerException
     */
    @WebMethod
    boolean resume(String id) throws ServicePollerException;

}
