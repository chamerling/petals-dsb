
package org.petals.ow2.admin;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
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
 *         &lt;element name="providerName" type="{http://www.w3.org/2001/XMLSchema}QName" minOccurs="0"/>
 *         &lt;element name="serviceName" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="fractalServiceItfName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="classServiceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="providerEndpointName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fractalProviderEndpointItfName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="classProviderEndpointName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="classProviderEndpointBehaviourName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="wsdl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
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
    "providerName",
    "serviceName",
    "fractalServiceItfName",
    "classServiceName",
    "providerEndpointName",
    "fractalProviderEndpointItfName",
    "classProviderEndpointName",
    "classProviderEndpointBehaviourName",
    "wsdl",
    "clientName"
})
@XmlRootElement(name = "createServiceEndpointOnSoapClient")
public class CreateServiceEndpointOnSoapClient {

    @XmlElementRef(name = "providerName", type = JAXBElement.class)
    protected JAXBElement<QName> providerName;
    @XmlElement(required = true)
    protected QName serviceName;
    @XmlElementRef(name = "fractalServiceItfName", type = JAXBElement.class)
    protected JAXBElement<String> fractalServiceItfName;
    @XmlElementRef(name = "classServiceName", type = JAXBElement.class)
    protected JAXBElement<String> classServiceName;
    @XmlElement(required = true)
    protected String providerEndpointName;
    protected String fractalProviderEndpointItfName;
    protected String classProviderEndpointName;
    @XmlElement(required = true)
    protected String classProviderEndpointBehaviourName;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String wsdl;
    @XmlElementRef(name = "clientName", type = JAXBElement.class)
    protected JAXBElement<QName> clientName;

    /**
     * Gets the value of the providerName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link QName }{@code >}
     *     
     */
    public JAXBElement<QName> getProviderName() {
        return providerName;
    }

    /**
     * Sets the value of the providerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link QName }{@code >}
     *     
     */
    public void setProviderName(JAXBElement<QName> value) {
        this.providerName = ((JAXBElement<QName> ) value);
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
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFractalServiceItfName() {
        return fractalServiceItfName;
    }

    /**
     * Sets the value of the fractalServiceItfName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFractalServiceItfName(JAXBElement<String> value) {
        this.fractalServiceItfName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the classServiceName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getClassServiceName() {
        return classServiceName;
    }

    /**
     * Sets the value of the classServiceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setClassServiceName(JAXBElement<String> value) {
        this.classServiceName = ((JAXBElement<String> ) value);
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

    /**
     * Gets the value of the clientName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link QName }{@code >}
     *     
     */
    public JAXBElement<QName> getClientName() {
        return clientName;
    }

    /**
     * Sets the value of the clientName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link QName }{@code >}
     *     
     */
    public void setClientName(JAXBElement<QName> value) {
        this.clientName = ((JAXBElement<QName> ) value);
    }

}
