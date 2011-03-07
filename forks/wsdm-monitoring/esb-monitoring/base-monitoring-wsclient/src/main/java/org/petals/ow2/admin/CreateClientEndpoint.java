
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
 *         &lt;element name="clientName" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="clientEndpointName" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="fractalClientEndpointItfName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="classClientEndpointName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="classClientEndpointBehaviourName" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "clientName",
    "clientEndpointName",
    "fractalClientEndpointItfName",
    "classClientEndpointName",
    "classClientEndpointBehaviourName"
})
@XmlRootElement(name = "createClientEndpoint")
public class CreateClientEndpoint {

    @XmlElement(required = true)
    protected QName clientName;
    @XmlElement(required = true)
    protected QName clientEndpointName;
    @XmlElement(required = true)
    protected String fractalClientEndpointItfName;
    @XmlElement(required = true)
    protected String classClientEndpointName;
    @XmlElement(required = true)
    protected String classClientEndpointBehaviourName;

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
     * Gets the value of the clientEndpointName property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getClientEndpointName() {
        return clientEndpointName;
    }

    /**
     * Sets the value of the clientEndpointName property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setClientEndpointName(QName value) {
        this.clientEndpointName = value;
    }

    /**
     * Gets the value of the fractalClientEndpointItfName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFractalClientEndpointItfName() {
        return fractalClientEndpointItfName;
    }

    /**
     * Sets the value of the fractalClientEndpointItfName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFractalClientEndpointItfName(String value) {
        this.fractalClientEndpointItfName = value;
    }

    /**
     * Gets the value of the classClientEndpointName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassClientEndpointName() {
        return classClientEndpointName;
    }

    /**
     * Sets the value of the classClientEndpointName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassClientEndpointName(String value) {
        this.classClientEndpointName = value;
    }

    /**
     * Gets the value of the classClientEndpointBehaviourName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassClientEndpointBehaviourName() {
        return classClientEndpointBehaviourName;
    }

    /**
     * Sets the value of the classClientEndpointBehaviourName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassClientEndpointBehaviourName(String value) {
        this.classClientEndpointBehaviourName = value;
    }

}
