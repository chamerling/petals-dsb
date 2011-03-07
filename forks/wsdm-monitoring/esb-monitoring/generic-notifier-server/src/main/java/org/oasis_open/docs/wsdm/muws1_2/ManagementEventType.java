
package org.oasis_open.docs.wsdm.muws1_2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


/**
 * <p>Java class for ManagementEventType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ManagementEventType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EventId" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="SourceComponent" type="{http://docs.oasis-open.org/wsdm/muws1-2.xsd}ComponentType"/>
 *         &lt;element name="ReporterComponent" type="{http://docs.oasis-open.org/wsdm/muws1-2.xsd}ComponentType" minOccurs="0"/>
 *         &lt;any/>
 *       &lt;/sequence>
 *       &lt;attribute name="ReportTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManagementEventType", propOrder = {
    "eventId",
    "sourceComponent",
    "reporterComponent",
    "any"
})
public class ManagementEventType {

    @XmlElement(name = "EventId", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String eventId;
    @XmlElement(name = "SourceComponent", required = true)
    protected ComponentType sourceComponent;
    @XmlElement(name = "ReporterComponent")
    protected ComponentType reporterComponent;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "ReportTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar reportTime;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the eventId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the value of the eventId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventId(String value) {
        this.eventId = value;
    }

    /**
     * Gets the value of the sourceComponent property.
     * 
     * @return
     *     possible object is
     *     {@link ComponentType }
     *     
     */
    public ComponentType getSourceComponent() {
        return sourceComponent;
    }

    /**
     * Sets the value of the sourceComponent property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComponentType }
     *     
     */
    public void setSourceComponent(ComponentType value) {
        this.sourceComponent = value;
    }

    /**
     * Gets the value of the reporterComponent property.
     * 
     * @return
     *     possible object is
     *     {@link ComponentType }
     *     
     */
    public ComponentType getReporterComponent() {
        return reporterComponent;
    }

    /**
     * Sets the value of the reporterComponent property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComponentType }
     *     
     */
    public void setReporterComponent(ComponentType value) {
        this.reporterComponent = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * {@link Object }
     * 
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the reportTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReportTime() {
        return reportTime;
    }

    /**
     * Sets the value of the reportTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReportTime(XMLGregorianCalendar value) {
        this.reportTime = value;
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
