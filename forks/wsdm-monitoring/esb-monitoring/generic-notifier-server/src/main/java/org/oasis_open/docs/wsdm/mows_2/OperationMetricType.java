
package org.oasis_open.docs.wsdm.mows_2;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * <p>Java class for OperationMetricType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OperationMetricType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://docs.oasis-open.org/wsdm/mows-2.xsd}NumberOfRequests" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsdm/mows-2.xsd}NumberOfSuccessfulRequests" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsdm/mows-2.xsd}NumberOfFailedRequests" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsdm/mows-2.xsd}ServiceTime" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsdm/mows-2.xsd}MaxResponseTime" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsdm/mows-2.xsd}LastResponseTime" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsdm/mows-2.xsd}MaxRequestSize" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsdm/mows-2.xsd}LastRequestSize" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsdm/mows-2.xsd}MaxResponseSize" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsdm/mows-2.xsd}LastResponseSize" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://docs.oasis-open.org/wsdm/mows-2.xsd}OperationNameGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OperationMetricType", propOrder = {
    "numberOfRequests",
    "numberOfSuccessfulRequests",
    "numberOfFailedRequests",
    "serviceTime",
    "maxResponseTime",
    "lastResponseTime",
    "maxRequestSize",
    "lastRequestSize",
    "maxResponseSize",
    "lastResponseSize"
})
public class OperationMetricType {

    @XmlElement(name = "NumberOfRequests")
    protected IntegerCounter numberOfRequests;
    @XmlElement(name = "NumberOfSuccessfulRequests")
    protected IntegerCounter numberOfSuccessfulRequests;
    @XmlElement(name = "NumberOfFailedRequests")
    protected IntegerCounter numberOfFailedRequests;
    @XmlElement(name = "ServiceTime")
    protected DurationMetric serviceTime;
    @XmlElement(name = "MaxResponseTime")
    protected DurationMetric maxResponseTime;
    @XmlElement(name = "LastResponseTime")
    protected DurationMetric lastResponseTime;
    @XmlElement(name = "MaxRequestSize")
    protected IntegerCounter maxRequestSize;
    @XmlElement(name = "LastRequestSize")
    protected IntegerCounter lastRequestSize;
    @XmlElement(name = "MaxResponseSize")
    protected IntegerCounter maxResponseSize;
    @XmlElement(name = "LastResponseSize")
    protected IntegerCounter lastResponseSize;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String operationName;
    @XmlAttribute
    protected QName portType;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the numberOfRequests property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerCounter }
     *     
     */
    public IntegerCounter getNumberOfRequests() {
        return numberOfRequests;
    }

    /**
     * Sets the value of the numberOfRequests property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerCounter }
     *     
     */
    public void setNumberOfRequests(IntegerCounter value) {
        this.numberOfRequests = value;
    }

    /**
     * Gets the value of the numberOfSuccessfulRequests property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerCounter }
     *     
     */
    public IntegerCounter getNumberOfSuccessfulRequests() {
        return numberOfSuccessfulRequests;
    }

    /**
     * Sets the value of the numberOfSuccessfulRequests property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerCounter }
     *     
     */
    public void setNumberOfSuccessfulRequests(IntegerCounter value) {
        this.numberOfSuccessfulRequests = value;
    }

    /**
     * Gets the value of the numberOfFailedRequests property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerCounter }
     *     
     */
    public IntegerCounter getNumberOfFailedRequests() {
        return numberOfFailedRequests;
    }

    /**
     * Sets the value of the numberOfFailedRequests property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerCounter }
     *     
     */
    public void setNumberOfFailedRequests(IntegerCounter value) {
        this.numberOfFailedRequests = value;
    }

    /**
     * Gets the value of the serviceTime property.
     * 
     * @return
     *     possible object is
     *     {@link DurationMetric }
     *     
     */
    public DurationMetric getServiceTime() {
        return serviceTime;
    }

    /**
     * Sets the value of the serviceTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DurationMetric }
     *     
     */
    public void setServiceTime(DurationMetric value) {
        this.serviceTime = value;
    }

    /**
     * Gets the value of the maxResponseTime property.
     * 
     * @return
     *     possible object is
     *     {@link DurationMetric }
     *     
     */
    public DurationMetric getMaxResponseTime() {
        return maxResponseTime;
    }

    /**
     * Sets the value of the maxResponseTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DurationMetric }
     *     
     */
    public void setMaxResponseTime(DurationMetric value) {
        this.maxResponseTime = value;
    }

    /**
     * Gets the value of the lastResponseTime property.
     * 
     * @return
     *     possible object is
     *     {@link DurationMetric }
     *     
     */
    public DurationMetric getLastResponseTime() {
        return lastResponseTime;
    }

    /**
     * Sets the value of the lastResponseTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DurationMetric }
     *     
     */
    public void setLastResponseTime(DurationMetric value) {
        this.lastResponseTime = value;
    }

    /**
     * Gets the value of the maxRequestSize property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerCounter }
     *     
     */
    public IntegerCounter getMaxRequestSize() {
        return maxRequestSize;
    }

    /**
     * Sets the value of the maxRequestSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerCounter }
     *     
     */
    public void setMaxRequestSize(IntegerCounter value) {
        this.maxRequestSize = value;
    }

    /**
     * Gets the value of the lastRequestSize property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerCounter }
     *     
     */
    public IntegerCounter getLastRequestSize() {
        return lastRequestSize;
    }

    /**
     * Sets the value of the lastRequestSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerCounter }
     *     
     */
    public void setLastRequestSize(IntegerCounter value) {
        this.lastRequestSize = value;
    }

    /**
     * Gets the value of the maxResponseSize property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerCounter }
     *     
     */
    public IntegerCounter getMaxResponseSize() {
        return maxResponseSize;
    }

    /**
     * Sets the value of the maxResponseSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerCounter }
     *     
     */
    public void setMaxResponseSize(IntegerCounter value) {
        this.maxResponseSize = value;
    }

    /**
     * Gets the value of the lastResponseSize property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerCounter }
     *     
     */
    public IntegerCounter getLastResponseSize() {
        return lastResponseSize;
    }

    /**
     * Sets the value of the lastResponseSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerCounter }
     *     
     */
    public void setLastResponseSize(IntegerCounter value) {
        this.lastResponseSize = value;
    }

    /**
     * Gets the value of the operationName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationName() {
        return operationName;
    }

    /**
     * Sets the value of the operationName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationName(String value) {
        this.operationName = value;
    }

    /**
     * Gets the value of the portType property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getPortType() {
        return portType;
    }

    /**
     * Sets the value of the portType property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setPortType(QName value) {
        this.portType = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
