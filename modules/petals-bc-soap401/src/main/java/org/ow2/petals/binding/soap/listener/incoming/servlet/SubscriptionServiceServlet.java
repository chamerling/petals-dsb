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

package org.ow2.petals.binding.soap.listener.incoming.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultValue;
import org.ow2.petals.ws.notification.WsnIsolationLayer;

/**
 * Servlet used to handle incoming WSN calls. The incoming calls are
 * subscription to WS notifications. They are handled by the WSN isolation
 * layer.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class SubscriptionServiceServlet extends HttpServlet {

    private static final long serialVersionUID = 15121978L;

    /**
     * The WSN isolation layer
     */
    private final WsnIsolationLayer isolationLayer;

    /**
     * The logger
     */
    private final Logger logger;

    /**
     * Creates a new instance of {@link SubscriptionServiceServlet}
     * 
     * @param
     * @param
     */
    public SubscriptionServiceServlet(final WsnIsolationLayer isolation, final Logger logger) {
        super();
        this.isolationLayer = isolation;
        this.logger = logger;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType(SOAP12Constants.SOAP_12_CONTENT_TYPE);
        this.logger.fine("Receiving a new request on WSN subscription service");
        try {
            OMElement reply = this.isolationLayer.handleRequest(request);
            if (reply == null) {
                reply = this.createFaultResponse("No response from WSN manager");
            }
            this.sendResponse(reply, response);

        } catch (final XMLStreamException e) {
            final String message = "Got an exception on XML serialization";
            throw new ServletException(message, e);
        } catch (final Exception e) {
            final String message = "Got an exception WSN subscription handling";
            throw new ServletException(message, e);
        } finally {
            response.getOutputStream().flush();
        }
    }

    /**
     * Handle an exception. A WS subscription error will be sent to the WSN
     * client.
     * 
     * @param message
     * @return a {@link SOAPEnvelope} with the SOAP fault
     */
    protected SOAPEnvelope createFaultResponse(final String message) {
        final SOAPFactory factory = OMAbstractFactory.getSOAP12Factory();
        final SOAPEnvelope envelope = factory.createSOAPEnvelope();
        final SOAPBody body = factory.createSOAPBody(envelope);
        envelope.addChild(body);
        final SOAPFault fault = factory.createSOAPFault(body);

        final SOAPFaultCode code = factory.createSOAPFaultCode(fault);
        final SOAPFaultValue value = factory.createSOAPFaultValue(code);
        value.setText(SOAP12Constants.FAULT_CODE_RECEIVER);
        code.setValue(value);
        fault.setCode(code);

        final SOAPFaultReason reason = factory.createSOAPFaultReason(fault);
        reason.setText(message);
        fault.setReason(reason);
        body.addFault(fault);

        return envelope;
    }

    /**
     * Send the response
     * 
     * @param soapResponse
     * @param httpResponse
     * @throws ServletException
     */
    protected void sendResponse(final OMElement soapResponse, final HttpServletResponse httpResponse)
            throws XMLStreamException, IOException {
        soapResponse.serialize(httpResponse.getOutputStream());
    }
}
