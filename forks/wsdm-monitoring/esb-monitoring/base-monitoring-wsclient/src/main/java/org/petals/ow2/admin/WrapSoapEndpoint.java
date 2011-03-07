
package org.petals.ow2.admin;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="soapAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="wsdl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="interceptorClassName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
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
    "soapAddress",
    "wsdl",
    "interceptorClassName",
    "clientName"
})
@XmlRootElement(name = "wrapSoapEndpoint")
public class WrapSoapEndpoint {

    @XmlElementRef(name = "providerName", type = JAXBElement.class)
    protected JAXBElement<QName> providerName;
    @XmlElement(required = true)
    protected String soapAddress;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String wsdl;
    protected List<String> interceptorClassName;
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
     * Gets the value of the soapAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSoapAddress() {
        return soapAddress;
    }

    /**
     * Sets the value of the soapAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSoapAddress(String value) {
        this.soapAddress = value;
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
     * Gets the value of the interceptorClassName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the interceptorClassName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInterceptorClassName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getInterceptorClassName() {
        if (interceptorClassName == null) {
            interceptorClassName = new ArrayList<String>();
        }
        return this.interceptorClassName;
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
