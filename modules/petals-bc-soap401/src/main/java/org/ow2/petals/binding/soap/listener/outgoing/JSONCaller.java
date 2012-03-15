/**
 * PETALS - PETALS Services Platform. Copyright (c) 2008 EBM Websourcing,
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

import java.net.URI;
import java.util.logging.Logger;

import javax.jbi.messaging.MessagingException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.Constants;
import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;
import org.ow2.petals.binding.soap.SoapComponentContext;
import org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions;
import org.ow2.petals.component.framework.api.exception.PEtALSCDKException;

import static org.ow2.petals.binding.soap.Constants.ServiceUnit.MODE.JSON;

/**
 * A caller for JSON services
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * 
 */
public class JSONCaller extends RESTCaller {

    /**
     * Creates a new instance of JSONCaller
     * 
     * @param soapContext
     * @param logger
     */
    public JSONCaller(final SoapComponentContext soapContext, final Logger logger) {
        super(soapContext, logger);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.binding.soap.listener.outgoing.ExternalServiceCaller#getCallerType()
     */
    public String getCallerType() {
        return JSON;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.binding.soap.listener.outgoing.RESTCaller#createOptions(org.ow2.petals.component.framework.api.configuration.ConfigurationExtensions,
     *      java.lang.String, java.net.URI)
     */
    @Override
    protected Options createOptions(ConfigurationExtensions extensions, String httpMethod, URI epr) {
        Options options = super.createOptions(extensions, httpMethod, epr);
        options.setProperty(Constants.Configuration.MESSAGE_TYPE,
                HTTPConstants.MEDIA_TYPE_X_WWW_FORM);
        return options;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.binding.soap.listener.outgoing.RESTCaller#buildMessageBody(javax.xml.namespace.QName,
     *      org.apache.axiom.om.OMElement)
     */
    @Override
    protected OMElement buildMessageBody(final QName operation, final OMElement source)
            throws PEtALSCDKException, XMLStreamException, MessagingException {
        return source;
    }
}
