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
package org.ow2.petals.component.framework;

import java.util.Map;
import java.util.Set;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface ComponentInformation {
    /**
     * Get the list of exposed services ie URLs of services which are accessible
     * from outside of the service bus.
     * 
     * @return
     */
    Set<String> getExposedServices();

    /**
     * Get the list of services which are bound to the bus by this component.
     * The values are URLs.
     * 
     * @return
     */
    Set<String> getConsumedServices();

    /**
     * Get a map or properties which can be shared between the component and the
     * service bus.
     * 
     * @return
     */
    Map<String, String> getProperties();

    void addProperty(String name, String value);

    String getProperty(String name);
}
