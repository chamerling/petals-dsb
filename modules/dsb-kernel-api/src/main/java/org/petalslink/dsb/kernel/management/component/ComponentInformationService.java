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
package org.petalslink.dsb.kernel.management.component;

import java.util.Set;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface ComponentInformationService {

    /**
     * Get a list of service URLs which are exposed by the given component. If
     * the component is not found, return an empty list.
     * 
     * @param componentName
     * @return
     */
    Set<String> getExposedServiceURLs(String componentName);

    /**
     * Get a list of service URLs which are bound to the bus by the given
     * component. If no such component is found, return an empty list.
     * 
     * @param componentName
     * @return
     */
    Set<String> getConsumedServiceURLs(String componentName);

    /**
     * Get a property value from a component.
     * 
     * @param name
     * @return value or null if not found
     */
    String getProperty(String componentName, String name);

}
