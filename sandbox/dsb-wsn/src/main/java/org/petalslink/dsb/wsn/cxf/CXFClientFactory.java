/**
 * 
 */
package org.petalslink.dsb.wsn.cxf;

import org.petalslink.dsb.wsn.api.NotificationConsumerService;
import org.petalslink.dsb.wsn.api.NotificationConsumerServiceStr;
import org.petalslink.dsb.wsn.api.NotificationProducerService;
import org.petalslink.dsb.wsn.api.NotificationProducerServiceStr;

/**
 * @author chamerling
 *
 */
public class CXFClientFactory {
    
    public static NotificationConsumerService getClient(String address) {
        // TODO : cache to avoid creation each time...
        return new NotificationConsumerServiceClientImpl(address);
    }
    
    public static NotificationProducerService getProducerClient(String address) {
        // TODO : cache to avoid creation each time...
        return new NotificationProducerServiceClientImpl(address);
    }
    
    public static NotificationConsumerServiceStr getClientSTr(String address) {
        // TODO : cache to avoid creation each time...
        return new NotificationConsumerServiceClientStrImpl(address);
    }
    
    public static NotificationProducerServiceStr getProducerClientStr(String address) {
        // TODO : cache to avoid creation each time...
        return new NotificationProducerServiceClientStrImpl(address);
    }

}
