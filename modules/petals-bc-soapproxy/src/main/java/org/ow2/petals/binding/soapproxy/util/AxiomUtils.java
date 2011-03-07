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
package org.ow2.petals.binding.soapproxy.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.i18n.Messages;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * An utility class to manipulate AXIOM Elements.
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @author Christophe DENEUX - Capgemini Sud
 * @date Created on 15 janv. 08
 * @date Reworked on 28 aug. 08
 * @since 3.0
 * 
 */
public class AxiomUtils {

    private final static int DEFAULT_BYTE_ARRAY_SIZE = 8 * 1024;

    /**
     * Creates a new instance of AxiomUtils
     */
    private AxiomUtils(){
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
    public static Source createSource(final OMElement element) throws IOException,
            XMLStreamException {

        final ByteArrayOutputStream osRequest = new ByteArrayOutputStream(DEFAULT_BYTE_ARRAY_SIZE);

        final XMLStreamWriter xsw = StAXUtils.createXMLStreamWriter(osRequest);
        // For performance reasons, the AXIOM's cache is not used to optimize
        // the namespace writing. The service provider must be in charge of this
        // optimization.
        element.serializeAndConsume(xsw);

        xsw.close();
        osRequest.close();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DOMSource res= null;
        try {
            Document doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(osRequest.toByteArray()));
            res = new DOMSource(doc);
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        } catch (ParserConfigurationException e) {
            throw new XMLStreamException(e);
        }
        
        return res;

    }
    
    /**
     * Create the right SOAP factory
     * @param msgContext
     * @return
     * @throws AxisFault
     */
    public static SOAPFactory getSOAPFactory(MessageContext msgContext) throws AxisFault {
        String nsURI = msgContext.getEnvelope().getNamespace().getNamespaceURI();
        if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(nsURI)) {
            return OMAbstractFactory.getSOAP12Factory();
        } else if (SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(nsURI)) {
            return OMAbstractFactory.getSOAP11Factory();
        } else {
            throw new AxisFault(Messages.getMessage("invalidSOAPversion"));
        }
    }
}
