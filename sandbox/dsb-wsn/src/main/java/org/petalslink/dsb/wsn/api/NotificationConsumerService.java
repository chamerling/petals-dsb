/**
 * 
 */
package org.petalslink.dsb.wsn.api;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.ebmwebsourcing.wsstar.jaxb.notification.base.Notify;

/**
 * A notificaiton consumer, ie the one which is receiving notifications
 * 
 * @author chamerling
 * 
 */
@WebService
public interface NotificationConsumerService {

    /**
     * 
     * @param notify
     */
    @WebMethod
    void notify(Notify notify);

}
