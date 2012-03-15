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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.junit.Test;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class SOAPFlattenerTest {

    @Test
    public void testNoMultiref() throws Exception {
        SOAPEnvelope soapEnvelope = SOAPTestUtil.loadSOAPEnvelope("/multiref-002-nohref.xml", 11);

        AxiomSOAPEnvelopeFlattener soapEnvelopeFlattener = new AxiomSOAPEnvelopeFlattener();
        SOAPEnvelope flattened = soapEnvelopeFlattener.flatten(soapEnvelope);

        assertEquals(soapEnvelope, flattened);
    }

    /**
     * There are two multiref elements in the body so they need to be processed
     * both
     * 
     * @throws Exception
     */
    @Test
    public void testTwoMultirefInBody() throws Exception {
        SOAPEnvelope soapEnvelope = SOAPTestUtil.loadSOAPEnvelope("/multiref-003-twohref.xml", 11);
        AxiomSOAPEnvelopeFlattener soapEnvelopeFlattener = new AxiomSOAPEnvelopeFlattener();
        SOAPEnvelope flattened = soapEnvelopeFlattener.flatten(soapEnvelope);

        assertNotNull(flattened);
        assertNull(flattened.getHeader());
        assertNotNull(flattened.getBody());
        SOAPBody body = flattened.getBody();
        assertNull(body.getFirstChildWithName(QName.valueOf("multiRef")));
        assertNotNull(body.getFirstElement());
        assertEquals("createEventsResponse", body.getFirstElementLocalName());

        OMElement createEventResponseElement = body.getFirstElement();

        // count the chidren
        Iterator<?> iter = createEventResponseElement
                .getChildrenWithLocalName("createEventsReturn");
        int i = 0;
        while (iter.hasNext()) {
            iter.next();
            i++;
        }
        assertEquals(2, i);

        // let see if each child got its children
        Set<String> set = new HashSet<String>();
        set.add("message");
        set.add("resultCode");
        set.add("results");
        iter = createEventResponseElement.getChildrenWithLocalName("createEventsReturn");
        while (iter.hasNext()) {
            OMElement e = (OMElement) iter.next();
            // get the inner elements
            Iterator<?> ii = e.getChildElements();
            int j = 0;
            while (ii.hasNext()) {
                j++;
                OMElement inner = (OMElement) ii.next();
                assertTrue(set.contains(inner.getLocalName()));
            }
            assertEquals(3, j);
        }
    }

    /**
     * Old flattener had problems when some elements local name are 'id' ones.
     * Test here if the element does not disapear.
     * 
     * @throws Exception
     */
    @Test
    public void testFlattenSOAPEnvelopeWithIdElement() throws Exception {
        SOAPEnvelope soapEnvelope = SOAPTestUtil.loadSOAPEnvelope(
                "/multiref-004-withidelements.xml", 11);
        AxiomSOAPEnvelopeFlattener soapEnvelopeFlattener = new AxiomSOAPEnvelopeFlattener();
        SOAPEnvelope flattened = soapEnvelopeFlattener.flatten(soapEnvelope);

        assertNotNull(flattened);
        assertNull(flattened.getHeader());
        assertNotNull(flattened.getBody());
        SOAPBody body = flattened.getBody();
        assertNull(body.getFirstChildWithName(QName.valueOf("multiRef")));
        assertNotNull(body.getFirstElement());
        assertEquals("createEventsResponse", body.getFirstElementLocalName());

        OMElement first = body.getFirstElement();
        assertNotNull(first.getFirstChildWithName(QName.valueOf("id")));
        assertNotNull(first.getFirstChildWithName(QName.valueOf("createEventsReturn")));
    }

    @Test
    public void testFlattenSOAPEnvelopeWithIdElementInMultiref() throws Exception {
        SOAPEnvelope soapEnvelope = SOAPTestUtil.loadSOAPEnvelope(
                "/multiref-005-withidelementinmultiref.xml", 11);
        AxiomSOAPEnvelopeFlattener soapEnvelopeFlattener = new AxiomSOAPEnvelopeFlattener();
        SOAPEnvelope flattened = soapEnvelopeFlattener.flatten(soapEnvelope);

        assertNotNull(flattened);
        assertNull(flattened.getHeader());
        assertNotNull(flattened.getBody());
        SOAPBody body = flattened.getBody();
        assertNull(body.getFirstChildWithName(QName.valueOf("multiRef")));
        assertNotNull(body.getFirstElement());
        assertEquals("createEventsResponse", body.getFirstElementLocalName());

        OMElement first = body.getFirstElement();
        OMElement firstFirst = first.getFirstElement();
        assertNotNull(firstFirst);

        // get the inner id element
        assertNotNull(firstFirst.getFirstChildWithName(QName.valueOf("id")));
    }

    @Test
    public void testFlattenSOAPEnvelopeImbriquedIds() throws Exception {
        SOAPEnvelope soapEnvelope = SOAPTestUtil.loadSOAPEnvelope(
                "/multiref-006-imbriquedhrefs.xml", 11);
        AxiomSOAPEnvelopeFlattener soapEnvelopeFlattener = new AxiomSOAPEnvelopeFlattener();
        SOAPEnvelope flattened = soapEnvelopeFlattener.flatten(soapEnvelope);

        assertNotNull(flattened);
        assertNull(flattened.getHeader());
        assertNotNull(flattened.getBody());
        SOAPBody body = flattened.getBody();
        assertNull(body.getFirstChildWithName(QName.valueOf("multiRef")));
        assertNotNull(body.getFirstElement());
        assertEquals("createEventsResponse", body.getFirstElementLocalName());

        OMElement first = body.getFirstElement();
        OMElement createEventsReturn = first.getFirstElement();
        assertNotNull(createEventsReturn);
        assertEquals("createEventsReturn", createEventsReturn.getLocalName());

        // get the another element
        OMElement another = createEventsReturn.getFirstChildWithName(QName.valueOf("another"));
        assertNotNull(another);

        // count the inner elements
        Iterator<?> iter = another.getChildElements();
        int i = 0;
        while (iter.hasNext()) {
            iter.next();
            i++;
        }
        assertEquals(3, i);

        // check the inner values
        Set<String> set = new HashSet<String>();
        set.add("message2");
        set.add("resultCode2");
        set.add("results2");
        iter = another.getChildElements();
        while (iter.hasNext()) {
            OMElement element = (OMElement) iter.next();
            assertTrue(set.contains(element.getLocalName()));
        }
    }

    @Test
    public void testtestFlattenSOAPEnvelopeHrefInHeaderAndBody() throws Exception {
        SOAPEnvelope soapEnvelope = SOAPTestUtil.loadSOAPEnvelope(
                "/multiref-007-hrefheaderandbody.xml", 11);
        AxiomSOAPEnvelopeFlattener soapEnvelopeFlattener = new AxiomSOAPEnvelopeFlattener();
        SOAPEnvelope flattened = soapEnvelopeFlattener.flatten(soapEnvelope);

        assertNotNull(flattened);
        // let's go for header
        assertNotNull(flattened.getHeader());
        assertEquals("myheader", flattened.getHeader().getFirstElement().getLocalName());
        // look at the processed element
        OMElement myheaderElement = flattened.getHeader().getFirstElement();
        assertNotNull(myheaderElement.getFirstElement());
        Iterator<?> iter = myheaderElement.getChildElements();
        int i = 0;
        while (iter.hasNext()) {
            iter.next();
            i++;
        }
        assertEquals(3, i);

        // let's go for body
        assertNotNull(flattened.getBody());
        SOAPBody body = flattened.getBody();
        assertNull(body.getFirstChildWithName(QName.valueOf("multiRef")));
        assertNotNull(body.getFirstElement());
        assertEquals("createEventsResponse", body.getFirstElementLocalName());

        OMElement first = body.getFirstElement();
        OMElement firstFirst = first.getFirstElement();
        assertNotNull(firstFirst);

        iter = firstFirst.getChildElements();
        i = 0;
        while (iter.hasNext()) {
            iter.next();
            i++;
        }
        assertEquals(3, i);
    }

    /**
     * In the old version the ID disapears when no multiref was present...
     * 
     * @throws Exception
     */
    @Test
    public void testWithIDAttribute() throws Exception {
        SOAPEnvelope soapEnvelope = SOAPTestUtil.loadSOAPEnvelope(
                "/multiref-008-withidattribute.xml", 11);
        AxiomSOAPEnvelopeFlattener soapEnvelopeFlattener = new AxiomSOAPEnvelopeFlattener();
        SOAPEnvelope flattened = soapEnvelopeFlattener.flatten(soapEnvelope);

        assertNotNull(flattened);
        // let's go for header
        assertNull(flattened.getHeader());

        assertEquals("createEventsResponse", flattened.getBody().getFirstElementLocalName());

        OMElement createEventsResponse = flattened.getBody().getFirstChildWithName(
                QName.valueOf("createEventsResponse"));

        assertNotNull(createEventsResponse);

        OMElement createEventsReturn = createEventsResponse.getFirstChildWithName(QName
                .valueOf("createEventsReturn"));

        assertNotNull(createEventsReturn);

        // look if the attribute ID is here

        OMAttribute id = createEventsReturn.getAttribute(QName.valueOf("id"));
        assertNotNull(id);
        assertEquals("0", id.getAttributeValue());

    }

    /**
     * Test with an ID attribute which is not a multref reference and with an ID
     * which is one...
     * 
     * @throws Exception
     */
    @Test
    public void testWithIDAttributeAndMultiref() throws Exception {
        SOAPEnvelope soapEnvelope = SOAPTestUtil.loadSOAPEnvelope(
                "/multiref-009-withidattributeandhref.xml", 11);
        AxiomSOAPEnvelopeFlattener soapEnvelopeFlattener = new AxiomSOAPEnvelopeFlattener();
        SOAPEnvelope flattened = soapEnvelopeFlattener.flatten(soapEnvelope);

        assertNotNull(flattened);
        // let's go for header
        assertNull(flattened.getHeader());

        assertEquals("createEventsResponse", flattened.getBody().getFirstElementLocalName());

        OMElement createEventsResponse = flattened.getBody().getFirstChildWithName(
                QName.valueOf("createEventsResponse"));

        assertNotNull(createEventsResponse);

        OMElement createEventsReturn = createEventsResponse.getFirstChildWithName(QName
                .valueOf("createEventsReturn"));

        assertNotNull(createEventsReturn);

        // look if the attribute ID is here

        OMAttribute id = createEventsReturn.getAttribute(QName.valueOf("id"));
        assertNotNull(id);
        assertEquals("0", id.getAttributeValue());

        OMElement myelement = createEventsResponse
                .getFirstChildWithName(QName.valueOf("myelement"));

        assertNotNull(myelement);

        Set<String> set = new HashSet<String>();
        set.add("message");

        int i = 0;
        Iterator<?> iter = myelement.getChildElements();
        while (iter.hasNext()) {
            OMElement element = (OMElement) iter.next();
            assertTrue(set.contains(element.getLocalName()));
            i++;
        }

        assertEquals(1, i);
    }
}
