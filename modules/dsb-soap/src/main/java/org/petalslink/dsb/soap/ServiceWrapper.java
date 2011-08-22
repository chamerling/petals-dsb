/**
 * 
 */
package org.petalslink.dsb.soap;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Provider;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.handler.MessageContext;

import org.petalslink.dsb.saaj.utils.SOAPMessageUtils;
import org.petalslink.dsb.soap.api.Service;
import org.petalslink.dsb.soap.api.ServiceException;
import org.petalslink.dsb.soap.api.SimpleExchange;
import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
@WebServiceProvider
@ServiceMode(value = javax.xml.ws.Service.Mode.MESSAGE)
public class ServiceWrapper implements Provider<SOAPMessage> {

    @Resource
    WebServiceContext wsContext;

    private Service service;

    private static Logger logger = Logger.getLogger(ServiceWrapper.class.getName());

    /**
     * 
     */
    public ServiceWrapper(Service service) {
        this.service = service;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.ws.Provider#invoke(java.lang.Object)
     */
    public SOAPMessage invoke(SOAPMessage request) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Invoking a service from request");
            logger.fine("PAYLOAD is " + SOAPMessageUtils.getSOAPMessageAsString(request));
        }

        SimpleExchange exchange = new Exchange();
        try {
            exchange.setIn(getIn(request));
        } catch (SOAPException e1) {
            // TODO : return exception in the return message...
            return null;
        }

        exchange.setOperation(getOperation());
        try {
            service.invoke(exchange);
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        try {
            return createResponse(exchange);
        } catch (SOAPException e) {
            // TODO = set the exception as the return message...
            return null;
        }
    }

    /**
     * @return
     */
    private QName getOperation() {
        QName result = null;
        if (wsContext != null && wsContext.getMessageContext() != null) {
            Object o = wsContext.getMessageContext().get(MessageContext.WSDL_OPERATION);
            if (o != null && o instanceof QName) {
                result = (QName) o;
            }
        }
        return result;
    }

    /**
     * @param exchange
     * @return
     * @throws SOAPException
     */
    private SOAPMessage createResponse(SimpleExchange exchange) throws SOAPException {
        // TODO : get the version from the incoming soap message...
        SOAPMessage result = null;
        if (exchange.getFault() != null) {
            // TODO
        } else {
            result = SOAPMessageUtils.createSOAPMessageFromBodyContent(exchange.getOut());
        }
        return result;
    }

    /**
     * @param request
     * @return
     * @throws SOAPException
     */
    private Document getIn(SOAPMessage request) throws SOAPException {
        return SOAPMessageUtils.getBodyFromMessage(request);
    }

    /**
     * @return the service
     */
    public Service getService() {
        return service;
    }

}
