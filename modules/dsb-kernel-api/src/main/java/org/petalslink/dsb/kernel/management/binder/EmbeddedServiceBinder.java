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
package org.petalslink.dsb.kernel.management.binder;

import java.util.List;
import java.util.Map;

/**
 * A Service which is called to automatically bind a list of service at node
 * startup. This is only an utility for node restart! This is done only once!!!
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface EmbeddedServiceBinder {

    /**
     * Bind all the services which have been defined somewhere... (config file)
     */
    void bindAll();

    /**
     * Get the remaining services to bind
     * 
     * @return
     */
    Map<String, List<String>> getServicesToBind();
}
