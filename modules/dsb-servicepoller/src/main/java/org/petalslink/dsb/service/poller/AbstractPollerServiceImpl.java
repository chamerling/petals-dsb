/**
 * 
 */
package org.petalslink.dsb.service.poller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.petalslink.dsb.service.poller.api.PollerService;
import org.petalslink.dsb.service.poller.api.PollingContext;

/**
 * @author chamerling
 * 
 */
public abstract class AbstractPollerServiceImpl implements PollerService {

    protected PollingContext context;
    
    final Log logger = LogFactory.getLog(AbstractPollerServiceImpl.class);

    public AbstractPollerServiceImpl(PollingContext context) {
        this.context = context;
    }

    public PollingContext getContext() {
        return this.context;
    }

}
