
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
 *         &lt;element name="providerName" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="serviceName" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="fractalServiceItfName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="classServiceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "providerName",
    "serviceName",
    "fractalServiceItfName",
    "classServiceName"
})
@XmlRootElement(name = "createService")
public class CreateService {

    @XmlElement(required = true)
    protected QName providerName;
    @XmlElement(required = true)
    protected QName serviceName;
    protected String fractalServiceItfName;
    protected String classServiceName;

    /**
     * Gets the value of the providerName property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getProviderName() {
        return providerName;
    }

    /**
     * Sets the value of the providerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setProviderName(QName value) {
        this.providerName = value;
    }

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
     * Gets the value of the fractalServiceItfName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFractalServiceItfName() {
        return fractalServiceItfName;
    }

    /**
     * Sets the value of the fractalServiceItfName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFractalServiceItfName(String value) {
        this.fractalServiceItfName = value;
    }

    /**
     * Gets the value of the classServiceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassServiceName() {
        return classServiceName;
    }

    /**
     * Sets the value of the classServiceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassServiceName(String value) {
        this.classServiceName = value;
    }

}
