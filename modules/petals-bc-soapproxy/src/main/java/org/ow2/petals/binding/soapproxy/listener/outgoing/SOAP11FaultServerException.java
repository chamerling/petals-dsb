/**
 * PETALS - PETALS Services Platform. Copyright (c) 2007 OW2 Consortium,
 * http://www.ow2.org/
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
 * $Id: $
 * -------------------------------------------------------------------------
 */
package org.ow2.petals.binding.soapproxy.listener.outgoing;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * 
 */
public class SOAP11FaultServerException extends
        org.ow2.petals.component.framework.api.exception.SOAP11FaultServerException {

    private static final long serialVersionUID = 5737575556293686164L;

    /**
     * Creates a new instance of {@link SOAP11FaultServerException}
     * 
     * @param faultString
     * @param externalWS
     * @throws URISyntaxException
     */
    public SOAP11FaultServerException(final String faultString, final String externalWS)
            throws URISyntaxException {
        super(faultString, new URI("http://petals.ow2.org/bc-soap/" + externalWS));
    }

    /**
     * Creates a new instance of {@link SOAP11FaultServerException}
     * 
     * @param faultString
     * @param externalWS
     * @throws URISyntaxException
     */
    public SOAP11FaultServerException(final String faultString) throws URISyntaxException {
        super(faultString, new URI("http://petals.ow2.org/bc-soap/"));
    }

}
