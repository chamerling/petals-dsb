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

package org.ow2.petals.binding.soap.listener.outgoing;

import java.util.logging.Logger;

import org.ow2.petals.binding.soap.SoapComponent;
import org.ow2.petals.binding.soap.SoapComponentContext;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.listener.AbstractJBIListener;

import com.ebmwebsourcing.easycommons.logger.Level;

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
     * The SOAP caller
     */
    private SOAPCaller soapCaller;

    @Override
    public void init() {
        SoapComponentContext soapContext = ((SoapComponent) getComponent()).getSoapContext();
        logger = getLogger();
        soapCaller = new SOAPCaller(soapContext, logger);
    }

    /**
     * Called by the JBI channel listener when a jbi message is accepted.
     * 
     * @param exchange
     * @param extensions
     */
    @Override
    public boolean onJBIMessage(final Exchange exchange) {
        if (exchange.isActiveStatus()) {
            if (exchange.isProviderRole()) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "JBI message received on SOAP JBI listener");
                }

                ConfigurationExtensions extensions = getExtensions();
                soapCaller.call(exchange, extensions, getProvides());
            } else {
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.INFO,
                            "Role not supported in SOAP JBIListener : " + exchange.getRole());
                }
            }
        }
        return true;
    }
}
