/**
 * 
 */
package org.petalslink.dsb.kernel.resources.service;

import javax.xml.namespace.QName;

import org.petalslink.dsb.api.ServiceEndpoint;

/**
 * @author chamerling
 * 
 */
public class ResourceIdBuilder {

    public static String ROOT = "http://dsb.petalslink.org/endpoint/identifier";

    /**
     * Component/domain/container
     */
    public static final String PATTERN = ROOT + "/%s/%s/%s/";

    private ResourceIdBuilder() {
    }

    public static final String getId(ServiceEndpoint serviceEndpoint) {
        String NS = String.format(PATTERN, serviceEndpoint.getComponentLocation(),
                serviceEndpoint.getSubdomainLocation(), serviceEndpoint.getContainerLocation());
        QName qname = new QName(NS, serviceEndpoint.getEndpointName());
        return qname.toString();
    }

    public static String getEndpointName(String id) {
        return QName.valueOf(id).getLocalPart();
    }

    /**
     * @param id
     * @return
     */
    public static String getComponent(String id) {
        String path = getPath(id);
        return path.substring(0, path.indexOf("/"));
    }

    /**
     * @param id
     * @return
     */
    public static String getContainer(String id) {
        String path = getPath(id);
        String subPath = path.substring(path.indexOf('/') + 1);
        String subsubPath = subPath.substring(subPath.indexOf('/') + 1);
        return subsubPath.substring(0, subsubPath.indexOf("/"));
    }

    /**
     * @param id
     * @return
     */
    public static String getDomain(String id) {
        String path = getPath(id);
        String subPath = path.substring(path.indexOf('/') + 1);
        return subPath.substring(0, subPath.indexOf("/"));
    }

    private static String getPath(String id) {
        QName qname = QName.valueOf(id);

        if (qname.getNamespaceURI().startsWith(ROOT)
                && qname.getNamespaceURI().length() > ROOT.length() + 1) {
            return qname.getNamespaceURI().substring(ROOT.length() + 1);
        }
        return "";
    }

}
