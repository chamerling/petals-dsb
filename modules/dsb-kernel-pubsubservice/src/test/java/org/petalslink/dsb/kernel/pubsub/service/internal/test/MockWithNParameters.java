/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service.internal.test;

import org.petalslink.dsb.annotations.notification.Notify;
import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public class MockWithNParameters {

    private Report report;

    /**
     * 
     */
    public MockWithNParameters(Report report) {
        this.report = report;

    }

    @Notify
    public void mockMe(String foo, Document document) {
        System.out.println("Called with only document set!");
        report.ok = true;
        if (document == null) {
            report.e = new IllegalArgumentException();
        }
    }
}
