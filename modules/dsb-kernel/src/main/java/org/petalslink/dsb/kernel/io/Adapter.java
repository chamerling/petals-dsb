/**
 * 
 */
package org.petalslink.dsb.kernel.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ow2.petals.commons.stream.InputStreamForker;
import org.ow2.petals.commons.stream.ReaderInputStream;
import org.ow2.petals.commons.threadlocal.DocumentBuilders;
import org.ow2.petals.commons.threadlocal.Transformers;
import org.ow2.petals.jbi.messaging.exchange.NormalizedMessageImpl;
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.service.client.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author chamerling
 * 
 */
public class Adapter {

    private Adapter() {
    }

    public static Message transform(final NormalizedMessage in) {
        return new Message() {

            public Document getPayload() {
                try {
                    return Adapter.createDocument(in.getContent(), true);
                } catch (DSBException e) {
                    e.printStackTrace();
                }
                return null;
            }

            public QName getOperation() {
                return null;
            }

            public Map<String, String> getProperties() {
                Map<String, String> properties = new HashMap<String, String>();
                Set keys = in.getPropertyNames();
                for (Object key : keys) {
                    properties.put(key.toString(), in.getProperty(key.toString()).toString());
                }
                return properties;
            }

            public Map<String, Document> getHeaders() {
                return new HashMap<String, Document>();
            }

            public QName getService() {
                return null;
            }

            public QName getInterface() {
                return null;
            }

            public String getEndpoint() {
                return null;
            }

        };
    }

    public static NormalizedMessage transform(final Message message) {

        NormalizedMessageImpl result = new NormalizedMessageImpl();
        try {
            result.setContent(createStreamSource(message.getPayload()));
        } catch (MessagingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Set<String> keys = message.getProperties().keySet();
        for (String key : keys) {
            result.setProperty(key, message.getProperties().get(key));
        }
        return result;

    }

    public static Document createDocument(final Source source, final boolean forkSource)
            throws DSBException {
        Document document = null;
        try {
            if (source instanceof DOMSource) {
                Node originalNode = ((DOMSource) source).getNode();
                if (originalNode instanceof Document) {
                    Document originalDocument = (Document) originalNode;
                    if (forkSource) {
                        // clone the document
                        document = DocumentBuilders.getNamespaceDocumentBuilder().newDocument();
                        document.appendChild(document.importNode(
                                originalDocument.getDocumentElement(), true));
                    } else {
                        // refer the document
                        document = originalDocument;
                    }
                } else {
                    document = DocumentBuilders.getNamespaceDocumentBuilder().newDocument();
                    if (forkSource) {
                        // clone the node
                        document.appendChild(document.importNode(originalNode, true));
                    } else {
                        // adopt the node
                        document.appendChild(document.adoptNode(originalNode));
                    }

                }
            }

            if (document == null) {
                Source tempSource = source;
                if (forkSource && (source instanceof StreamSource)) {
                    tempSource = new StreamSource(forkStreamSource((StreamSource) source));
                }
                document = DocumentBuilders.getNamespaceDocumentBuilder().newDocument();
                final DOMResult domResult = new DOMResult(document);
                final Transformer transformer = Transformers.getDefaultTransformer();
                try {
                    transformer.transform(tempSource, domResult);
                } finally {
                    transformer.reset();
                }
            }
        } catch (TransformerException e) {
            throw new DSBException(e);
        }
        return document;
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
            streamForker = new InputStreamForker(new ReaderInputStream(streamSource.getReader()));
        }
        streamSource.setInputStream(streamForker.getInputStreamOne());
        return streamForker.getInputStreamTwo();
    }

    public static InputStream getInputStream(Document doc) throws IOException {
        return createStreamSource(doc).getInputStream();
    }

    public static StreamSource createStreamSource(final Document document) throws IOException {

        final ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
        final Result sResult = new StreamResult(bufferOut);
        final Transformer transformer = Transformers.getDefaultTransformer();
        try {
            transformer.transform(new DOMSource(document), sResult);
        } catch (TransformerException e) {
            throw new IOException("Error while transform DOM2 document to StreamSource", e);
        } finally {
            transformer.reset();
        }
        final ByteArrayInputStream bufferIn = new ByteArrayInputStream(bufferOut.toByteArray());
        return new StreamSource(bufferIn);
    }

}
