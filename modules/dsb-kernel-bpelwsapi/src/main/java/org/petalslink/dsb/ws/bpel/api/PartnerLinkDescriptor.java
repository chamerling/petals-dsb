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
public class PartnerLinkDescriptor {

    /**
     * The data handler to handle WSDL file
     */
    private DataHandler wsdl;

    /**
     * The original wsdl file name
     */
    private String fileName;

    public PartnerLinkDescriptor() {
    }

    @XmlMimeType("application/octet-stream")
    public DataHandler getWSDL() {
        return wsdl;
    }

    public void setWSDL(DataHandler dh) {
        this.wsdl = dh;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
