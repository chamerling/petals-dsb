package org.ow2.petals.soap.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SOAPSender {

	/**
	 * The logger
	 */
	private final Logger log = Logger.getLogger(SOAPSender.class.getName());

	public SOAPSender() {
	}

	public Document sendSoapRequest(Document request, String address)
			throws SOAPException {
		Document response = null;
		try {
			// First create the connection
			SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory
					.newInstance();

			SOAPConnection connection = soapConnFactory.createConnection();

			// Next, create the actual message
			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage message = messageFactory.createMessage();

			// Create objects for the message parts
			SOAPPart soapPart = message.getSOAPPart();
			SOAPEnvelope envelope = soapPart.getEnvelope();
			SOAPBody body = envelope.getBody();

			// Populate the Message
			StreamSource preppedMsgSrc = new StreamSource(this
					.convertDOMSource2InputSource(new DOMSource(request))
					.getByteStream());
			soapPart.setContent(preppedMsgSrc);

			// Save the message
			message.saveChanges();

			// Check the input
			this.log.finest("\nREQUEST:\n");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(out);
			message.writeTo(ps);
			this.log.finest(out.toString());

			// Send the message and get a reply

			// Set the destination
			String destination = address;
			this.log.finest("\ndestination = " + destination);
			// Send the message

			SOAPMessage reply = connection.call(message, destination);

			if (reply != null) {
				// Check the output
				this.log.finest("\nRESPONSE:\n");
				// Create the transformer
				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				// Extract the content of the reply
				Source sourceContent = reply.getSOAPPart().getContent();
				// Set the output for the transformation
				Writer writer = new StringWriter();
				StreamResult result = new StreamResult(writer);
				transformer.transform(sourceContent, result);

				String res = writer.toString();
				this.log.finest(res);
				DocumentBuilderFactory builder = DocumentBuilderFactory
						.newInstance();
				builder.setNamespaceAware(true);
				response = builder.newDocumentBuilder().parse(
						new ByteArrayInputStream(res.getBytes()));

			}
			// Close the connection
			connection.close();

		} catch (UnsupportedOperationException e) {
			throw new SOAPException(e);
		} catch (SOAPException e) {
			throw new SOAPException(e);
		} catch (TransformerConfigurationException e) {
			throw new SOAPException(e);
		} catch (TransformerException e) {
			throw new SOAPException(e);
		} catch (SAXException e) {
			throw new SOAPException(e);
		} catch (IOException e) {
			throw new SOAPException(e);
		} catch (ParserConfigurationException e) {
			throw new SOAPException(e);
		} catch (javax.xml.soap.SOAPException e) {
			throw new SOAPException(e);
		}
		return response;
	}

	private static InputSource convertDOMSource2InputSource(
			final DOMSource domSource) throws SOAPException {
		InputSource source = null;
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			StreamResult streamResult = new StreamResult(os);
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
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

}
