
package org.oasis_open.docs.wsdm.mows_2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.wsaddressing.W3CEndpointReference;


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
 *         &lt;element ref="{http://docs.oasis-open.org/wsdm/muws1-2.xsd}ManageabilityEndpointReference" maxOccurs="unbounded"/>
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
    "manageabilityEndpointReference"
})
@XmlRootElement(name = "GetManageabilityReferencesResponse")
public class GetManageabilityReferencesResponse {

    @XmlElement(name = "ManageabilityEndpointReference", namespace = "http://docs.oasis-open.org/wsdm/muws1-2.xsd", required = true)
    protected List<W3CEndpointReference> manageabilityEndpointReference;

    /**
     * Gets the value of the manageabilityEndpointReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the manageabilityEndpointReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getManageabilityEndpointReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link W3CEndpointReference }
     * 
     * 
     */
    public List<W3CEndpointReference> getManageabilityEndpointReference() {
        if (manageabilityEndpointReference == null) {
            manageabilityEndpointReference = new ArrayList<W3CEndpointReference>();
        }
        return this.manageabilityEndpointReference;
    }

}
