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
public interface ServiceArtefactsInformationService {

    @WebMethod
    @WebResult(name = "saNames")
    Set<String> getSAs() throws DSBWebServiceException;

    @WebMethod
    @WebResult(name = "suNames")
    Set<String> getSUForSA(@WebParam(name = "saName") String saName) throws DSBWebServiceException;

    @WebMethod
    @WebResult(name = "jbiDescription")
    String getSUDescription(@WebParam(name = "saName") String saName,
            @WebParam(name = "suName") String suName) throws DSBWebServiceException;

    @WebMethod
    @WebResult(name = "suNames")
    Set<String> getSUForComponent(@WebParam(name = "componentName") String componentName)
            throws DSBWebServiceException;

    @WebMethod
    @WebResult(name = "jbiDescription")
    String getSADescription(@WebParam(name = "saName") String saName) throws DSBWebServiceException;
}
