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
public class BPELDescriptor {

    /**
     * The data handler to handle BPEL file
     */
    private DataHandler dh;
    
    /**
     * The original bpel file name
     */
    private String fileName;

    /**
         * 
         */
    public BPELDescriptor() {
    }

    @XmlMimeType("application/octet-stream")
    public DataHandler getAttachment() {
        return dh;
    }

    public void setAttachment(DataHandler dh) {
        this.dh = dh;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
