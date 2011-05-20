/**
 * 
 */
package org.petalslink.dsb.service.poller.api;

/**
 * @author chamerling
 * 
 */
public interface PollingManager {

    /**
     * Get a poller service or create it if it do not exists.
     * 
     * @param context
     * @return a new poller service or null if the manager is not started
     * @throws PollerException is context is null
     */
    PollerService getPollerService(PollingContext context) throws PollerException;

    void init() throws PollerException;

    void start() throws PollerException;

    void stop() throws PollerException;

}
