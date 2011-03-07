
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
 *         &lt;element name="clientAndProviderName" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="fractalClientAndProviderInterfaceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="classClientAndProviderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fractalClientItfName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="classClientName" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "clientAndProviderName",
    "fractalClientAndProviderInterfaceName",
    "classClientAndProviderName",
    "fractalClientItfName",
    "classClientName",
    "fractalProviderItfName",
    "classProviderName"
})
@XmlRootElement(name = "createClientAndProvider")
public class CreateClientAndProvider {

    @XmlElement(required = true)
    protected QName componentName;
    @XmlElement(required = true)
    protected QName clientAndProviderName;
    @XmlElement(required = true)
    protected String fractalClientAndProviderInterfaceName;
    @XmlElement(required = true)
    protected String classClientAndProviderName;
    @XmlElement(required = true)
    protected String fractalClientItfName;
    @XmlElement(required = true)
    protected String classClientName;
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
     * Gets the value of the clientAndProviderName property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getClientAndProviderName() {
        return clientAndProviderName;
    }

    /**
     * Sets the value of the clientAndProviderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setClientAndProviderName(QName value) {
        this.clientAndProviderName = value;
    }

    /**
     * Gets the value of the fractalClientAndProviderInterfaceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFractalClientAndProviderInterfaceName() {
        return fractalClientAndProviderInterfaceName;
    }

    /**
     * Sets the value of the fractalClientAndProviderInterfaceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFractalClientAndProviderInterfaceName(String value) {
        this.fractalClientAndProviderInterfaceName = value;
    }

    /**
     * Gets the value of the classClientAndProviderName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassClientAndProviderName() {
        return classClientAndProviderName;
    }

    /**
     * Sets the value of the classClientAndProviderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassClientAndProviderName(String value) {
        this.classClientAndProviderName = value;
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
