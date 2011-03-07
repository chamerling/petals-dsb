
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
 *         &lt;element name="serviceName" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="providerEndpointName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="clientName" type="{http://www.w3.org/2001/XMLSchema}QName" minOccurs="0"/>
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
    "serviceName",
    "providerEndpointName",
    "clientName"
})
@XmlRootElement(name = "exposeInSoapOnClient")
public class ExposeInSoapOnClient {

    @XmlElement(required = true)
    protected QName serviceName;
    @XmlElement(required = true)
    protected String providerEndpointName;
    protected QName clientName;

    /**
     * Gets the value of the serviceName property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getServiceName() {
        return serviceName;
    }

    /**
     * Sets the value of the serviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setServiceName(QName value) {
        this.serviceName = value;
    }

    /**
     * Gets the value of the providerEndpointName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProviderEndpointName() {
        return providerEndpointName;
    }

    /**
     * Sets the value of the providerEndpointName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProviderEndpointName(String value) {
        this.providerEndpointName = value;
    }

    /**
     * Gets the value of the clientName property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getClientName() {
        return clientName;
    }

    /**
     * Sets the value of the clientName property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setClientName(QName value) {
        this.clientName = value;
    }

}
