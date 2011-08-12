/**
 * 
 */
package org.petalslink.notification;

import junit.framework.TestCase;

import org.petalslink.dsb.commons.service.api.Service;
import org.petalslink.dsb.cxf.CXFHelper;
import org.petalslink.dsb.notification.jaxws.api.NotificationConsumer;

import com.ebmwebsourcing.wsstar.jaxb.notification.base.Notify;

/**
 * @author chamerling
 *
 */
public class NotificationProducerServiceTest extends TestCase {
	
	public void testSubscribe() throws Exception {
	    
	    Service server = CXFHelper.getServiceFromFinalURL("http://localhost:8889/foo/bar/Service", NotificationConsumer.class, new NotificationConsumer() {
            
            public void notify(Notify notify) {
                // TODO Auto-generated method stub
                
            }
        });
	    
	    server.start();
	    //Thread.sleep(1000000L);
		
	}

}
