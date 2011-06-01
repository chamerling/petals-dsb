/**
 * 
 */
package org.petalslink.dsb.servicepoller.client;

import java.io.ByteArrayOutputStream;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.petalslink.dsb.servicepoller.api.ByteDataSource;
import org.petalslink.dsb.servicepoller.api.DocumentHandler;
import org.petalslink.dsb.servicepoller.api.ServicePoller;
import org.petalslink.dsb.servicepoller.api.ServicePollerService;
import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public class ServicePollerClient implements ServicePoller {

    ServicePollerService client;

    String address;

    /**
     * 
     */
    public ServicePollerClient(String address) {
        this.address = address;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.servicepoller.api.ServicePoller#start(java.lang.String
     * , javax.xml.namespace.QName, javax.xml.namespace.QName,
     * javax.xml.namespace.QName,
     * org.petalslink.dsb.servicepoller.api.DocumentHandler)
     */
    public void start(String endpointName, QName service, QName itf, QName operation,
            Document inputMessage) {
        Source source = new DOMSource(inputMessage);
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
        getWSClient().start(endpointName, service, itf, operation, data);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.servicepoller.api.ServicePoller#stop(java.lang.String,
     * javax.xml.namespace.QName, javax.xml.namespace.QName,
     * org.petalslink.dsb.servicepoller.api.DocumentHandler)
     */
    public void stop(String endpointName, QName service, QName itf, QName operation) {
        getWSClient().stop(endpointName, service, itf, operation);
    }

    private synchronized ServicePollerService getWSClient() {
        if (client == null) {
            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
            factory.setAddress(address);
            factory.setServiceClass(ServicePollerService.class);
            client = (ServicePollerService) factory.create();
        }
        return this.client;
    }

}
