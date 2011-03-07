
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
 *         &lt;element name="componentName" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="clientName" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="fractalClientItfName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="classClientName" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "componentName",
    "clientName",
    "fractalClientItfName",
    "classClientName"
})
@XmlRootElement(name = "createClient")
public class CreateClient {

    @XmlElement(required = true)
    protected QName componentName;
    @XmlElement(required = true)
    protected QName clientName;
    @XmlElement(required = true)
    protected String fractalClientItfName;
    @XmlElement(required = true)
    protected String classClientName;

    /**
     * Gets the value of the componentName property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getComponentName() {
        return componentName;
    }

    /**
     * Sets the value of the componentName property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setComponentName(QName value) {
        this.componentName = value;
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

    /**
     * Gets the value of the fractalClientItfName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFractalClientItfName() {
        return fractalClientItfName;
    }

    /**
     * Sets the value of the fractalClientItfName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFractalClientItfName(String value) {
        this.fractalClientItfName = value;
    }

    /**
     * Gets the value of the classClientName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassClientName() {
        return classClientName;
    }

    /**
     * Sets the value of the classClientName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassClientName(String value) {
        this.classClientName = value;
    }

}
