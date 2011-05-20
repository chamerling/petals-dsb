/**
 * 
 */
package org.petalslink.dsb.service.poller.api;

/**
 * 
 * @author chamerling
 * 
 */
public interface PollerService {

    /**
     * @return
     */
    PollingContext getContext();

    /**
     * 
     * @return
     */
    String getId();

    /**
     * Start polling service
     */
    void start() throws PollerException;

    /**
     * Stop polling service
     */
    void stop() throws PollerException;

    /**
     * Pause polling service
     */
    void pause() throws PollerException;

    /**
     * Resume polling service
     */
    void resume() throws PollerException;

}
