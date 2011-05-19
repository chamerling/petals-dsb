/**
 * 
 */
package org.petalslink.dsb.wsn.cxf;

import java.io.PrintWriter;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.petalslink.dsb.wsn.api.NotificationConsumerService;
import org.petalslink.dsb.wsn.api.NotificationConsumerServiceStr;

import com.ebmwebsourcing.wsstar.jaxb.notification.base.Notify;

/**
 * A client which is in charge if sending notify to remote Web service
 * 
 * @author chamerling
 * 
 */
public class NotificationConsumerServiceClientStrImpl implements NotificationConsumerServiceStr {

    private NotificationConsumerServiceStr client;

    public NotificationConsumerServiceClientStrImpl(String address) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setAddress(address);
        factory.setServiceClass(NotificationConsumerServiceStr.class);
        factory.getInInterceptors().add(new LoggingInInterceptor(new PrintWriter(System.out)));
        factory.getOutInterceptors().add(new LoggingOutInterceptor(new PrintWriter(System.out)));
        this.client = (NotificationConsumerServiceStr) factory.create();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.wsn.api.NotificationConsumerService#notify(com.
     * ebmwebsourcing.wsstar.jaxb.notification.base.Notify)
     */
    public void notify(String notify) {
        this.client.notify(notify);
    }

}
