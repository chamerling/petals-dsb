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
package org.petalslink.dsb.kernel.monitoring.router;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import mx4j.log.Logger;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.ow2.petals.container.lifecycle.ServiceUnitLifeCycle;
import org.ow2.petals.jbi.component.context.ComponentContext;
import org.ow2.petals.jbi.messaging.exchange.MessageExchangeImpl;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.tools.monitoring.to.Role;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.kernel.monitoring.util.TOConverter;


/**
 * @author aruffie - EBM WebSourcing
 *
 */
@FractalComponent
@Provides(interfaces = @Interface(name = "service", signature = org.ow2.petals.jbi.messaging.routing.RouterService.class))
public class MonitoringModuleImpl extends RouterMonitorImpl implements MonitoringModule {
    
    @Requires(name = "storageService", signature = MonitoringStorageService.class)
    private MonitoringStorageService storageService;
    
    public MonitoringModuleImpl(){

    }
    
    private static final int MAX_STORAGE = 1000;
    /* (non-Javadoc)
     * @see org.ow2.petals.monitoring.router.MonitoringModule#getExchangeDuration(java.lang.String)
     */    
    public long getExchangeDuration(final String exchangeId) {
        
        if(exchangeId== null){
            throw new IllegalArgumentException("The 'timestamp' parameter must not be null ");
        }
        
        final ExchangeContext eContext = this.storageService.getStorage().get(exchangeId);
        
        if(eContext != null){
        return eContext.getEndingExchange().getTime() - eContext.getBeginExchange().getTime();
        } else {
            return 0;
        }
    }

    /* (non-Javadoc)
     * @see org.ow2.petals.monitoring.router.MonitoringModule#getMessageExchange(java.util.Date)
     */
    public org.ow2.petals.tools.monitoring.to.MessageExchange getMessageExchange(final Date timestamp) {
        
        if(timestamp == null){
            throw new IllegalArgumentException("The 'timestamp' parameter must not be null ");
        }
        
        synchronized (this.storageService.getStorage()) {
            final Set<String> keys = this.storageService.getStorage().keySet();
            
            // If storage is not null
            if(keys != null){
                final Iterator<String> it = keys.iterator();
                while(it.hasNext()){
                    final String exchangeId = it.next();
                    final ExchangeContext eContext = this.storageService.getStorage().get(exchangeId);
                    if(eContext.getBeginExchange().equals(timestamp)){
                        return eContext.getExchange();
                    }
                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.ow2.petals.monitoring.router.MonitoringModule#getMessageExchange(java.util.Date, java.util.Date)
     */
    public List<org.ow2.petals.tools.monitoring.to.MessageExchange> getMessageExchange(final Date begin, final Date ending) {
        
        final List<org.ow2.petals.tools.monitoring.to.MessageExchange> exchanges = Collections.synchronizedList(new ArrayList<org.ow2.petals.tools.monitoring.to.MessageExchange>());
        final Set<String> keys = this.storageService.getStorage().keySet();
        
        // If exchanges is not null
        if(keys != null){
            final Iterator<String> it = keys.iterator();
            
            if((begin == null) && (ending == null)){
                while(it.hasNext()){
                    exchanges.add(this.storageService.getStorage().get(it.next()).getExchange());
                }
            } else {
                
                if(begin == null){
                    throw new IllegalArgumentException("The 'begin' parameter must not be null if 'ending' parameter is null too ");
                }
                
                if(ending == null){
                    throw new IllegalArgumentException("The 'ending' parameter must not be null if 'begin' parameter is null too ");
                }
                
                while(it.hasNext()){
                    final String exchangeId = it.next();
                    final ExchangeContext eContext = this.storageService.getStorage().get(exchangeId);
                    
                    /*
                     * Get all exchanges began between 
                     * "begin" and "ending" parameters
                     */
                    if((eContext.getBeginExchange().getTime()>begin.getTime())
                            && 
                        (eContext.getEndingExchange().getTime()<ending.getTime())){
                        exchanges.add(eContext.getExchange());
                    }
                }
            }
        }
        return exchanges;
    }

    /* (non-Javadoc)
     * @see org.ow2.petals.monitoring.router.MonitoringModule#getMessageExchanges(java.lang.String)
     */
    public org.ow2.petals.tools.monitoring.to.MessageExchange getMessageExchanges(final String exchangeId) {
        
        if(exchangeId == null){
            throw new IllegalArgumentException("The 'exchangeId' parameter must not be null ");
        }
        
        final ExchangeContext eContext = this.storageService.getStorage().get(exchangeId);
        
        if(eContext != null){
            return eContext.getExchange();
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.ow2.petals.monitoring.router.MonitoringModule#getConsumer(java.lang.String exchangeId)
     */
    public String getConsumer(final String exchangeId) {
        
        if(exchangeId == null){
            throw new IllegalArgumentException("The 'exchangeId' parameter must not be null ");
        }
        
        final ExchangeContext eContext = this.storageService.getStorage().get(exchangeId);
        
        if(eContext != null){
            return eContext.getConsumer();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.ow2.petals.monitoring.router.MonitoringModule#getProvider(java.lang.String exchangeId)
     */
    public String getProvider(final String exchangeId) {
        
        if(exchangeId == null){
            throw new IllegalArgumentException("The 'exchangeId' parameter must not be null ");
        }
        
        final ExchangeContext eContext = this.storageService.getStorage().get(exchangeId);
        
        if(eContext != null){
            return eContext.getProvider();
        }
        return null;
    }

    /**
     * Allow to remove the message exchange
     * for a specified exchagne id
     * @param String exchangeId
     */
    public void removeMessageExchange(final String exchangeId) {
        
        if(exchangeId == null){
            throw new IllegalArgumentException("The 'exchangeId' parameter must not be null ");
        }
        
        this.storageService.getStorage().remove(exchangeId);
    }

    /**
     * Allow to store a message exchange
     * @param MessageExchange exchange
     * @param Date begin
     * @param Date ending
     */
    public void storeMessageExchange(final ComponentContext source, final org.ow2.petals.tools.monitoring.to.MessageExchange exchange,
            final Date begin, final Date ending) {
        
        if(exchange == null){
            throw new IllegalArgumentException("The 'exchange' parameter must not be null ");
        }
        
        final ExchangeContext eContext = new ExchangeContext();
        eContext.setBeginExchange(begin);
        eContext.setEndingExchange(ending);
        eContext.setExchange(exchange);
        if(exchange.getRole().equals(Role.CONSUMER)){
            eContext.setConsumer(source.getComponentName());
        }else  if(exchange.getRole().equals(Role.PROVIDER)){
            eContext.setProvider(source.getComponentName());
        }
        synchronized (this.storageService.getStorage()) {
            /*
             * If the maximum message number is
             * reaches clean the storage list
             */
            if(this.storageService.getStorage().size() == MAX_STORAGE){
                this.storageService.getStorage().clear();
            }
            
            this.storageService.getStorage().put(exchange.getExchangeId(), eContext);
        }
    }

    /**
     * Allow to update a message exchange
     * information (it's just a remove 
     * followed by a store)
     * @param MessageExchange exchange
     * @param Date begin
     * @param Date ending
     */
    public void updateMessageExchange(final ComponentContext source, final org.ow2.petals.tools.monitoring.to.MessageExchange exchange,
            final Date begin, final Date ending) {
        
        if(exchange == null){
            throw new IllegalArgumentException("The 'exchange' parameter must not be null ");
        }
        
        this.removeMessageExchange(exchange.getExchangeId());
        this.storeMessageExchange(source, exchange, begin, ending);
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
    public MessageExchangeImpl receive(final ComponentContext source, final long timeoutMS)
            throws RoutingException {
        // Get exchange

            
        final MessageExchangeImpl exchange = (MessageExchangeImpl) this.router.receive(source, timeoutMS);
        // Convert exchange to exchangeTO
        if(exchange != null){
            System.out.println("***********************");
            System.out.println("[receive]exchange.getFault: "+exchange.getFault());
            System.out.println("[receive]exchange.getOut: "+exchange.getMessage("OUT"));
            System.out.println("***********************");
            this.report(exchange,this.logger,source);
        }
        // Forward
        return exchange;
    }

    @Override
    public void removeComponent(final ComponentContext componentContext) throws RoutingException {
        super.removeComponent(componentContext);
    }

    public void send(final ComponentContext source, final MessageExchangeImpl exchange) throws RoutingException {       
        if(exchange != null){
            System.out.println("***********************");
            System.out.println("[send]exchange.getFault: "+exchange.getFault());
            System.out.println("[send]exchange.getOut: "+exchange.getMessage("OUT"));
            System.out.println("***********************");
            this.report(exchange,this.logger,source);
        }
        // Forward
        this.router.send(source, exchange);
    }

    public MessageExchangeImpl sendSync(final ComponentContext source,final  MessageExchangeImpl exchange,
            final long timeout) throws RoutingException {
        System.out.println("***********************");
        System.out.println("[sendSync]avantReport|exchange.getFault: "+exchange.getFault());
        System.out.println("[sendSync]avantReport|exchange.getOut: "+exchange.getMessage("OUT"));
        System.out.println("***********************");
        this.report(exchange,this.logger,source);
        System.out.println("***********************");
        System.out.println("[sendSync]exchange.getFault: "+exchange.getFault());
        System.out.println("[sendSync]exchange.getOut: "+exchange.getMessage("OUT"));
        System.out.println("***********************");
        // Get message exchange
        final MessageExchangeImpl responseExchange = (MessageExchangeImpl) this.router.sendSync(source, exchange, timeout);
        
        if(responseExchange != null){
            System.out.println("***********************");
            System.out.println("[sendSync]responseExchange.getFault: "+responseExchange.getFault());
            System.out.println("[sendSync]responseExchange.getOut: "+responseExchange.getMessage("OUT"));
            System.out.println("***********************");
            this.report(responseExchange,this.logger,source);
        }    
        //Forward
        return responseExchange;
    }

    private void report(final MessageExchangeImpl exchange, final org.objectweb.util.monolog.api.Logger logger, final ComponentContext source ) {
        // Convert exchange to exchangeTO
        org.ow2.petals.tools.monitoring.to.MessageExchange exchangeTO = null;
        if(exchange != null){
            System.out.println("***********************");
            System.out.println("[report]exchange.getFault: "+exchange.getFault());
            System.out.println("[report]exchange.getOut: "+exchange.getMessage("OUT"));
            System.out.println("***********************");
            try{
                exchangeTO = TOConverter.convert(exchange,this.logger);
            } catch (final SecurityException e) {
                this.logger.log(Logger.INFO, "[Error occured during the role conversion in the monitoring module]: "+e.getMessage());
            } catch (final NoSuchFieldException e) {
                this.logger.log(Logger.INFO, "[Error occured during the role conversion in the monitoring module]: "+e.getMessage());
            } catch (final IllegalArgumentException e) {
                this.logger.log(Logger.INFO, "[Error occured during the role conversion in the monitoring module]: "+e.getMessage());
            } catch (final IllegalAccessException e) {
                this.logger.log(Logger.INFO, "[Error occured during the role conversion in the monitoring module]: "+e.getMessage());
            } catch (final IOException e) {
                this.logger.log(Logger.INFO, "[Error occured during the role conversion in the monitoring module]: "+e.getMessage());
            } catch (final RoutingException e) {
                this.logger.log(Logger.INFO, "[Error occured during the role conversion in the monitoring module]: "+e.getMessage());
            }
        }
        if(exchangeTO != null){
            /*
             * Check if the exchange context linked to
             * the current exchange is already stored
             */
            ExchangeContext eContext = this.storageService.getStorage().get(exchangeTO.getExchangeId());
            if(eContext != null){
            /*
             * Update the message exchange and
             * set the ending timestamp
             */
            //updateMessageExchange(eContext.getExchange(), eContext.beginExchange, GregorianCalendar.getInstance().getTime());
                this.removeMessageExchange(exchangeTO.getExchangeId());
                if(eContext.getExchange().getIn() != null){
                    exchangeTO.setIn(eContext.getExchange().getIn());
                }
                if(eContext.getExchange().getOut() != null){
                    exchangeTO.setOut(eContext.getExchange().getOut());
                }
                if(eContext.getExchange().getFault() != null){
                    exchangeTO.setFault(eContext.getExchange().getFault());
                }
                this.storeMessageExchange(source, exchangeTO, GregorianCalendar.getInstance().getTime(), null);
            } else {
                this.storeMessageExchange(source, exchangeTO, GregorianCalendar.getInstance().getTime(), null);
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
     * Class representing an exchange context
     * this the current message exchange, its
     * provider and consumer, its begin and
     * ending timestamp
     * 
     * @author aruffie
     *
     */
    public class ExchangeContext{
        private org.ow2.petals.tools.monitoring.to.MessageExchange exchange;
        private Date beginExchange;
        private Date endingExchange;
        private String provider;
        private String consumer;
        /**
         * @param beginExchange
         * @param consumer
         * @param endingExchange
         * @param exchange
         * @param provider
         */
        public ExchangeContext(final Date beginExchange,
                                                  final String consumer,
                                                  final Date endingExchange,
                                                  final org.ow2.petals.tools.monitoring.to.MessageExchange exchange,
                                                  final String provider) {
            super();
            this.beginExchange = beginExchange;
            this.consumer = consumer;
            this.endingExchange = endingExchange;
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
         * @param exchange the exchange to set
         */
        public void setExchange(final org.ow2.petals.tools.monitoring.to.MessageExchange exchange) {
            this.exchange = exchange;
        }
        /**
         * @return the beginExchange
         */
        public Date getBeginExchange() {
            return this.beginExchange;
        }
        /**
         * @param beginExchange the beginExchange to set
         */
        public void setBeginExchange(final Date beginExchange) {
            this.beginExchange = beginExchange;
        }
        /**
         * @return the endingExchange
         */
        public Date getEndingExchange() {
            return this.endingExchange;
        }
        /**
         * @param endingExchange the endingExchange to set
         */
        public void setEndingExchange(final Date endingExchange) {
            this.endingExchange = endingExchange;
        }
        /**
         * @return the provider
         */
        public String getProvider() {
            return this.provider;
        }
        /**
         * @param provider the provider to set
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
         * @param consumer the consumer to set
         */
        public void setConsumer(final String consumer) {
            this.consumer = consumer;
        }
    }
}
