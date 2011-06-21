/**
 * 
 */
package org.petalslink.dsb.ws.api;

import java.util.List;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * This service is in charge of binding external Web services to the DSB. It is
 * up to the DSB to manage the bindings.
 * 
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
     * @return the DSB services information. These services are created by the bind operation.
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    List<ServiceEndpoint> bindWebService(@WebParam(name = "wsdlURL") String wsdlURL)
            throws DSBWebServiceException;

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
            throws DSBWebServiceException;

    /**
     * Get a list of bound Web services
     * 
     * @return a list of Web service WSDL which are bound to the DSB
     * @throws PEtALSWebServiceException
     */
    @WebMethod
    Set<String> getWebServices() throws DSBWebServiceException;

}
