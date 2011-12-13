/**
 * 
 */
package org.petalslink.dsb.notification.light;

import junit.framework.TestCase;

/**
 * @author chamerling
 *
 */
public class NotificationGeneratorTest extends TestCase {


    public void testGenerateNotify() throws Exception {
        String result = NotificationGenerator.generate(null, null);
        assertNotNull(result);
        System.out.println("Result = " + result);
    }
}
