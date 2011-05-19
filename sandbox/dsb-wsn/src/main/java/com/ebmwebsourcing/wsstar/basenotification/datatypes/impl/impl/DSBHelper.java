/**
 * 
 */
package com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe;

/**
 * @author chamerling
 *
 */
public class DSBHelper {
    
    public static Subscribe asModel(com.ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe subscribe) {
        return new SubscribeImpl(subscribe);
    }
    
    public static Notify asModel(com.ebmwebsourcing.wsstar.jaxb.notification.base.Notify notify) {
        return new NotifyImpl(notify);
    }

}
