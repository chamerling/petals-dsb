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

import org.ow2.petals.binding.soap.util.SUPropertiesHelper;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;

/**
 * WS-Addressing Helper. Creates the JBI information from strings.
 * 
 * @author chamerling - eBM WebSourcing
 */
public class WSAHelper {
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
		final Addressing addressing = new Addressing();
		addressing.setFrom(SUPropertiesHelper.getWSAFrom(extensions));
		addressing.setTo(SUPropertiesHelper.getWSATo(extensions));
		// allow address
		if (addressing.getTo() == null) {
			addressing.setTo(SUPropertiesHelper.getAddress(extensions));
		}
		addressing.setFaultTo(SUPropertiesHelper.getWSAFaultTo(extensions));
		addressing.setReplyTo(SUPropertiesHelper.getWSAReplyTo(extensions));
		return addressing;
	}

	/**
	 * Create an addressing object
	 * 
	 * @param address
	 *            The address
	 * @return The Addressing object created or null if the address provided is
	 *         null or empty
	 */
	public static Addressing getAddressing(final String address) {
		if (address != null && !address.isEmpty()) {
			final Addressing addressing = new Addressing();
			addressing.setTo(address);
			return addressing;
		}
		return null;
	}

	/**
	 * Merge the addressing beans. This bean is defined like this :
	 * <ol>
	 * <li><b>If it exists</b>, get the TO address from wsAddressing</li>
	 * <li>b>If it exists</b>, get the others address from the service unit</li>
	 * </ol>
	 * 
	 * @param wsAddressing
	 *            The bean from the
	 *            {@link org.ow2.petals.component.framework.api.SoapConstants.WSStar.ADDRESSING_KEY}
	 *            property
	 * @param suAddressing
	 *            The bean from the service unit
	 * @return The bean merged
	 */
	public static final Addressing merge(final Addressing wsAddressing, final Addressing suAddressing) {
		final Addressing result = new Addressing();
		// wsAddressing
		if (wsAddressing != null && wsAddressing.getTo() != null && wsAddressing.getTo().length() != 0) {
			result.setTo(wsAddressing.getTo());
		}
		// suAddressing
		if (suAddressing != null) {
			if (result.getTo() == null && suAddressing.getTo() != null && suAddressing.getTo().length() != 0) {
				result.setTo(suAddressing.getTo());
			}
			if (result.getFaultTo() == null && suAddressing.getFaultTo() != null && suAddressing.getFaultTo().length() != 0) {
				result.setFaultTo(suAddressing.getFaultTo());
			}
			if (result.getFrom() == null && suAddressing.getFrom() != null && suAddressing.getFrom().length() != 0) {
				result.setFrom(suAddressing.getFrom());
			}
			if (result.getMessageId() == null && suAddressing.getMessageId() != null && suAddressing.getMessageId().length() != 0) {
				result.setMessageId(suAddressing.getMessageId());
			}
			if (result.getReplyTo() == null && suAddressing.getReplyTo() != null && suAddressing.getReplyTo().length() != 0) {
				result.setReplyTo(suAddressing.getReplyTo());
			}
		}
		return result;
	}

}
