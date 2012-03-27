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

/**
 * Launcher interface
 * 
 * @author chamerling - eBM WebSourcing
 * @since 1.2
 *
 */
public interface PetalsLauncher {
    
    /*
     * FIXME : These constants are already defined in the Configuration class of
     * the petals-kernel library. On the next version these constants and
     * interfaces must be moved to the petals-kernel-api
     */ 
    public static final String TOPOLOGY_FILE = "/topology.xml";

    public static final String SERVER_PROPS_FILE = "/server.properties";
    
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
    void stop() throws Exception;
    
    /**
     * Shutdown PEtALS
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
}
