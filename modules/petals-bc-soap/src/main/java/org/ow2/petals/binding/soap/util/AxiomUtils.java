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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jbi.messaging.MessagingException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.i18n.Messages;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * An utility class to manipulate AXIOM Elements.
 */
public class AxiomUtils {

	private final static int DEFAULT_BYTE_ARRAY_SIZE = 8 * 1024;

	private static final QName REFID = QName.valueOf("id");

	private static final QName HREF = QName.valueOf("href");

	private static final String MULTIREF = "multiRef";

	/**
	 * Creates a new instance of AxiomUtils
	 */
	private AxiomUtils() {
	}

	/**
	 * Create a <code>{@link Source}</code> from an
	 * <code>{@link OMElement}</code>. No optimization is made about namespace
	 * writing. The service provider must be in charge of this optimization.
	 * 
	 * @param element
	 *            The <code>{@link OMElement}</code> to convert.
	 * @return The <code>{@link OMElement}</code> converted in
	 *         <code>{@link Source}</code>.
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public static Source createSource(final OMElement element)
			throws IOException, XMLStreamException {

		final ByteArrayOutputStream osRequest = new ByteArrayOutputStream(
				DEFAULT_BYTE_ARRAY_SIZE);

		final XMLStreamWriter xsw = StAXUtils.createXMLStreamWriter(osRequest);
		// For performance reasons, the AXIOM's cache is not used to optimize
		// the namespace writing. The service provider must be in charge of this
		// optimization.
		element.serializeAndConsume(xsw);

		xsw.close();
		osRequest.close();

		final DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		factory.setNamespaceAware(true);
		DOMSource res = null;
		try {
			final Document doc = factory.newDocumentBuilder().parse(
					new ByteArrayInputStream(osRequest.toByteArray()));
			res = new DOMSource(doc);
		} catch (final SAXException e) {
			throw new XMLStreamException(e);
		} catch (final ParserConfigurationException e) {
			throw new XMLStreamException(e);
		}

		return res;

	}

    /**
     * Create the SOAP factory
     * 
     * @param msgContext
     *            the message context
     * @return the SOAP factory
     * @throws MessagingException
     *             if it is not possible to determine the SOAP version
     */
	public static SOAPFactory getSOAPFactory(final MessageContext msgContext)
			throws MessagingException {
		return getSOAPFactory(msgContext.getEnvelope());
	}

    /**
     * Get the SOAP factory from the SOAP envelope version
     * 
     * @param envelope
     *            the SOAP envelope
     * @return the SOAP factory
     * @throws MessagingException
     *             if it is not possible to determine the SOAP version
     * 
     */
	public static SOAPFactory getSOAPFactory(SOAPEnvelope envelope)
			throws MessagingException {
		final String nsURI = envelope.getNamespace().getNamespaceURI();
		if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(nsURI)) {
			return OMAbstractFactory.getSOAP12Factory();
		} else if (SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(nsURI)) {
			return OMAbstractFactory.getSOAP11Factory();
		} else {
			throw new MessagingException(Messages.getMessage("invalidSOAPversion"));
		}
	}

	/**
	 * Get all the HREF elements which are children of the input root element
	 * 
	 * @param rootElement
	 * @return a map of <HREF ID, MULTIREF {@link OMElement}>
	 */
	public static Map<String, OMElement> getHrefElements(OMElement element) {
		Map<String, OMElement> result = new HashMap<String, OMElement>();

		if (element.getQName().getLocalPart().equals(MULTIREF)
				&& (element.getAttribute(REFID) != null)) {
			result.put(element.getAttributeValue(REFID), element);
		}

		// go into chidlren
		Iterator<?> iter = element.getChildElements();
		while (iter.hasNext()) {
			OMElement child = (OMElement) iter.next();
			Map<String, OMElement> tmp = getHrefElements(child);
			result.putAll(tmp);
		}
		return result;
	}

	/**
	 * Get the HREF ID from the givem element if exists.
	 * 
	 * @param element
	 *            to get HREF id from
	 * @return id or empty string if not found
	 */
	public static String getHrefId(OMElement element) {
		return ((element != null) && (element.getAttribute(HREF) != null)) ? element
				.getAttributeValue(HREF).substring(1)
				: "";
	}

	/**
	 * Let's look if the element is a multiref one ie
	 * 
	 * <pre>
	 * <multiRef ... >
	 * </pre>
	 * 
	 * @param element
	 * @return
	 */
	public static boolean isMultirefElement(OMElement element) {
		return (element != null) && element.getLocalName().equals(MULTIREF);
	}

	/**
	 * Let's look if the given element contains a HFRE attribute
	 * 
	 * @param element
	 * @return
	 */
	public static boolean isHrefRedirect(OMElement element) {
		return ((element != null) && (element.getAttribute(HREF) != null));
	}

	// Copy Axiom elements taken from Axiom CopyUtils class since most of the
	// methods are private...



	/**
	 * Create a copy of the sourceNode and attach it to the targetParent
	 * 
	 * @param factory
	 *            OMFactory
	 * @param targetParent
	 * @param sourceNode
	 */
	public static void copy(SOAPFactory factory, OMContainer targetParent,
			OMNode sourceNode) {

		// Create and attach a node of the same class
		// TODO It would be nice if you could do this directly from the
		// OMNode, but OMNode.clone() does not gurantee that an object of the
		// correct
		// class is created.
		if (sourceNode instanceof SOAPHeaderBlock) {
			copySOAPHeaderBlock(factory, targetParent,
					(SOAPHeaderBlock) sourceNode);
		} else if (sourceNode instanceof SOAPFault) {
			copySOAPFault(factory, targetParent, (SOAPFault) sourceNode);
		} else if (sourceNode instanceof OMSourcedElement) {
			copyOMSourcedElement(factory, targetParent,
					(OMSourcedElement) sourceNode);
		} else if (sourceNode instanceof OMElement) {
			copyOMElement(factory, targetParent, (OMElement) sourceNode);
		} else if (sourceNode instanceof OMText) {
			copyOMText(factory, targetParent, (OMText) sourceNode);
		} else if (sourceNode instanceof OMComment) {
			copyOMComment(factory, targetParent, (OMComment) sourceNode);
		} else {
			throw new OMException("Internal Failure: Cannot make a copy of "
					+ sourceNode.getClass().getName());
		}
	}



	/**
	 * Create a copy of the source OMComment
	 * 
	 * @param factory
	 * @param targetParent
	 * @param sourceComment
	 */
	public static void copyOMComment(SOAPFactory factory,
			OMContainer targetParent, OMComment sourceComment) {
		// Create and attach the comment
		factory.createOMComment(targetParent, sourceComment.getValue());
	}

	/**
	 * Create a copy of the OM Text
	 * 
	 * @param factory
	 * @param targetParent
	 * @param sourceText
	 */
	public static void copyOMText(SOAPFactory factory,
			OMContainer targetParent, OMText sourceText) {
		if (sourceText.isBinary()) {
			// This forces a load of the datahandler so that it is saved on the
			// copy.
			sourceText.getDataHandler();
		}
		factory.createOMText(targetParent, sourceText);
	}

	/**
	 * Create a copy of an ordinary OMElement
	 * 
	 * @param factory
	 * @param targetParent
	 * @param sourceElement
	 */
	public static void copyOMElement(SOAPFactory factory,
			OMContainer targetParent, OMElement sourceElement) {
		// Clone and attach the OMElement.
		// REVIEW This clone will expand the underlying tree. We may want
		// consider traversing
		// a few levels deeper to see if there are any additional
		// OMSourcedElements.
		targetParent.addChild(sourceElement.cloneOMElement());
	}

	/**
	 * Create a copy of the OMSourcedElement
	 * 
	 * @param factory
	 * @param targetParent
	 * @param sourceOMSE
	 */
	public static void copyOMSourcedElement(SOAPFactory factory,
			OMContainer targetParent, OMSourcedElement sourceOMSE) {
		// If already expanded or this is not an OMDataSourceExt, then
		// create a copy of the OM Tree
		OMDataSource ds = sourceOMSE.getDataSource();
		if ((ds == null) || sourceOMSE.isExpanded()
				|| !(ds instanceof OMDataSourceExt)) {
			copyOMElement(factory, targetParent, sourceOMSE);
			return;
		}

		// If copying is destructive, then copy the OM tree
		OMDataSourceExt sourceDS = (OMDataSourceExt) ds;
		if (sourceDS.isDestructiveRead() || sourceDS.isDestructiveWrite()) {
			copyOMElement(factory, targetParent, sourceOMSE);
			return;
		}
		OMDataSourceExt targetDS = ((OMDataSourceExt) ds).copy();
		if (targetDS == null) {
			copyOMElement(factory, targetParent, sourceOMSE);
			return;
		}
		// Otherwise create a target OMSE with the copied DataSource
		OMSourcedElement targetOMSE = factory.createOMElement(targetDS,
				sourceOMSE.getLocalName(), sourceOMSE.getNamespace());
		targetParent.addChild(targetOMSE);

	}

	/**
	 * Create a copy of the SOAPHeaderBlock
	 * 
	 * @param factory
	 * @param targetParent
	 * @param sourceSHB
	 */
	public static void copySOAPHeaderBlock(SOAPFactory factory,
			OMContainer targetParent, SOAPHeaderBlock sourceSHB) {
		// If already expanded or this is not an OMDataSourceExt, then
		// create a copy of the OM Tree
		OMDataSource ds = sourceSHB.getDataSource();
		if ((ds == null) || sourceSHB.isExpanded()
				|| !(ds instanceof OMDataSourceExt)) {
			copySOAPHeaderBlock_NoDataSource(factory, targetParent, sourceSHB);
			return;
		}

		// If copying is destructive, then copy the OM tree
		OMDataSourceExt sourceDS = (OMDataSourceExt) ds;
		if (sourceDS.isDestructiveRead() || sourceDS.isDestructiveWrite()) {
			copySOAPHeaderBlock_NoDataSource(factory, targetParent, sourceSHB);
			return;
		}

		// Otherwise create a copy of the OMDataSource
		OMDataSourceExt targetDS = ((OMDataSourceExt) ds).copy();
		SOAPHeaderBlock targetSHB = factory.createSOAPHeaderBlock(sourceSHB
				.getLocalName(), sourceSHB.getNamespace(), targetDS);
		targetParent.addChild(targetSHB);
		copySOAPHeaderBlockData(sourceSHB, targetSHB);
	}

	/**
	 * Create a copy of the SOAPHeaderBlock
	 * 
	 * @param factory
	 * @param targetParent
	 * @param sourceSHB
	 */
	public static void copySOAPHeaderBlock_NoDataSource(SOAPFactory factory,
			OMContainer targetParent, SOAPHeaderBlock sourceSHB) {

		SOAPHeader header = (SOAPHeader) targetParent;
		String localName = sourceSHB.getLocalName();
		OMNamespace ns = sourceSHB.getNamespace();
		SOAPHeaderBlock targetSHB = factory.createSOAPHeaderBlock(localName,
				ns, header);

		// A SOAPHeaderBlock has tag data, plus extra header processing flags
		copyTagData(sourceSHB, targetSHB);
		copySOAPHeaderBlockData(sourceSHB, targetSHB);
		Iterator<?> i = sourceSHB.getChildren();
		while (i.hasNext()) {
			OMNode node = (OMNode) i.next();
			copy(factory, targetSHB, node);
		}
	}

	/**
	 * Create a copy of a SOAPFault
	 * 
	 * @param factory
	 * @param targetParent
	 * @param sourceSOAPFault
	 */
	public static void copySOAPFault(SOAPFactory factory,
			OMContainer targetParent, SOAPFault sourceSOAPFault) {
		Exception e = sourceSOAPFault.getException();

		SOAPFault newSOAPFault = (e == null) ? factory
				.createSOAPFault((SOAPBody) targetParent) : factory
				.createSOAPFault((SOAPBody) targetParent, e);

		copyTagData(sourceSOAPFault, newSOAPFault);
		Iterator<?> i = sourceSOAPFault.getChildren();
		while (i.hasNext()) {
			OMNode node = (OMNode) i.next();
			// Copy the tree under the SOAPFault
			copyFaultData(factory, newSOAPFault, node);
		}
	}

	/**
	 * Copy the source Node, which is a child fo a SOAPFault, to the target
	 * SOAPFault
	 * 
	 * @param factory
	 * @param targetFault
	 * @param sourceNode
	 */
	public static void copyFaultData(SOAPFactory factory,
			SOAPFault targetFault, OMNode sourceNode) {

		if (sourceNode instanceof SOAPFaultCode) {
			copySOAPFaultCode(factory, targetFault, (SOAPFaultCode) sourceNode);
		} else if (sourceNode instanceof SOAPFaultDetail) {
			copySOAPFaultDetail(factory, targetFault,
					(SOAPFaultDetail) sourceNode);
		} else if (sourceNode instanceof SOAPFaultNode) {
			copySOAPFaultNode(factory, targetFault, (SOAPFaultNode) sourceNode);
		} else if (sourceNode instanceof SOAPFaultReason) {
			copySOAPFaultReason(factory, targetFault,
					(SOAPFaultReason) sourceNode);
		} else if (sourceNode instanceof SOAPFaultRole) {
			copySOAPFaultRole(factory, targetFault, (SOAPFaultRole) sourceNode);
		} else if (sourceNode instanceof OMText) {
			copyOMText(factory, targetFault, (OMText) sourceNode);
		} else if (sourceNode instanceof OMComment) {
			copyOMComment(factory, targetFault, (OMComment) sourceNode);
		} else {
			throw new OMException("Internal Failure: Cannot make a copy of "
					+ sourceNode.getClass().getName()
					+ " object found in a SOAPFault.");
		}
	}

	/**
	 * Create a copy of a SOAPFaultRole
	 * 
	 * @param factory
	 * @param targetFault
	 * @param sourceRole
	 */
	public static void copySOAPFaultRole(SOAPFactory factory,
			SOAPFault targetFault, SOAPFaultRole sourceRole) {
		SOAPFaultRole targetRole = factory.createSOAPFaultRole(targetFault);
		copyTagData(sourceRole, targetRole);
		targetRole.setRoleValue(sourceRole.getRoleValue());
	}

	/**
	 * Create a copy of a SOAPFaultNode
	 * 
	 * @param factory
	 * @param targetFault
	 * @param sourceNode
	 */
	public static void copySOAPFaultNode(SOAPFactory factory,
			SOAPFault targetFault, SOAPFaultNode sourceNode) {
		SOAPFaultNode targetNode = factory.createSOAPFaultNode(targetFault);
		copyTagData(sourceNode, targetNode);
		targetNode.setNodeValue(sourceNode.getNodeValue());
	}

	/**
	 * Create a copy of a SOAPFaultDetail
	 * 
	 * @param factory
	 * @param targetFault
	 * @param sourceDetail
	 */
	public static void copySOAPFaultDetail(SOAPFactory factory,
			SOAPFault targetFault, SOAPFaultDetail sourceDetail) {
		SOAPFaultDetail targetDetail = factory
				.createSOAPFaultDetail(targetFault);
		copyTagData(sourceDetail, targetDetail);

		// Copy the detail entries
		Iterator<?> i = sourceDetail.getChildren();
		while (i.hasNext()) {
			OMNode node = (OMNode) i.next();
			copy(factory, targetDetail, node);
		}
	}

	/**
	 * Create a copy of the SOAPFaultReason
	 * 
	 * @param factory
	 * @param targetFault
	 * @param sourceReason
	 */
	public static void copySOAPFaultReason(SOAPFactory factory,
			SOAPFault targetFault, SOAPFaultReason sourceReason) {
		SOAPFaultReason targetReason = factory
				.createSOAPFaultReason(targetFault);
		copyTagData(sourceReason, targetReason);
		Iterator<?> i = sourceReason.getChildren();
		while (i.hasNext()) {
			OMNode node = (OMNode) i.next();
			if (node instanceof SOAPFaultText) {
				SOAPFaultText oldText = (SOAPFaultText) node;
				SOAPFaultText newText = factory
						.createSOAPFaultText(targetReason);
				copyTagData(oldText, newText); // The lang is copied as an
				// attribute
			} else {
				// Copy any comments or child nodes
				copy(factory, targetReason, node);
			}
		}
	}

	/**
	 * Copy the SOAPFaultCode tree
	 * 
	 * @param factory
	 * @param targetFault
	 * @param sourceCode
	 */
	public static void copySOAPFaultCode(SOAPFactory factory,
			SOAPFault targetFault, SOAPFaultCode sourceCode) {
		SOAPFaultCode targetCode = factory.createSOAPFaultCode(targetFault);
		copyTagData(sourceCode, targetCode);

		// Create the Value
		SOAPFaultValue sourceValue = sourceCode.getValue();
		SOAPFaultValue targetValue = factory.createSOAPFaultValue(targetCode);
		copyTagData(sourceValue, targetValue);

		// There should only be a text node for the value, but in case there is
		// more
		Iterator<?> i = sourceValue.getChildren();
		while (i.hasNext()) {
			OMNode node = (OMNode) i.next();
			copy(factory, targetValue, node);
		}

		// Now get process the SubCode
		SOAPFaultSubCode sourceSubCode = sourceCode.getSubCode();
		if (sourceSubCode != null) {
			copySOAPFaultSubCode(factory, targetCode, sourceSubCode);
		}
	}

	/**
	 * Copy the SOAPFaultSubCode tree
	 * 
	 * @param factory
	 * @param targetParent
	 *            (SOAPFaultCode or SOAPFaultSubCode)
	 * @param sourceSubCode
	 */
	public static void copySOAPFaultSubCode(SOAPFactory factory,
			OMElement targetParent, SOAPFaultSubCode sourceSubCode) {
		SOAPFaultSubCode targetSubCode;
		if (targetParent instanceof SOAPFaultSubCode) {
			targetSubCode = factory
					.createSOAPFaultSubCode((SOAPFaultSubCode) targetParent);
		} else {
			targetSubCode = factory
					.createSOAPFaultSubCode((SOAPFaultCode) targetParent);
		}
		copyTagData(sourceSubCode, targetSubCode);

		// Process the SOAP FaultValue
		SOAPFaultValue sourceValue = sourceSubCode.getValue();
		SOAPFaultValue targetValue = factory
				.createSOAPFaultValue(targetSubCode);
		copyTagData(sourceValue, targetValue);
		// There should only be a text node for the value, but in case there is
		// more
		Iterator<?> i = sourceValue.getChildren();
		while (i.hasNext()) {
			OMNode node = (OMNode) i.next();
			copy(factory, targetValue, node);
		}

		// Now process the SubCode of the SubCode
		SOAPFaultSubCode sourceSubSubCode = sourceSubCode.getSubCode();
		if (sourceSubSubCode != null) {
			copySOAPFaultSubCode(factory, targetSubCode, sourceSubSubCode);
		}
	}

	/**
	 * Copy the tag data (attributes and namespaces) from the source element to
	 * the target element.
	 * 
	 * @param sourceElement
	 * @param targetElement
	 */
	public static void copyTagData(OMElement sourceElement,
			OMElement targetElement) {
		for (Iterator<?> i = sourceElement.getAllDeclaredNamespaces(); i
				.hasNext();) {
			OMNamespace ns = (OMNamespace) i.next();
			targetElement.declareNamespace(ns);
		}

		for (Iterator<?> i = sourceElement.getAllAttributes(); i.hasNext();) {
			OMAttribute attr = (OMAttribute) i.next();
			targetElement.addAttribute(attr);
		}
	}

	/**
	 * Copy Header data (currently only the processed flag) from the source
	 * SOAPHeaderBlock to the target SOAPHeaderBlock
	 * 
	 * @param sourceSHB
	 * @param targetSHB
	 */
	public static void copySOAPHeaderBlockData(SOAPHeaderBlock sourceSHB,
			SOAPHeaderBlock targetSHB) {
		// Copy the processed flag. The other SOAPHeaderBlock information
		// (e.g. role, mustUnderstand) are attributes on the tag and are copied
		// in copyTagData.
		if (sourceSHB.isProcessed()) {
			targetSHB.setProcessed();
		}
	}

}
