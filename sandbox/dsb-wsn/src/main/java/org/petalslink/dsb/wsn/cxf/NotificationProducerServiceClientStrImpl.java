/**
 * 
 */
package org.petalslink.dsb.wsn.cxf;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.petalslink.dsb.wsn.api.NotificationProducerServiceStr;

/**
 * A client which is in charge of sending Producer related messages to the remote Web service
 * 
 * @author chamerling
 * 
 */
public class NotificationProducerServiceClientStrImpl implements NotificationProducerServiceStr {

    private NotificationProducerServiceStr client;

    public NotificationProducerServiceClientStrImpl(String address) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        //factory.setDataBinding(new JAXBDataBinding(new WSNContext(WsnbJAXBContext.getInstance())));
        factory.setAddress(address);
        factory.setServiceClass(NotificationProducerServiceStr.class);
        Logger LOG = LogUtils.getL7dLogger(LoggingInInterceptor.class);
        LOG.setLevel(Level.INFO);
        LOG = LogUtils.getL7dLogger(LoggingOutInterceptor.class);
        LOG.setLevel(Level.INFO);

        factory.getInInterceptors().add(new LoggingInInterceptor(new PrintWriter(System.out)));
        factory.getOutInterceptors().add(new LoggingOutInterceptor(new PrintWriter(System.out)));
        factory.getOutInterceptors().add(new CustomOutInterceptor());

        this.client = (NotificationProducerServiceStr) factory.create();

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.wsn.api.NotificationProducerService#subscribe(com.
     * ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe)
     */
    public String subscribe(String request) {
        return client.subscribe(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.wsn.api.NotificationProducerService#getCurrentMessage
     * (com.ebmwebsourcing.wsstar.jaxb.notification.base.GetCurrentMessage)
     */
    public String getCurrentMessage(String request) {
        return client.getCurrentMessage(request);
    }

}
