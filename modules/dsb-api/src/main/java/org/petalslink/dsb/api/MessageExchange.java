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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "org.petalslink.dsb.api", name = "MessageExchange")
public class MessageExchange {

    @XmlElement
    protected NormalizedMessage in;

    @XmlElement
    protected NormalizedMessage out;

    @XmlElement
    protected NormalizedMessage fault;

    @XmlAttribute
    protected String id;

    @XmlElement
    protected QName interfaceName;

    @XmlElement
    protected QName operation;

    @XmlElement
    protected URI pattern;

    @XmlElement
    protected String error;

    /**
     * Consumer or provider...
     */
    @XmlElement
    protected String role;

    @XmlElement
    protected QName service;

    @XmlElement
    protected String status;

    @XmlElement
    protected boolean terminated;

    @XmlElement
    protected boolean transacted;

    @XmlElement
    protected List<Property> properties;

    @XmlElement
    protected ServiceEndpoint endpoint;

    @XmlElement
    protected ServiceEndpoint consumer;

    /**
     * 
     */
    public MessageExchange() {
        this.properties = new ArrayList<Property>(0);
    }

    public NormalizedMessage getIn() {
        return this.in;
    }

    public void setIn(NormalizedMessage in) {
        this.in = in;
    }

    public NormalizedMessage getOut() {
        return this.out;
    }

    public void setOut(NormalizedMessage out) {
        this.out = out;
    }

    public NormalizedMessage getFault() {
        return this.fault;
    }

    public void setFault(NormalizedMessage fault) {
        this.fault = fault;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public QName getInterfaceName() {
        return this.interfaceName;
    }

    public void setInterfaceName(QName interfaceName) {
        this.interfaceName = interfaceName;
    }

    public QName getOperation() {
        return this.operation;
    }

    public void setOperation(QName operation) {
        this.operation = operation;
    }

    public URI getPattern() {
        return this.pattern;
    }

    public void setPattern(URI pattern) {
        this.pattern = pattern;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public QName getService() {
        return this.service;
    }

    public void setService(QName service) {
        this.service = service;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isTerminated() {
        return this.terminated;
    }

    public void setTerminated(boolean terminated) {
        this.terminated = terminated;
    }

    public boolean isTransacted() {
        return this.transacted;
    }

    public void setTransacted(boolean transacted) {
        this.transacted = transacted;
    }

    public List<Property> getProperties() {
        return this.properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public ServiceEndpoint getEndpoint() {
        return this.endpoint;
    }

    public void setEndpoint(ServiceEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public ServiceEndpoint getConsumer() {
        return this.consumer;
    }

    public void setConsumer(ServiceEndpoint consumer) {
        this.consumer = consumer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MessageExchange [consumer=");
        builder.append(this.consumer);
        builder.append(", endpoint=");
        builder.append(this.endpoint);
        builder.append(", interfaceName=");
        builder.append(this.interfaceName);
        builder.append(", operation=");
        builder.append(this.operation);
        builder.append(", id=");
        builder.append(this.id);
        builder.append(", in=");
        builder.append(this.in);
        builder.append(", out=");
        builder.append(this.out);
        builder.append(", fault=");
        builder.append(this.fault);
        builder.append(", error=");
        builder.append(this.error);
        builder.append(", pattern=");
        builder.append(this.pattern);
        builder.append(", properties=");
        builder.append(this.properties);
        builder.append(", role=");
        builder.append(this.role);
        builder.append(", service=");
        builder.append(this.service);
        builder.append(", status=");
        builder.append(this.status);
        builder.append(", terminated=");
        builder.append(this.terminated);
        builder.append(", transacted=");
        builder.append(this.transacted);
        builder.append("]");
        return builder.toString();
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
