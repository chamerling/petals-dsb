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
package org.ow2.petals.binding.soap.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;

/**
 * Helper class for SOAP attachment handling
 * 
 * @author chamerling - eBMWebSourcing
 */
public final class AttachmentHelper {

	/**
	 * No constructor because it's a class conytaining only static methods.
	 */
	private AttachmentHelper() {
		// NOP
	}


	/**
	 * Test if the given {@link OMElement} has an attachment corresponding to
	 * the provided CID. If it exists, it this replace by itself using AXIOM
	 * API. This replacement is needed by Axis2
	 * 
	 * @param omElement
	 * @param dh
	 * @param cid
	 *            the attachment content id
	 * @return The OMElement containing the attachement link (ie. containing:
	 *         &lt;xop:Include href="..."/&gt; in case of a XOP attachement, the
	 *         element containing the attribut "href" in case of Swa)
	 * @throws UnsupportedEncodingException
	 */
	public static OMElement hasAttachmentElement(final OMElement omElement, final DataHandler dh,
			final String cid) throws UnsupportedEncodingException {
		OMElement result = null;
		// get attachments
		final Iterator<?> iter = omElement.getChildren();
		while (iter.hasNext() && (result == null)) {
			final OMNode node = (OMNode) iter.next();
			if (node instanceof OMElement) {
				final OMElement element = (OMElement) node;
				// all the nodes that are attachments have an href attribute
				final OMAttribute attr = element.getAttribute(new QName("href"));
				if (attr != null) {
					if ("Include".equalsIgnoreCase(element.getLocalName())
							&& "http://www.w3.org/2004/08/xop/include".equalsIgnoreCase(element
									.getNamespace().getNamespaceURI())) {


						// MTOM/XOP
						if ( attr.getAttributeValue().substring(0, 3).equalsIgnoreCase("cid")
								&& (compare(attr.getAttributeValue().substring(4),cid))) {

							result = (OMElement) element.getParent();
						}
					} else {
						// SwA : SOAP with attachment
						if ( attr.getAttributeValue().equals(cid) ) {

							result = element;
						}
					}
				} else {
					// try to go down in children
					result = AttachmentHelper.hasAttachmentElement(element, dh, cid);
				}
			}
		}
		return result;
	}

	/**
	 * Compare two strings, the first is encoded as an URL and not the second
	 * @param text1
	 * @param text2
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static boolean compare(String text1, String text2) throws UnsupportedEncodingException {

		if (text1.equals(text2))
			return true;

		// In some cases, the attachment ID is encoded as an URL
		text2 = URLEncoder.encode(text2, "UTF-8");
		if (text1.equals(text2))
			return true;

		// Last attempt
		text1  = text1.replace("@", "%40");
		if (text1.equals(text2))
			return true;

		return false;
	}
}
