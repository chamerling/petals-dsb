/**
 * 
 */
package org.petalslink.dsb.servicepoller.api;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author chamerling
 * 
 */
public class ServicePollerServiceImpl implements ServicePollerService {

    private ServicePoller bean;

    /**
     * 
     */
    public ServicePollerServiceImpl(ServicePoller bean) {
        this.bean = bean;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.servicepoller.api.ServicePollerService#start(java.
     * lang.String, javax.xml.namespace.QName, javax.xml.namespace.QName,
     * javax.xml.namespace.QName,
     * org.petalslink.dsb.servicepoller.api.DocumentHandler)
     */
    public void start(String endpointName, QName service, QName itf, QName operation,
            DocumentHandler inputMessage) {
        if (bean != null) {
            // transform the input message as DOM
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document document = dBuilder.parse(inputMessage.getDom().getInputStream());
                document.getDocumentElement().normalize();
                bean.start(endpointName, service, itf, operation, document);
            } catch (ParserConfigurationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SAXException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.servicepoller.api.ServicePollerService#stop(java.lang
     * .String, javax.xml.namespace.QName, javax.xml.namespace.QName,
     * javax.xml.namespace.QName)
     */
    public void stop(String endpointName, QName service, QName itf, QName operation) {
        if (bean != null) {
            bean.stop(endpointName, service, itf, operation);
        }
    }
}
