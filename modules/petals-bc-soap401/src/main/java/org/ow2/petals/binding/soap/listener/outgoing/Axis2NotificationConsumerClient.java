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
 * -------------------------------------------------------------------------
 * $Id$
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soap.listener.outgoing;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jbi.messaging.MessagingException;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.ow2.petals.component.framework.api.Message.MEPConstants;
import org.ow2.petals.ws.addressing.EndpointReference;
import org.ow2.petals.ws.client.SoapClient;

import static org.ow2.petals.binding.soap.Constants.Axis2.ADDRESSING_MODULE;

/**
 * A {@link SoapClient} implementation that will be used to send Web Service
 * Notifications.
 * 
 * @author Christophe HAMERLING (chamerling) - eBM WebSourcing
 * 
 */
public class Axis2NotificationConsumerClient implements SoapClient {

    private final Logger logger;

    /**
     * Creates a new instance of {@link Axis2NotificationConsumerClient}
     * 
     */
    public Axis2NotificationConsumerClient(final Logger logger) {
        this.logger = logger;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.objectweb.petals.ws.client.SoapClient#send(org.objectweb.petals.ws
     * .addressing.EndpointReference,
     * org.objectweb.petals.ws.addressing.EndpointReference, java.lang.String,
     * org.apache.axiom.om.OMElement, org.apache.axiom.om.OMElement[])
     */
    public SOAPEnvelope send(final EndpointReference src, final EndpointReference dest,
            final String wsaAction, final OMElement body, final OMElement[] extraHeaders) {
        this.logger.fine("Sending SOAP message to " + dest.getAddress());

        final SOAPEnvelope result = null;
        ServiceClient client = null;

        try {
            // create the service client
            client = Axis2Utils.createServiceClient(MEPConstants.IN_OPTIONAL_OUT_PATTERN.value(),
                    new QName(wsaAction));

            // create options
            final Options options = new Options();
            options.setTo(new org.apache.axis2.addressing.EndpointReference(dest.getAddress()
                    .toString()));
            options.setAction(wsaAction);
            options.setFrom(new org.apache.axis2.addressing.EndpointReference(src.getAddress()
                    .toString()));
            client.setOptions(options);
            client.engageModule(ADDRESSING_MODULE);

            // send request
            final OMElement response = client.sendReceive(new QName(wsaAction), body);
            if ((response != null) && this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("Got a response : " + response.toString());
            }

        } catch (final AxisFault e) {
            this.logger.warning(e.getMessage());
        } catch (final MessagingException e) {
            this.logger.warning(e.getMessage());
        }

        return result;
    }
}
