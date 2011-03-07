/**
 * PETALS - PETALS Services Platform.
 * Copyright (c) 2005 EBM Websourcing, http://www.ebmwebsourcing.com/
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
 * $Id: MonitoringMBean.java msauvage $
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.monitoring.router;

public interface MonitoringMBean {

    /**
     * Size of list of messages to send by notification. Default value is set to
     * 50
     */
    int messagesThreshold = 10;

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
     * Return the threshold number of messages to send
     */
    int getMessagesThreshold();

    /**
     * Returns the time to wait between two notifications if messages threshold
     * number has not been reached
     */
    int getTimeSendLimit();

    /**
     * Indicates if content of messages have to be set in the messages monitored
     * 
     * @return true if content of messages have to be in the messages, false if
     *         not
     */
    boolean isMessageContentShown();
    
    /**
     * Indicates if monitoring is active or not.
     * 
     * @return true if monitoring is running, false if not.
     */
    boolean isMonitoring();

    /**
     * Sets the threshold of messages to send.
     * 
     * @param threshold
     *            the new number of messages
     */
    void setMessagesThreshold(int threshold);

    /**
     * Sets the time to wait between two notifications, if messages threshold
     * number has not been reached
     * 
     * @param time
     *            the time to wait in milliseconds
     */
    void setTimeSendLimit(int time);
}
