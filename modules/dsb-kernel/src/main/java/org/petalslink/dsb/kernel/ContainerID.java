/**
 * 
 */
package org.petalslink.dsb.kernel;

import java.util.UUID;

/**
 * @author chamerling
 * 
 */
public class ContainerID {

    private static String id;

    static {
        id = UUID.randomUUID().toString();
    }

    public static final String get() {
        return id;
    }

    private ContainerID() {
    }

}
