/**
 * 
 */
package org.petalslink.dsb.saaj.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.output.DOMOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * @author chamerling
 * 
 */
public class SOAPMessageUtils {

    static DocumentBuilder builder;

    static MessageFactory messageFactory;

    private static Logger logger = Logger.getLogger(SOAPMessageUtils.class.getName());

    static {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            messageFactory = MessageFactory.newInstance();
            // TODO : version in message factory SOAPConstants.SOAP_1_1_PROTOCOL
        } catch (ParserConfigurationException e) {
        } catch (SOAPException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    private SOAPMessageUtils() throws SOAPException {

    }

    public static Document getBodyFromMessage(SOAPMessage message) throws SOAPException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("SOAPMessage: \n " + getSOAPMessageAsString(message));
        }

        if (containsFault(message)) {
            // throwFaultException(message);
        }

        Document result = builder.newDocument();
        Node soapBody;
        try {
            soapBody = result.importNode(getBodyContent(message.getSOAPBody()), true);
        } catch (SOAPException e) {
            throw new SOAPException(e);
        }
        result.appendChild(soapBody);
        return result;
    }

    /**
     * Create a {@link SOAPMessage} and set the input document as
     * {@link SOAPBody} content.
     * 
     * @param bodyDocument
     * @return
     * @throws SOAPException
     */
    public static SOAPMessage createSOAPMessageFromBodyContent(Document bodyDocument) throws SOAPException {
        Document message = null;
        try {
            message = createSOAPMessageDocumentFromBodyContent(bodyDocument);
        } catch (JDOMException e) {
            throw new SOAPException(e);
        }
        return buildSOAPMessage(message, messageFactory);
    }

    /**
     * Build a <code>SOAPMessage</code> from an <code>InputStream</code>.
     * 
     * @param input
     *            the stream of XML to create the <code>SOAPMessage</code> from.
     * @param messageFactory
     *            a reference to the MessageFactory to use
     * @return the <code>SOAPMessage</code> constructed from the input
     */
    public static SOAPMessage buildSOAPMessage(InputStream input, MessageFactory messageFactory) {
        SOAPMessage soapMessage = null;
        try {
            soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            StreamSource preppedMsgSrc = new StreamSource(input);
            soapPart.setContent(preppedMsgSrc);
            soapMessage.saveChanges();
        } catch (SOAPException ex) {
            ex.printStackTrace();
        }
        return soapMessage;
    }

    /**
     * Build a {@link SOAPMessage} from a DOM document. The DOM document is the
     * complete SOAP message (from SOAP Envelope)
     * 
     * @param soapMessageAsDocument
     * @param messageFactory
     * @return
     */
    public static SOAPMessage buildSOAPMessage(Document soapMessageAsDocument,
            MessageFactory messageFactory) {
        try {
            return buildSOAPMessage(
                    convertDOMSource2InputSource(new DOMSource(soapMessageAsDocument))
                            .getByteStream(), messageFactory);
        } catch (SOAPException e) {
        }
        return null;
    }
    
    /**
     * 
     * @param soapMessageAsDocument
     * @return
     */
    public static SOAPMessage buildSOAPMessage(Document soapMessageAsDocument) {
        try {
            return buildSOAPMessage(
                    convertDOMSource2InputSource(new DOMSource(soapMessageAsDocument))
                            .getByteStream(), messageFactory);
        } catch (SOAPException e) {
        }
        return null;
    }

    /**
     * Serializes a <code>SOAPMessage</code> as XML encoded as a byte array
     * 
     * @param message
     *            the <code>SOAPMessage</code> to encode as bytes
     * @return the encoded <code>SOAPMessage</code>
     */
    public static byte[] getSOAPMessageAsBytes(SOAPMessage message) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            message.writeTo(byteStream);
        } catch (SOAPException ex) {
        } catch (IOException ex) {
        }
        return byteStream.toByteArray();
    }

    /**
     * Serialize a <code>SOAPMessage</code> as a String
     * 
     * @param message
     *            the <code>SOAPMessage</code>
     * @return the String value of the <code>SOAPMessage</code>
     */
    public static String getSOAPMessageAsString(SOAPMessage message) {
        return new String(getSOAPMessageAsBytes(message));
    }

    /**
     * Extract the first <code>Node</code> contaning the payload of a
     * <code>SOAPBody</code>/
     * 
     * @param body
     *            the <code>SOAPBody</code> to extract the payload from
     * @return the first <code>Node</code> of the payload
     */
    public static Node getBodyContent(SOAPBody body) {
        Iterator iterator = body.getChildElements();

        Node firstNode = null;
        while (iterator.hasNext()) {
            Node currentNode = (Node) iterator.next();
            if (currentNode instanceof SOAPBodyElement) {
                firstNode = currentNode;
                break;
            }
        }
        return firstNode;
    }

    /**
     * Determine if a SOAP fault is present in a <code>SOAPMessage</code>'s body
     * 
     * @param soapMessage
     *            the <code>SOAPMessage</code> to evaluate
     * @return true if the message contains a fault, false if not
     */
    public static boolean containsFault(SOAPMessage soapMessage) {
        boolean result = false;
        try {
            result = soapMessage.getSOAPBody().getFault() != null;
        } catch (SOAPException ex) {
        }
        return result;
    }

    private static InputSource convertDOMSource2InputSource(final DOMSource domSource)
            throws SOAPException {
        InputSource source = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            StreamResult streamResult = new StreamResult(os);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(domSource, streamResult);
            os.flush();
            os.close();
            InputStream is = new java.io.ByteArrayInputStream(os.toByteArray());

            StreamSource attach = new StreamSource(is);
            source = SAXSource.sourceToInputSource(attach);
        } catch (final IOException e) {
            throw new SOAPException(e);
        } catch (TransformerConfigurationException e) {
            throw new SOAPException(e);
        } catch (TransformerFactoryConfigurationError e) {
            throw new SOAPException(e);
        } catch (TransformerException e) {
            throw new SOAPException(e);
        }
        return source;
    }

    /**
     * Create a SOAP Message and insert the input document as {@link SOAPBody}
     * content.
     * 
     * @param bodyContent
     * @return
     * @throws JDOMException
     */
    public static Document createSOAPMessageDocumentFromBodyContent(Document bodyContent)
            throws JDOMException {
        Document res = null;
        // TODO: create soap1.1 or 1.2 message
        Element env = new Element("Envelope", Namespace.getNamespace("soap-env",
                "http://schemas.xmlsoap.org/soap/envelope/"));
        env.addNamespaceDeclaration(Namespace.getNamespace("xsd",
                "http://www.w3.org/1999/XMLSchema"));
        env.addNamespaceDeclaration(Namespace.getNamespace("xsi",
                "http://www.w3.org/1999/XMLSchema-instance"));
        org.jdom.Document jdom = new org.jdom.Document(env);
        Element body = new Element("Body", Namespace.getNamespace("soap-env",
                "http://schemas.xmlsoap.org/soap/envelope/"));
        env.addContent(body);

        if (bodyContent != null) {
            DOMBuilder builder = new DOMBuilder();
            org.jdom.Document jdomDocument = builder.build(bodyContent);
            body.addContent(((Element) ((Element) jdomDocument.getRootElement()).clone()).detach());
        }

        DOMOutputter converter = new DOMOutputter();
        // converter.setForceNamespaceAware(true);
        res = converter.output(jdom);
        return res;
    }

    /**
     * @param request
     * @return
     */
    public static String getOperation(SOAPMessage request) {
        if (request == null) {
            return null;
        }
        MimeHeaders hd = request.getMimeHeaders();
        
        Iterator iter = hd.getAllHeaders();
        while (iter.hasNext()) {
            MimeHeader object = (MimeHeader) iter.next();
            System.out.println(object.getName() + " " + object.getValue());
        }

        String result = null;
        if (hd != null) {
            String[] hds = hd.getHeader("SOAPAction");
            if (hds != null && hds.length > 0) {
                result = hds[0];
            }
        }
        return result;
    }

}
