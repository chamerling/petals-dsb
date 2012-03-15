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

import java.util.Map;

import javax.xml.namespace.QName;

import org.ow2.petals.binding.soap.util.SUPropertiesHelper;
import org.ow2.petals.component.framework.api.Constants;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;

/**
 * WS-Addressing Helper. Creates the JBI information from strings.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class WSAHelper {
    /**
     * Get the addressing from the map
     * 
     * @param map
     *            the map properties
     * @return the adressing
     * @deprecated use
     *             {@link #org.ow2.petals.binding.soap.addressing.getAddressing(String address)}
     */
    public static final Addressing getAddressing(final Map<QName, String> map) {
        if (map == null) {
            return null;
        }
        Addressing addressing = new Addressing();
        addressing.setReplyTo(map.get(Constants.WSStar.Addressing.REPLY_TO_QNAME));
        addressing.setFaultTo(map.get(Constants.WSStar.Addressing.FAULT_TO_QNAME));
        addressing.setFrom(map.get(Constants.WSStar.Addressing.FROM_QNAME));
        addressing.setTo(map.get(Constants.WSStar.Addressing.TO_QNAME));
        return addressing;
    }

    /**
     * Get the addressing information from the extensions
     * 
     * @param extensions
     * @return null if extensions is null or the addressing bean filled with the
     *         extensions properties.
     */
    public static final Addressing getAddressing(final ConfigurationExtensions extensions) {
        if (extensions == null) {
            return null;
        }
        Addressing addressing = new Addressing();
        addressing.setFrom(SUPropertiesHelper.getWSAFrom(extensions));
        addressing.setTo(SUPropertiesHelper.getWSATo(extensions));
        addressing.setFaultTo(SUPropertiesHelper.getWSAFaultTo(extensions));
        addressing.setReplyTo(SUPropertiesHelper.getWSAReplyTo(extensions));
        return addressing;
    }

    /**
     * Merge the addressing beans. This bean is defined like this :
     * <ol>
     * <li><b>If it exists</b>, get the TO address from wsAddressing</li>
     * <li><b>If it exists</b>, get the others addresses from
     * wsaToAddressing</li>
     * <li>b>If it exists</b>, get the others address from the service unit</li>
     * </ol>
     * @deprecated use {@link #merge(Addressing, Addressing)}
     * @param wsAddressing
     *            The bean from the
     *            {@link org.ow2.petals.component.framework.api.Constants.WSStar.ADDRESSING_KEY}
     *            property
     * @param wsaToAddressing
     *            The bean from the WSA property
     * @param suAddressing
     *            The bean from the service unit
     * @return The bean merged     * 
     */
    public static final Addressing merge(Addressing wsAddressing, Addressing wsaToAddressing,
            Addressing suAddressing) {
        Addressing result = new Addressing();
        // wsAddressing
        if (wsAddressing != null && wsAddressing.getTo() != null
                && wsAddressing.getTo().length() != 0) {
            result.setTo(wsAddressing.getTo());
        }
        // wsaToAddressing
        if (wsaToAddressing != null) {
            if (result.getTo() == null && wsaToAddressing.getTo() != null
                    && wsaToAddressing.getTo().length() != 0) {
                result.setTo(wsaToAddressing.getTo());
            }
            if (wsaToAddressing.getFaultTo() != null && wsaToAddressing.getFaultTo().length() != 0) {
                result.setFaultTo(wsaToAddressing.getFaultTo());
            }
            if (wsaToAddressing.getFrom() != null && wsaToAddressing.getFrom().length() != 0) {
                result.setFrom(wsaToAddressing.getFrom());
            }
            if (wsaToAddressing.getMessageId() != null
                    && wsaToAddressing.getMessageId().length() != 0) {
                result.setMessageId(wsaToAddressing.getMessageId());
            }
            if (wsaToAddressing.getReplyTo() != null && wsaToAddressing.getReplyTo().length() != 0) {
                result.setReplyTo(wsaToAddressing.getReplyTo());
            }
        }
        // suAddressing
        if (suAddressing != null) {
            if (result.getTo() == null && suAddressing.getTo() != null
                    && suAddressing.getTo().length() != 0) {
                result.setTo(suAddressing.getTo());
            }
            if (result.getFaultTo() == null && suAddressing.getFaultTo() != null
                    && suAddressing.getFaultTo().length() != 0) {
                result.setFaultTo(suAddressing.getFaultTo());
            }
            if (result.getFrom() == null && suAddressing.getFrom() != null
                    && suAddressing.getFrom().length() != 0) {
                result.setFrom(suAddressing.getFrom());
            }
            if (result.getMessageId() == null && suAddressing.getMessageId() != null
                    && suAddressing.getMessageId().length() != 0) {
                result.setMessageId(suAddressing.getMessageId());
            }
            if (result.getReplyTo() == null && suAddressing.getReplyTo() != null
                    && suAddressing.getReplyTo().length() != 0) {
                result.setReplyTo(suAddressing.getReplyTo());
            }
        }
        return result;
    }
    
    /**
     * Merge the addressing beans. This bean is defined like this :
     * <ol>
     * <li><b>If it exists</b>, get the TO address from wsAddressing</li>
     * <li>b>If it exists</b>, get the others address from the service unit</li>
     * </ol>
     * @param wsAddressing
     *            The bean from the
     *            {@link org.ow2.petals.component.framework.api.Constants.WSStar.ADDRESSING_KEY}
     *            property
     * @param suAddressing
     *            The bean from the service unit
     * @return The bean merged
     */
    public static final Addressing merge(Addressing wsAddressing,
            Addressing suAddressing) {
        Addressing result = new Addressing();
        // wsAddressing
        if (wsAddressing != null && wsAddressing.getTo() != null
                && wsAddressing.getTo().length() != 0) {
            result.setTo(wsAddressing.getTo());
        }
        // suAddressing
        if (suAddressing != null) {
            if (result.getTo() == null && suAddressing.getTo() != null
                    && suAddressing.getTo().length() != 0) {
                result.setTo(suAddressing.getTo());
            }
            if (result.getFaultTo() == null && suAddressing.getFaultTo() != null
                    && suAddressing.getFaultTo().length() != 0) {
                result.setFaultTo(suAddressing.getFaultTo());
            }
            if (result.getFrom() == null && suAddressing.getFrom() != null
                    && suAddressing.getFrom().length() != 0) {
                result.setFrom(suAddressing.getFrom());
            }
            if (result.getMessageId() == null && suAddressing.getMessageId() != null
                    && suAddressing.getMessageId().length() != 0) {
                result.setMessageId(suAddressing.getMessageId());
            }
            if (result.getReplyTo() == null && suAddressing.getReplyTo() != null
                    && suAddressing.getReplyTo().length() != 0) {
                result.setReplyTo(suAddressing.getReplyTo());
            }
        }
        return result;
    }

    /**
     * Create an addressing object
     * 
     * @param address
     *            The address
     * @return The Addressing object created or null if the address provided is
     *         null or empty
     */
    public static Addressing getAddressing(String address) {
        if (address == null || address.length() == 0) {
            Addressing addressing = new Addressing();
            addressing.setTo(address);
            return addressing;
        }
        return null;
    }

}
