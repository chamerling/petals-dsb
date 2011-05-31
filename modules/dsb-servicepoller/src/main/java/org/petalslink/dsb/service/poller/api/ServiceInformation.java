/**
 * 
 */
package org.petalslink.dsb.service.poller.api;

import javax.xml.namespace.QName;

/**
 * @author chamerling
 * 
 */
public class ServiceInformation {

    public QName operation;

    public String endpoint;

    public QName service;

    public QName itf;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ServiceInformation [operation=");
        builder.append(operation);
        builder.append(", endpoint=");
        builder.append(endpoint);
        builder.append(", service=");
        builder.append(service);
        builder.append(", itf=");
        builder.append(itf);
        builder.append("]");
        return builder.toString();
    }

}
