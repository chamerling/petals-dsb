/**
 * PETALS - PETALS Services Platform. Copyright (c) 2008 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * -------------------------------------------------------------------------
 * $Id$
 * -------------------------------------------------------------------------
 */
package org.ow2.petals.binding.soap.listener.incoming.jetty;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * Created on 21 févr. 08
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since 3.1
 * 
 */
public class ServerStats {

    private long startTime;
    
    private long stopTime;

    private final AtomicLong getRequests;

    private final AtomicLong postRequests;

    /**
     * 
     */
    public ServerStats() {
        this.getRequests = new AtomicLong();
        this.postRequests = new AtomicLong();
    }

    /**
     * 
     * 
     */
    public void newGetRequest() {
        this.getRequests.incrementAndGet();
    }

    /**
     * 
     * @return
     */
    public long getGetRequests() {
        return this.getRequests.get();
    }

    /**
     * 
     * 
     */
    public void newPostRequest() {
        this.postRequests.incrementAndGet();
    }

    /**
     * 
     * @return
     */
    public long getPostRequests() {
        return this.postRequests.get();
    }

    /**
     * @return the startTime
     */
    public long getStartTime() {
        return this.startTime;
    }

    /**
     * @param startTime
     *            the startTime to set
     */
    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the stopTime
     */
    public long getStopTime() {
        return stopTime;
    }

    /**
     * @param stopTime the stopTime to set
     */
    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

}
