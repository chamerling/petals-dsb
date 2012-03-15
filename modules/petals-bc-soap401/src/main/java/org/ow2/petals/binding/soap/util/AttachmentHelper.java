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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;

/**
 * Helper class for SOAP attachment handling
 * 
 * @author chamerling - eBMWebSourcing
 * 
 */
public final class AttachmentHelper {
    
    /**
     * No constructor because it's a class conytaining only static methods.
     */
    private AttachmentHelper() {
        // NOP
    }

    /**
     * Get all the direct attachments from an {@link OMElement}
     * 
     * @param omElement
     * @return
     */
    public static List<DataHandler> getAttachments(final OMElement omElement) {
        final List<DataHandler> result = new ArrayList<DataHandler>();
        final Iterator<?> iter = omElement.getChildren();
        while (iter.hasNext()) {
            final OMNode node = (OMNode) iter.next();
            if (node instanceof OMElement) {
                final OMElement element = (OMElement) node;
                // all the nodes that have an href attributes are attachments
                final OMAttribute attr = element.getAttribute(new QName("href"));
                if ((attr != null) && (node instanceof OMText)) {
                    final OMText binaryNode = (OMText) node;
                    final DataHandler dh = (DataHandler) binaryNode.getDataHandler();
                    result.add(dh);
                }
            }
        }
        return result;
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
     */
    public static OMElement hasAttachmentElement(final OMElement omElement, final DataHandler dh,
            final String cid) {
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
                                && attr.getAttributeValue().substring(4).equals(cid) ) {

                            result = omElement;
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
}
