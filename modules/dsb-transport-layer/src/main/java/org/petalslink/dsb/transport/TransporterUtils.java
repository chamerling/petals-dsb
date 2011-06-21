/**
 * 
 */
package org.petalslink.dsb.transport;

import java.util.Iterator;
import java.util.List;

import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.Property;

/**
 * @author chamerling
 * 
 */
public class TransporterUtils {

    /**
     * @param propertySendsyncConsumer
     * @return
     */
    public static String getPropertyValue(MessageExchange exchange, String propName) {
        String result = null;
        List<Property> properties = exchange.getProperties();
        Iterator<Property> iter = properties.iterator();
        boolean found = false;
        while (iter.hasNext() && !found) {
            Property property = iter.next();
            found = (propName.equals(property.getName()));
            if (found) {
                result = property.getValue();
            }
        }
        return result;
    }
    
    /**
     * Set or create a value if not already here...
     * 
     * @param name
     * @param value
     */
    public static void setProperty(MessageExchange exchange, String name, String value) {
        List<Property> properties = exchange.getProperties();
        Iterator<Property> iter = properties.iterator();
        boolean found = false;
        while (iter.hasNext() && !found) {
            Property property = iter.next();
            found = (name.equals(property.getName()));
            if (found) {
                property.setValue(value);
            }
        }
        if (!found) {
            Property prop = new Property();
            prop.setName(name);
            prop.setValue(value);
            exchange.getProperties().add(prop);
        }
    }

}
