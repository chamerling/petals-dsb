package org.petalslink.dsb.wsn;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.petalslink.dsb.wsn.api.NotificationProducerService;
import org.petalslink.dsb.wsn.service.NotificationProducerServiceService;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.ebmwebsourcing.wsstar.addressing.datatypes.impl.impl.WsaModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basefaults.datatypes.impl.impl.WsrfbfModelFactoryImpl;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.impl.WsnbModelFactoryImpl;
import com.ebmwebsourcing.wsstar.jaxb.addressing.EndpointReferenceType;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.FilterType;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe;
import com.ebmwebsourcing.wsstar.resource.datatypes.impl.impl.WsrfrModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourcelifetime.datatypes.impl.impl.WsrfrlModelFactoryImpl;
import com.ebmwebsourcing.wsstar.resourceproperties.datatypes.impl.impl.WsrfrpModelFactoryImpl;
import com.ebmwebsourcing.wsstar.topics.datatypes.impl.impl.WstopModelFactoryImpl;
import com.ebmwebsourcing.wsstar.wsnb.services.impl.util.Wsnb4ServUtils;

public class TranslateTest extends TestCase {
    
    public void testTransalate() {
        Wsnb4ServUtils.initModelFactories(new WsaModelFactoryImpl(),
                new WsrfbfModelFactoryImpl(), new WsrfrModelFactoryImpl(),
                new WsrfrlModelFactoryImpl(), new WsrfrpModelFactoryImpl(),
                new WstopModelFactoryImpl(), new WsnbModelFactoryImpl());
        
        NotificationProducerService service = new NotificationProducerServiceService(null);
        
        // create the subscribe from XML file
        //Document requestAsDoc = fromStreamToDocument(TranslateTest.class.getResourceAsStream("/Subscribe.xml"));
        Subscribe request = new Subscribe();
        request.setConsumerReference(new EndpointReferenceType());
        request.setFilter(new FilterType());

        
        service.subscribe(request);
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
