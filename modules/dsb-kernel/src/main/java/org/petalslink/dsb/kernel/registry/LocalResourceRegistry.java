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
package org.petalslink.dsb.kernel.registry;

import java.util.Set;

/**
 * Services URL (Set<String>) are under /components/COMPONENT_NAME/services/url
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface LocalResourceRegistry {

    /**
     * Create all the initial resources
     */
    void create();

    /**
     * Create a resource directory for the given component
     * 
     * @param componentName
     */
    void createComponent(String componentName);

    /**
     * 
     * @param path
     * @param o
     */
    void putResource(String path, Object o);

    /**
     * 
     * @param path
     * @return
     */
    Object getResource(String path);

    /**
     * Get a list of exposed service URLs for the given component
     * 
     * @param componentName
     * @return
     */
    Set<String> getExposedServiceURLs(String componentName);

}
