/**
 * 
 */
package org.petalslink.dsb.jaxws;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;

/**
 * A simple attachment
 * 
 * @author chamerling
 * 
 */
@XmlType
public class Attachment {

    /**
     * The data handler to handle BPEL file
     */
    private DataHandler dh;

    private String name;

    public Attachment() {
    }

    @XmlMimeType("application/octet-stream")
    public DataHandler getAttachment() {
        return dh;
    }

    public void setAttachment(DataHandler dh) {
        this.dh = dh;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
