/**
 * 
 */
package org.petalslink.dsb.service.client;

import org.petalslink.dsb.api.ServiceEndpoint;

/**
 * It is up the factory implementation to create all things required by the DSB
 * context (and so by the Petals ESB runtime).
 * 
 * @author chamerling
 * 
 */
public interface ClientFactory {

    /**
     * Get a client for the given service. Note that service is just defined by
     * name/endpoint/interface. There is no hard link between client and service
     * and it will be up to the DSB to find the service to call or not...
     * 
     * @param service
     * @return
     */
    Client getClient(ServiceEndpoint service) throws ClientException;
    
    /**
     * Release a client ie free it!
     * 
     * @param client
     */
    void release(Client client) throws ClientException;

}
