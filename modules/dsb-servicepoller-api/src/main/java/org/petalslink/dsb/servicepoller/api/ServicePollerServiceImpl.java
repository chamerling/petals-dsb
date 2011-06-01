/**
 * 
 */
package org.petalslink.dsb.servicepoller.api;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

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
            DocumentHandler inputMessage) throws ServicePollerException {
        if (bean == null) {
            throw new ServicePollerException("Can not find any inner poller service implementation");
        }
        // transform the input message as DOM
        Document document = null;
        if (inputMessage != null && inputMessage.getDom() != null) {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                document = dBuilder.parse(inputMessage.getDom().getInputStream());
                document.getDocumentElement().normalize();
            } catch (Exception e) {
                throw new ServicePollerException(
                        "Can not transform input message into DOM document", e);

            }
        }
        bean.start(endpointName, service, itf, operation, document);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.servicepoller.api.ServicePollerService#stop(java.lang
     * .String, javax.xml.namespace.QName, javax.xml.namespace.QName,
     * javax.xml.namespace.QName)
     */
    public void stop(String endpointName, QName service, QName itf, QName operation)
            throws ServicePollerException {
        if (bean == null) {
            throw new ServicePollerException("Can not find any inner poller service implementation");
        }
        
        bean.stop(endpointName, service, itf, operation);
    }
}
