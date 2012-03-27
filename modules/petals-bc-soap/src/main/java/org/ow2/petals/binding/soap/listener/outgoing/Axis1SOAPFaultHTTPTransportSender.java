package org.ow2.petals.binding.soap.listener.outgoing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.CommonsHTTPTransportSender;

public class Axis1SOAPFaultHTTPTransportSender extends CommonsHTTPTransportSender {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.axis2.transport.http.CommonsHTTPTransportSender#invoke(org
     * .apache.axis2.context.MessageContext)
     */
    @Override
    public InvocationResponse invoke(MessageContext msgContext) throws AxisFault {
        InvocationResponse ir = super.invoke(msgContext);
        
        // only on client side
        if (!msgContext.isServerSide()) {
            InputStream inStream = (InputStream) msgContext
                    .getProperty(MessageContext.TRANSPORT_IN);

            // If the body input stream is not null
            if (inStream != null) {

                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    try {
                        correctSOAPFault(inStream, baos);
                        InputStream is = new ByteArrayInputStream(baos.toByteArray());
                        msgContext.setProperty(MessageContext.TRANSPORT_IN, is);
                    } finally {
                        baos.close();
                    }
                } catch (Exception e) {
                    throw new AxisFault("Can not correct the incoming response", e);
                }

            }
        }

        return ir;
    }

    private static final String SOAP_RESPONSE_FAULT_LOCAL_NAME = "Fault";

    private static final String SOAP_RESPONSE_ENVELOPE_LOCAL_NAME = "Envelope";

    private static final String SOAP_ENVELOPE_NAMESPACE_URI = "http://schemas.xmlsoap.org/soap/envelope/";

    private static void correctSOAPFault(InputStream inStream, ByteArrayOutputStream baos)
            throws FactoryConfigurationError, XMLStreamException {
        // create the XML stream reader
        XMLInputFactory inFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inFactory.createXMLStreamReader(inStream);

        try {
            // create the XML output writer
            XMLOutputFactory outFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = outFactory.createXMLStreamWriter(baos);

            try {
                // start to parse the SOAP response
                boolean done = false;
                while (!done) {
                    int event = reader.next();

                    if (event == XMLStreamConstants.START_ELEMENT) {

                        String prefix = reader.getPrefix();
                        String localName = reader.getLocalName();
                        String namespaceURI = reader.getNamespaceURI();

                        // if the fault tag is found
                        if (localName.equals(SOAP_RESPONSE_FAULT_LOCAL_NAME)
                                && namespaceURI.equals(SOAP_ENVELOPE_NAMESPACE_URI)) {

                            // write the fault tag
                            writer.writeStartElement(prefix, localName, namespaceURI);
                            writeNamespacesAndAttributes(reader, writer);

                            int levelFromFault = 1;
                            // parse the level below the fault tag
                            do {
                                event = reader.next();

                                if (event == XMLStreamConstants.START_ELEMENT) {
                                    prefix = reader.getPrefix();
                                    localName = reader.getLocalName();
                                    namespaceURI = reader.getNamespaceURI();

                                    // if it is the first level below the fault
                                    // tag
                                    if (levelFromFault == 1) {
                                        // write unqualified tag (faultcode,
                                        // faultstring, detail...)
                                        writer.writeStartElement(localName);
                                    } else {
                                        // else copy the start tag without
                                        // changes
                                        writer.writeStartElement(prefix, localName, namespaceURI);
                                    }

                                    writeNamespacesAndAttributes(reader, writer);
                                    levelFromFault++;
                                } else if (event == XMLStreamConstants.CHARACTERS) {
                                    // copy the characters without changes
                                    String text = reader.getText();
                                    writer.writeCharacters(text);
                                } else if (event == XMLStreamConstants.END_ELEMENT) {
                                    // write the end tag corresponding to the
                                    // start tag (with or without prefix)
                                    levelFromFault--;
                                    writer.writeEndElement();
                                }
                            } while (levelFromFault > 0);

                        } else {
                            // else copy the start tag without changes
                            writer.writeStartElement(prefix, localName, namespaceURI);
                            writeNamespacesAndAttributes(reader, writer);
                        }

                    } else if (event == XMLStreamConstants.CHARACTERS) {
                        // copy the characters without changes
                        String text = reader.getText();
                        writer.writeCharacters(text);
                    } else if (event == XMLStreamConstants.END_ELEMENT) {
                        // write the end tag corresponding to the start tag
                        writer.writeEndElement();

                        String localName = reader.getLocalName();
                        String namespaceURI = reader.getNamespaceURI();
                        if (localName.equals(SOAP_RESPONSE_ENVELOPE_LOCAL_NAME)
                                && namespaceURI.equals(SOAP_ENVELOPE_NAMESPACE_URI)) {
                            done = true;
                        }

                    }
                }

            } finally {
                writer.close();
            }
        } finally {
            reader.close();
        }
    }

    private static final void writeNamespacesAndAttributes(XMLStreamReader reader, XMLStreamWriter writer)
            throws XMLStreamException {
        int namespaceCount = reader.getNamespaceCount();
        for (int i = 0; i < namespaceCount; i++) {
            String namespacePrefix = reader.getNamespacePrefix(i);
            String namespaceURI = reader.getNamespaceURI(i);
            writer.writeNamespace(namespacePrefix, namespaceURI);
        }

        int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            String attributePrefix = reader.getAttributePrefix(i);
            String attributeNamespaceURI = reader.getAttributeNamespace(i);
            String attributeLocalName = reader.getAttributeLocalName(i);
            String attributeValue = reader.getAttributeValue(i);
            writer.writeAttribute(attributePrefix, attributeNamespaceURI, attributeLocalName, attributeValue);
        }
    }

}
