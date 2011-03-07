
package org.petals.ow2.admin;

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
 *         &lt;element name="clientEndpointName" type="{http://www.w3.org/2001/XMLSchema}QName"/>
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
    "clientEndpointName"
})
@XmlRootElement(name = "addSoapListener")
public class AddSoapListener {

    @XmlElement(required = true)
    protected QName clientEndpointName;

    /**
     * Gets the value of the clientEndpointName property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getClientEndpointName() {
        return clientEndpointName;
    }

    /**
     * Sets the value of the clientEndpointName property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setClientEndpointName(QName value) {
        this.clientEndpointName = value;
    }

}
