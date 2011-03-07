/**
 * 
 */
package org.petalslink.dsb.ws.api;

import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;

/**
 * @author chamerling
 *
 */
@WebService
public interface SOAPServiceBinder {

    /**
     * Bind a Web service to the DSB
     * 
     * @param wsdlURL
     *            the WSDL of the Web service to bind to the DSB
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    boolean bindWebService(@WebParam(name = "wsdlURL") String wsdlURL)
            throws PEtALSWebServiceException;

    /**
     * Unbind an already bound service
     * 
     * @param wsdlURL
     *            The WSDL of the Web service to unbind
     * 
     * @return
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    boolean unbindWebService(@WebParam(name = "wsdlURL") String wsdlURL)
            throws PEtALSWebServiceException;

    /**
     * Get a list of bound Web services
     * 
     * @return a list of Web service WSDL which are bound to the DSB
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    Set<String> getWebServices() throws PEtALSWebServiceException;

}
