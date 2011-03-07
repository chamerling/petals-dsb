
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
 *         &lt;element name="providerName" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="fractalProviderItfName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="classProviderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "providerName",
    "fractalProviderItfName",
    "classProviderName"
})
@XmlRootElement(name = "createProvider")
public class CreateProvider {

    @XmlElement(required = true)
    protected QName componentName;
    @XmlElement(required = true)
    protected QName providerName;
    @XmlElement(required = true)
    protected String fractalProviderItfName;
    @XmlElement(required = true)
    protected String classProviderName;

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
     * Gets the value of the fractalProviderItfName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFractalProviderItfName() {
        return fractalProviderItfName;
    }

    /**
     * Sets the value of the fractalProviderItfName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFractalProviderItfName(String value) {
        this.fractalProviderItfName = value;
    }

    /**
     * Gets the value of the classProviderName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassProviderName() {
        return classProviderName;
    }

    /**
     * Sets the value of the classProviderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassProviderName(String value) {
        this.classProviderName = value;
    }

}
