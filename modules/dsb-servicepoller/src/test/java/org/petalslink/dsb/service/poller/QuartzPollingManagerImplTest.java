package org.petalslink.dsb.service.poller;

import junit.framework.TestCase;

import org.petalslink.dsb.service.poller.api.PollerException;
import org.petalslink.dsb.service.poller.api.PollerService;
import org.petalslink.dsb.service.poller.api.PollingContext;
import org.petalslink.dsb.service.poller.api.PollingManager;

public class QuartzPollingManagerImplTest extends TestCase {

    public void testNotInitialized() throws Exception {
        PollingManager manager = new QuartzPollingManagerImpl();
        PollerService poller = manager.getPollerService(null);
        assertNull(poller);
    }

    public void testNotStarted() throws Exception {
        PollingManager manager = new QuartzPollingManagerImpl();
        manager.init();
        PollerService poller = manager.getPollerService(null);
        assertNull(poller);
    }

    public void testBadContext() {
        PollingManager manager = new QuartzPollingManagerImpl();
        try {
            manager.init();
        } catch (PollerException e) {
            fail();
        }
        try {
            manager.start();
        } catch (PollerException e) {
            fail();
        }
        PollingContext context = new PollingContext();
        try {
            manager.getPollerService(context);
            fail();
        } catch (PollerException e) {
        }
    }

    public void testStartedNotNull() throws Exception {
        PollingManager manager = new QuartzPollingManagerImpl();
        manager.init();
        manager.start();
        PollingContext context = new PollingContext();
        context.setCron("0/20 * * * * ?");
        PollerService poller = manager.getPollerService(context);
        assertNotNull(poller);
    }

    public void testStartService() {
        PollingManager manager = new QuartzPollingManagerImpl();
        try {
            manager.init();
            manager.start();
        } catch (PollerException e) {
            fail();
        }
        PollingContext context = new PollingContext();
        context.setCron("0/20 * * * * ?");
        PollerService poller;
        try {
            poller = manager.getPollerService(context);
            poller.start();
            poller.stop();
        } catch (PollerException e) {
            fail(e.getMessage());
        }
    }

}
