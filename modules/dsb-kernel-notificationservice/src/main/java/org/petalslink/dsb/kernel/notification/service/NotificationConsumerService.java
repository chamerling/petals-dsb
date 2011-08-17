/**
 * 
 */
package org.petalslink.dsb.kernel.notification.service;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.util.LoggingUtil;
import org.petalslink.dsb.annotations.LifeCycleListener;
import org.petalslink.dsb.annotations.service.WebService;
import org.petalslink.dsb.kernel.api.DSBConfigurationService;
import org.petalslink.dsb.soap.api.Service;
import org.petalslink.dsb.soap.api.ServiceException;
import org.petalslink.dsb.soap.api.SimpleExchange;

import com.ebmwebsourcing.wsstar.wsnb.services.INotificationConsumer;

/**
 * @author chamerling
 * 
 */
@WebService
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = Service.class) })
public class NotificationConsumerService implements Service {

    private static final String WSDL = "WS-NotificationConsumer.wsdl";

    private static final String NS = "http://docs.oasis-open.org/wsn/bw-2";

    private static final QName INTERFACE = new QName(NS, "NotificationConsumer");

    private static final QName ENDPOINT = new QName(NS, "NotificationConsumerPort");

    private static final QName SERVICE = new QName(NS, "NotificationConsumerService");

    private static final String SERVICE_NAME = "NotificationConsumer";

    @Requires(name = "configuration", signature = DSBConfigurationService.class)
    private DSBConfigurationService configurationService;

    @Requires(name = "notification-consumer", signature = INotificationConsumer.class)
    private INotificationConsumer consumer;

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private org.petalslink.dsb.notification.service.NotificationConsumerService delegate;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    @LifeCycleListener
    public void init() {
        this.delegate = new org.petalslink.dsb.notification.service.NotificationConsumerService(
                INTERFACE, SERVICE, ENDPOINT, WSDL, buildURL(), consumer);
    }

    /**
     * @return
     */
    private String buildURL() {
        return configurationService.getWSKernelBaseURL() + SERVICE_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.Service#getWSDLURL()
     */
    public String getWSDLURL() {
        return getDelegate().getWSDLURL();
    }

    /**
     * @return
     */
    private Service getDelegate() {
        return this.delegate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.Service#getURL()
     */
    public String getURL() {
        return getDelegate().getURL();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.Service#getEndpoint()
     */
    public QName getEndpoint() {
        return getDelegate().getEndpoint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.Service#getInterface()
     */
    public QName getInterface() {
        return getDelegate().getInterface();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.Service#getService()
     */
    public QName getService() {
        return getDelegate().getService();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.soap.api.Service#invoke(org.petalslink.dsb.soap.api
     * .SimpleExchange)
     */
    public void invoke(SimpleExchange exchange) throws ServiceException {
        getDelegate().invoke(exchange);
    }

}
