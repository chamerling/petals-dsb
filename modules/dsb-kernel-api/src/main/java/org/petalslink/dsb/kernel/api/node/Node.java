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
    String name;

    @XmlElement
    String host;

    @XmlElement
    long port;

}
