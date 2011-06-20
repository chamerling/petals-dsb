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
package org.petalslink.dsb.kernel.federation;

import java.util.Map;
import java.util.Set;

/**
 * A persistent store service to get/update endpoint visibility
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface FederationFilterStore {

    /**
     * Load the store and returns the result
     * 
     * @return
     */
    Map<String, Set<String>> load();

    /**
     * Get the values already loaded...
     * 
     * @return
     */
    Map<String, Set<String>> getValues();

    /**
     * Save the given values (persist depends on the implementation)
     * 
     * @param filters
     */
    void save(Map<String, Set<String>> filters);

}
