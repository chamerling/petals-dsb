/**
 * 
 */
package org.petalslink.dsb.kernel.io.server;


/**
 * @author chamerling
 *
 */
public class ServerFactoryRegistry {
    private static DSBServiceServerFactory factory;

    public static void setFactory(DSBServiceServerFactory factory) {
        ServerFactoryRegistry.factory = factory;
    }

    public static DSBServiceServerFactory getFactory() {
        return ServerFactoryRegistry.factory;
    }
}
