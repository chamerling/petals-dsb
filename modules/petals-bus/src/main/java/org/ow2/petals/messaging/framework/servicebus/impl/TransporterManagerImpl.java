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
package org.ow2.petals.messaging.framework.servicebus.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ow2.petals.messaging.framework.servicebus.TransportManager;
import org.ow2.petals.messaging.framework.servicebus.Transporter;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class TransporterManagerImpl implements TransportManager {

    Map<String, Transporter> transporters;

    /**
     * 
     */
    public TransporterManagerImpl() {
        this.transporters = new ConcurrentHashMap<String, Transporter>();
    }

    /**
     * {@inheritDoc}
     */
    public void addTransporter(String ns, Transporter transporter) {
        this.transporters.put(ns, transporter);
    }

    /**
     * {@inheritDoc}
     */
    public Transporter getTransporter(String ns) {
        return this.transporters.get(ns);
    }

}
