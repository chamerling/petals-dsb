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
package org.ow2.petals.messaging.framework.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ow2.petals.messaging.framework.EngineException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class XMLUtil {

	public static Source createSource(final String message) throws IOException {
		String charsetEncoding = Charset.defaultCharset().displayName();
		return createSource(message, charsetEncoding);
	}

	public static Source createSource(final String message,
			final String charFormat) throws IOException {
		StreamSource source = new StreamSource();
		byte[] msgByte = message.getBytes(charFormat);
		ByteArrayInputStream in = new ByteArrayInputStream(msgByte);
		source.setInputStream(in);
		return source;
	}

	public static String createString(final Source source,
			final boolean forkSource) throws EngineException {

		Source tempSource = source;
		if (forkSource && (source instanceof StreamSource)) {
			tempSource = new StreamSource(
					forkStreamSource((StreamSource) source));
		}

		try {
			final StringWriter buffer = new StringWriter();
			final Result sresult = new StreamResult(buffer);
			final Transformer transformer = Transformers
					.getDefaultTransformer();
			try {
				transformer.transform(tempSource, sresult);
			} finally {
				transformer.reset();
			}

			return buffer.toString();
		} catch (TransformerConfigurationException e) {
			throw new EngineException(e);
		} catch (TransformerException e) {
			throw new EngineException(e);
		}
	}

	public static Document loadDocument(InputStream inputStream)
			throws IOException, SAXException {

		Document document = null;
		try {
			document = DocumentBuilders.getNamespaceDocumentBuilder().parse(
					inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}

	public static StreamSource createStreamSource(final Document document)
			throws EngineException {

		final ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
		final Result sResult = new StreamResult(bufferOut);
		final Transformer transformer = Transformers.getDefaultTransformer();
		try {
			transformer.transform(new DOMSource(document), sResult);
		} catch (TransformerException e) {
			throw new EngineException(
					"Error while transform DOM2 document to StreamSource", e);
		} finally {
			transformer.reset();
		}
		// TODO (CDE): Use CircularBuffer to increase performances.
		final ByteArrayInputStream bufferIn = new ByteArrayInputStream(
				bufferOut.toByteArray());
		return new StreamSource(bufferIn);

	}

	private static final InputStream forkStreamSource(StreamSource streamSource) {
		InputStreamForker streamForker;
		final InputStream isContent = streamSource.getInputStream();
		if (isContent != null) {
			// The StreamSource was created from an InputStream
			streamForker = new InputStreamForker(isContent);
		} else {
			// The StreamSource was created from a Reader
			// we wrap it as an InputStream
			streamForker = new InputStreamForker(new ReaderInputStream(
					streamSource.getReader()));
		}
		streamSource.setInputStream(streamForker.getInputStreamOne());
		return streamForker.getInputStreamTwo();
	}

}
