/**
 * 
 */
package org.petalslink.dsb.wsn.utils;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.DSBHelper;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.NotifyImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.SubscribeImpl;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.Notify;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

/**
 * @author chamerling
 * 
 */
public class Adapters {

    public static com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe asModel(
            Subscribe subscribe) throws WsnbException {
        return DSBHelper.asModel(subscribe);
    }

    public static com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify asModel(
            Notify notify) {
        return DSBHelper.asModel(notify);
    }

    public static com.ebmwebsourcing.wsstar.jaxb.notification.base.Notify asJAXB(
            com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify payload)
            throws WsnbException, JAXBException {
        return NotifyImpl.toJaxbModel(payload);
    }

    public static Document asDOM(
            com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify notify)
            throws WsnbException {
        return Wsnb4ServUtils.getWsnbWriter().writeNotifyAsDOM(notify);
    }

    public static Subscribe asJAXB(
            com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe subscribe)
            throws JAXBException, WsnbException {
        return SubscribeImpl.toJaxbModel(subscribe);
    }

    public static Document asDOM(
            com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Subscribe subscribe)
            throws WsnbException {
        return Wsnb4ServUtils.getWsnbWriter().writeSubscribeAsDOM(subscribe);
    }
    
    public static Document fromStreamToDocument(InputStream stream){

        Document result = null;
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();  
            Document parsedXmlDoc = builder.parse(stream);
            result = parsedXmlDoc;
        }catch(ParserConfigurationException pce){
            pce.printStackTrace();
        }catch(SAXException se){
            se.printStackTrace();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        return result;
    }
}
