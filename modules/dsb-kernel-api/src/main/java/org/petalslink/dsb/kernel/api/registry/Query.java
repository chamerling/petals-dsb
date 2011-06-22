/**
 * 
 */
package org.petalslink.dsb.kernel.api.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

/**
 * @author chamerling
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class Query {

    @XmlElement
    private QName[] interfaces;

    @XmlElement
    private QName serviceName;

    @XmlElement
    private String endpointName;
    
    /**
     * 
     */
    public Query() {
    }

    /**
     * @return the interfaces
     */
    public QName[] getInterfaces() {
        return interfaces;
    }

    /**
     * @param interfaces the interfaces to set
     */
    public void setInterfaces(QName[] interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * @return the serviceName
     */
    public QName getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(QName serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the endpointName
     */
    public String getEndpointName() {
        return endpointName;
    }

    /**
     * @param endpointName the endpointName to set
     */
    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

}
