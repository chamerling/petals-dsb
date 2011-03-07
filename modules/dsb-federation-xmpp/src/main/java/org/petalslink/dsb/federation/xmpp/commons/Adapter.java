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
package org.petalslink.dsb.federation.xmpp.commons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.jivesoftware.smack.packet.Message;
import org.petalslink.dsb.api.EndpointQuery;
import org.petalslink.dsb.api.MessageExchange;
import org.petalslink.dsb.api.MessageExchangeException;
import org.petalslink.dsb.api.ServiceEndpoint;
import org.petalslink.dsb.api.util.JAXBEndpointQueryBuilder;
import org.petalslink.dsb.api.util.JAXBMessageExchangeBuilder;
import org.petalslink.dsb.federation.xmpp.commons.util.JAXBServiceEndpointCollectionHelper;
import org.petalslink.dsb.federation.xmpp.commons.util.ServiceEndpointCollection;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class Adapter {

    private Adapter() {
    }

    public static Message createMessage(MessageExchange exchange, String clientId, String id) {
        Message message = new Message();
        message.setBody(toString(exchange));
        setClientId(message, clientId);
        setMessageId(message, id);
        return message;
    }

    public static Message createMessage(Set<ServiceEndpoint> endpoints, String clientId, String id) {
        Message message = new Message();
        message.setBody(toString(endpoints));
        setClientId(message, clientId);
        setMessageId(message, id);
        return message;
    }

    /**
     * @param message
     * @param id
     */
    private static void setMessageId(Message message, String id) {
        message.setProperty("fedmessageid", id);
    }

    /**
     * @param endpoints
     * @return
     */
    private static String toString(Set<ServiceEndpoint> endpoints) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ServiceEndpointCollection collection = new ServiceEndpointCollection();
        collection.setEndpoints(endpoints.toArray(new ServiceEndpoint[endpoints.size()]));
        try {
            JAXBServiceEndpointCollectionHelper.marshall(collection, bos);
        } catch (MessageExchangeException e) {
            e.printStackTrace();
        }
        return bos.toString();
    }

    public static Message createMessage(EndpointQuery query, String clientId, String id) {
        Message message = new Message();
        message.setBody(toString(query));
        setClientId(message, clientId);
        setMessageId(message, id);
        return message;
    }

    /**
     * @param query
     * @return
     */
    private static String toString(EndpointQuery query) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            JAXBEndpointQueryBuilder.marshall(query, bos);
        } catch (MessageExchangeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bos.toString();
    }

    public static String getAction(Message message) {
        return message.getProperty("action").toString();
    }

    /**
     * @param message2
     * @return
     */
    public static MessageExchange getMessageExchange(Message message) {
        return messageExchangeFromString(message.getBody());
    }

    /**
     * @param message2
     * @return
     */
    public static EndpointQuery getQuery(Message message) {
        return endpointQueryFromString(message.getBody());
    }

    /**
     * @param message2
     * @return
     */
    public static String getCallbackURL(Message message) {
        return message.getProperty("callbackurl").toString();
    }

    public static void setCallBackURL(Message message, String callbackURL) {
        message.setProperty("callbackurl", callbackURL);
    }

    public static void setAction(Message message, String action) {
        message.setProperty("action", action);
    }

    /**
     * @param message2
     * @return
     */
    public static String getClientId(Message message) {
        return message.getProperty("clientid").toString();
    }

    public static void setClientId(Message message, String clientId) {
        message.setProperty("clientid", clientId);
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

    private static org.petalslink.dsb.api.MessageExchange messageExchangeFromString(String s) {
        try {
            return JAXBMessageExchangeBuilder.unmarshall(new ByteArrayInputStream(s.getBytes()));
        } catch (MessageExchangeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static EndpointQuery endpointQueryFromString(String s) {
        try {
            return JAXBEndpointQueryBuilder.unmarshall(new ByteArrayInputStream(s.getBytes()));
        } catch (MessageExchangeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param message
     * @return
     */
    public static Set<ServiceEndpoint> getServiceEndpoints(Message message) {
        return endpointsFromString(message.getBody());
    }

    /**
     * @param body
     * @return
     */
    private static Set<ServiceEndpoint> endpointsFromString(String body) {
        Set<ServiceEndpoint> result = new HashSet<ServiceEndpoint>();
        try {
            ServiceEndpointCollection collection = JAXBServiceEndpointCollectionHelper
                    .unmarshall(new ByteArrayInputStream(body.getBytes()));
            if (collection != null) {
                for (ServiceEndpoint serviceEndpoint : collection.getEndpoints()) {
                    result.add(serviceEndpoint);
                }
            }
        } catch (MessageExchangeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param mode
     */
    public static void setMode(Message message, String mode) {
        message.setProperty("messagemode", mode);
    }

    /**
     * @param m
     * @return
     */
    public static String getMode(Message m) {
        return m.getProperty("messagemode") != null ? m.getProperty("messagemode").toString()
                : null;
    }

    /**
     * @param message
     * @return
     */
    public static String getFedId(Message m) {
        return m.getProperty("fedmessageid") != null ? m.getProperty("fedmessageid").toString()
                : null;
    }

}
