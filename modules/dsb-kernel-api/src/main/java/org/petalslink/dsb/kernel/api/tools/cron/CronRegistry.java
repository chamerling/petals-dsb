/**
 * 
 */
package org.petalslink.dsb.kernel.api.tools.cron;

import java.util.List;

/**
 * @author chamerling
 * 
 */
public interface CronRegistry {

    /**
     * Store a job when it has been succesfully submitted
     * 
     */
    void store(Job job);

    /**
     * Get a job from its ID
     * 
     * @param id
     * @return
     */
    Job get(String id);
    
    /**
     * Get all the jobs
     * 
     * @return
     */
    List<Job> get();

}
