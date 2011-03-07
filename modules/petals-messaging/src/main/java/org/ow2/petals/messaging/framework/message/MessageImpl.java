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
package org.ow2.petals.messaging.framework.message;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic typesafe message container implementation.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class MessageImpl implements Message {

    private final Map<String, Object> properties;

    private final Map<Class<?>, Object> content;

    /**
     * 
     */
    public MessageImpl() {
        this.properties = new HashMap<String, Object>(6);
        this.content = new HashMap<Class<?>, Object>(6);
    }

    /**
     * {@inheritDoc}
     */
    public Object get(String property) {
        return this.properties.get(property);
    }

    /**
     * {@inheritDoc}
     */
    public void put(String property, Object value) {
        this.properties.put(property, value);
    }

    /**
     * {@inheritDoc}
     */
    public void putAll(Map<String, Object> props) {
        if (props != null) {
            this.properties.putAll(props);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getAll() {
        return this.properties;
    }

    /**
     * {@inheritDoc}
     */
    public <T> void setContent(Class<T> clazz, Object content) {
        this.content.put(clazz, content);
    }

    /**
     * {@inheritDoc}
     */
    public <T> T getContent(Class<T> c) {
        return c.cast(this.content.get(c));
    }
}
