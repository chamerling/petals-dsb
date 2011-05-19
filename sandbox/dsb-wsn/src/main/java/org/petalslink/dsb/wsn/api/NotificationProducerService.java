/**
 * 
 */
package org.petalslink.dsb.wsn.api;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.ebmwebsourcing.wsstar.jaxb.notification.base.GetCurrentMessage;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.GetCurrentMessageResponse;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.SubscribeResponse;


/**
 * A Web service API for the producer service
 * 
 * @author chamerling
 *
 */
@WebService
public interface NotificationProducerService {
    
    @WebMethod
    SubscribeResponse subscribe(Subscribe request);
    
    @WebMethod
    GetCurrentMessageResponse getCurrentMessage(GetCurrentMessage request);


}
