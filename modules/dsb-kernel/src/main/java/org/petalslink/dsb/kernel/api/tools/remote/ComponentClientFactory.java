package org.petalslink.dsb.kernel.api.tools.remote;

/**
 * A remote component client factory. The goal is to get a client to invoke a
 * remote service on a remote container...
 * 
 * @author chamerling
 * 
 */
public interface ComponentClientFactory {

    /**
     * Get a client for the given container and given component.
     * 
     * @param <T>
     * @param clazz
     * @param containerId
     *            can be anything, it depends on the implementation
     * @return
     */
    <T> T getClient(Class<T> clazz, String containerId) throws RemoteComponentException;

}
