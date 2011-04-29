/**
 * 
 */
package org.petalslink.gms.proto.cxf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The GMS Message to be serialized by CXF
 * 
 * @author chamerling
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class GMSMessage {
    
    @XmlElement
    String source;
    
    @XmlElement
    String type;

}
