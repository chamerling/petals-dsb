
package org.petals.ow2.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="wsdmEndpointName" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "wsdmEndpointName"
})
@XmlRootElement(name = "createMonitoringEndpointResponse")
public class CreateMonitoringEndpointResponse {

    @XmlElement(required = true)
    protected String wsdmEndpointName;

    /**
     * Gets the value of the wsdmEndpointName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWsdmEndpointName() {
        return wsdmEndpointName;
    }

    /**
     * Sets the value of the wsdmEndpointName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWsdmEndpointName(String value) {
        this.wsdmEndpointName = value;
    }

}
