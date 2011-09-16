/**
 * 
 */
package org.petalslink.dsb.kernel.wsnpoller;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.petalslink.dsb.servicepoller.api.DocumentHandler;
import org.petalslink.dsb.servicepoller.api.ServicePollerException;
import org.petalslink.dsb.servicepoller.api.ServicePollerInformation;
import org.petalslink.dsb.servicepoller.api.WSNPoller;
import org.petalslink.dsb.servicepoller.api.WSNPollerService;
import org.petalslink.dsb.servicepoller.api.WSNPollerServiceInformation;
import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public class ServicePollerServiceAdapter implements WSNPollerService {

    private WSNPoller bean;

    /**
     * 
     */
    public ServicePollerServiceAdapter(WSNPoller bean) {
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
    public String start(ServicePollerInformation toPoll, DocumentHandler inputMessage,
            String cronExpression, ServicePollerInformation replyTo, String topicName,
            String topicURI, String topicPrefix) throws ServicePollerException {
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
        return bean.start(toPoll, document, cronExpression, replyTo, new QName(topicURI, topicName, topicPrefix));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.servicepoller.api.ServicePollerService#stop(java.lang
     * .String)
     */
    public boolean stop(String id) throws ServicePollerException {
        if (id == null) {
            throw new ServicePollerException("ID can not be null");
        }
        return bean.stop(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.servicepoller.api.ServicePollerService#pause(java.
     * lang.String)
     */
    public boolean pause(String id) throws ServicePollerException {
        if (id == null) {
            throw new ServicePollerException("ID can not be null");
        }
        return bean.pause(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.servicepoller.api.ServicePollerService#resume(java
     * .lang.String)
     */
    public boolean resume(String id) throws ServicePollerException {
        if (id == null) {
            throw new ServicePollerException("ID can not be null");
        }
        return bean.resume(id);
    }
    
    /* (non-Javadoc)
     * @see org.petalslink.dsb.servicepoller.api.WSNPollerService#getInformation()
     */
    public List<WSNPollerServiceInformation> getInformation() {
        return bean.getInformation();
    }
}
