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

package org.ow2.petals.binding.soap.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axis2.util.XMLUtils;
import org.junit.Test;
import org.ow2.petals.component.framework.util.JVMDocumentBuilders;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class SOAHeaderTest {

    private static final Map<String, DocumentFragment> createHeaders() throws Exception {
        String SOAPMessage = "<SOAP-ENV:Envelope xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tns=\"http://www.SoapClient.com/xml/SoapResponder.wsdl\" xmlns:xsd1=\"http://www.SoapClient.com/xml/SoapResponder.xsd\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\">"
                + "<SOAP-ENV:Header><wsa:To>http://example.org/To</wsa:To><wsa:ReplyTo><wsa:Address>http://schemas.xmlsoap.org/ws/2003/03/addressing/role/anonymous</wsa:Address></wsa:ReplyTo><wsa:FaultTo><wsa:Address>http://client/myReceiver</wsa:Address></wsa:FaultTo></SOAP-ENV:Header>"
                + "<SOAP-ENV:Body></SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>";

        Map<String, DocumentFragment> soapHeaderElementsMap = new HashMap<String, DocumentFragment>();

        InputStream toSOAP = new ByteArrayInputStream(SOAPMessage.getBytes());
        XMLStreamReader parser = StAXUtils.getXMLInputFactory().createXMLStreamReader(toSOAP);
        StAXSOAPModelBuilder builder = new StAXSOAPModelBuilder(parser, null);
        SOAPEnvelope envelope = (SOAPEnvelope) builder.getDocumentElement();
        SOAPHeader header = envelope.getHeader();

        Iterator<OMElement> elements = header.getChildElements();

        // We need to use the DocumentBuilder provided by the JVM to have a
        // DocumentFragment implementation provided by JVM, otherwise we can
        // have ClassNotFoundException on outside container because the
        // DocumentFragment implementation is not available on the other
        // side.
        Document doc = JVMDocumentBuilders.newDocument();
        while (elements != null && elements.hasNext()) {
            OMElement element = elements.next();
            Element elt = XMLUtils.toDOM(element);
            DocumentFragment docfrag = doc.createDocumentFragment();
            docfrag.appendChild(doc.importNode(elt, true));
            docfrag.normalize();

            soapHeaderElementsMap.put(element.getQName().toString(), docfrag);
        }

        return soapHeaderElementsMap;
    }

    @Test
    public void testGetAsMapOfString() throws Exception {
        final Map<String, DocumentFragment> map = createHeaders();
        final Iterator<Map.Entry<String, DocumentFragment>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<String, DocumentFragment> entry = iter.next();
            DocumentFragment df = entry.getValue();
            if (df.getFirstChild() != null && df.getFirstChild() instanceof Element) {
                df.getFirstChild().getTextContent();
            }
        }
    }
}
