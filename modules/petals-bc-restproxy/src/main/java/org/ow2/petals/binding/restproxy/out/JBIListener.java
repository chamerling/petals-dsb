/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.ow2.petals.binding.restproxy.out;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.jbidescriptor.generated.Provides;
import org.ow2.petals.component.framework.listener.AbstractJBIListener;
import org.ow2.petals.component.framework.util.XMLUtil;
import org.ow2.petals.messaging.framework.EngineFactory;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.message.MessageImpl;
import org.ow2.petals.messaging.framework.message.MessagingEngine;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class JBIListener extends AbstractJBIListener {

    private Logger logger;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        this.logger = this.getLogger();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onJBIMessage(Exchange exchange) {
        if (exchange.isProviderRole()) {
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("JBI message received on RESTPROXY JBI listener");
            }
            this.invoke(exchange);
        } else {
            this.logger.info("Role not supported in RESTPROXY JBIListener : " + exchange.getRole());
        }
        return true;
    }

    /**
     * @param exchange
     */
    protected void invoke(Exchange exchange) {
        NormalizedMessage in = exchange.getInMessage();
        if (in == null) {
            this.handleError(exchange, "MessageImpl exchange must handle an IN normalized message");
        } else {
            String address = null;
            // get the information from the service unit if any...
            Provides provides = this.getProvides();
            if (provides != null) {
                ConfigurationExtensions extensions = this.getExtensions();
                if (extensions != null) {
                    this.logger.fine("Extensions are not null, getting address");
                    address = extensions.get("address");
                } else {
                    this.logger.fine("Extensions are null");
                }
            } else {
                this.logger.fine("This is not a listener which has been activated from a SU");
            }

            Message inMessage = new MessageImpl();
            if (in.getPropertyNames() != null) {
                for (Object o : in.getPropertyNames()) {
                    String key = o.toString();
                    inMessage.put(key, in.getProperty(key).toString());
                }
            }

            QName operation = exchange.getOperation();
            inMessage.put(org.ow2.petals.messaging.framework.message.Constants.HTTP_METHOD,
                    operation.getLocalPart());

            Object o = in
                    .getProperty(org.ow2.petals.messaging.framework.message.Constants.HTTP_URL);
            if (o != null) {
                if (address == null) {
                    // proxy
                    address = o.toString();
                } else {
                    // service invoke
                    address = address.trim() + "/" + o.toString().trim();
                }
            }

            try {
                if (this.logger.isLoggable(Level.INFO)) {
                    this.logger.log(Level.INFO, "Let's call REST service at : " + address);
                }
                inMessage.setContent(Source.class, in.getContent());
                inMessage.put(org.ow2.petals.messaging.framework.message.Constants.HTTP_URL,
                        new URL(address));

                // FIXME = This should be set in the MessagingEngine
                // context!
                inMessage
                        .put(org.ow2.petals.messaging.framework.message.Constants.PROTOCOL, "http");

                MessagingEngine engine = EngineFactory.getEngine().getComponent(MessagingEngine.class);
                Message result = null;
                if (engine != null) {
                    result = engine.send(inMessage);
                }

                if (result != null) {
                    if (result.getContent(Source.class) != null) {
                        exchange.setOutMessageContent(result.getContent(Source.class));
                    } else {
                        // if content is null we cannot get properties on
                        // the consumer side... Probably a bug
                        exchange.setOutMessageContent(XMLUtil.createDocumentFromString("<empty/>"));
                    }
                    exchange.setOutMessageProperties(result.getAll());
                } else {
                    // TODO!
                    // fill empty return data...
                }
            } catch (Exception e) {
                this.handleException(exchange, e);
            }
        }
    }

    /**
     * @param string
     * @param exchange
     */
    protected void handleError(final Exchange exchange, final String exception) {
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.log(Level.FINE, "Handling Error", exception);
        }
        String message = null;

        if (exception == null) {
            message = "The exception generated by the component is unknown";
        }
        exchange.setError(new MessagingException("REST PROXY Error => " + message));
    }

    protected void handleException(final Exchange exchange, final Exception exception) {
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.log(Level.FINE, "Handling Exception", exception);
        }
        String message = null;

        message = exception.getMessage();
        if ((message == null) && (exception.getCause() != null)) {
            message = exception.getCause().getMessage();
        }
        if (message == null) {
            message = "The exception generated by the component is unknown";
        }
        exchange.setError(new MessagingException("REST PROXY Error => " + message));
    }

}
