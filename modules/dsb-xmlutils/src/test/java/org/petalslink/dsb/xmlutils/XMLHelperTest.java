/**
 * 
 */
package org.petalslink.dsb.xmlutils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.w3c.dom.Document;

import com.ebmwebsourcing.easycommons.stream.StreamHelper;
import com.ebmwebsourcing.easycommons.xml.DOMHelper;
import com.ebmwebsourcing.easycommons.xml.SourceHelper;

/**
 * 
 * @author chamerling
 * 
 */
public class XMLHelperTest extends TestCase {

    public void testCreateDocument() throws Exception {
        String str = "<foo>bar</foo>";
        Source source = DOMHelper.parseAsDOMSource(new ByteArrayInputStream(str.getBytes()));
        Document document = XMLHelper.createDocument(source, false);
        assertNotNull(document);
        assertEquals(str,
                com.ebmwebsourcing.easycommons.xml.XMLHelper.createStringFromDOMNode(document));
    }

    public void testCreateStreamSource() throws Exception {
        String str = "<foo>bar</foo>";
        Document document = com.ebmwebsourcing.easycommons.xml.XMLHelper
                .createDocumentFromString(str);
        StreamSource source = XMLHelper.createStreamSource(document);
        assertNotNull(source);
        String result = SourceHelper.toString(source);
        assertTrue(result.contains(str));
    }

    public void testGetInputStream() throws Exception {
        Source source = DOMHelper.parseAsDOMSource(XMLHelperTest.class
                .getResourceAsStream("/soap-hello.xml"));
        Document doc = XMLHelper.createDocument(source, false);
        InputStream is = XMLHelper.getInputStream(doc);
        assertNotNull(is);
        String out = StreamHelper.getString(is);
        System.out.println(out);
        assertNotNull(out);
    }
}
