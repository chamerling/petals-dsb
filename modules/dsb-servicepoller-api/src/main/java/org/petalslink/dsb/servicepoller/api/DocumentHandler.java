/**
 * 
 */
package org.petalslink.dsb.servicepoller.api;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author chamerling
 * 
 */
@XmlType
public class DocumentHandler {

    /**
     * The data handler to handle WSDL file
     */
    private DataHandler dom;

    @XmlMimeType("application/octet-stream")
    public DataHandler getDom() {
        return dom;
    }

    public void setDom(DataHandler dom) {
        this.dom = dom;
    }

}
