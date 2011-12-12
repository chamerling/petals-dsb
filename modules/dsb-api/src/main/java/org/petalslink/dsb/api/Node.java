/**
 * 
 */
package org.petalslink.dsb.api;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A simple node representation, contains properties and that's almost all! Up
 * to the implementation to put and retrieve information as needed dependingon
 * its policies.
 * 
 * @author chamerling
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "org.petalslink.dsb.api", name = "Node")
public class Node {

    @XmlElement(name = "uuid")
    private String uuid;

    @XmlElement(name = "properties")
    private List<Property> properties;

    /**
     * 
     */
    public Node() {
        properties = new ArrayList<Property>();
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid
     *            the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the properties
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * @param properties
     *            the properties to set
     */
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Node [uuid=");
        builder.append(uuid);
        builder.append(", properties=");
        builder.append(properties);
        builder.append("]");
        return builder.toString();
    }

}
