
package org.oasis_open.docs.wsdm.muws2_2;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EventCorrelationPropertiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EventCorrelationPropertiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="repeatCount" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}short">
 *               &lt;minInclusive value="0"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="elapsedTime" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}long">
 *               &lt;minInclusive value="0"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="sequenceNumber" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventCorrelationPropertiesType", propOrder = {
    "repeatCount",
    "elapsedTime"
})
public class EventCorrelationPropertiesType {

    protected Short repeatCount;
    protected Long elapsedTime;
    @XmlAttribute
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger sequenceNumber;

    /**
     * Gets the value of the repeatCount property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getRepeatCount() {
        return repeatCount;
    }

    /**
     * Sets the value of the repeatCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setRepeatCount(Short value) {
        this.repeatCount = value;
    }

    /**
     * Gets the value of the elapsedTime property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getElapsedTime() {
        return elapsedTime;
    }

    /**
     * Sets the value of the elapsedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setElapsedTime(Long value) {
        this.elapsedTime = value;
    }

    /**
     * Gets the value of the sequenceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets the value of the sequenceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSequenceNumber(BigInteger value) {
        this.sequenceNumber = value;
    }

}
