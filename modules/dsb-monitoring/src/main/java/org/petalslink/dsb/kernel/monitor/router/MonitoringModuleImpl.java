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
package org.petalslink.dsb.kernel.monitor.router;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.ow2.petals.container.lifecycle.ServiceUnitLifeCycle;
import org.ow2.petals.jbi.component.context.ComponentContext;
import org.ow2.petals.jbi.messaging.exchange.MessageExchange;
import org.ow2.petals.jbi.messaging.exchange.MessageExchangeImpl;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.tools.monitoring.to.Role;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.kernel.monitor.util.TOConverter;


/**
 * @author aruffie - EBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = @Interface(name = "service", signature = org.ow2.petals.jbi.messaging.routing.RouterService.class))
public class MonitoringModuleImpl extends RouterMonitorImpl implements MonitoringModule {

    @Requires(name = "storageService", signature = MonitoringStorageService.class)
    private MonitoringStorageService storageService;

    public MonitoringModuleImpl() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.monitoring.router.MonitoringModule#getExchangeDuration
     * (java.lang.String)
     */
    public long getExchangeDuration(final String exchangeId) {

        if (exchangeId == null) {
            throw new IllegalArgumentException("The 'timestamp' parameter must not be null ");
        }

        final ExchangeContext ec = this.storageService.getExchangeContext(exchangeId);

        if (ec != null) {
            return ec.getTimestamp().getTime() - ec.getTimestamp().getTime();
        } else {
            return 0;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.monitoring.router.MonitoringModule#getMessageExchange(
     * java.util.Date)
     */
    public org.ow2.petals.tools.monitoring.to.MessageExchange getMessageExchange(
            final Date timestamp) {

        if (timestamp == null) {
            throw new IllegalArgumentException("The 'timestamp' parameter must not be null ");
        }

        synchronized (this.storageService.getStorage()) {
            final ExchangeContext[] array = this.storageService.getStorage().toArray(
                    new ExchangeContext[this.storageService.getStorage().size()]);

            // If storage is not null
            if ((array != null) && (array.length > 0)) {
                for (final ExchangeContext ec : array) {
                    if (ec.getTimestamp().equals(timestamp)) {
                        return ec.getExchange();
                    }
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.monitoring.router.MonitoringModule#getMessageExchange(
     * java.util.Date, java.util.Date)
     */
    public List<org.ow2.petals.tools.monitoring.to.MessageExchange> getMessageExchanges(
            final Date begin, final Date ending) {

        final List<org.ow2.petals.tools.monitoring.to.MessageExchange> exchanges = Collections
                .synchronizedList(new ArrayList<org.ow2.petals.tools.monitoring.to.MessageExchange>());
        final ExchangeContext[] array = this.storageService.getStorage().toArray(
                new ExchangeContext[this.storageService.getStorage().size()]);

        // If exchanges is not null
        if ((array != null) && (array.length > 0)) {
            if ((begin == null) && (ending == null)) {
                for (final ExchangeContext ec : array) {
                    exchanges.add(ec.getExchange());
                }
            } else {

                if (begin == null) {
                    throw new IllegalArgumentException(
                            "The 'begin' parameter must not be null if 'ending' parameter is null too ");
                }

                if (ending == null) {
                    throw new IllegalArgumentException(
                            "The 'ending' parameter must not be null if 'begin' parameter is null too ");
                }

                for (final ExchangeContext ec : array) {

                    /*
                     * Get all exchanges began between "begin" and "ending"
                     * parameters
                     */
                    if ((ec.getTimestamp().getTime() > begin.getTime())
                            && (ec.getTimestamp().getTime() < ending.getTime())) {
                        exchanges.add(ec.getExchange());
                    }
                }
            }
        }
        return exchanges;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.monitoring.router.MonitoringModule#getMessageExchanges
     * (java.lang.String)
     */
    public org.ow2.petals.tools.monitoring.to.MessageExchange getMessageExchange(
            final String exchangeId) {

        if (exchangeId == null) {
            throw new IllegalArgumentException("The 'exchangeId' parameter must not be null ");
        }

        final ExchangeContext ec = this.storageService.getExchangeContext(exchangeId);

        if (ec != null) {
            return ec.getExchange();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.monitoring.router.MonitoringModule#getConsumer(java.lang
     * .String exchangeId)
     */
    public String getConsumer(final String exchangeId) {

        if (exchangeId == null) {
            throw new IllegalArgumentException("The 'exchangeId' parameter must not be null ");
        }

        final ExchangeContext ec = this.storageService.getExchangeContext(exchangeId);

        if (ec != null) {
            return ec.getConsumer();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ow2.petals.monitoring.router.MonitoringModule#getProvider(java.lang
     * .String exchangeId)
     */
    public String getProvider(final String exchangeId) {

        if (exchangeId == null) {
            throw new IllegalArgumentException("The 'exchangeId' parameter must not be null ");
        }

        final ExchangeContext ec = this.storageService.getExchangeContext(exchangeId);

        if (ec != null) {
            return ec.getProvider();
        }
        return null;
    }

    /**
     * Allow to remove the message exchange for a specified exchagne id
     * 
     * @param String
     *            exchangeId
     */
    public void removeMessageExchange(final String exchangeId) {

        if (exchangeId == null) {
            throw new IllegalArgumentException("The 'exchangeId' parameter must not be null ");
        }

        this.storageService.getStorage().remove(exchangeId);
    }

    /**
     * Allow to store a message exchange
     * 
     * @param MessageExchange
     *            exchange
     * @param Date
     *            begin
     * @param Date
     *            ending
     */
    public void storeMessageExchange(final ComponentContext source,
            final org.ow2.petals.tools.monitoring.to.MessageExchange exchange, final Date timestamp) {

        if (exchange == null) {
            throw new IllegalArgumentException("The 'exchange' parameter must not be null ");
        }

        final ExchangeContext ec = new ExchangeContext();
        final Calendar calendar = GregorianCalendar.getInstance();
        final SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        calendar.setTime(timestamp);
        exchange
                .setTimestamp(simpleFormat.format(timestamp) + "." + calendar.get(Calendar.MILLISECOND));
        ec.setTimestamp(timestamp);
        ec.setExchange(exchange);
        if (exchange.getRole().equals(Role.CONSUMER)) {
            ec.setConsumer(source.getComponentName());
        } else if (exchange.getRole().equals(Role.PROVIDER)) {
            ec.setProvider(source.getComponentName());
        }
        synchronized (this.storageService.getStorage()) {
            try {
                this.storageService.getStorage().put(ec);
            } catch (final InterruptedException e) {
                this.log
                        .info("[Error occured during the storage message exchange in the monitoring module]: "
                                + e.getMessage());
            }
        }
    }

    /**
     * Allow to update a message exchange information (it's just a remove
     * followed by a store)
     * 
     * @param MessageExchange
     *            exchange
     * @param Date
     *            begin
     * @param Date
     *            ending
     */
    public void updateMessageExchange(final ComponentContext source,
            final org.ow2.petals.tools.monitoring.to.MessageExchange exchange, final Date timestamp) {

        if (exchange == null) {
            throw new IllegalArgumentException("The 'exchange' parameter must not be null ");
        }

        this.removeMessageExchange(exchange.getExchangeId());
        this.storeMessageExchange(source, exchange, timestamp);
    }

    @Override
    public void addComponent(final ComponentContext componentContext) throws RoutingException {
        super.addComponent(componentContext);
    }

    @Override
    public void modifiedSALifeCycle(final List<ServiceUnitLifeCycle> serviceUnitLifes) {
        super.modifiedSALifeCycle(serviceUnitLifes);
    }

    @Override
    public MessageExchange receive(final ComponentContext source, final long timeoutMS)
            throws RoutingException {
        // Get exchange
        final MessageExchangeImpl exchange = (MessageExchangeImpl) this.router.receive(source,
                timeoutMS);
        // Convert exchange to exchangeTO
        if (exchange != null) {
            this.report(exchange, this.logger, source);
        }
        // Forward
        return exchange;
    }

    @Override
    public void removeComponent(final ComponentContext componentContext) throws RoutingException {
        super.removeComponent(componentContext);
    }

    @Override
    public void send(final ComponentContext source, final MessageExchange exchange)
            throws RoutingException {
        if (exchange != null) {
            this.report(exchange, this.logger, source);
        }
        // Forward
        this.router.send(source, exchange);
    }

    @Override
    public MessageExchange sendSync(final ComponentContext source,
            final MessageExchange exchange, final long timeout) throws RoutingException {

        this.report(exchange, this.logger, source);
        // Get message exchange
        final MessageExchangeImpl responseExchange = (MessageExchangeImpl) this.router.sendSync(
                source, exchange, timeout);

        if (responseExchange != null) {
            this.report(responseExchange, this.logger, source);
        }
        // Forward
        return responseExchange;
    }

    private void report(final MessageExchange exchange,
            final org.objectweb.util.monolog.api.Logger logger, final ComponentContext source) {
        // Convert exchange to exchangeTO
        org.ow2.petals.tools.monitoring.to.MessageExchange exchangeTO = null;
        if (exchange != null) {
            try {
                exchangeTO = TOConverter.convert(exchange, this.logger);
            } catch (final SecurityException e) {
                this.log
                        .info("[Error occured during the role conversion in the monitoring module]: "
                                + e.getMessage());
            } catch (final NoSuchFieldException e) {
                this.log
                        .info("[Error occured during the role conversion in the monitoring module]: "
                                + e.getMessage());
            } catch (final IllegalArgumentException e) {
                this.log
                        .info("[Error occured during the role conversion in the monitoring module]: "
                                + e.getMessage());
            } catch (final IllegalAccessException e) {
                this.log
                        .info("[Error occured during the role conversion in the monitoring module]: "
                                + e.getMessage());
            } catch (final IOException e) {
                this.log
                        .info("[Error occured during the role conversion in the monitoring module]: "
                                + e.getMessage());
            } catch (final RoutingException e) {
                this.log
                        .info("[Error occured during the role conversion in the monitoring module]: "
                                + e.getMessage());
            }
        }
        if (exchangeTO != null) {
            /*
             * Check if the exchange context linked to the current exchange is
             * already stored
             */
            final ExchangeContext ec = this.storageService.getExchangeContext(exchangeTO
                    .getExchangeId());
            if (ec != null) {
                /*
                 * Update the message exchange and set the ending timestamp
                 */
                this.removeMessageExchange(exchangeTO.getExchangeId());
                if (ec.getExchange().getIn() != null) {
                    exchangeTO.setIn(ec.getExchange().getIn());
                }
                if (ec.getExchange().getOut() != null) {
                    exchangeTO.setOut(ec.getExchange().getOut());
                }
                if (ec.getExchange().getFault() != null) {
                    exchangeTO.setFault(ec.getExchange().getFault());
                }
                this.storeMessageExchange(source, exchangeTO, GregorianCalendar.getInstance()
                        .getTime());
            } else {
                this.storeMessageExchange(source, exchangeTO, GregorianCalendar.getInstance()
                        .getTime());
            }
        }
    }

    public void startTraffic() {

    }

    @Override
    public void stopTraffic() {
        super.stopTraffic();
    }

    @Override
    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        super.start();
        this.log = new LoggingUtil(this.logger);
        this.log.call();
    }

    @Override
    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() throws Exception {
        super.stop();
        this.log.call();
    }

    /**
     * Class representing an exchange context this the current message exchange,
     * its provider and consumer, its begin and ending timestamp
     * 
     * @author aruffie
     * 
     */
    public static class ExchangeContext {
        private org.ow2.petals.tools.monitoring.to.MessageExchange exchange;

        private Date timestamp;

        private String provider;

        private String consumer;

        /**
         * @param beginExchange
         * @param consumer
         * @param endingExchange
         * @param exchange
         * @param provider
         */
        public ExchangeContext(final Date timestamp, final String consumer,
                final org.ow2.petals.tools.monitoring.to.MessageExchange exchange,
                final String provider) {
            super();
            this.timestamp = timestamp;
            this.consumer = consumer;
            this.exchange = exchange;
            this.provider = provider;
        }

        /**
         * 
         */
        public ExchangeContext() {
            super();
        }

        /**
         * @return the exchange
         */
        public org.ow2.petals.tools.monitoring.to.MessageExchange getExchange() {
            return this.exchange;
        }

        /**
         * @param exchange
         *            the exchange to set
         */
        public void setExchange(final org.ow2.petals.tools.monitoring.to.MessageExchange exchange) {
            this.exchange = exchange;
        }

        /**
         * @return the provider
         */
        public String getProvider() {
            return this.provider;
        }

        /**
         * @param provider
         *            the provider to set
         */
        public void setProvider(final String provider) {
            this.provider = provider;
        }

        /**
         * @return the consumer
         */
        public String getConsumer() {
            return this.consumer;
        }

        /**
         * @param consumer
         *            the consumer to set
         */
        public void setConsumer(final String consumer) {
            this.consumer = consumer;
        }

        /**
         * @return the timestamp
         */
        public Date getTimestamp() {
            return timestamp;
        }

        /**
         * @param timestamp the timestamp to set
         */
        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }
        
        
    }
    
    
}
