/**
 * 
 */
package org.petalslink.dsb.cloud.registry;

import org.ow2.petals.jbi.messaging.registry.EndpointRegistry;
import org.ow2.petals.registry.client.api.RegistryClient;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.kernel.api.PetalsService;
import org.petalslink.dsb.kernel.api.messaging.RegistryListenerManager;
import org.petalslink.dsb.kernel.registry.EndpointRegistryImpl;

/**
 * A basic endpoint registry which just cache information for some time and
 * update when needed...
 * 
 * @author chamerling
 * 
 */
public class EndpointRegistryService extends EndpointRegistryImpl implements EndpointRegistry,
        PetalsService, RegistryListenerManager {

    /**
     * @param arg0
     */
    public EndpointRegistryService(LoggingUtil arg0) {
        super(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.registry.EndpointRegistryImpl#setup()
     */
    @Override
    public void setup() throws Exception {
        // let's hack the standard registry behaviour...
        log.info("Setup the Endpoint Cloud Registry...");
        CloudRegistryClient cloudRegistryClient = new CloudRegistryClient();
        cloudRegistryClient.setTopologyService(this.localTopologyService);
        this.client = cloudRegistryClient;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.kernel.registry.EndpointRegistryImpl#shutdown()
     */
    @Override
    public void shutdown() throws Exception {
    }

}
