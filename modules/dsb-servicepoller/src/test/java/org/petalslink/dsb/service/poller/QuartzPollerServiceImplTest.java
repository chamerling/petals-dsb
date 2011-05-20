package org.petalslink.dsb.service.poller;

import org.petalslink.dsb.service.poller.api.PollerService;
import org.petalslink.dsb.service.poller.api.PollingContext;
import org.petalslink.dsb.service.poller.api.PollingManager;

import junit.framework.TestCase;

public class QuartzPollerServiceImplTest extends TestCase {
    
    public void testStartedNotNull() throws Exception {
        PollingManager manager = new QuartzPollingManagerImpl();
        manager.init();
        manager.start();
        PollingContext context = new PollingContext();
        context.setCron("0/20 * * * * ?");
        PollerService poller = manager.getPollerService(context);
        
        
        System.out.println("Starting service : " + poller.getId());
        // start the service poller
        
        
    }

}
