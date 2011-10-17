/**
 * 
 */
package org.petalslink.dsb.kernel.api.tools.cron;

import java.util.List;

/**
 * @author chamerling
 * 
 */
public interface CronScanner {

    /**
     * Get all teh jobs from the framework
     * 
     * @return
     */
    List<Job> scan();

}
