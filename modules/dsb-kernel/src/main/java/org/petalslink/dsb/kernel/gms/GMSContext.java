package org.petalslink.dsb.kernel.gms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A context is exchanged between group members with all the required information
 * 
 * @author chamerling
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "org.ow2.petals.dsb.gms", name = "GMSContext")
public class GMSContext {

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "current")
    private long current;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

}
