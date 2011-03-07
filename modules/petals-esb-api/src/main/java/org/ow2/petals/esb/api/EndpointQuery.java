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
package org.ow2.petals.esb.api;

import java.util.HashMap;
import java.util.Map;

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
@XmlType(namespace = "org.ow2.petals.esb.api", name = "EndpointQuery")
public class EndpointQuery {

    @XmlElement(name = "endpoint")
    private String endpoint;

    @XmlElement(name = "service")
    private QName service;

    @XmlElement(name = "interface")
    private QName itf;

    @XmlElement(name = "ignoreParams")
    private boolean ignoreParams;

    @XmlElement(name = "container")
    private String container;

    @XmlElement(name = "component")
    private String component;

    @XmlElement(name = "subdomain")
    private String subDomain;

    @XmlElement(name = "type")
    private String type;

    @XmlElement(name = "strategy")
    private String strategy;

    /**
     * others...
     */
    private Map<String, String> params;

    @XmlElement(name = "linktype")
    private String linkType;

    public EndpointQuery() {
        this.params = new HashMap<String, String>();
        this.ignoreParams = true;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public QName getService() {
        return this.service;
    }

    public void setService(QName service) {
        this.service = service;
    }

    public QName getInterface() {
        return this.itf;
    }

    public void setInterface(QName itf) {
        this.itf = itf;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public boolean isIgnoreParams() {
        return this.ignoreParams;
    }

    public void setIgnoreParams(boolean ignoreParams) {
        this.ignoreParams = ignoreParams;
    }

    public String getContainer() {
        return this.container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public String getComponent() {
        return this.component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getSubDomain() {
        return this.subDomain;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param strategy
     *            the strategy to set
     */
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    /**
     * @return the strategy
     */
    public String getStrategy() {
        return this.strategy;
    }

    /**
     * @param linkType
     */
    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    /**
     * @return the linkType
     */
    public String getLinkType() {
        return this.linkType;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EndpointQuery [component=");
        builder.append(this.component);
        builder.append(", container=");
        builder.append(this.container);
        builder.append(", endpoint=");
        builder.append(this.endpoint);
        builder.append(", ignoreParams=");
        builder.append(this.ignoreParams);
        builder.append(", itf=");
        builder.append(this.itf);
        builder.append(", linkType=");
        builder.append(this.linkType);
        builder.append(", params=");
        builder.append(this.params);
        builder.append(", service=");
        builder.append(this.service);
        builder.append(", strategy=");
        builder.append(this.strategy);
        builder.append(", subDomain=");
        builder.append(this.subDomain);
        builder.append(", type=");
        builder.append(this.type);
        builder.append("]");
        return builder.toString();
    }

    public QName getItf() {
        return this.itf;
    }

    public void setItf(QName itf) {
        this.itf = itf;
    }

}
