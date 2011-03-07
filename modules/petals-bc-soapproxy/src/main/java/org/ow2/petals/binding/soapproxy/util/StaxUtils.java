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
package org.ow2.petals.binding.soapproxy.util;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import org.apache.axiom.om.util.StAXUtils;

/**
 * StAX utilities
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since 3.0
 * 
 */
public class StaxUtils extends StAXUtils {

    public static XMLStreamReader createXMLStreamReader(final Source in) throws XMLStreamException {
        final XMLInputFactory inputFactory = getXMLInputFactory();
        try {
            final XMLStreamReader reader = inputFactory.createXMLStreamReader(in);
            return reader;
        } finally {
            releaseXMLInputFactory(inputFactory);
        }
    }
}
