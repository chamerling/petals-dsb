/*
 * PETALS: PETALS Services Platform
 * Copyright (C) 2007 EBM WebSourcing
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA.
 *
 * Initial developer(s): Adrien LOUIS
 * --------------------------------------------------------------------------
 * $Id:
 * --------------------------------------------------------------------------
 */

package org.ow2.petals.monitoring.router;


/**
 * @version $Rev: 617 $ $Date: 2006-06-19 17:28:41 +0200 (lun, 19 jun 2006) $
 * @since Petals 1.1
 * @author alouis
 */
public interface RouterMonitor {

    /**
     * start the monitoring with or without monitoring content of messages. If
     * monitoring is already started, just modify the monitoring of messages
     * content
     *
     * @param showMessageContent
     *            indicates if content of messages have to be monitored, true
     *            for monitor the content, false for ignoring it
     */
    void activateMonitoring(boolean showMessageContent);

    /**
     * stops the monitoring of messages and their content.
     */
    void deactivateMonitoring();

    /**
     * Indicates if content of messages have to be set in the messages monitored
     *
     * @return true if content of messages have to be in the messages, false if
     *         not
     */
    boolean isMessageContentShown();

    /**
     * Indicates if monitoring is started or not
     *
     * @return true if monitoring is started, false if not
     */
    boolean isMonitoring();

    void showMessageContent(boolean showMessageContent);

}
