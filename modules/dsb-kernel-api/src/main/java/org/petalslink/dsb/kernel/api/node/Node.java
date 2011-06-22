/**
 * 
 */
package org.petalslink.dsb.kernel.api.node;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author chamerling
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class Node {

    @XmlElement
    private String name;

    @XmlElement
    private String host;

    @XmlElement
    private long port;

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
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the port
     */
    public long getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(long port) {
        this.port = port;
    }

}
