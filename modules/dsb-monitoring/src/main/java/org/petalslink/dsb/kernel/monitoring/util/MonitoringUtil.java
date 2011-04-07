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
package org.petalslink.dsb.kernel.monitoring.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;

/**
 * @author aruffie - EBM WebSourcing
 *
 */
public class MonitoringUtil {
    public static String splitPattern(final URI uri){
        final String[] splittedPattern = uri.toString().split("/");
        if(splittedPattern != null){
            return splittedPattern[splittedPattern.length-1];
        }
        return null;
    }
    
    public static String cloneSourceInString(final Source s) throws IOException, ClassNotFoundException{
        final StreamSource ss = (StreamSource) s;
        byte[] bytes = new byte[ss.getInputStream().available()];
        ss.getInputStream().read(bytes);
        byte[] bytes2 = ClonerHelper.clone(bytes);
        final InputStream in = new ByteArrayInputStream(bytes);
        ss.setInputStream(in);
        return new String(bytes2);
    }
    
    public static Source cloneSource(final Source s) throws IOException, ClassNotFoundException{
        final StreamSource ss = (StreamSource) s;
        byte[] bytes = new byte[ss.getInputStream().available()];
        ss.getInputStream().read(bytes);
        byte[] bytes2 = ClonerHelper.clone(bytes);
        final InputStream in = new ByteArrayInputStream(bytes);
        ss.setInputStream(in);
        final ByteArrayInputStream inCopy = new ByteArrayInputStream(bytes2);
        return new StreamSource(inCopy);
    }
    
    public static String parseToString(final Node node) throws TransformerFactoryConfigurationError, TransformerException{
        String result = null;
        if(node != null){
            node.normalize();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(node), new StreamResult(stringWriter));
            StringBuffer buffer = stringWriter.getBuffer();
            result = buffer.toString();
        }
        return result;
    }
    /*
    private static DOMSource[] cloneSource(final DOMSource source) throws TransformerFactoryConfigurationError, IOException, TransformerException, ClassNotFoundException, ParserConfigurationException, SAXException{
        
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final StreamResult streamResult = new StreamResult(os);
        final Transformer transformer = 
            TransformerFactory.newInstance().newTransformer();
        transformer.transform(source, streamResult);
        os.flush();
        os.close();
        final InputStream is = new java.io.ByteArrayInputStream(os.toByteArray());
        final StreamSource attach = new StreamSource(is);
        final StreamSource ss = (StreamSource) cloneSource(attach);
        
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(ss.getInputStream());
        document.normalize();
        DOMSource domSource = new DOMSource();
        domSource.setNode(document);
        return new Source[]{domSource,attach};
    }*/
}
