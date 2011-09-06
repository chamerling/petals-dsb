/**
 * PETALS - PETALS Services Platform. Copyright (c) 2007 EBM Websourcing,
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
 */

package org.petalslink.dsb.jbi.se.wsn.listeners;

import java.util.logging.Level;

import javax.jbi.messaging.NormalizedMessage;

import org.ow2.petals.component.framework.api.exception.PEtALSCDKException;
import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.util.UtilFactory;
import org.petalslink.dsb.jbi.se.wsn.Component;
import org.petalslink.dsb.jbi.se.wsn.NotificationEngine;
import org.w3c.dom.Document;

/**
 * @author
 * 
 */
public class JBIListener extends NotificationV2JBIListener {

    /**
     * Creates a new instance of {@link JBIListener}
     * 
     */
    public JBIListener() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.component.framework.listener.AbstractJBIListener
     */
    public boolean onJBIMessage(final Exchange exchange) {
        NotificationEngine engine = getNotificationEngine();

        try {
            if (exchange.isActiveStatus()) {
                if (exchange.getFault() != null) {

                    if (this.getLogger().isLoggable(Level.WARNING)) {
                        if (UtilFactory.getExchangeUtil().isPetalsException(exchange.getFault())) {
                            this.getLogger().warning(
                                    "notification technical fault message content: "
                                            + UtilFactory.getSourceUtil().createString(
                                                    exchange.getFault().getContent()));
                        } else {
                            this.getLogger().warning(
                                    "notification business fault message content: "
                                            + UtilFactory.getSourceUtil().createString(
                                                    exchange.getFault().getContent()));
                        }
                    }

                } else {
                    System.out.println("Got a message = " + exchange.getOperationName());
                    NormalizedMessage normalizedMessage = exchange.getInMessage();
                    Document document = UtilFactory.getSourceUtil().createDocument(
                            normalizedMessage.getContent());
                    org.petalslink.dsb.soap.Exchange e = new org.petalslink.dsb.soap.Exchange();
                    e.setIn(document);
                    engine.getServiceEngine().invoke(e);
                    if (e.getOut() != null) {
                        normalizedMessage = exchange.getOutMessage();
                        normalizedMessage.setContent(UtilFactory.getSourceUtil()
                                .createStreamSource(document));
                        exchange.setOutMessage(normalizedMessage);
                    }
                    if (e.getFault() != null) {
                        // TODO
                    }
                }
            }
        } catch (final PEtALSCDKException e) {
            exchange.setError(new Exception(e));
        } catch (Exception e) {
            exchange.setError(new Exception(e));
        }
        // manager resource properties related stuff...
        return true;
    }

    NotificationEngine getNotificationEngine() {
        return ((Component) getComponent()).getNotificationEngine();
    }

}
