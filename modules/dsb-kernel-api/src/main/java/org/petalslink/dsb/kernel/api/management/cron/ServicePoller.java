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
package org.petalslink.dsb.kernel.api.management.cron;

import java.util.concurrent.TimeUnit;

/**
 * Service which polls the endpoint registry
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface ServicePoller {

    /**
     * Start polling new services
     */
    void startPolling();

    /**
     * Stop polling new services
     */
    void stopPolling();

    /**
     * Set the polling period
     * 
     * @param time
     * @param timeUnit
     */
    void setPeriod(long time, TimeUnit timeUnit);

    /**
     * 
     * @return true if the poller has been paused, false if not (it is already
     *         paused)
     */
    void pause();

    /**
     * 
     * @return true if the poller has been resumed, false if not (it is already
     *         running)
     */
    void resume();

}
