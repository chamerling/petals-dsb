/**
 * 
 */
package org.petalslink.dsb.xmlutils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ebmwebsourcing.easycommons.stream.InputStreamForker;
import com.ebmwebsourcing.easycommons.stream.ReaderInputStream;
import com.ebmwebsourcing.easycommons.xml.Transformers;

/**
 * @author chamerling
 * 
 */
public class XMLHelper {

    public static Document createDocument(final Source source, final boolean forkSource)
            throws Exception {
        Document document = null;
        try {
            if (source instanceof DOMSource) {
                Node originalNode = ((DOMSource) source).getNode();
                if (originalNode instanceof Document) {
                    Document originalDocument = (Document) originalNode;
                    if (forkSource) {
                        // clone the document
                        DocumentBuilder builder = com.ebmwebsourcing.easycommons.xml.DocumentBuilders.takeDocumentBuilder();
                        document =  builder.newDocument();
                        document.appendChild(document.importNode(
                                originalDocument.getDocumentElement(), true));
                        com.ebmwebsourcing.easycommons.xml.DocumentBuilders.releaseDocumentBuilder(builder);
                    } else {
                        // refer the document
                        document = originalDocument;
                    }
                } else {
                    DocumentBuilder builder = com.ebmwebsourcing.easycommons.xml.DocumentBuilders.takeDocumentBuilder();
                    document = builder.newDocument();
                    if (forkSource) {
                        // clone the node
                        document.appendChild(document.importNode(originalNode, true));
                    } else {
                        // adopt the node
                        document.appendChild(document.adoptNode(originalNode));
                    }
                    com.ebmwebsourcing.easycommons.xml.DocumentBuilders.releaseDocumentBuilder(builder);

                }
            }

            if (document == null) {
                Source tempSource = source;
                if (forkSource && (source instanceof StreamSource)) {
                    tempSource = new StreamSource(forkStreamSource((StreamSource) source));
                }
                DocumentBuilder builder = com.ebmwebsourcing.easycommons.xml.DocumentBuilders.takeDocumentBuilder();
                document = builder.newDocument();
                final DOMResult domResult = new DOMResult(document);
                final Transformer transformer = com.ebmwebsourcing.easycommons.xml.Transformers.takeTransformer();
                try {
                    transformer.transform(tempSource, domResult);
                } finally {
                    transformer.reset();
                    com.ebmwebsourcing.easycommons.xml.Transformers.releaseTransformer(transformer);
                }
            }
        } catch (TransformerException e) {
            throw new Exception(e);
        }
        return document;
    }

    private static final InputStream forkStreamSource(StreamSource streamSource) throws IOException {
        InputStreamForker streamForker;
        final InputStream isContent = streamSource.getInputStream();
        if (isContent != null) {
            // The StreamSource was created from an InputStream
            streamForker = new InputStreamForker(isContent);
        } else {
            // The StreamSource was created from a Reader
            // we wrap it as an InputStream
            streamForker = new InputStreamForker(new ReaderInputStream(streamSource.getReader()));
        }
        streamSource.setInputStream(streamForker.fork());
        return streamForker.fork();
    }

    public static InputStream getInputStream(Document doc) throws IOException {
        return createStreamSource(doc).getInputStream();
    }

    public static StreamSource createStreamSource(final Document document) throws IOException {

        final ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
        final Result sResult = new StreamResult(bufferOut);
        final Transformer transformer = Transformers.takeTransformer();
        try {
            transformer.transform(new DOMSource(document), sResult);
        } catch (TransformerException e) {
            throw new IOException("Error while transform DOM2 document to StreamSource", e);
        } finally {
            transformer.reset();
            Transformers.releaseTransformer(transformer);
        }
        final ByteArrayInputStream bufferIn = new ByteArrayInputStream(bufferOut.toByteArray());
        return new StreamSource(bufferIn);
    }

}
