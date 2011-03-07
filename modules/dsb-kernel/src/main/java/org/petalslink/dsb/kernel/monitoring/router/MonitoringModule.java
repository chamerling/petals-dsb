/**
 * PETALS - PETALS Services Platform. Copyright (c) 2007 EBM Websourcing,
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
 * $Id: Router.java,v 1.2 2006/03/17 10:24:27 alouis Exp $
 * -------------------------------------------------------------------------
 */
package org.petalslink.dsb.kernel.monitoring.router;

import java.util.Date;
import java.util.List;

/**
 * @author aruffie - EBM WebSourcing
 *  Provider several functions for monitoring
 *  features in the router
 */
public interface MonitoringModule {
    
    /**
     * Allow to retrieve a message exchange
     * by providing its begin timestamp.
     * Return null if no message exchange
     * is linked to the specified timestamp.
     * 
     * @param Date timestamp
     * @return MessageExchange
     */
    public org.ow2.petals.tools.monitoring.to.MessageExchange getMessageExchange(final Date timestamp);
    
    /**
     * Allow to retrieve a message exchange
     * by providing its exchange id.
     * Return null if no message exchange
     * is linked to the specified exchange id.
     * @param String exchangeId
     * @return MessageExchange
     */
    public org.ow2.petals.tools.monitoring.to.MessageExchange getMessageExchanges(final String exchangeId);
    
    /**
     * Allow to retrieve all message
     * exchanges with a included begin
     * timestamp between the begin and
     * ending timestamp parameters.
     * Return all message exchanges if
     * begin and ending timestamp
     * parameters are null.
     * 
     * @param Date begin
     * @param Date ending
     * @return List<MessageExchange>
     */
    public List<org.ow2.petals.tools.monitoring.to.MessageExchange> getMessageExchange(final Date begin, final Date ending);
    
    /**
     * Return the specified message exchange
     * duration by providing its exchange id.
     * Return 0 if no message exchange is
     * linked to the specified exchange id.
     * @param String exchangeId
     * @return long
     */
    public long getExchangeDuration(final String exchangeId);
    
    /**
     * Return the message exchange component
     * consumer for a specified exchange id
     * @param String exchangeId
     * @return String
     */
    public String getConsumer(final String exchangeId);
    
    /**
     * Return the message exchange component
     * provider for a specified exchange id
     * @param String exchangeId
     * @return String
     */
    public String getProvider(final String exchangeId);
}
