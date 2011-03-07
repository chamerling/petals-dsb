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
package org.petalslink.dsb.api.util;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.MessageExchangeException;
import org.petalslink.dsb.api.ServiceEndpoint;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class JAXBServiceEndpointBuilder {
    private static Marshaller marshaller;

    // private static MessageExchangeException marshallerEx;

    private static Unmarshaller unmarshaller;

    // private static MessageExchangeException unmarshallerEx;

    static {
        try {
            final JAXBContext jaxbContext = JAXBContext
                    .newInstance(new Class[] { ServiceEndpoint.class });

            unmarshaller = jaxbContext.createUnmarshaller();
            // unmarshaller.setSchema(schema);

            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT,
                    java.lang.Boolean.TRUE);

            // TopologyBuilder.marshaller.setSchema(schema);

        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Serialize the message exchange in the {@link OutputStream}
     * 
     * @param messageExchange
     * @param os
     */
    public static final void marshall(ServiceEndpoint serviceEndpoint, OutputStream os)
            throws MessageExchangeException {
        if ((serviceEndpoint != null) && (os != null)) {
            try {
                JAXBElement<ServiceEndpoint> element = new JAXBElement<ServiceEndpoint>(new QName(
                        "org.petalslink.dsb.api", "ServiceEndpoint"), ServiceEndpoint.class, null,
                        serviceEndpoint);
                marshaller.marshal(element, os);
            } catch (JAXBException ex) {
                throw new MessageExchangeException(
                        "Can not marshall the message to the output stream", ex);
            }
        } else {
            throw new MessageExchangeException("Message exchnage of output stream is/are null");
        }
    }

    /**
     * Read the {@link InputStream} to build a {@link MessageExchange}
     * 
     * @param is
     * @return
     * @throws MessageExchangeException
     */
    public static final ServiceEndpoint unmarshall(InputStream is) throws MessageExchangeException {
        ServiceEndpoint m = null;
        if (is != null) {
            try {
                final JAXBElement<ServiceEndpoint> root;
                // The default Xerces unmarshaller is not thread safe
                synchronized (unmarshaller) {
                    root = unmarshaller.unmarshal(new StreamSource(is), ServiceEndpoint.class);
                }
                m = root.getValue();
            } catch (Exception e) {
                throw new MessageExchangeException(
                        "Can not unmarshall the message from the input stream", e);
            }
        } else {
            throw new MessageExchangeException("Input stream is null!");
        }
        return m;
    }
}
