/**
 * 
 */
package org.petalslink.dsb.notification.commons;

import javax.xml.parsers.DocumentBuilderFactory;

import com.ebmwebsourcing.easybox.api.XmlContext;
import com.ebmwebsourcing.easybox.api.XmlContextFactory;
import com.ebmwebsourcing.easybox.api.XmlObjectFactory;
import com.ebmwebsourcing.easybox.api.XmlObjectReader;
import com.ebmwebsourcing.easybox.api.XmlObjectWriter;

/**
 * @author chamerling
 * 
 */
public class SOAUtil {
    private DocumentBuilderFactory domBuilder = null;

    private XmlContext xmlContext = null;

    private XmlObjectFactory xmlObjectFactory = null;

    private ThreadLocal<XmlObjectWriter> xmlwriter = null;

    private ThreadLocal<XmlObjectReader> xmlreader = null;

    private static SOAUtil INSTANCE = null;

    private SOAUtil() {
        xmlContext = new XmlContextFactory().newContext();
        xmlObjectFactory = xmlContext.getXmlObjectFactory();
        xmlwriter = new ThreadLocal<XmlObjectWriter>() {
            protected XmlObjectWriter initialValue() {
                return xmlContext.createWriter();
            }
        };
        xmlreader = new ThreadLocal<XmlObjectReader>() {
            protected XmlObjectReader initialValue() {
                return xmlContext.createReader();
            }
        };

        domBuilder = DocumentBuilderFactory.newInstance();
        domBuilder.setNamespaceAware(true);
    }

    public static SOAUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SOAUtil();
        }
        return INSTANCE;
    }

    public ThreadLocal<XmlObjectWriter> getWriter() {
        return xmlwriter;
    }

    public ThreadLocal<XmlObjectReader> getReader() {
        return xmlreader;
    }

    public XmlContext getXmlContext() {
        return xmlContext;
    }

    public XmlObjectFactory getXmlObjectFactory() {
        return xmlObjectFactory;
    }

    public DocumentBuilderFactory getDocumentBuilderFactory() {
        return domBuilder;
    }

}
