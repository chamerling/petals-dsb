/**
 * 
 */
package org.petalslink.dsb.monitoring.api;

import org.petalslink.dsb.api.MessageExchangeException;

import junit.framework.TestCase;

/**
 * @author chamerling
 * 
 */
public class JAXBHelperTest extends TestCase {

    public void testGenerateXML() {
        ReportListBean bean = new ReportListBean();
        bean.getReports().add(new ReportBean());
        try {
            JAXBHelper.marshall(bean, System.out);
        } catch (MessageExchangeException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
