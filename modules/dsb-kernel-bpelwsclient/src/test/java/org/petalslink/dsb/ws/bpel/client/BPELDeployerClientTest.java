/**
 * 
 */
package org.petalslink.dsb.ws.bpel.client;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import javax.activation.DataHandler;

import junit.framework.TestCase;

import org.petalslink.dsb.commons.service.api.Service;
import org.petalslink.dsb.cxf.CXFHelper;
import org.petalslink.dsb.ws.api.DSBWebServiceException;
import org.petalslink.dsb.ws.bpel.api.BPELDeployer;
import org.petalslink.dsb.ws.bpel.api.BPELDescriptor;
import org.petalslink.dsb.ws.bpel.api.LinkedResourceDescriptor;

/**
 * @author chamerling
 * 
 */
public class BPELDeployerClientTest extends TestCase {

    public void testSendWithoutResource() {
        String address = "http://localhost:7676/foo/bar/";
        final String bpelFileName = "process.bpel";
        final AtomicInteger called = new AtomicInteger(0);
        final AtomicInteger match = new AtomicInteger(0);
        final AtomicInteger assertions = new AtomicInteger(0);

        // get the files
        URL url = BPELDeployerClientTest.class.getResource("/bpel");
        if (url == null) {
            fail("Can not find the resources in the project");
        }

        File folder = null;
        try {
            folder = new File(url.toURI());
        } catch (URISyntaxException e1) {
            fail();
        }

        File bpel = new File(folder, bpelFileName);

        // create the server
        Service server = CXFHelper.getService(address, BPELDeployer.class, new BPELDeployer() {
            public boolean deploy(BPELDescriptor bpelDescriptor,
                    LinkedResourceDescriptor[] resources) throws DSBWebServiceException {
                called.incrementAndGet();
                System.out.println("Received a file : " + bpelDescriptor.getFileName());
                if (bpelDescriptor.getFileName().equals(bpelFileName)) {
                    match.incrementAndGet();
                }
                if (resources == null) {
                    assertions.incrementAndGet();
                }
                DataHandler dh = bpelDescriptor.getAttachment();
                if (dh != null) {
                    assertions.incrementAndGet();
                }
                return true;
            }
        });
        server.start();

        BPELDeployerClient client = new BPELDeployerClient(address);
        boolean result = false;
        try {
            result = client.deploy(bpel, null);
        } catch (DSBWebServiceException e) {
            fail(e.getMessage());
        } finally {
            server.stop();
        }

        assertEquals(1, called.intValue());
        assertEquals(1, match.intValue());
        assertEquals(2, assertions.intValue());
        assertTrue(result);

    }

    public void testSendWithResources() {
        String address = "http://localhost:7676/foo/bar/";
        final String bpelFileName = "process.bpel";
        final String rFileName1 = "process.wsdl";
        final String rFileName2 = "wsdl.wsdl";

        final AtomicInteger called = new AtomicInteger(0);
        final AtomicInteger match = new AtomicInteger(0);
        final AtomicInteger assertions = new AtomicInteger(0);

        // get the files
        URL url = BPELDeployerClientTest.class.getResource("/bpel");
        if (url == null) {
            fail("Can not find the resources in the project");
        }

        File folder = null;
        try {
            folder = new File(url.toURI());
        } catch (URISyntaxException e1) {
            fail();
        }

        File bpel = new File(folder, bpelFileName);
        File resource1 = new File(folder, rFileName1);
        File resource2 = new File(folder, rFileName2);

        // create the server
        Service server = CXFHelper.getService(address, BPELDeployer.class, new BPELDeployer() {
            public boolean deploy(BPELDescriptor bpelDescriptor,
                    LinkedResourceDescriptor[] resources) throws DSBWebServiceException {
                called.incrementAndGet();
                System.out.println("Received a file : " + bpelDescriptor.getFileName());
                if (bpelDescriptor.getFileName().equals(bpelFileName)) {
                    match.incrementAndGet();
                }
                if (resources == null) {
                    throw new DSBWebServiceException("Resources are null and should not...");
                }
                
                if (resources.length == 2) {
                    assertions.incrementAndGet();
                }

                DataHandler dh = bpelDescriptor.getAttachment();
                if (dh != null) {
                    assertions.incrementAndGet();
                }
                return true;
            }
        });
        server.start();

        BPELDeployerClient client = new BPELDeployerClient(address);
        boolean result = false;
        try {
            result = client.deploy(bpel, new File[] { resource1, resource2 });
        } catch (DSBWebServiceException e) {
            fail(e.getMessage());
        } finally {
            server.stop();
        }

        assertEquals(1, called.intValue());
        assertEquals(1, match.intValue());
        assertEquals(2, assertions.intValue());

        assertTrue(result);

    }
}
