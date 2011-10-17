/**
 * 
 */
package org.petalslink.dsb.kernel.api.tools.cron;

import org.petalslink.dsb.api.DSBException;

/**
 * @author chamerling
 * 
 */
public interface CronManager {
    
    /**
     * Initialize the job pool size
     * 
     * @param size
     */
    void initialize(int size);

    /**
     * Start a job and returns its ID
     * 
     * @param job
     * @return
     */
    String startJob(Job job) throws DSBException;

    /**
     * 
     * @param job
     */
    void stopJob(Job job) throws DSBException;

}
