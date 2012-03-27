/**
 * PETALS - PETALS Services Platform. Copyright (c) 2009 EBM Websourcing,
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
package org.ow2.petals.binding.soap.addressing;

/**
 * The WS-Addressing Bean
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class Addressing {

    private String to = null;

    private String from = null;

    private String replyTo = null;

    private String faultTo = null;

    private String messageId = null;

    /**
     * 
     */
    public Addressing() {
        this(null);
    }

    /**
     * 
     * @param to
     */
    public Addressing(String to) {
        this(to, null, null, null, null);
    }

    /**
     * 
     * @param to
     * @param from
     * @param replyTo
     * @param faultTo
     * @param messageId
     */
    public Addressing(String to, String from, String replyTo, String faultTo, String messageId) {
        super();
        this.to = to;
        this.from = from;
        this.replyTo = replyTo;
        this.faultTo = faultTo;
        this.messageId = messageId;
    }

    /**
     * @return the to
     */
    public String getTo() {
        return to;
    }

    /**
     * @param to
     *            the to to set
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * @return the from
     */
    public String getFrom() {
        return from;
    }

    /**
     * @param from
     *            the from to set
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * @return the replyTo
     */
    public String getReplyTo() {
        return replyTo;
    }

    /**
     * @param replyTo
     *            the replyTo to set
     */
    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    /**
     * @return the faultTo
     */
    public String getFaultTo() {
        return faultTo;
    }

    /**
     * @param faultTo
     *            the faultTo to set
     */
    public void setFaultTo(String faultTo) {
        this.faultTo = faultTo;
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @param messageId
     *            the messageId to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
