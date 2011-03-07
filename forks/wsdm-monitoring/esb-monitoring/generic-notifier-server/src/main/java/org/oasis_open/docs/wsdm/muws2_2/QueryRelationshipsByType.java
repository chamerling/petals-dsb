
package org.oasis_open.docs.wsdm.muws2_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RequestedType" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "requestedType"
})
@XmlRootElement(name = "QueryRelationshipsByType")
public class QueryRelationshipsByType {

    @XmlElement(name = "RequestedType", required = true)
    protected QName requestedType;

    /**
     * Gets the value of the requestedType property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getRequestedType() {
        return requestedType;
    }

    /**
     * Sets the value of the requestedType property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setRequestedType(QName value) {
        this.requestedType = value;
    }

}
