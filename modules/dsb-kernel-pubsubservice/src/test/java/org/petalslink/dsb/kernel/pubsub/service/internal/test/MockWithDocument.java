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
public class MockWithDocument {

    private Report report;

    /**
     * 
     */
    public MockWithDocument(Report report) {
        this.report = report;
    }

    @Notify
    public void mockMe(Document document) {
        System.out.println("Called and document is " + document);
        report.ok = true;
        if (document == null) {
            report.e = new IllegalArgumentException();
        }
    }

}
