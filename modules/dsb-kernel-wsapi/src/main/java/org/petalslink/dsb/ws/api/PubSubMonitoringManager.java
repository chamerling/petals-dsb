/**
 * 
 */
package org.petalslink.dsb.ws.api;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.namespace.QName;

/**
 * @author chamerling
 * 
 */
@WebService
public interface PubSubMonitoringManager {

    @WebMethod
    QName getTopic();

    @WebMethod
    boolean getState();

    @WebMethod
    void setState(boolean state);
}
