
package org.ow2.petals.ws._1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for EndpointQuery complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EndpointQuery">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="endpoint" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="service" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="interface">
 *         &lt;simpleType>
 *           &lt;list itemType="{http://www.w3.org/2001/XMLSchema}QName" />
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="containerName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="componentName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="subdomainName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EndpointQuery")
public class EndpointQuery {

    @XmlAttribute
    protected QName endpoint;
    @XmlAttribute
    protected QName service;
    @XmlAttribute(name = "interface")
    protected List<QName> _interface;
    @XmlAttribute
    protected String containerName;
    @XmlAttribute
    protected String componentName;
    @XmlAttribute
    protected String subdomainName;

    /**
     * Gets the value of the endpoint property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getEndpoint() {
        return endpoint;
    }

    /**
     * Sets the value of the endpoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setEndpoint(QName value) {
        this.endpoint = value;
    }

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
     * Gets the value of the containerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContainerName() {
        return containerName;
    }

    /**
     * Sets the value of the containerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContainerName(String value) {
        this.containerName = value;
    }

    /**
     * Gets the value of the componentName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * Sets the value of the componentName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComponentName(String value) {
        this.componentName = value;
    }

    /**
     * Gets the value of the subdomainName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubdomainName() {
        return subdomainName;
    }

    /**
     * Sets the value of the subdomainName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubdomainName(String value) {
        this.subdomainName = value;
    }

}
