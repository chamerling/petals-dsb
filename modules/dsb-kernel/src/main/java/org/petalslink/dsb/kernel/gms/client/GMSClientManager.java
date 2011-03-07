package org.petalslink.dsb.kernel.gms.client;

public interface GMSClientManager {

    /**
     * Get a client to communicate with a remote container. The containerId is
     * unique in the topology and is implementation dependant (ie it can be
     * something else than the container name from the petals topology)
     * 
     * @param containerId
     * @return
     */
    GMSClient getClient(String containerId);

}
