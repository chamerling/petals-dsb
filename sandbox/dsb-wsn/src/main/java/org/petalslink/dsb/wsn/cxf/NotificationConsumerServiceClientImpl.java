/**
 * 
 */
package org.petalslink.dsb.wsn.cxf;

import java.io.PrintWriter;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.petalslink.dsb.wsn.api.NotificationConsumerService;

import com.ebmwebsourcing.wsstar.basenotification.datatypes.impl.WsnbJAXBContext;
import com.ebmwebsourcing.wsstar.jaxb.notification.base.Notify;

/**
 * A client which is in charge if sending notify to remote Web service
 * 
 * @author chamerling
 * 
 */
public class NotificationConsumerServiceClientImpl implements NotificationConsumerService {

    private NotificationConsumerService client;

    public NotificationConsumerServiceClientImpl(String address) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setDataBinding(new JAXBDataBinding(new WSNContext(WsnbJAXBContext.getInstance())));
        factory.setAddress(address);
        factory.setServiceClass(NotificationConsumerService.class);
        factory.getInInterceptors().add(new LoggingInInterceptor(new PrintWriter(System.out)));
        factory.getOutInterceptors().add(new LoggingOutInterceptor(new PrintWriter(System.out)));

        this.client = (NotificationConsumerService) factory.create();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.wsn.api.NotificationConsumerService#notify(com.
     * ebmwebsourcing.wsstar.jaxb.notification.base.Notify)
     */
    public void notify(Notify notify) {
        this.client.notify(notify);
    }

}
