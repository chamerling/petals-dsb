/**
 * 
 */
package org.petalslink.dsb.kernel.pubsub.service.internal;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author chamerling
 * 
 */
public class NotificationTargetBean {

    /**
     * Object to call
     */
    Object target;

    /**
     * Method to invoke
     */
    Method m;

    /**
     * Topics interested in
     */
    String[] topic;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NotificationTargetBean [target=");
        builder.append(target);
        builder.append(", m=");
        builder.append(m);
        builder.append(", topic=");
        builder.append(Arrays.toString(topic));
        builder.append("]");
        return builder.toString();
    }

}
