
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
 *         &lt;element name="wsdmServiceName" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="wsdmProviderEndpointName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="exposeInSoap" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "wsdmServiceName",
    "wsdmProviderEndpointName",
    "exposeInSoap"
})
@XmlRootElement(name = "createMonitoringEndpoint")
public class CreateMonitoringEndpoint {

    @XmlElement(required = true)
    protected QName wsdmServiceName;
    @XmlElement(required = true)
    protected String wsdmProviderEndpointName;
    protected boolean exposeInSoap;

    /**
     * Gets the value of the wsdmServiceName property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getWsdmServiceName() {
        return wsdmServiceName;
    }

    /**
     * Sets the value of the wsdmServiceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setWsdmServiceName(QName value) {
        this.wsdmServiceName = value;
    }

    /**
     * Gets the value of the wsdmProviderEndpointName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWsdmProviderEndpointName() {
        return wsdmProviderEndpointName;
    }

    /**
     * Sets the value of the wsdmProviderEndpointName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWsdmProviderEndpointName(String value) {
        this.wsdmProviderEndpointName = value;
    }

    /**
     * Gets the value of the exposeInSoap property.
     * 
     */
    public boolean isExposeInSoap() {
        return exposeInSoap;
    }

    /**
     * Sets the value of the exposeInSoap property.
     * 
     */
    public void setExposeInSoap(boolean value) {
        this.exposeInSoap = value;
    }

}
