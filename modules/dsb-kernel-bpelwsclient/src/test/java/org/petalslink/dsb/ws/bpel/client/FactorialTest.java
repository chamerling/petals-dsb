/**
 * 
 */
package org.petalslink.dsb.ws.bpel.client;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URISyntaxException;
import java.net.URL;

import org.petalslink.dsb.ws.api.DSBWebServiceException;

/**
 * @author chamerling
 * 
 */
public class FactorialTest {
    public static void main(String[] args) {
        String address = "http://localhost:7600/petals/ws/";
        final String bpelFileName = "factorial.bpel";

        // get the files
        URL url = DSBCallTest.class.getResource("/factorial");
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

        // get all the other resources
        File[] files = folder.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                System.out.println(name);
                return name.endsWith("wsdl");
            }
        });

        BPELDeployerClient client = new BPELDeployerClient(address);
        boolean result = false;
        try {
            result = client.deploy(bpel, files);
        } catch (DSBWebServiceException e) {
            e.printStackTrace();
        }

        System.out.println(result);

        System.exit(0);

    }

}
