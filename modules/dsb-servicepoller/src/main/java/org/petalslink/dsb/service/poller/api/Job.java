/**
 * 
 */
package org.petalslink.dsb.service.poller.api;

/**
 * @author chamerling
 *
 */
public interface Job {
    
    /**
     * 
     * @param context
     * @throws PollerException
     */
    void invoke(PollingContext context) throws PollerException;

}
