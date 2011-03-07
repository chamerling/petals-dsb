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
package org.petalslink.dsb.transport.cxf;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jbi.messaging.Fault;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.jbi.messaging.MessageExchange.Role;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ow2.petals.commons.stream.InputStreamForker;
import org.ow2.petals.commons.stream.ReaderInputStream;
import org.ow2.petals.commons.threadlocal.Transformers;
import org.ow2.petals.jbi.messaging.endpoint.JBIServiceEndpointImpl;
import org.ow2.petals.jbi.messaging.exchange.MessageExchange;
import org.ow2.petals.kernel.api.service.Location;

/**
 * Adapter to change JAXB annotated
 * {@link org.petalslink.dsb.api.MessageExchange} to core Petals
 * {@link MessageExchange}
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class Adapter {

    private Adapter() {
    }

    /**
     * @param messageExchange
     * @return
     */
    public static MessageExchange createJBIMessage(
            org.petalslink.dsb.api.MessageExchange messageExchange) throws MessagingException {

        org.petalslink.dsb.transport.MessageExchange me = new org.petalslink.dsb.transport.MessageExchange(
                createServiceEndpoint(messageExchange.getConsumer()));
        me.setEndpoint(createServiceEndpoint(messageExchange.getEndpoint()));

        me.setExchangeId(messageExchange.getId());
        me.setInterfaceName(messageExchange.getInterfaceName());

        me.setOperation(messageExchange.getOperation());
        me.setPattern(messageExchange.getPattern());
        for (org.petalslink.dsb.api.Property property : messageExchange.getProperties()) {
            me.setProperty(property.getName(), property.getValue());
        }

        if (messageExchange.getRole() != null) {
            if (messageExchange.getRole().equalsIgnoreCase("consumer")) {
                me.setRole(MessageExchange.Role.CONSUMER);
            } else if (messageExchange.getRole().equalsIgnoreCase("provider")) {
                me.setRole(MessageExchange.Role.PROVIDER);
            }
        }

        me.setService(messageExchange.getService());
        me.setTerminated(messageExchange.isTerminated());
        me.setTransacted(messageExchange.isTransacted());

        // set the core status without any control
        if ((messageExchange.getStatus() != null)) {
            me.setCoreStatus(javax.jbi.messaging.ExchangeStatus
                    .valueOf(messageExchange.getStatus()));
        }

        // once all is set, set the normalized messages
        if (messageExchange.getIn() != null) {
            me.setCoreMessage(createNormalizedMessage(me.createMessage(), messageExchange.getIn()),
                    "in");
        }
        if (messageExchange.getOut() != null) {
            me.setCoreMessage(
                    createNormalizedMessage(me.createMessage(), messageExchange.getOut()), "out");
        }

        if (messageExchange.getFault() != null) {
            me.setCoreFault(createFault(me.createFault(), messageExchange.getFault()));
        }

        if (messageExchange.getError() != null) {
            // TODO :: Build an error, a real one from a real exception!!!
            me.setCoreError(new Exception(messageExchange.getError()));
        }
        return me;
    }

    /**
     * @param normalizedMessage
     * @param in
     * @return
     */
    private static NormalizedMessage createNormalizedMessage(NormalizedMessage normalizedMessage,
            org.petalslink.dsb.api.NormalizedMessage in) throws MessagingException {
        // create the source from the string...
        normalizedMessage.setContent(createSource(in.getContent()));

        for (org.petalslink.dsb.api.Property prop : in.getProperties()) {
            normalizedMessage.setProperty(prop.getName(), prop.getValue());
        }

        // TODO
        // normalizedMessage.addAttachment(id, content)
        return normalizedMessage;
    }

    private static Fault createFault(Fault fault, org.petalslink.dsb.api.NormalizedMessage in)
            throws MessagingException {
        // create the source from the string...
        fault.setContent(createSource(in.getContent()));

        for (org.petalslink.dsb.api.Property prop : in.getProperties()) {
            fault.setProperty(prop.getName(), prop.getValue());
        }

        // TODO
        // normalizedMessage.addAttachment(id, content)
        return fault;
    }

    /**
     * @param content
     * @return
     */
    private static Source createSource(String content) {
        BufferedInputStream buffer = new BufferedInputStream(new ByteArrayInputStream(content
                .getBytes()));
        return new StreamSource(buffer);
    }

    /**
     * @param exchange
     * @return
     */
    public static org.petalslink.dsb.api.MessageExchange createWSMessage(MessageExchange exchange) {
        org.petalslink.dsb.api.MessageExchange me = new org.petalslink.dsb.api.MessageExchange();
        me.setConsumer(createServiceEndpoint(exchange.getConsumerEndpoint()));
        me
                .setEndpoint(createServiceEndpoint((org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint) exchange
                        .getEndpoint()));
        me.setId(exchange.getExchangeId());
        me.setInterfaceName(exchange.getInterfaceName());
        me.setOperation(exchange.getOperation());
        me.setPattern(exchange.getPattern());

        Set set = exchange.getPropertyNames();
        for (Object object : set) {
            String key = object.toString();
            org.petalslink.dsb.api.Property prop = new org.petalslink.dsb.api.Property();
            prop.setName(key);
            Object o = exchange.getProperty(key);
            if (o != null) {
                prop.setValue(o.toString());
            }
            me.getProperties().add(prop);
        }

        if (exchange.getRole() != null) {
            if (Role.CONSUMER.equals(exchange.getRole())) {
                me.setRole("consumer");
            } else if (Role.PROVIDER.equals(exchange.getRole())) {
                me.setRole("provider");
            } else {
                // ?
            }
        }
        me.setService(exchange.getService());
        me.setStatus(exchange.getStatus().toString());
        me.setTerminated(exchange.isTerminated());
        me.setTransacted(exchange.isTransacted());

        if (exchange.getMessage("in") != null) {
            me.setIn(createNormalizedMessage(exchange.getMessage("in")));
        }
        if (exchange.getMessage("out") != null) {
            me.setOut(createNormalizedMessage(exchange.getMessage("out")));
        }

        if (exchange.getFault() != null) {
            me.setFault(createNormalizedMessage(exchange.getFault()));
        }

        if (exchange.getError() != null) {
            me.setError(exchange.getError().getMessage());
        }

        return me;
    }

    /**
     * @param message
     * @return
     */
    private static org.petalslink.dsb.api.NormalizedMessage createNormalizedMessage(
            NormalizedMessage message) {
        org.petalslink.dsb.api.NormalizedMessage nm = new org.petalslink.dsb.api.NormalizedMessage();

        // create a string from the source...
        nm.setContent(createStringFromSource(message.getContent()));

        Set set = message.getPropertyNames();
        for (Object object : set) {
            String key = object.toString();
            org.petalslink.dsb.api.Property prop = new org.petalslink.dsb.api.Property();
            prop.setName(key);
            Object o = message.getProperty(key);
            if (o != null) {
                prop.setValue(o.toString());
            }
            nm.getProperties().add(prop);
        }

        return nm;
    }

    /**
     * @param content
     * @return
     */
    private static String createStringFromSource(Source s) {
        if (s == null) {
            return null;
        }
        Source tempSource = s;
        if ((s instanceof StreamSource)) {
            tempSource = new StreamSource(forkStreamSource((StreamSource) s));
        }

        try {
            final StringWriter buffer = new StringWriter();
            final Result sresult = new StreamResult(buffer);
            final Transformer transformer = Transformers.getDefaultTransformer();
            try {
                transformer.transform(tempSource, sresult);
            } finally {
                transformer.reset();
            }

            return buffer.toString();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static org.petalslink.dsb.api.ServiceEndpoint createServiceEndpoint(
            org.ow2.petals.kernel.api.service.ServiceEndpoint serviceEndpoint) {
        if (serviceEndpoint == null) {
            return null;
        }
        org.petalslink.dsb.api.ServiceEndpoint se = new org.petalslink.dsb.api.ServiceEndpoint();
        if (serviceEndpoint.getLocation() != null) {
            se.setComponentLocation(serviceEndpoint.getLocation().getComponentName());
            se.setSubdomainLocation(serviceEndpoint.getLocation().getSubdomainName());
            se.setContainerLocation(serviceEndpoint.getLocation().getContainerName());
        }

        // TODO : change description to string
        // se.setDescription(description);

        se.setEndpointName(serviceEndpoint.getEndpointName());
        se.setServiceName(serviceEndpoint.getServiceName());
        List<QName> itfs = serviceEndpoint.getInterfacesName();
        if (itfs != null) {
            se.setInterfaces(itfs.toArray(new QName[itfs.size()]));
        }
        return se;
    }

    public static JBIServiceEndpointImpl createServiceEndpoint(
            org.petalslink.dsb.api.ServiceEndpoint serviceEndpoint) {
        JBIServiceEndpointImpl se = new JBIServiceEndpointImpl();
        if (serviceEndpoint != null) {
            Location location = new Location(serviceEndpoint.getSubdomainLocation(),
                    serviceEndpoint.getContainerLocation(), serviceEndpoint.getComponentLocation());
            se.setLocation(location);

            se.setStringDescription(serviceEndpoint.getDescription());

            se.setEndpointName(serviceEndpoint.getEndpointName());
            se.setServiceName(serviceEndpoint.getServiceName());
            QName[] itfs = serviceEndpoint.getInterfaces();
            if (itfs != null) {
                List<QName> list = new ArrayList<QName>();
                for (QName qName : itfs) {
                    list.add(qName);
                }
                se.setInterfacesName(list);
            }
        }
        return se;
    }

    private static final InputStream forkStreamSource(StreamSource streamSource) {
        InputStreamForker streamForker;
        final InputStream isContent = streamSource.getInputStream();
        if (isContent != null) {
            // The StreamSource was created from an InputStream
            streamForker = new InputStreamForker(isContent);
        } else {
            // The StreamSource was created from a Reader
            // we wrap it as an InputStream
            streamForker = new InputStreamForker(new ReaderInputStream(streamSource.getReader()));
        }
        streamSource.setInputStream(streamForker.getInputStreamOne());
        return streamForker.getInputStreamTwo();
    }

}
