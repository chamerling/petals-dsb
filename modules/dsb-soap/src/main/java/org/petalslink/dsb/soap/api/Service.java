/**
 * 
 */
package org.petalslink.dsb.soap.api;

import javax.xml.namespace.QName;

/**
 * @author chamerling
 * 
 */
public interface Service {

    /**
     * Get the WSDL description
     * 
     * @return
     */
    String getWSDLURL();

    /**
     * Get the service URL ie where to publish it...
     * 
     * @return
     */
    String getURL();

    QName getEndpoint();

    QName getInterface();

    QName getService();

    /**
     * Invoke the service
     * 
     * @param request
     * @param action
     */
    void invoke(SimpleExchange exchange) throws ServiceException;

}
