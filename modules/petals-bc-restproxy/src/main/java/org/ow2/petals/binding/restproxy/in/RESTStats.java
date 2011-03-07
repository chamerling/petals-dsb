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
package org.ow2.petals.binding.restproxy.in;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class RESTStats {

    public final AtomicLong getNb;

    public final AtomicLong postNb;

    public final AtomicLong deleteNb;

    public final AtomicLong putNb;

    public final AtomicLong proxyNb;

    /**
     * 
     */
    public RESTStats() {
        this.getNb = new AtomicLong(0L);
        this.postNb = new AtomicLong(0L);
        this.deleteNb = new AtomicLong(0L);
        this.putNb = new AtomicLong(0L);
        this.proxyNb = new AtomicLong(0L);
    }

    /**
     * 
     */
    public void newGet() {
        this.getNb.incrementAndGet();
    }

    public void newPost() {
        this.postNb.incrementAndGet();
    }

    public void newDelete() {
        this.deleteNb.incrementAndGet();
    }

    public void newPut() {
        this.putNb.incrementAndGet();
    }

    public long getGetNb() {
        return this.getNb.longValue();
    }

    public long getPostNb() {
        return this.postNb.longValue();
    }

    public long getDeleteNb() {
        return this.deleteNb.longValue();
    }

    public long getPutNb() {
        return this.putNb.longValue();
    }

    /**
     * @return the proxyNb
     */
    public long getProxyNb() {
        return this.proxyNb.longValue();
    }

    /**
     * 
     */
    public void newProxy() {
        this.proxyNb.incrementAndGet();
    }

}
