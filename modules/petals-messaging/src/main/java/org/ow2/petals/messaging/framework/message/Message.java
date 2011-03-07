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

import java.util.Map;

/**
 * A genric message container. The content can be multiple but unique for a
 * type...
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface Message {

    Object get(String property);

    void put(String property, Object value);

    void putAll(Map<String, Object> props);

    Map<String, Object> getAll();

    <T> void setContent(Class<T> clazz, Object content);

    <T> T getContent(Class<T> c);

}
