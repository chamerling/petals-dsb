
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
 *         &lt;element name="fractalItfName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="classComponentName" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "fractalItfName",
    "classComponentName"
})
@XmlRootElement(name = "createComponent")
public class CreateComponent {

    @XmlElement(required = true)
    protected QName componentName;
    @XmlElement(required = true)
    protected String fractalItfName;
    @XmlElement(required = true)
    protected String classComponentName;

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
     * Gets the value of the fractalItfName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFractalItfName() {
        return fractalItfName;
    }

    /**
     * Sets the value of the fractalItfName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFractalItfName(String value) {
        this.fractalItfName = value;
    }

    /**
     * Gets the value of the classComponentName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassComponentName() {
        return classComponentName;
    }

    /**
     * Sets the value of the classComponentName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassComponentName(String value) {
        this.classComponentName = value;
    }

}
