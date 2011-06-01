/**
 * 
 */
package org.petalslink.dsb.servicepoller.api;

import java.io.ByteArrayOutputStream;

import javax.activation.DataHandler;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public class Utils {

    public static final DocumentHandler toDataHandler(Document document) {
        Source source = new DOMSource(document);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            TransformerFactory.newInstance().newTransformer()
                    .transform(source, new StreamResult(outputStream));
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        }
        DocumentHandler data = new DocumentHandler();
        data.setDom(new DataHandler(new ByteDataSource(outputStream.toByteArray())));
        return data;
    }

}
