/**
 * 
 */
package org.petalslink.dsb.saaj.utils;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;

import junit.framework.TestCase;

import org.petalslink.dsb.saaj.utils.SOAPMessageUtils;
import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public class SOAPMessageUtilsTest extends TestCase {

    public void testTransformMessageNoNS() throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(SOAPMessageUtilsTest.class
                .getResourceAsStream("/message1.xml"));
        SOAPMessage message = SOAPMessageUtils.createSOAPMessageFromBodyContent(
                document);
        assertNotNull(message);
        assertNotNull(message.getSOAPBody());
        System.out.println("SOAP Message : " + SOAPMessageUtils.getSOAPMessageAsString(message));
    }

    public void testTransformMessageWithNS() throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(SOAPMessageUtilsTest.class
                .getResourceAsStream("/message2.xml"));
        SOAPMessage message = null;
        try {
            message = SOAPMessageUtils.createSOAPMessageFromBodyContent(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(message);
        assertNotNull(message.getSOAPBody());
        System.out.println("SOAP Message : " + SOAPMessageUtils.getSOAPMessageAsString(message));
    }

    public void testSOAPMessageFromDocument() throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(SOAPMessageUtilsTest.class
                .getResourceAsStream("/soapmessage1.xml"));
        SOAPMessage message = SOAPMessageUtils.buildSOAPMessage(document,
                MessageFactory.newInstance());
        assertNotNull(message);
        assertNotNull(message.getSOAPBody());
        assertNotNull(message.getSOAPBody().getFirstChild().getNodeName());
        message.writeTo(System.out);
    }

    public void testCreateNotify() throws Exception {

    }
    
    public void testGetSOAPMessageBodyContent() throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(SOAPMessageUtilsTest.class
                .getResourceAsStream("/soapmessage1.xml"));
        SOAPMessage message = SOAPMessageUtils.buildSOAPMessage(document,
                MessageFactory.newInstance());
        Document bodyContent = SOAPMessageUtils.getBodyFromMessage(message);
        assertNotNull(bodyContent);
        assertEquals("sayHello", bodyContent.getFirstChild().getLocalName());
    }
    
    public void testGetSOAPAction() throws Exception {
        SOAPMessage message = MessageFactory.newInstance().createMessage(null, SOAPMessageUtilsTest.class
                .getResourceAsStream("/soapmessage1.xml"));
        assertNotNull(message);
        message.getMimeHeaders().addHeader("SOAPAction", "MySOAPAction");
        assertEquals("MySOAPAction", SOAPMessageUtils.getOperation(message));
    }
}
