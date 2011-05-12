/**
 * 
 */
package org.petalslink.dsb.kernel.io.client;

import org.petalslink.dsb.service.client.ClientFactory;

/**
 * Statically stores the factory so there is no need to use fractal bindings...
 * 
 * @author chamerling
 * 
 */
public class ClientFactoryRegistry {

    private static ClientFactory factory;

    public static void setFactory(ClientFactory factory) {
        ClientFactoryRegistry.factory = factory;
    }

    public static ClientFactory getFactory() {
        return ClientFactoryRegistry.factory;
    }

}
