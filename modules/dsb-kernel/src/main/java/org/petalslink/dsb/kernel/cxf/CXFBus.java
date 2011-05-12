/**
 * 
 */
package org.petalslink.dsb.kernel.cxf;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;

/**
 * This class is used to preconfigure the CXF Bus with the right Petals
 * configuration. TODO : Singleton initialization checking at startup
 * 
 * @author chamerling
 * 
 */
public class CXFBus {

    private static CXFBus instance = new CXFBus();

    public static CXFBus getInstance() {
        return CXFBus.instance;
    }

    private Bus bus;

    private DSBTransportFactory petalsTransportFactory;

    private CXFBus() {
        // Note : We can check that the factory are well filled here instead of waiting later...
        this.bus = BusFactory.getDefaultBus();
        this.petalsTransportFactory = new DSBTransportFactory();
        this.petalsTransportFactory.setBus(this.bus);
        this.petalsTransportFactory.registerWithBindingManager();
    }

}
