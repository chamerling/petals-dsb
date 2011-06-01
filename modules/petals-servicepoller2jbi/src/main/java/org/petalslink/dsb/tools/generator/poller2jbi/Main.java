/**
 * 
 */
package org.petalslink.dsb.tools.generator.poller2jbi;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.ow2.petals.tools.generator.commons.Constants;
import org.ow2.petals.tools.generator.jbi.api.JBIGenerationException;
import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public class Main {

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {

        File f = new File(".");
        System.out.println(f);
        System.out.println(Main.class.getResource("/input.xml"));
        File xml = new File(Main.class.getResource("/input.xml").toURI());
        System.out.println(xml);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(xml);
        document.getDocumentElement().normalize();
        
        Map<String, String> extensions = new HashMap<String, String>();
        extensions.put(Constants.COMPONENT_VERSION, "1.0");
        Poller2Jbi generator = new Poller2Jbi("HelloServicePort",
                QName.valueOf("{http://api.ws.dsb.petalslink.org/}HelloService"),
                QName.valueOf("{http://api.ws.dsb.petalslink.org/}HelloServiceService"),
                QName.valueOf("sayHello"), document, "ToEnpoint",
                QName.valueOf("{http://petals.ow2.org}ToInterface"),
                QName.valueOf("{http://petals.ow2.org}ToService"),
                QName.valueOf("{http://petals.ow2.org}ToOperation"), "* * * * * ?", extensions);
        try {
            generator.generate();
        } catch (JBIGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
