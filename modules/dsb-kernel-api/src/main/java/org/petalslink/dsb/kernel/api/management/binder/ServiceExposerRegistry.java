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
package org.petalslink.dsb.kernel.api.management.binder;

/**
 * A {@link ServiceExposer} registry!
 * 
 * NOTE : It looks like fractal/spoon have problems at compile time if this
 * class is in the API, NPE at compilation...
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface ServiceExposerRegistry {

    /**
     * Get the binder for the given protocol name
     * 
     * @param protocolName
     * @return the binder if found, else null!
     */
    ServiceExposer getServiceExposer(String protocolName);

}
