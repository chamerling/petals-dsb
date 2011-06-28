/**
 * 
 */
package org.petalslink.dsb.ws.bpel.client;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import org.petalslink.dsb.ws.api.DSBWebServiceException;

/**
 * @author chamerling
 * 
 */
public class DSBCallTest {

    public static void main(String[] args) {
        String address = "http://localhost:7600/petals/ws/";
        final String bpelFileName = "process.bpel";

        // get the files
        URL url = DSBCallTest.class.getResource("/bpel");
        if (url == null) {
            System.out.println("No resource found...");
            System.exit(-1);
        }

        File folder = null;
        try {
            folder = new File(url.toURI());
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }

        File bpel = new File(folder, bpelFileName);
        
        BPELDeployerClient client = new BPELDeployerClient(address);
        boolean result = false;
        try {
            result = client.deploy(bpel, null);
        } catch (DSBWebServiceException e) {
            e.printStackTrace();
        }
        
        System.out.println(result);
        
        System.exit(0);

    }

}
