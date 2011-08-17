/**
 * 
 */
package org.petalslink.dsb.soap;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.petalslink.dsb.soap.api.Service;
import org.petalslink.dsb.soap.api.ServiceException;
import org.petalslink.dsb.soap.api.SimpleExchange;

/**
 * @author chamerling
 * 
 */
public abstract class AbstractService implements Service {

    private static Logger logger = Logger.getLogger(AbstractService.class.getName());

    protected QName interfaceName;

    protected QName serviceName;

    protected QName endpointName;

    protected String wsdl;

    protected String url;

    public AbstractService(QName interfaceName, QName serviceName, QName endpointName, String wsdl,
            String url) {
        super();
        this.interfaceName = interfaceName;
        this.serviceName = serviceName;
        this.endpointName = endpointName;
        this.wsdl = wsdl;
        this.url = url;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.Service#getWSDLURL()
     */
    public String getWSDLURL() {
        return wsdl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.Service#getURL()
     */
    public String getURL() {
        return url;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.Service#getEndpoint()
     */
    public QName getEndpoint() {
        return endpointName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.Service#getInterface()
     */
    public QName getInterface() {
        return interfaceName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.Service#getService()
     */
    public QName getService() {
        return serviceName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.soap.api.Service#invoke(org.petalslink.dsb.soap.api
     * .SimpleExchange)
     */
    public final void invoke(SimpleExchange exchange) throws ServiceException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Got an invoke on service");
        }

        if (exchange == null) {
            final String message = "Exchange is null and should not...";
            logger.warning(message);
            throw new ServiceException(message);
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Operation is " + exchange.getOperation());
        }

        pre(exchange);
        doInvoke(exchange);
        post(exchange);
    }

    /**
     * @param exchange
     */
    protected void post(SimpleExchange exchange) throws ServiceException {
    }

    /**
     * Need to be implemented as the method which processes the incoming
     * message. At this step, the exchange is not null but inner values may
     * be...
     * 
     * @param exchange
     */
    protected abstract void doInvoke(SimpleExchange exchange) throws ServiceException;

    /**
     * @param exchange
     */
    protected void pre(SimpleExchange exchange) throws ServiceException {
    }

}
