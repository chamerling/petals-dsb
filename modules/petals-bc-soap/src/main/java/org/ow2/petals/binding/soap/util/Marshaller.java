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
 * $Id$
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soap.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.util.XMLUtils;
import org.ow2.petals.binding.soap.Constants;
import org.ow2.petals.commons.threadlocal.DocumentBuilders;
import org.ow2.petals.component.framework.util.UtilFactory;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static javax.jbi.messaging.NormalizedMessageProperties.PROTOCOL_HEADERS;

import static org.ow2.petals.binding.soap.Constants.SOAP.FAULT_SERVER;

/**
 * A marshaller to create JBI message from SOAP ones and vice versa.
 * 
 * @author Christophe HAMERLING (chamerling) - eBM WebSourcing
 * 
 */
public final class Marshaller {

    /**
     * No constructor because <code>Marshaller</code> is an utility class.
     */
    private Marshaller() {
        // NOP
    }

    /**
     * Create a the JBI payload from the SOAP body. The SOAP envelope is
     * required to get namespaces.
     * 
     * @param envelope
     * @param removeRootBodyElement
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static Source createSource(final SOAPEnvelope envelope,
            final boolean removeRootBodyElement) throws Exception {
        if (envelope == null) {
            throw new Exception("Envelope can not be null to create a source");
        }

        Source result = null;
        final SOAPBody body = envelope.getBody();
        final OMNamespace namespace = envelope.getNamespace();
        final Iterator<OMNamespace> envNS = envelope.getAllDeclaredNamespaces();
        final Iterator<OMNamespace> bodyNS = body.getAllDeclaredNamespaces();

        OMElement rootElement = body.getFirstElement();
        if (removeRootBodyElement) {
            rootElement = rootElement.getFirstElement();
        }

        if (rootElement != null) {
            rootElement.declareNamespace(namespace);
            while (envNS.hasNext()) {
                rootElement.declareNamespace(envNS.next());
            }
            while (bodyNS.hasNext()) {
                rootElement.declareNamespace(bodyNS.next());
            }

            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            rootElement.serialize(os);

            os.toByteArray();
            result = new StreamSource(new ByteArrayInputStream(os.toByteArray()));
        }
        return result;
    }

    /**
     * Put the SOAP attachments in the normalized message
     * 
     * @param attachments
     * @param to
     */
    public static void setAttachments(final Attachments attachments, final NormalizedMessage to) {
        if ((to != null) && (attachments != null)) {
            for (final Object object : attachments.getContentIDSet()) {
                final String id = (String) object;
                final DataHandler dh = attachments.getDataHandler(id);
                try {
                    to.addAttachment(id, dh);
                } catch (final MessagingException e) {
                    // TODO: The exception should be processed.
                }
            }
        }
    }

    /**
     * Copy properties from a SOAP message to the normalized message
     * 
     * @param from
     * @param to
     *            Normalized message
     */
    public static void setProperties(final MessageContext from, final NormalizedMessage to)
            throws Exception {

        // get the SOAP header from envelope and add it as normalized message
        // property if
        // it exists
        final SOAPEnvelope env = from.getEnvelope();
        final SOAPHeader header = env.getHeader();
        if (header != null) {
            final Iterator<OMElement> elements = header.getChildElements();
            final Map<String, DocumentFragment> soapHeaderElementsMap = new HashMap<String, DocumentFragment>();
            // We need to use the DocumentBuilder provided by the JVM to have a
            // DocumentFragment implementation provided by JVM, otherwise we can
            // have ClassNotFoundException on outside container because the
            // DocumentFragment implementation is not available on the other
            // side.
            final DocumentBuilder docBuilder = DocumentBuilders.getJvmDocumentBuilder();
            final Document doc = docBuilder.newDocument();
            while (elements != null && elements.hasNext()) {
                final OMElement element = elements.next();
                final Element elt = XMLUtils.toDOM(element);
                final DocumentFragment docfrag = doc.createDocumentFragment();
                docfrag.appendChild(doc.importNode(elt, true));
                docfrag.normalize();

                soapHeaderElementsMap.put(element.getQName().toString(), docfrag);
            }

            to.setProperty(PROTOCOL_HEADERS, soapHeaderElementsMap);
        }
    }

    /**
     * Create a SOAPBody from a source
     * 
     * @param factory
     * @param envelope
     * @param response
     * @return
     * @throws AxisFault
     */
    public static SOAPBody createSOAPBody(final SOAPFactory factory, final SOAPEnvelope envelope,
            final Source source) throws AxisFault {
        SOAPBody body = null;

        try {
            final XMLStreamReader parser = StaxUtils.createXMLStreamReader(source);
            final StAXOMBuilder builder = new StAXOMBuilder(factory, parser);
            final OMElement bodyContent = builder.getDocumentElement();

            body = factory.createSOAPBody(envelope);
            body.addChild(bodyContent);

        } catch (final Exception e) {
            throw new AxisFault("Error parsing the response from JBI service to a SOAPBody",
                    FAULT_SERVER, e);
        }
        return body;
    }
    
    
    
    
    
    
    /**
     * Create a SOAPBody from a source
     * 
     * @param factory
     * @param envelope
     * @param response
     * @return
     * @throws AxisFault
     */
    public static SOAPBody createSOAPBodyWithFault(final SOAPFactory factory, final SOAPEnvelope envelope,
            final Source source) throws AxisFault {
        SOAPBody body = null;

        try {
            final XMLStreamReader parser = StaxUtils.createXMLStreamReader(source);
            final StAXOMBuilder builder = new StAXOMBuilder(factory, parser);
            final OMElement bodyContent = builder.getDocumentElement();

            body = factory.createSOAPBody(envelope);
            SOAPFault soapFault = factory.createSOAPFault();
            SOAPFaultDetail soapFaultDetail = factory.createSOAPFaultDetail();
            soapFaultDetail.addChild(bodyContent);
            soapFault.setDetail(soapFaultDetail);
            body.addFault(soapFault);
            

        } catch (final Exception e) {
            throw new AxisFault("Error parsing the fault from JBI service to a SOAPBody",
                    FAULT_SERVER, e);
        }
        return body;
    }
    
    
    

    /**
     * Create a SOAPHeader from a source
     * 
     * @param factory
     * @param envelope
     * @param protocolHeadersProperty
     * @throws AxisFault
     */
    private static void createSOAPHeader(final SOAPFactory factory, final SOAPEnvelope envelope,
            final Map<String, DocumentFragment> protocolHeadersProperty) throws AxisFault {

        if (protocolHeadersProperty != null) {
            try {
                final SOAPHeader header = factory.createSOAPHeader(envelope);

                for (DocumentFragment docfrag : protocolHeadersProperty.values()) {
                    final Node node = docfrag.getFirstChild();
                    if (node instanceof Element) {
                        header.addChild(XMLUtils.toOM((Element) node));
                    }
                }

            } catch (final Exception e) {
                throw new AxisFault("Error parsing the response from JBI service to a SOAPHeader",
                        FAULT_SERVER, e);
            }
        }
    }

    /**
     * Creates a SOAP response from a NormalizedMessage
     * 
     * @param factory
     *            soap factory
     * @param nm
     *            NormalizedMessage containing the response
     * @return a SOAPEnveloppe created from the nm NomalizedMessage content
     * @throws AxisFault
     */
    public static SOAPEnvelope createSOAPEnvelope(final SOAPFactory factory,
            final NormalizedMessage nm, final boolean isJBIFault) throws AxisFault {

        /*
         * Create and fill the Soap body with the content of the Normalized
         * message
         */
        final Source source;
        Map<String, DocumentFragment> protocolHeadersProperty = null;
        if ((nm == null) || (nm.getContent() == null)) {
            final Document document = DocumentBuilders.getDefaultDocumentBuilder().newDocument();
            final Element responseElement = document.createElement("Response");
            responseElement.setNodeValue("Done");
            document.appendChild(responseElement);
            source = UtilFactory.getSourceUtil().createDOMSource(document);
        } else {
            source = nm.getContent();
            final Object protocolHeadersPropertyObject = nm.getProperty(PROTOCOL_HEADERS);
            if (protocolHeadersPropertyObject != null
                    && protocolHeadersPropertyObject instanceof Map) {
                protocolHeadersProperty = (Map<String, DocumentFragment>) protocolHeadersPropertyObject;
            }
        }
        
        final SOAPEnvelope responseEnv = factory.createSOAPEnvelope();
        Marshaller.createSOAPHeader(factory, responseEnv, protocolHeadersProperty);
        if (!isJBIFault)
            Marshaller.createSOAPBody(factory, responseEnv, source);
        else
            Marshaller.createSOAPBodyWithFault(factory, responseEnv, source);
        
        return responseEnv;
    }

    /**
     * Add the normalized message attachments to the Axis message context.
     * 
     * @param from
     * @param to
     * 
     * @throws AxisFault
     */
    @SuppressWarnings("unchecked")
    public static void copyAttachments(final NormalizedMessage from, final MessageContext to) {

        if ((from != null) && (to != null)) {
            to.setDoingMTOM(true);
            final Set attachmentNames = from.getAttachmentNames();
            for (final Object id : attachmentNames) {
                final String name = (String) id;
                to.addAttachment(name, from.getAttachment(name));
            }
        }
    }

    /**
     * <p>
     * Handles attachments. Two cases can occurs:
     * <ul>
     * <li>NMR attachments are declared in the XML of the NMR message using
     * MTOM/XOP,</li>
     * <li>NMR are not declared in the XML.</li>
     * </ul>
     * </p>
     * <p>
     * In the first case, it is needed to replace the XML node that declares the
     * attachement by the same using OMElement (it's needed by Axis API).
     * </p>
     * <p>
     * In the other case, each attachment is added, using MTOM/XOP, in the
     * special node inside the SOAP Body:
     * <code>&lt;soapbc:attachments xmlns:soapbc="http://petals.ow2.org/ns/soapbc"&gt;/&lt;soapbc:attachment&gt;</code>
     * </p>
     * 
     * @param soapFactory
     * @param messageContext
     */
    public static final void fillSOAPBodyWithAttachments(final SOAPFactory soapFactory,
            final MessageContext messageContext) {
        final SOAPEnvelope env = messageContext.getEnvelope();

        if (messageContext.getAttachmentMap().getContentIDList().size() > 0) {
            SOAPBody body = env.getBody();
            if (body == null) {
                body = soapFactory.createSOAPBody(env);
            }

            final OMNamespace omNs = soapFactory.createOMNamespace(Constants.Component.NS_URI,
                    Constants.Component.NS_PREFIX);
            OMElement rootElement = body.getFirstElement();
            if (rootElement == null) {
                rootElement = soapFactory.createOMElement("response", omNs, body);
            }

            // set property
            messageContext.setDoingMTOM(true);
            messageContext.setProperty(org.apache.axis2.Constants.Configuration.ENABLE_MTOM,
                    org.apache.axis2.Constants.VALUE_TRUE);

            // handle attachments
            final Attachments attachments = messageContext.getAttachmentMap();
            Set<?> set = attachments.getContentIDSet();

            // try to find if the attachment has already been defined in the
            // SOAPBody, avoid duplicates
            final Attachments toAttach = new Attachments();
            for (final Object object : set) {
                final String id = (String) object;
                final DataHandler dh = attachments.getDataHandler(id);

                final OMElement attachRefElt = AttachmentHelper.hasAttachmentElement(rootElement,
                        dh, id);
                if (attachRefElt == null) {
                    // The attachment is not alreday declared in the SOAP Body,
                    // we add it in the special XML node
                    OMElement attachmentsElement = rootElement.getFirstChildWithName(new QName(
                            Constants.Component.NS_URI, "attachments"));
                    if (attachmentsElement == null) {
                        attachmentsElement = soapFactory.createOMElement("attachments", omNs,
                                rootElement);
                    }
                    final OMElement element = soapFactory.createOMElement("attachment", omNs,
                            attachmentsElement);
                    final OMText attach = soapFactory.createOMText(dh, true);
                    element.addChild(attach);
                    attachmentsElement.addChild(element);

                } else {
                    // An element references the attachement, we replace it by
                    // itself using AXIOM API (It's a requirement of Axis2)
                    attachRefElt.getFirstChildWithName(
                            new QName("http://www.w3.org/2004/08/xop/include", "Include")).detach();
                    final OMText attach = soapFactory.createOMText(dh, true);
                    attachRefElt.addChild(attach);
                }

                toAttach.addDataHandler(id, dh);
            }
        }
    }

    /**
     * Create the JBI message from the OMElement object.
     * 
     * @param from
     * @param to
     */
    @SuppressWarnings("unchecked")
    public static void copyAttachments(final OMElement from, final NormalizedMessage to) {

        // get attachments
        final Iterator iter = from.getChildren();
        while (iter.hasNext()) {
            final OMNode node = (OMNode) iter.next();
            if (node instanceof OMElement) {
                final OMElement element = (OMElement) node;
                // (all the nodes that have an href attributes are
                // attachments)
                final OMAttribute attr = element.getAttribute(new QName("href"));
                if ((attr != null) && (node instanceof OMText)) {
                    final OMText binaryNode = (OMText) node;
                    final DataHandler dh = (DataHandler) binaryNode.getDataHandler();
                    try {
                        to.addAttachment(dh.getName(), dh);
                    } catch (final MessagingException e) {
                    }
                }
            }
        }
    }
}
