/**
 * 
 */
package org.petalslink.dsb.transport;

import org.petalslink.dsb.api.MessageExchange;

import junit.framework.TestCase;

/**
 * @author chamerling
 * 
 */
public class TransporterUtilsTest extends TestCase {

    public void testSetProperty() throws Exception {
        MessageExchange me = new MessageExchange();
        TransporterUtils.setProperty(me, "foo", "bar");
        assertTrue(me.getProperties().size() == 1);
        assertTrue(me.getProperties().get(0).getName().equals("foo"));
    }
    
    public void testGetPropertyValue() throws Exception {
        MessageExchange me = new MessageExchange();
        TransporterUtils.setProperty(me, "foo", "bar");
        assertTrue(me.getProperties().size() == 1);
        assertEquals("bar", TransporterUtils.getPropertyValue(me, "foo"));
    }


}
