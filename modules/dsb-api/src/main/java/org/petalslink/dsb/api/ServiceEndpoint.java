/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.petalslink.dsb.api;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "org.petalslink.dsb.api", name = "ServiceEndpoint")
public class ServiceEndpoint {

    private static final long serialVersionUID = 2294406617783993296L;

    @XmlElement
    private QName[] interfaces;

    @XmlElement
    private QName serviceName;

    @XmlElement
    private String endpointName;

    @XmlElement
    private String description;

    @XmlElement
    private String componentLocation;

    @XmlElement
    private String containerLocation;

    @XmlElement
    private String subdomainLocation;

    public String getEndpointName() {
        return this.endpointName;
    }

    public void setEndpointName(final String endpointName) {
        this.endpointName = endpointName;
    }

    /**
     * 
     */
    public ServiceEndpoint() {
        super();
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the componentLocation
     */
    public String getComponentLocation() {
        return this.componentLocation;
    }

    /**
     * @param componentLocation
     *            the componentLocation to set
     */
    public void setComponentLocation(String componentLocation) {
        this.componentLocation = componentLocation;
    }

    /**
     * @return the containerLocation
     */
    public String getContainerLocation() {
        return this.containerLocation;
    }

    /**
     * @param containerLocation
     *            the containerLocation to set
     */
    public void setContainerLocation(String containerLocation) {
        this.containerLocation = containerLocation;
    }

    /**
     * @return the subdomainLocation
     */
    public String getSubdomainLocation() {
        return this.subdomainLocation;
    }

    /**
     * @param subdomainLocation
     *            the subdomainLocation to set
     */
    public void setSubdomainLocation(String subdomainLocation) {
        this.subdomainLocation = subdomainLocation;
    }

    /**
     * @return the interfaces
     */
    public QName[] getInterfaces() {
        return this.interfaces;
    }

    /**
     * @param interfaces
     *            the interfaces to set
     */
    public void setInterfaces(QName[] interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * @return the serviceName
     */
    public QName getServiceName() {
        return this.serviceName;
    }

    /**
     * @param serviceName
     *            the serviceName to set
     */
    public void setServiceName(QName serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ServiceEndpoint [componentLocation=");
        builder.append(this.componentLocation);
        builder.append(", containerLocation=");
        builder.append(this.containerLocation);
        builder.append(", description=");
        builder.append(this.description);
        builder.append(", endpointName=");
        builder.append(this.endpointName);
        builder.append(", interfaces=");
        builder.append(Arrays.toString(this.interfaces));
        builder.append(", serviceName=");
        builder.append(this.serviceName);
        builder.append(", subdomainLocation=");
        builder.append(this.subdomainLocation);
        builder.append("]");
        return builder.toString();
    }

}
