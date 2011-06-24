/**
 * 
 */
package org.petalslink.dsb.ukernel;

import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.kernel.api.lifecycle.LifeCycleException;

import junit.framework.TestCase;

/**
 * @author chamerling
 *
 */
public class ServiceBusTest extends TestCase {
    
    public void testInvoke() {
        ServiceBus bus = new ServiceBus("A", "localhost", 6666);
        
        try {
            bus.init();
        } catch (LifeCycleException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            bus.start();
        } catch (LifeCycleException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            bus.invoke(new ServiceEndpoint(), new MessageExchange());
        } catch (DSBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
