/**
 * 
 */
package org.petalslink.dsb.wsn.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.transform.stream.StreamSource;

/**
 * @author chamerling
 *
 */
@WebService
public interface NotificationProducerDOM {

    @WebMethod
    void subcribe(@WebParam()StreamSource document);
}
