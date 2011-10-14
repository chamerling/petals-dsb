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
 * A really simplistic definition of a domain. Enough in mostly all the cases...
 * A domain is just a set of nodes.
 * 
 * @author chamerling
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "org.petalslink.dsb.api", name = "Domain")
public class Domain {

    @XmlElement
    private String id;

    @XmlElement(name = "nodes")
    private List<Node> nodes;

    /**
     * 
     */
    public Domain() {
        this.nodes = new ArrayList<Node>(1);
    }

    /**
     * @return the nodes
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * @param nodes
     *            the nodes to set
     */
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

}
