
package org.ow2.petals.ws._1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for endpoint complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="endpoint">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="service" type="{http://www.w3.org/2001/XMLSchema}QName" minOccurs="0"/>
 *         &lt;element name="interface" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="container" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="component" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subdomain" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "endpoint", propOrder = {
    "service",
    "_interface",
    "container",
    "component",
    "subdomain",
    "description"
})
public class Endpoint {

    protected QName service;
    @XmlElement(name = "interface")
    protected List<QName> _interface;
    protected String container;
    protected String component;
    protected String subdomain;
    protected String description;
    @XmlAttribute
    protected String name;

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setService(QName value) {
        this.service = value;
    }

    /**
     * Gets the value of the interface property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the interface property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInterface().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QName }
     * 
     * 
     */
    public List<QName> getInterface() {
        if (_interface == null) {
            _interface = new ArrayList<QName>();
        }
        return this._interface;
    }

    /**
     * Gets the value of the container property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContainer() {
        return container;
    }

    /**
     * Sets the value of the container property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContainer(String value) {
        this.container = value;
    }

    /**
     * Gets the value of the component property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComponent() {
        return component;
    }

    /**
     * Sets the value of the component property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComponent(String value) {
        this.component = value;
    }

    /**
     * Gets the value of the subdomain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubdomain() {
        return subdomain;
    }

    /**
     * Sets the value of the subdomain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubdomain(String value) {
        this.subdomain = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
