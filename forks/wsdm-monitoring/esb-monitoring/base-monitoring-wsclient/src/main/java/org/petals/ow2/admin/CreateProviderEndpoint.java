
package org.petals.ow2.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="fractalProviderEndpointItfName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="classProviderEndpointName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="classProviderEndpointBehaviourName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="wsdl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
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
    "fractalProviderEndpointItfName",
    "classProviderEndpointName",
    "classProviderEndpointBehaviourName",
    "wsdl"
})
@XmlRootElement(name = "createProviderEndpoint")
public class CreateProviderEndpoint {

    @XmlElement(required = true)
    protected QName serviceName;
    @XmlElement(required = true)
    protected String providerEndpointName;
    protected String fractalProviderEndpointItfName;
    protected String classProviderEndpointName;
    @XmlElement(required = true)
    protected String classProviderEndpointBehaviourName;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String wsdl;

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
     * Gets the value of the fractalProviderEndpointItfName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFractalProviderEndpointItfName() {
        return fractalProviderEndpointItfName;
    }

    /**
     * Sets the value of the fractalProviderEndpointItfName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFractalProviderEndpointItfName(String value) {
        this.fractalProviderEndpointItfName = value;
    }

    /**
     * Gets the value of the classProviderEndpointName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassProviderEndpointName() {
        return classProviderEndpointName;
    }

    /**
     * Sets the value of the classProviderEndpointName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassProviderEndpointName(String value) {
        this.classProviderEndpointName = value;
    }

    /**
     * Gets the value of the classProviderEndpointBehaviourName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassProviderEndpointBehaviourName() {
        return classProviderEndpointBehaviourName;
    }

    /**
     * Sets the value of the classProviderEndpointBehaviourName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassProviderEndpointBehaviourName(String value) {
        this.classProviderEndpointBehaviourName = value;
    }

    /**
     * Gets the value of the wsdl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWsdl() {
        return wsdl;
    }

    /**
     * Sets the value of the wsdl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWsdl(String value) {
        this.wsdl = value;
    }

}
