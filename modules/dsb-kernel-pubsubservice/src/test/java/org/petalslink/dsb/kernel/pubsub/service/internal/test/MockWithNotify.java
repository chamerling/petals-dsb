/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service.internal.test;

import org.petalslink.dsb.annotations.notification.Notify;

/**
 * NOTE : This is not supported for now...
 * 
 * @author chamerling
 * 
 */
public class MockWithNotify {
    
    private Report report;

    /**
     * 
     */
    public MockWithNotify(Report report) {
        this.report = report;

    }

    @Notify
    public void mockMe(
            com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify notify) {
        System.out.println("Called and notify is " + notify);
        report.ok = true;
        if (notify == null) {
            report.e = new IllegalArgumentException();
        }

    }
}
