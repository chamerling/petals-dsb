/**
 * 
 */
package org.petalslink.dsb.ukernel.utils;

import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.Phase;

import junit.framework.TestCase;

/**
 * @author chamerling
 * 
 */
public class LifeCycleHelperTest extends TestCase {

    public void testInit() throws Exception {
        Component component = new Component();
        LifeCycleHelper.invokeMethods(component, Phase.INIT);
        assertEquals(2, component.init);
        assertEquals(0, component.start);
    }
    
    public void testStart() throws Exception {
        Component component = new Component();
        LifeCycleHelper.invokeMethods(component, Phase.START);
        assertEquals(0, component.init);
        assertEquals(2, component.start);
    }

    class Component {

        protected int init = 0;

        protected int start = 0;

        @LifeCycleListener(phase = Phase.INIT)
        public void init() {
            System.out.println("Called init");
            init++;
        }

        @LifeCycleListener(phase = Phase.INIT)
        public void init2() {
            System.out.println("Called init2");
            init++;
        }
        
        public void init3() {
            System.out.println("Called init3");
            init++;
        }
        

        @LifeCycleListener(phase = Phase.START)
        public void start() {
            System.out.println("Called start");
            start++;
        }
        
        @LifeCycleListener
        public void startDefault() {
            System.out.println("Called start default");
            start++;
        }
    }

}
