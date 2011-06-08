/**
 * 
 */
package org.petalslink.dsb.ws.api.jbi;

import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.petalslink.dsb.ws.api.DSBWebServiceException;

/**
 * @author chamerling
 * 
 */
@WebService
public interface ComponentInformationService {

    /**
     * Get a list of embedded component WSDLs.
     * 
     * @param componentName
     * @return a list of XML strings
     * @throws DSBWebServiceException
     */
    @WebMethod
    @WebResult(name = "wsdls")
    Set<String> getComponentWSDL(@WebParam(name = "componentName") String componentName)
            throws DSBWebServiceException;

    /**
     * Get the JBI description of the given component.
     * 
     * @param componentName
     * @return a XML string
     * @throws DSBWebServiceException
     */
    @WebMethod
    @WebResult(name = "jbiDescription")
    String getComponentDescription(@WebParam(name = "componentName") String componentName)
            throws DSBWebServiceException;

    /**
     * Get the names of all the components available in the DSB. No matter about
     * their state.
     * 
     * @return
     * @throws DSBWebServiceException
     */
    @WebMethod
    @WebResult(name = "componentNames")
    Set<String> getComponentNames() throws DSBWebServiceException;

}
