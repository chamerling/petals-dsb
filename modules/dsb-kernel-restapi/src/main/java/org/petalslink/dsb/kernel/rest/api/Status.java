/**
 * 
 */
package org.petalslink.dsb.kernel.rest.api;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A simple status as a return value.
 * 
 * @author chamerling
 *
 */
@XmlRootElement(name = "Status")
public class Status {
    
    private String code;
    
    /**
     * 
     */
    public Status() {
    }
    
    /**
     * 
     */
    public Status(String code) {
        this.code = code;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

}
