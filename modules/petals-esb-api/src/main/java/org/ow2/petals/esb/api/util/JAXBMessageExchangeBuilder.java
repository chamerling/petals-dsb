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
package org.ow2.petals.esb.api.util;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.ow2.petals.esb.api.MessageExchange;
import org.ow2.petals.esb.api.MessageExchangeException;

/**
 * Builder for message exchanges. TODO : handle exception on marshallers
 * creation.
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class JAXBMessageExchangeBuilder {

    private static Marshaller marshaller;

    // private static MessageExchangeException marshallerEx;

    private static Unmarshaller unmarshaller;

    // private static MessageExchangeException unmarshallerEx;

    static {
        try {
            final JAXBContext jaxbContext = JAXBContext
                    .newInstance(new Class[] { MessageExchange.class });

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
    public static final void marshall(MessageExchange messageExchange, OutputStream os)
            throws MessageExchangeException {
        if ((messageExchange != null) && (os != null)) {
            try {
                JAXBElement<MessageExchange> element = new JAXBElement<MessageExchange>(new QName(
                        "org.ow2.petals.esb.api", "MessageExchange"), MessageExchange.class, null,
                        messageExchange);
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
    public static final MessageExchange unmarshall(InputStream is) throws MessageExchangeException {
        MessageExchange m = null;
        if (is != null) {
            try {
                final JAXBElement<MessageExchange> root;
                // The default Xerces unmarshaller is not thread safe
                synchronized (unmarshaller) {
                    root = unmarshaller.unmarshal(new StreamSource(is), MessageExchange.class);
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
