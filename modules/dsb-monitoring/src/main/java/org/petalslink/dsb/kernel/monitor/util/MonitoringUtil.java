/**
 * PETALS - PETALS Services Platform. Copyright (c) 2007 EBM Websourcing,
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
 * $Id: Router.java,v 1.2 2006/03/17 10:24:27 alouis Exp $
 * -------------------------------------------------------------------------
 */
package org.petalslink.dsb.kernel.monitor.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ow2.petals.commons.stream.InputStreamForker;
import org.ow2.petals.commons.stream.ReaderInputStream;
import org.ow2.petals.commons.threadlocal.DocumentBuilders;
import org.ow2.petals.commons.threadlocal.Transformers;
import org.petalslink.dsb.api.DSBException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * @author aruffie - EBM WebSourcing
 * 
 */
public class MonitoringUtil {
    public static String splitPattern(final URI uri) {
        final String[] splittedPattern = uri.toString().split("/");
        if (splittedPattern != null) {
            return splittedPattern[splittedPattern.length - 1];
        }
        return null;
    }

    public static String cloneSourceInString(Source s) throws DSBException {
        // String result = null;
        // if (s instanceof StreamSource) {
        // final StreamSource ss = (StreamSource) s;
        // byte[] bytes = new byte[ss.getInputStream().available()];
        // ss.getInputStream().read(bytes);
        // byte[] bytes2 = ClonerHelper.clone(bytes);
        // final InputStream in = new ByteArrayInputStream(bytes);
        // ss.setInputStream(in);
        // result = new String(bytes2);
        // } else if (s instanceof DOMSource) {
        // Document doc = (Document) ((DOMSource) s).getNode();
        // s = new DOMSource(doc);
        // try {
        // result = XMLUtil.createStringFromDOMDocument(doc);
        // } catch (TransformerException e) {
        // e.printStackTrace();
        // System.err.println(e.getMessage());
        // }
        // }
        // return result;

        Source tempSource = s;
        if ((s instanceof StreamSource)) {
            tempSource = new StreamSource(forkStreamSource((StreamSource) s));
        }

        try {
            final StringWriter buffer = new StringWriter();
            final Result sresult = new StreamResult(buffer);
            final Transformer transformer = Transformers.getDefaultTransformer();
            try {
                transformer.transform(tempSource, sresult);
            } finally {
                transformer.reset();
            }

            return buffer.toString();
        } catch (TransformerConfigurationException e) {
            throw new DSBException(e);
        } catch (TransformerException e) {
            throw new DSBException(e);
        }
    }

    public static Source cloneSource(Source s) throws DSBException {
        // Source result = null;
        // if (s instanceof StreamSource) {
        // // FIXME = premature end of file, voir comment utiliser le stream
        // // forker!
        // final StreamSource ss = (StreamSource) s;
        // byte[] bytes = new byte[ss.getInputStream().available()];
        // ss.getInputStream().read(bytes);
        // byte[] bytes2 = ClonerHelper.clone(bytes);
        // final InputStream in = new ByteArrayInputStream(bytes);
        // ss.setInputStream(in);
        // final ByteArrayInputStream inCopy = new ByteArrayInputStream(bytes2);
        // result = new StreamSource(inCopy);
        // } else if (s instanceof DOMSource) {
        // Document doc = (Document) ((DOMSource) s).getNode();
        // s = new DOMSource(doc);
        // result = new DOMSource(doc);
        // }
        // return result;

        Document doc = createDocument(s, true);
        return createDOMSource(doc);

    }

    private static StreamSource createStreamSource(final Document document) throws DSBException {

        final ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
        final Result sResult = new StreamResult(bufferOut);
        final Transformer transformer = Transformers.getDefaultTransformer();
        try {
            transformer.transform(new DOMSource(document), sResult);
        } catch (TransformerException e) {
            throw new DSBException("Error while transform DOM2 document to StreamSource", e);
        } finally {
            transformer.reset();
        }
        // TODO (CDE): Use CircularBuffer to increase performances.
        final ByteArrayInputStream bufferIn = new ByteArrayInputStream(bufferOut.toByteArray());
        return new StreamSource(bufferIn);

    }

    private static DOMSource createDOMSource(final Document document) {
        // normalize the document to assure the resolution of each node
        // namespace
        document.normalizeDocument();
        return new DOMSource(document);
    }

    public static String parseToString(final Node node)
            throws TransformerFactoryConfigurationError, TransformerException {
        String result = null;
        if (node != null) {
            node.normalize();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(node), new StreamResult(stringWriter));
            StringBuffer buffer = stringWriter.getBuffer();
            result = buffer.toString();
        }
        return result;
    }

    private static Document createDocument(final Source source, final boolean forkSource)
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
                        document.appendChild(document.importNode(originalDocument
                                .getDocumentElement(), true));
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
}
