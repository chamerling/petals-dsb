/**
 * PETALS - PETALS Services Platform. Copyright (c) 2006 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * -------------------------------------------------------------------------
 * $Id: JBIListener.java 154 27 sept. 06 alouis $
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soapproxy.listener.outgoing;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.messaging.MessagingException;

import org.ow2.petals.binding.soapproxy.SoapComponent;
import org.ow2.petals.binding.soapproxy.SoapComponentContext;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.listener.AbstractJBIListener;

/**
 * Listener for incoming JBI messages. A SOAP message is created from the JBI
 * one.
 * 
 * @author alouis - EBM Websourcing
 * @author Christophe Hamerling - EBM Websourcing
 * 
 */
public class JBIListener extends AbstractJBIListener {

    /**
     * The logger
     */
    protected Logger logger;

    /**
     * The SOAP Component Context
     */
    protected SoapComponentContext soapContext;

    /**
     * Map of service callers. One instance of each caller is available on each
     * worker.
     */
    protected Map<String, AbstractExternalServiceCaller> callers;

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.component.framework.listener.AbstractListener#init()
     */
    @Override
    public void init() {
        this.soapContext = ((SoapComponent) this.getComponent()).getSoapContext();
        this.logger = this.getLogger();
        this.callers = new HashMap<String, AbstractExternalServiceCaller>(1);
        this.initCallers();
    }

    /**
     * Instantiate and initialize the service callers.
     * 
     */
    protected void initCallers() {
        this.addCaller(new SOAPCaller(this.soapContext, this.logger));
    }

    /**
     * 
     * @param dispatcher
     */
    protected void addCaller(final AbstractExternalServiceCaller caller) {
        this.callers.put(caller.getCallerType().toLowerCase(), caller);
    }

    /**
     * Called by the JBI channel listener when a jbi message is accepted. The
     * call is dispatched like this :
     * <ul>
     * <li>If the extensions contains a topic-name property, the message will be
     * published in the topic</li>
     * <li>If there is no topic-name extension property, the JBI message is sent
     * to an external web service. The external web service address is the one
     * defined in the adress extension</li>
     * </ul>
     * 
     * @param exchange
     * @param extensions
     */
    @Override
    public boolean onJBIMessage(final Exchange exchange) {
        if (exchange.isProviderRole()) {
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("JBI message received on SOAP JBI listener");
            }
            this.invokeCaller(exchange);
        } else {
            this.logger.info("Role not supported in SOAP JBIListener : " + exchange.getRole());
        }

        // FIXME : What is the return value for?
        return true;
    }

    /**
     * Dispatch the outgoing WS call. The good dispatcher is retrieved from the
     * MODE parameter if it exists. If no valid dispatcher has been found for
     * the specified MODE, the SOAP one is used.
     * 
     * @param exchange
     * @param extensions
     * @param address
     */
    protected void invokeCaller(final Exchange exchange) {
        final ConfigurationExtensions extensions = this.getExtensions();
        final String mode = "SOAP";

        // Possible feature : Create a caller from a user class... The caller
        // will be
        // loaded here and added to the callers list.
        final ExternalServiceCaller caller = this.callers.get(mode.toLowerCase());

        if (caller == null) {
            // should never happen...

            try {
                exchange.setFault(new SOAP11FaultServerException("No outgoing dispatcher found"));
            } catch (final URISyntaxException e) {
                this.logger.log(Level.SEVERE, "Can't create SOAP11FaultServerException", e);
            } catch (final MessagingException e) {
                this.logger.log(Level.SEVERE, "Can't return fault to consumer", e);
            }
        }

        if (caller != null) {
            caller.call(exchange, extensions, this.getProvides());
        }
    }
}
