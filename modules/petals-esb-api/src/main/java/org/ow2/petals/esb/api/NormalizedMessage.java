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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "org.ow2.petals.esb.api", name = "NormalizedMessage")
public class NormalizedMessage {

    @XmlElement
    private List<Property> properties;

    @XmlElement
    private String content;

    /**
     * 
     */
    public NormalizedMessage() {
        this.properties = new ArrayList<Property>(0);
    }

    /**
     * @return the properties
     */
    public List<Property> getProperties() {
        return this.properties;
    }

    /**
     * @param properties
     *            the properties to set
     */
    public void setProperties(final List<Property> properties) {
        this.properties = properties;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return this.content;
    }

    /**
     * @param content
     *            the content to set
     */
    public void setContent(final String content) {
        this.content = content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NormalizedMessage [content=");
        builder.append(this.content);
        builder.append(", properties=");
        builder.append(this.properties);
        builder.append("]");
        return builder.toString();
    }

}
