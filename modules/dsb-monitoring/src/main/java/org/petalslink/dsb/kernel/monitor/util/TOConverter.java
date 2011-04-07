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
 * $Id: Router.java,v 1.2 2006/03/17 10:24:27 alouis Exp $
 * -------------------------------------------------------------------------
 */
package org.petalslink.dsb.kernel.monitor.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.jbi.messaging.MessageExchange.Role;

import mx4j.log.Logger;

import org.ow2.petals.jbi.messaging.exchange.MessageExchangeImpl;
import org.ow2.petals.jbi.messaging.routing.RoutingException;

/**
 * @author aruffie - EBM WebSourcing
 * 
 */
public class TOConverter {

    public static org.ow2.petals.tools.monitoring.to.MessageExchange convert(
            final MessageExchange exchange, final org.objectweb.util.monolog.api.Logger logger)
            throws RoutingException, IOException, IllegalArgumentException, SecurityException,
            IllegalAccessException, NoSuchFieldException {
        final org.ow2.petals.tools.monitoring.to.MessageExchange exchangeTO = new org.ow2.petals.tools.monitoring.to.MessageExchange();
        final org.ow2.petals.tools.monitoring.to.ServiceEndpoint endpointTO = convert((org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint) exchange
                .getEndpoint());
        exchangeTO.setEndpoint(endpointTO);
        exchangeTO.setExchangeId(exchange.getExchangeId());
        if (exchange.getInterfaceName() != null) {
            exchangeTO.setInterfaceName(exchange.getInterfaceName().toString());
        }
        if (exchange.getOperation() != null) {
            exchangeTO.setOperation(exchange.getOperation().toString());
        }
        exchangeTO.setPattern(MonitoringUtil.splitPattern(exchange.getPattern()));
        exchangeTO.setRole(convert(exchange.getRole()));
        if (exchange.getService() != null) {
            exchangeTO.setService(exchange.getService().toString());
        }
        exchangeTO.setStatus(convert(exchange.getStatus()));
        exchangeTO.setProperties(getProperties(exchange));
        final MessageExchangeImpl exchangeImpl = (MessageExchangeImpl) exchange;
        if ((exchangeImpl.getConsumerEndpoint() != null)
                && (exchangeImpl.getConsumerEndpoint().getLocation() != null)) {
            exchangeTO.setConsumer(exchangeImpl.getConsumerEndpoint().getLocation()
                    .getComponentName());
        }
        if ((exchange.getEndpoint() != null)
                && (((org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint) exchange.getEndpoint())
                        .getLocation() != null)) {
            exchangeTO
                    .setProvider(((org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint) exchange
                            .getEndpoint()).getLocation().getComponentName());
        }
        /*
         * If an error is available marshal this into a string
         */
        if (exchange.getError() != null) {
            exchangeTO.setError(marshalError(exchange.getError()));
        }

        /*
         * If a fault is available convert it into a transfert object
         */
        if (exchange.getFault() != null) {
            exchangeTO.setFault(convert(exchange.getFault(), logger));
        }

        /*
         * If a in is available convert it into a transfert object
         */
        if (exchange.getMessage("IN") != null) {
            exchangeTO.setIn(convert(exchange.getMessage("IN"), logger));
        }

        /*
         * If a out is available convert it into a transfert object
         */
        if (exchange.getMessage("OUT") != null) {
            exchangeTO.setOut(convert(exchange.getMessage("OUT"), logger));
        }
        return exchangeTO;
    }

    @SuppressWarnings("unchecked")
    public static List<org.ow2.petals.tools.monitoring.to.Property> getProperties(
            final MessageExchange exchange) {
        final List<org.ow2.petals.tools.monitoring.to.Property> properties = new ArrayList<org.ow2.petals.tools.monitoring.to.Property>();
        final Set set = exchange.getPropertyNames();

        // If properties are available
        if (set != null) {
            final Object[] keys = set.toArray();
            for (int i = 0; i < keys.length; i++) {
                final String key = (String) keys[i];
                org.ow2.petals.tools.monitoring.to.Property property = null;
                if (exchange.getProperty(key) != null) {
                    property = new org.ow2.petals.tools.monitoring.to.Property(key, exchange
                            .getProperty(key).toString());
                } else {
                    property = new org.ow2.petals.tools.monitoring.to.Property(key, null);
                }
                properties.add(property);
            }
        }
        return properties;
    }

    public static org.ow2.petals.tools.monitoring.to.ExchangeStatus convert(
            final ExchangeStatus status) {
        final org.ow2.petals.tools.monitoring.to.ExchangeStatus to = new org.ow2.petals.tools.monitoring.to.ExchangeStatus();
        to.setStatus(status.toString());
        return to;
    }

    public static org.ow2.petals.tools.monitoring.to.Role convert(final Role role)
            throws IllegalArgumentException, SecurityException, IllegalAccessException,
            NoSuchFieldException {
        if (role != null) {
            if (MessageExchange.Role.CONSUMER.equals(role)) {
                return org.ow2.petals.tools.monitoring.to.Role.CONSUMER;
            } else if (MessageExchange.Role.PROVIDER.equals(role)) {
                return org.ow2.petals.tools.monitoring.to.Role.PROVIDER;
            }
        }
        return null;
    }

    public static org.ow2.petals.tools.monitoring.to.NormalizedMessage convert(
            final NormalizedMessage nm, final org.objectweb.util.monolog.api.Logger logger) {
        final org.ow2.petals.tools.monitoring.to.NormalizedMessage to = new org.ow2.petals.tools.monitoring.to.NormalizedMessage();
        /*
         * javax.xml.transform.TransformerFactory tfactory =
         * TransformerFactory.newInstance(); javax.xml.transform.Transformer
         * xform = null; try { xform = tfactory.newTransformer(); } catch (final
         * TransformerConfigurationException e) { } java.io.StringWriter writer
         * = new StringWriter(); javax.xml.transform.Result result = new
         * javax.xml.transform.stream.StreamResult(writer); try {
         * System.out.println("#1");
         * xform.transform(MonitoringUtil.cloneSource(nm.getContent()), result);
         * System.out.println("#2");
         * 
         * } catch (final TransformerException e) { System.out.println("T");
         * e.printStackTrace(); logger.log(Logger.INFO,
         * "[Error occured during the normalized message conversion in the monitoring module]: "
         * + e.getMessage()); } catch (final Exception e) {
         * System.out.println("E"); e.printStackTrace(); logger.log(Logger.INFO,
         * "[Error occured during the normalized message conversion in the monitoring module]: "
         * + e.getMessage()); }
         */

        if (nm.getContent() != null) {
            try {
                final String content = MonitoringUtil.cloneSourceInString(nm.getContent());
                to.setContent(content);
            } catch (final Exception e) {
                logger.log(Logger.WARN,
                        "[Error occured during the normalized message conversion in the monitoring module]: "
                                + e.getMessage());
            }
        }

        final Set<String> set = nm.getPropertyNames();
        if (set != null) {
            final Iterator<String> it = set.iterator();
            while (it.hasNext()) {
                final String key = it.next();
                final org.ow2.petals.tools.monitoring.to.Property property = new org.ow2.petals.tools.monitoring.to.Property();
                property.setName(key);
                property.setValue(nm.getProperty(key).toString());
                to.getProperties().add(property);
            }
        }
        return to;
    }

    public static String marshalError(final Exception error) throws IOException {
        final StringWriter stringWritter = new StringWriter();
        final PrintWriter printWritter = new PrintWriter(stringWritter, true);
        error.printStackTrace(printWritter);
        printWritter.flush();
        stringWritter.flush();
        final String result = stringWritter.toString();
        return result;
    }

    public static org.ow2.petals.tools.monitoring.to.ServiceEndpoint convert(
            final org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint endpoint) {
        final org.ow2.petals.tools.monitoring.to.ServiceEndpoint endpointTO = new org.ow2.petals.tools.monitoring.to.ServiceEndpoint();
        if (endpoint != null) {
            endpointTO.setEndpointName(endpoint.getEndpointName());
            final List<String> stringInterfaces = new ArrayList<String>();
            for (final javax.xml.namespace.QName _interface : endpoint.getInterfaces()) {
                stringInterfaces.add(_interface.toString());
            }
            endpointTO.setInterfaces(stringInterfaces.toArray(new String[stringInterfaces.size()]));
            endpointTO.setServiceName(endpoint.getServiceName().toString());
            endpointTO.setSubdomainLocation(endpoint.getLocation().getSubdomainName());
            endpointTO.setContainerLocation(endpoint.getLocation().getContainerName());
            endpointTO.setComponentLocation(endpoint.getLocation().getComponentName());
        }
        return endpointTO;
    }
}
