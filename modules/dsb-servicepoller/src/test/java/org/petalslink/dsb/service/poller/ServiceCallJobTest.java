/**
 * 
 */
package org.petalslink.dsb.service.poller;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.petalslink.dsb.service.poller.api.Job;
import org.petalslink.dsb.service.poller.api.PollerException;
import org.petalslink.dsb.service.poller.api.PollerService;
import org.petalslink.dsb.service.poller.api.PollingContext;
import org.petalslink.dsb.service.poller.api.PollingManager;

/**
 * @author chamerling
 * 
 */
public class ServiceCallJobTest extends TestCase {

    /**
     * @param name
     */
    public ServiceCallJobTest(String name) {
        super(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNoContextSet() throws Exception {
    }

    public void testJobInvoke() {
        final AtomicInteger count = new AtomicInteger(0);
        PollingManager manager = new QuartzPollingManagerImpl();
        try {
            manager.init();
            manager.start();
        } catch (PollerException e) {
            fail();
        }
        PollingContext context = new PollingContext();
        context.setJob(new Job() {

            public void invoke(PollingContext context) throws PollerException {
                System.out.println("Invoked! " + count.incrementAndGet());
            }
        });
        context.setCron("* * * * * ?");
        PollerService poller;
        try {
            poller = manager.getPollerService(context);
            poller.start();

            System.out.println("Wait 5s...");
            Thread.sleep(5000L);

            System.out.println("Stopping poller");
            poller.stop();

            System.out.printf("CALLED %s times", count.get());
            assertTrue(count.get() > 0);

        } catch (PollerException e) {
            fail(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void testPauseJob() throws Exception {
        final AtomicInteger count = new AtomicInteger(0);
        PollingManager manager = new QuartzPollingManagerImpl();
        try {
            manager.init();
            manager.start();
        } catch (PollerException e) {
            fail();
        }
        PollingContext context = new PollingContext();
        context.setJob(new Job() {

            public void invoke(PollingContext context) throws PollerException {
                System.out.println("Invoked! " + count.incrementAndGet());
            }
        });
        context.setCron("* * * * * ?");
        PollerService poller;
        try {
            poller = manager.getPollerService(context);
            poller.start();

            System.out.println("Wait 5s...");
            Thread.sleep(5000L);

            System.out.println("Pause poller");
            poller.pause();
            int called = count.get();
            
            System.out.println("Wait 5s...");
            Thread.sleep(5000L);
            int b = count.get();
            assertEquals(called, b);
        } catch (PollerException e) {
            fail(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void testPauseResume() throws Exception {
        final AtomicInteger count = new AtomicInteger(0);
        PollingManager manager = new QuartzPollingManagerImpl();
        try {
            manager.init();
            manager.start();
        } catch (PollerException e) {
            fail();
        }
        PollingContext context = new PollingContext();
        context.setJob(new Job() {

            public void invoke(PollingContext context) throws PollerException {
                System.out.println("Invoked! " + count.incrementAndGet());
            }
        });
        context.setCron("* * * * * ?");
        PollerService poller;
        try {
            poller = manager.getPollerService(context);
            poller.start();

            System.out.println("Wait 5s...");
            Thread.sleep(5000L);

            System.out.println("Pause poller");
            poller.pause();
            int called = count.get();
            
            System.out.println("Wait 5s...");
            Thread.sleep(5000L);
            int b = count.get();
            
            assertEquals(called, b);
            
            System.out.println("Resume poller");
            poller.resume();
            Thread.sleep(5000L);
            int c = count.get();
            
            assertTrue(c > b);
            
        } catch (PollerException e) {
            fail(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    

}
