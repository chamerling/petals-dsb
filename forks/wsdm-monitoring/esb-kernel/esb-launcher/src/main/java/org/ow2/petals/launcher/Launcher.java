/**
 * PETALS - PETALS Services Platform.
 * Copyright (c) 2008 EBM Websourcing, http://www.ebmwebsourcing.com/
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * -------------------------------------------------------------------------
 * $Id$
 * -------------------------------------------------------------------------
 */
package org.ow2.petals.launcher;

import org.ow2.petals.esb.kernel.api.ESBKernelFactory;

/**
 * Launcher interface
 * 
 * @author chamerling - eBM WebSourcing
 * @since 1.2
 *
 */
public interface Launcher {
    
    
    /**
     * Start PEtALS
     * 
     * @throws Exception
     */
    void start() throws Exception;
    
    /**
     * Stop PEtALS
     * 
     * @throws Exception
     */
    void shutdown() throws Exception;
    
    
    /**
     * Get the PEtALS version
     * 
     * @throws Exception
     */
    void version() throws Exception;
    
    
    ESBKernelFactory getFactory();
    

}
