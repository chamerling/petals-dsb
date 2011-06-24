/**
 * 
 */
package org.petalslink.dsb.tools.generator.bpel;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.Iterator;

import org.petalslink.abslayer.service.api.Interface;

/**
 * @author chamerling
 * 
 */
public class Utils {

    private static final String BPEL_FILE_EXTENSION = "bpel";

    private static final String XSD_FILE_EXTENSION = "xsd";

    private static final String WSDL_FILE_EXTENSION = "wsdl";

    /**
     * Get a BPEL file from a folder
     * 
     * @param folder
     * @return
     */
    public static File[] getBPELFiles(File folder) {
        File[] result = null;
        if (folder == null || !folder.isDirectory()) {
            return result;
        }
        return getAllFor(BPEL_FILE_EXTENSION, folder);
    }

    public static File[] getXSDFiles(File folder) {
        File[] result = null;
        if (folder == null || !folder.isDirectory()) {
            return result;
        }
        return getAllFor(XSD_FILE_EXTENSION, folder);
    }

    public static File[] getWSDLFiles(File folder) {
        File[] result = null;
        if (folder == null || !folder.isDirectory()) {
            return result;
        }
        return getAllFor(WSDL_FILE_EXTENSION, folder);
    }

    public static File[] getAllFor(final String extensionName, final File folder) {
        if (folder == null || extensionName == null) {
            return null;
        }
        return folder.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().endsWith("." + extensionName)
                        || pathname.getName().endsWith("." + extensionName.toUpperCase());
            }
        });
    }

    public static org.petalslink.abslayer.service.api.Service findService(Interface interf,
            Collection<org.petalslink.abslayer.service.api.Service> list) {
        org.petalslink.abslayer.service.api.Service res = null;
        Iterator<org.petalslink.abslayer.service.api.Service> it = list.iterator();
        while (it.hasNext()) {
            org.petalslink.abslayer.service.api.Service current = it.next();
            if (current != null && current.getEndpoints() != null
                    && current.getEndpoints().length > 0) {
                if (current.getEndpoints()[0].getBinding().getInterface().equals(interf)) {
                    res = current;
                    break;
                }
            }
        }
        return res;
    }

    public static org.petalslink.abslayer.service.api.Endpoint findEndpoint(Interface interf,
            org.petalslink.abslayer.service.api.Endpoint[] endpoints) {
        org.petalslink.abslayer.service.api.Endpoint res = null;
        for (int i = 0; i < endpoints.length; i++) {
            org.petalslink.abslayer.service.api.Endpoint current = endpoints[i];
            if (current.getBinding().getInterface().getQName().equals(interf.getQName())) {
                res = current;
                break;
            }
        }
        return res;
    }
}
