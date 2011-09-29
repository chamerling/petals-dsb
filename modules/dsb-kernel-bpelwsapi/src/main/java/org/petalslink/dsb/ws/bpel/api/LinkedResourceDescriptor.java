/**
 * 
 */
package org.petalslink.dsb.ws.bpel.api;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author chamerling
 * 
 */
@XmlType
public class LinkedResourceDescriptor {

    /**
     * The data handler to handle resources file
     */
    private DataHandler resource;

    /**
     * The original wsdl file name
     */
    private String fileName;

    public LinkedResourceDescriptor() {
    }

    @XmlMimeType("application/octet-stream")
    public DataHandler getResource() {
        return resource;
    }

    public void setResource(DataHandler dh) {
        this.resource = dh;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
