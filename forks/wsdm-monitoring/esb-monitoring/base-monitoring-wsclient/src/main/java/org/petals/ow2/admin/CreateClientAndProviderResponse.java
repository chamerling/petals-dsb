
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
 *         &lt;element name="clientAndProviderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "clientAndProviderName"
})
@XmlRootElement(name = "createClientAndProviderResponse")
public class CreateClientAndProviderResponse {

    @XmlElement(required = true)
    protected String clientAndProviderName;

    /**
     * Gets the value of the clientAndProviderName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientAndProviderName() {
        return clientAndProviderName;
    }

    /**
     * Sets the value of the clientAndProviderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientAndProviderName(String value) {
        this.clientAndProviderName = value;
    }

}
