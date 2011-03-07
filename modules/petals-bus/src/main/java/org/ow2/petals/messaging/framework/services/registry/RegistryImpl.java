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
package org.ow2.petals.messaging.framework.services.registry;

import java.util.HashMap;
import java.util.Map;

/**
 * Regsitry of things implementation
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class RegistryImpl<K, V> implements Registry<K, V> {

    Map<K, V> map;

    /**
     * 
     */
    public RegistryImpl() {
        this.map = new HashMap<K, V>();
    }

    /**
     * {@inheritDoc}
     */
    public V get(K k) {
        return this.map.get(k);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(K k) {
        this.remove(k);
    }

    /**
     * {@inheritDoc}
     */
    public void store(K k, V v) {
        this.map.put(k, v);
    }
}
