/**
 * 
 */
package org.petalslink.dsb.service.poller.api;

/**
 * @author chamerling
 *
 */
public interface Job {
    
    void invoke(PollingContext context) throws PollerException;

}
