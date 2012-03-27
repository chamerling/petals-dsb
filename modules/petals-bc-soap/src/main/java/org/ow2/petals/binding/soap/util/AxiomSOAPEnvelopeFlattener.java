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
package org.ow2.petals.binding.soap.util;

import java.util.Iterator;
import java.util.Map;

import javax.jbi.messaging.MessagingException;

import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPHeader;

public final class AxiomSOAPEnvelopeFlattener {

	/**
	 * Get back a soap envelope without the multiref elements.
	 * 
	 * @param the SOAP envelope with multiref elements
	 * @throws MessagingException if it is not possible to determine the SOAP version
	 * @return the SOAP envelope without multiref elements
	 */
	public static final SOAPEnvelope flatten(SOAPEnvelope envelope) throws MessagingException {
		SOAPFactory factory = AxiomUtils.getSOAPFactory(envelope);

		// Create envelope with the same prefix
		SOAPEnvelope targetEnv = factory.createSOAPEnvelope(envelope
				.getNamespace());

		// Copy the attributes and namespaces from the source
		// envelope to the target envelope.
		AxiomUtils.copyTagData(envelope, targetEnv);

		// get all the multiref elements from the input soap envelope
		Map<String, OMElement> multiref = AxiomUtils.getHrefElements(envelope);

		if (multiref.size() == 0) {
			return envelope;
		}

		Iterator<?> i = envelope.getChildren();
		while (i.hasNext()) {
			OMNode node = (OMNode) i.next();
			if (node instanceof SOAPHeader) {
				// Copy the SOAPHeader tree
				SOAPHeader targetHeader = factory.createSOAPHeader(targetEnv);
				Iterator<?> j = ((SOAPHeader) node).getChildren();
				while (j.hasNext()) {
					OMNode child = (OMNode) j.next();
					flatten(factory, targetHeader, child, multiref);
				}
			} else if (node instanceof SOAPBody) {
				// Copy the SOAPBody tree
				SOAPBody targetBody = factory.createSOAPBody(targetEnv);
				Iterator<?> j = ((SOAPBody) node).getChildren();
				while (j.hasNext()) {
					OMNode child = (OMNode) j.next();
					flatten(factory, targetBody, child, multiref);
				}

			} else {
				// Comments, text, etc.
				AxiomUtils.copy(factory, targetEnv, node);
			}
		}

		return targetEnv;
	}

	private static final void flatten(SOAPFactory factory, OMContainer targetParent,
			OMNode sourceNode, Map<String, OMElement> multirefs) {

		// Create and attach a node of the same class
		// TODO It would be nice if you could do this directly from the
		// OMNode, but OMNode.clone() does not gurantee that an object of the
		// correct
		// class is created.
		if (sourceNode instanceof SOAPFault) {
			AxiomUtils.copySOAPFault(factory, targetParent,
					(SOAPFault) sourceNode);
		} else if (sourceNode instanceof OMElement) {
			// 1. avoid to copy the multiref elements
			OMElement element = (OMElement) sourceNode;
			if (!AxiomUtils.isMultirefElement(element)) {
				if (AxiomUtils.isHrefRedirect(element)) {
					flattenOMElement(factory, targetParent, element, multirefs);
				} else {
					// go deeper...
					OMElement newElement = factory.createOMElement(element
							.getQName(), targetParent);
					AxiomUtils.copyTagData(element, newElement);
					Iterator<?> j = element.getChildren();
					while (j.hasNext()) {
						OMNode child = (OMNode) j.next();
						flatten(factory, newElement, child, multirefs);
					}
				}
			} else {
			}
		} else if (sourceNode instanceof OMText) {
			AxiomUtils.copyOMText(factory, targetParent, (OMText) sourceNode);
		} else if (sourceNode instanceof OMComment) {
			AxiomUtils.copyOMComment(factory, targetParent,
					(OMComment) sourceNode);
		} else {
			throw new OMException("Internal Failure: Cannot make a copy of "
					+ sourceNode.getClass().getName());
		}
	}

	public static final void flattenOMElement(SOAPFactory factory,
			OMContainer targetParent, OMElement sourceElement,
			Map<String, OMElement> multiref) {
		// Clone and attach the OMElement.
		// REVIEW This clone will expand the underlying tree. We may want
		// consider traversing
		// a few levels deeper to see if there are any additional
		// OMSourcedElements.

		// get the href
		String id = AxiomUtils.getHrefId(sourceElement);
		OMElement href = multiref.get(id);
		OMElement newElement = factory
				.createOMElement(sourceElement.getQName());
		AxiomUtils.copyTagData(sourceElement, newElement);
		// let's include the multiref part...
		AxiomUtils.copyTagData(href, newElement);

		// copy all the multiref children
		Iterator<?> i = href.getChildren();
		while (i.hasNext()) {
			OMNode node = (OMNode) i.next();
			// lets flatten inner elements
			flatten(factory, newElement, node, multiref);
		}
		targetParent.addChild(newElement);
	}

}
