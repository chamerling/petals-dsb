/**
 * 
 */
package org.petalslink.dsb.kernel.rest.api.beans;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author chamerling
 *
 */
@XmlRootElement(name = "SA")
public class SA {
    
    private String name;
    
    private String state;
    
    /**
     * 
     */
    public SA() {
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

}
