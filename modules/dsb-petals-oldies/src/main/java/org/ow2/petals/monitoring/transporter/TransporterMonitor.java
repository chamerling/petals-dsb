/**
 * PETALS - PETALS Services Platform.
 * Copyright (c) 2007 EBM Websourcing, http://www.ebmwebsourcing.com/
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

package org.ow2.petals.monitoring.transporter;

import java.util.Map;

import org.ow2.petals.transport.TransportException;

/**
 * ProtocolMonitoring interface defines methods allowing to retrieve the max
 * queue size (maximal number of messages in the queue) of component queues for
 * the current transporter protocol and the current queue size (number of
 * pending message in the queue) of all installed components for current
 * transporter protocol
 * 
 * @author ofabre - eBM Websourcing
 * 
 */
public interface TransporterMonitor {

    /**
     * Returns the queue size (number of pending message in the queue) of all
     * installed components for the current transporter protocol
     * 
     * @return a {@link Map} containing the queue size of all installed
     *         components for the given transporter protocol. Key is a component
     *         name and value is the queue size for this component. Queue sizes
     *         are positive integers
     * @throws TransportException
     *             if an error occurs during queue size retrieval
     */
    Map<String, Integer> getQueueSizes() throws TransportException;

    /**
     * Returns the max queue size (maximal number of messages in the queue) of
     * component queues for the given transporter protocol
     * 
     * @return an integer value containing the max queue size of component
     *         queues for the given transporter protocol. Value is a positive
     *         integer or -1 if there's no maximum
     * @throws TransportException
     *             if an error occurs during max queue size retrieval
     */
    int getQueueMaxSize() throws TransportException;

}
