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
package org.petalslink.dsb.transport.xmpp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.jbi.messaging.MessagingException;

import org.jivesoftware.smack.packet.Message;
import org.ow2.petals.jbi.messaging.exchange.MessageExchange;
import org.petalslink.dsb.api.MessageExchangeException;
import org.petalslink.dsb.api.util.JAXBMessageExchangeBuilder;

/**
 * FIXME : For now use JAXB for marshalling/unmarshalling
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class Adapter {

    private Adapter() {
    }

    public static MessageExchange createJBIMessage(Message message) throws MessagingException {
        // get the message content as string
        String body = message.getBody();
        if (body == null) {
            return null;
        }

        // create the JAXB Element
        org.petalslink.dsb.api.MessageExchange jaxbMessage = fromString(body);
        // use JAXB adapter
        MessageExchange result = org.petalslink.dsb.transport.Adapter
                .createJBIMessage(jaxbMessage);
        return result;
    }

    public static Message createJabberMessage(MessageExchange messageExchange) {
        // create JAXB message
        org.petalslink.dsb.api.MessageExchange jaxbMessage = org.petalslink.dsb.transport.Adapter
                .createWSMessage(messageExchange);
        // create String from JAXB message
        String body = toString(jaxbMessage);
        // create XMPP message
        Message message = new Message();
        message.setBody(body);
        return message;
    }

    private static String toString(org.petalslink.dsb.api.MessageExchange messageExchange) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            JAXBMessageExchangeBuilder.marshall(messageExchange, bos);
        } catch (MessageExchangeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bos.toString();
    }

    private static org.petalslink.dsb.api.MessageExchange fromString(String s) {
        try {
            return JAXBMessageExchangeBuilder.unmarshall(new ByteArrayInputStream(s.getBytes()));
        } catch (MessageExchangeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
