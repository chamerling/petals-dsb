/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service.internal.test;

import org.petalslink.dsb.annotations.notification.Notify;

/**
 * @author chamerling
 *
 */
public class MockWithNothing {
    
    private Report report;

    /**
     * 
     */
    public MockWithNothing(Report report) {
        this.report = report;

    }
    
    @Notify
    public void mockMe() {
        System.out.println("Called with no parameters");
        report.ok = true;
    }
}
