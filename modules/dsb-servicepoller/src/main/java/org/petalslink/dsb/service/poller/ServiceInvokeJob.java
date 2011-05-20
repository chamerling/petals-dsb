/**
 * 
 */
package org.petalslink.dsb.service.poller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.service.poller.api.Job;
import org.petalslink.dsb.service.poller.api.PollerException;
import org.petalslink.dsb.service.poller.api.PollingContext;

/**
 * @author chamerling
 *
 */
public class ServiceInvokeJob implements Job {

    final Log logger = LogFactory.getLog(ServiceInvokeJob.class);

    public void invoke(PollingContext context) throws PollerException {
        System.out.println("TODO FOR PETALS DSB");
    }

}
