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
import java.io.IOException;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.builder.BuilderUtil;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.util.XMLUtils;
import org.ow2.petals.binding.soap.SoapConstants;
import org.ow2.petals.component.framework.util.JVMDocumentBuilders;
import org.ow2.petals.component.framework.util.SourceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static javax.jbi.messaging.NormalizedMessageProperties.PROTOCOL_HEADERS;

import static org.ow2.petals.binding.soap.SoapConstants.SOAP.FAULT_CLIENT;
import static org.ow2.petals.binding.soap.SoapConstants.WSSE.WSSE_QNAME;

import com.ebmwebsourcing.easycommons.xml.DocumentBuilders;

/**
 * A marshaller to create JBI message from SOAP ones and vice versa.
 * 
 * @author Christophe HAMERLING (chamerling) - eBM WebSourcing
 * 
 */
public final class Marshaller {

    /**
     * Create the JBI message from the OMElement object.
     * 
     * @param from
     * @param outBodyElement
     * @param to
     * @throws MessagingException
     */
    public static void copyAttachments(final OMElement from, final NormalizedMessage to)
            throws MessagingException {

        // get attachments
        @SuppressWarnings("rawtypes")
        final Iterator iter = from.getChildren();
        while (iter.hasNext()) {
            final OMNode node = (OMNode) iter.next();
            if (node instanceof OMElement) {
                final OMElement element = (OMElement) node;
                // (all the nodes that have an href attributes are
                // attachments)
                final OMAttribute attr = element.getAttribute(new QName("href"));
                if ((attr != null) && (node instanceof OMText)) {
                    if ("Include".equalsIgnoreCase(element.getLocalName())
                            && "http://www.w3.org/2004/08/xop/include".equalsIgnoreCase(element
                                    .getNamespace().getNamespaceURI())) {
                        final String attachmentId = attr.getAttributeValue().substring(4);
                        final OMText binaryNode = (OMText) node;
                        final DataHandler dh = (DataHandler) binaryNode.getDataHandler();
                        to.addAttachment(attachmentId, dh);
                    }
                }
            }
        }
    }

    /**
     * Create a SOAPBody from a source
     * 
     * @param factory
     *            a SOAP factory
     * @param envelope
     *            a SOAP envelope
     * @param source
     *            a source
     * @return the SOAP body
     * @throws MessagingException
     */
    public static SOAPBody createSOAPBody(final SOAPFactory factory, final SOAPEnvelope envelope,
            final Source source) throws MessagingException {
        SOAPBody body = null;

        try {
            final XMLStreamReader parser = StaxUtils.createXMLStreamReader(source);
            final StAXOMBuilder builder = new StAXOMBuilder(factory, parser);
            final OMElement bodyContent = builder.getDocumentElement();

            body = factory.createSOAPBody(envelope);
            body.addChild(bodyContent);

        } catch (XMLStreamException xmlse) {
            throw new MessagingException(
                    "Error parsing the response from JBI service to a SOAPBody", xmlse);
        }
        return body;
    }

    /**
     * Create a SOAPBody from a source with a fault
     * 
     * @param factory
     *            a SOAP factory
     * @param envelope
     *            a SOAP envelope
     * @param source
     *            a source
     * @return the SOAP body
     * @throws MessagingException
     */
    public static SOAPBody createSOAPBodyWithFault(final SOAPFactory factory,
            final SOAPEnvelope envelope, final Source source) throws MessagingException {
        SOAPBody body = null;
        try {
            final XMLStreamReader parser = StaxUtils.createXMLStreamReader(source);
            final StAXOMBuilder builder = new StAXOMBuilder(factory, parser);
            final OMElement bodyContent = builder.getDocumentElement();
            body = factory.createSOAPBody(envelope);
            final SOAPFault soapFault = SOAPFaultHelper.createSOAPFault(factory, bodyContent);
            body.addFault(soapFault);
        } catch (XMLStreamException xmlse) {
            throw new MessagingException("Error parsing the fault from JBI service to a SOAPBody",
                    xmlse);
        }
        return body;
    }

    /**
     * Creates a SOAP response from a NormalizedMessage
     * 
     * @param factory
     *            soap factory
     * @param nm
     *            NormalizedMessage containing the response
     * @return a SOAPEnveloppe created from the nm NomalizedMessage content
     * @throws MessagingException
     */
    @SuppressWarnings("unchecked")
    public static SOAPEnvelope createSOAPEnvelope(final SOAPFactory factory,
            final NormalizedMessage nm, final boolean isJBIFault) throws MessagingException {

        /*
         * Create and fill the Soap body with the content of the Normalized
         * message
         */
        Source source;
        Map<String, DocumentFragment> protocolHeadersProperty = null;
        if ((nm == null) || (nm.getContent() == null)) {
            Document doc = DocumentBuilders.newDocument();
            
            Element responseElement = doc.createElement("Response");
            responseElement.setNodeValue("Done");
            doc.appendChild(responseElement);
            source = SourceUtil.createDOMSource(doc);
        } else {
            source = nm.getContent();
            Object protocolHeadersPropertyObject = nm.getProperty(PROTOCOL_HEADERS);
            if ((protocolHeadersPropertyObject != null)
                    && (protocolHeadersPropertyObject instanceof Map<?, ?>)) {
                protocolHeadersProperty = (Map<String, DocumentFragment>) protocolHeadersPropertyObject;
            }
        }

        SOAPEnvelope responseEnv = factory.createSOAPEnvelope();
        Marshaller.createSOAPHeader(factory, responseEnv, protocolHeadersProperty);
        if (!isJBIFault) {
            Marshaller.createSOAPBody(factory, responseEnv, source);
        } else {
            Marshaller.createSOAPBodyWithFault(factory, responseEnv, source);
        }

        return responseEnv;
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
            final Map<String, DocumentFragment> protocolHeadersProperty) throws MessagingException {

        if (protocolHeadersProperty != null) {
            final SOAPHeader header = factory.createSOAPHeader(envelope);

            for (final DocumentFragment docfrag : protocolHeadersProperty.values()) {
                final Node node = docfrag.getFirstChild();
                if (node instanceof Element) {
                    try {
                        header.addChild(XMLUtils.toOM((Element) node));
                    } catch (Exception e) {
                        throw new MessagingException(
                                "Error parsing the response from JBI service to a SOAPHeader", e);
                    }
                }
            }
        }
    }

    /**
     * Create a the JBI payload from the SOAP body. The SOAP envelope is
     * required to get namespaces.
     * 
     * @param envelope
     *            the SOAP envelope
     * @param axis1Compatibility
     *            a flag for Axis compatibility
     * @return the source
     * @throws MessagingException
     *             if there is an error when creating the source content
     */
    public static Source createSourceContent(final SOAPEnvelope soapEnvelope,
            boolean axis1Compatibility) throws MessagingException {
        if (soapEnvelope == null) {
            throw new MessagingException("Envelope can not be null to create a source");
        }

        final SOAPEnvelope envelope;
        if (axis1Compatibility) {
            // mutiref to document
            envelope = AxiomSOAPEnvelopeFlattener.flatten(soapEnvelope);
        } else {
            envelope = soapEnvelope;
        }

        return createSourceContent(envelope);
    }

    /**
     * Create the JBI payload from the soap body. The soap envelope is created
     * from the inContext.getAttachmentMap().getSOAPPartInputStream() stream
     * because it's dosen't contain the attachment as text element. That's avoid
     * to put the attachment both in the JBI payload and in the JBI attachments.
     * 
     * @param inContext
     *            the message context
     * 
     * @return the source
     * 
     * @throws MessagingException
     *             if there is an error when creating the source content and
     *             attachements
     */
    public static Source createSourceContentAndAttachment(final MessageContext inContext)
            throws MessagingException {
        try {

            final String charSetEncoding = (String) inContext
                    .getProperty(org.apache.axis2.Constants.Configuration.CHARACTER_SET_ENCODING);

            // Get the actual encoding by looking at the BOM of the InputStream
            final PushbackInputStream pis = BuilderUtil.getPushbackInputStream(inContext
                    .getAttachmentMap().getSOAPPartInputStream());
            final String actualCharSetEncoding = BuilderUtil.getCharSetEncoding(pis,
                    charSetEncoding);

            // Get the XMLStreamReader for this input stream
            XMLStreamReader streamReader;

            streamReader = StAXUtils.createXMLStreamReader(pis, actualCharSetEncoding);

            final StAXBuilder builder = new StAXSOAPModelBuilder(streamReader);
            final SOAPEnvelope envelope = (SOAPEnvelope) builder.getDocumentElement();

            return createSourceContent(envelope);

        } catch (XMLStreamException xmlse) {
            throw new MessagingException(xmlse);
        } catch (IOException ioe) {
            throw new MessagingException(ioe);
        }
    }

    @SuppressWarnings("unchecked")
    private static final Source createSourceContent(final SOAPEnvelope envelope)
            throws MessagingException {
        Source result = null;
        final OMElement body = envelope.getBody();
        final OMNamespace namespace = envelope.getNamespace();
        final Iterator<OMNamespace> envNS = envelope.getAllDeclaredNamespaces();
        final Iterator<OMNamespace> bodyNS = body.getAllDeclaredNamespaces();

        OMElement rootElement = body.getFirstElement();

        if (rootElement != null) {
            rootElement.declareNamespace(namespace);
            while (envNS.hasNext()) {
                rootElement.declareNamespace(envNS.next());
            }
            while (bodyNS.hasNext()) {
                rootElement.declareNamespace(bodyNS.next());
            }

            final ByteArrayOutputStream os = new ByteArrayOutputStream();

            try {
                rootElement.serialize(os);
                result = new StreamSource(new ByteArrayInputStream(os.toByteArray()));
            } catch (XMLStreamException xmlse) {
                throw new MessagingException(xmlse);
            }
        }
        return result;
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
     * attachment by the same using OMElement (it's needed by Axis API).
     * </p>
     * <p>
     * In the other case, each attachment is added, using MTOM/XOP, in the
     * special node inside the SOAP Body:
     * <code>&lt;soapbc:attachments xmlns:soapbc="http://petals.ow2.org/ns/soapbc"&gt;/&lt;soapbc:attachment&gt;</code>
     * </p>
     * 
     * @param nm
     * 
     * @param soapFactory
     * @param messageContext
     * @throws AxisFault 
     */
    public static final void fillSOAPBodyWithAttachments(final NormalizedMessage nm,
            final SOAPFactory soapFactory, final MessageContext messageContext) throws AxisFault {
        final SOAPEnvelope env = messageContext.getEnvelope();

        if ((nm.getAttachmentNames() != null) && (nm.getAttachmentNames().size() > 0)) {
            SOAPBody body = env.getBody();
            if (body == null) {
                body = soapFactory.createSOAPBody(env);
            }

            final OMNamespace omNs = soapFactory.createOMNamespace(SoapConstants.Component.NS_URI,
                    SoapConstants.Component.NS_PREFIX);
            OMElement rootElement = body.getFirstElement();
            if (rootElement == null) {
                rootElement = soapFactory.createOMElement("response", omNs, body);
            }

            // set property
            messageContext.setDoingMTOM(true);
            messageContext.setProperty(org.apache.axis2.Constants.Configuration.ENABLE_MTOM,
                    org.apache.axis2.Constants.VALUE_TRUE);

            // Add JBI attachments to the document element
            final Set<?> names = nm.getAttachmentNames();
            for (final Object key : names) {
                final DataHandler attachment = nm.getAttachment((String) key);
                OMElement attachRefElt;
                try {
                    attachRefElt = AttachmentHelper.hasAttachmentElement(rootElement,
                            attachment, (String) key);
                    if (attachRefElt != null) {
                        // An element references the attachment, we replace it by
                        // itself using AXIOM API (It's a requirement of Axis2)
                        OMElement firstElement = attachRefElt.getFirstChildWithName(new QName(
                                "http://www.w3.org/2004/08/xop/include", "Include"));
    
                        // FIXME: should we go through all the children and check
                        // the type and name?
                        if (firstElement == null) {
                            firstElement = attachRefElt.getFirstChildWithName(new QName(
                                    "http://www.w3.org/2004/08/xop/include", "include"));
                        }
    
                        // FIXME: if the element is null, should we set a new
                        // attachment anyway?
                        // It seemed to work if we did not detached...
                        if (firstElement != null) {
                            firstElement.detach();
                            final OMText attach = soapFactory.createOMText(attachment, true);
                            attachRefElt.addChild(attach);
                        }
    
                        // FIXME: log an error otherwise?
                    }
                } catch (UnsupportedEncodingException uee) {
                    throw new AxisFault(FAULT_CLIENT, uee);
                }
            }
        }
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
                // Avoid to copy the soap envelope in attachment
                if (!((dh.getContentType() != null) && dh.getContentType().contains("soap+xml"))) {
                    try {
                        to.addAttachment(id, dh);
                    } catch (final MessagingException e) {
                        // TODO: The exception should be processed.
                    }
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
     * @throws MessagingException 
     */
    @SuppressWarnings("unchecked")
    public static void setProperties(final MessageContext from, final NormalizedMessage to) throws MessagingException {

        // get the SOAP header from envelope and add it as normalized message
        // property if
        // it exists
        SOAPEnvelope env = from.getEnvelope();
        SOAPHeader header = env.getHeader();
        if (header != null) {
            Iterator<OMElement> elements = header.getChildElements();
            Map<String, DocumentFragment> soapHeaderElementsMap = new HashMap<String, DocumentFragment>();
            // We need to use the DocumentBuilder provided by the JVM to have a
            // DocumentFragment implementation provided by JVM, otherwise we can
            // have ClassNotFoundException on outside container because the
            // DocumentFragment implementation is not available on the other
            // side.
            Document doc = JVMDocumentBuilders.newDocument();
            while ((elements != null) && elements.hasNext()) {
                OMElement element = elements.next();
                

                if (!element.getQName().equals(WSSE_QNAME)) {
                    try {
                        Element elt = XMLUtils.toDOM(element);

                        DocumentFragment docfrag = doc.createDocumentFragment();
                        docfrag.appendChild(doc.importNode(elt, true));
                        docfrag.normalize();

                        soapHeaderElementsMap.put(element.getQName().toString(), docfrag);
                    } catch (Exception e) {
                        throw new MessagingException(e);
                    }
                }
            }

            to.setProperty(PROTOCOL_HEADERS, soapHeaderElementsMap);
        }
    }

    /**
     * No constructor because <code>Marshaller</code> is an utility class.
     */
    private Marshaller() {
        // NOP
    }

}
