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
package org.ow2.petals.binding.restproxy;

import java.util.Set;

import javax.jbi.JBIException;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessageExchangeFactory;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.ow2.petals.binding.restproxy.in.ListService;
import org.ow2.petals.binding.restproxy.in.REST2JBIService;
import org.ow2.petals.binding.restproxy.in.RESTEngineContext;
import org.ow2.petals.binding.restproxy.in.RESTProxyService;
import org.ow2.petals.binding.restproxy.in.StatsService;
import org.ow2.petals.binding.restproxy.in.server.RESTServerJob;
import org.ow2.petals.binding.restproxy.out.CommonsHTTPClient;
import org.ow2.petals.component.framework.ComponentInformation;
import org.ow2.petals.component.framework.PetalsBindingComponent;
import org.ow2.petals.component.framework.api.Message.MEPConstants;
import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.message.ExchangeImpl;
import org.ow2.petals.component.framework.su.AbstractServiceUnitManager;
import org.ow2.petals.messaging.framework.Engine;
import org.ow2.petals.messaging.framework.EngineException;
import org.ow2.petals.messaging.framework.EngineFactory;
import org.ow2.petals.messaging.framework.lifecycle.LifeCycleException;
import org.ow2.petals.messaging.framework.message.Callback;
import org.ow2.petals.messaging.framework.message.Client;
import org.ow2.petals.messaging.framework.message.Message;
import org.ow2.petals.messaging.framework.message.MessageImpl;
import org.ow2.petals.messaging.framework.message.MessagingEngine;
import org.ow2.petals.messaging.framework.message.MessagingEngineImpl;
import org.ow2.petals.messaging.framework.message.mime.ReaderRegistry;
import org.ow2.petals.messaging.framework.message.mime.WriterRegistry;
import org.ow2.petals.messaging.framework.message.mime.reader.ApplicationXMLReader;
import org.ow2.petals.messaging.framework.message.mime.reader.RawReader;
import org.ow2.petals.messaging.framework.message.mime.reader.TextHTMLReader;
import org.ow2.petals.messaging.framework.message.mime.reader.TextPlainReader;
import org.ow2.petals.messaging.framework.message.mime.reader.TextXMLReader;
import org.ow2.petals.messaging.framework.message.mime.reader.XwwwFormURLEncodedReader;
import org.ow2.petals.messaging.framework.message.mime.writer.TextHTMLWriter;
import org.ow2.petals.messaging.framework.message.mime.writer.TextPlainWriter;
import org.ow2.petals.messaging.framework.message.mime.writer.TextXMLWriter;
import org.ow2.petals.messaging.framework.plugins.job.JobManager;
import org.ow2.petals.messaging.framework.plugins.service.ServiceManager;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class Component extends PetalsBindingComponent implements Client {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postDoInit() throws JBIException {
        this.configureEngine();
        try {
            EngineFactory.getEngine().init();
        } catch (LifeCycleException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    private void configureEngine() {
        Engine engine = EngineFactory.getEngine();
        RESTEngineContext context = new RESTEngineContext();
        try {
            engine.addComponent(RESTEngineContext.class, context);
        } catch (EngineException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }

        String path = this.getComponentExtensions().get(Constants.PATH);
        if (path == null) {
            path = Constants.DEFAULT_PATH;
        }
        if ((path.trim().length() > 0) && (path.charAt(0) != '/')) {
            path = "/" + path;
        }

        String proxyPath = this.getComponentExtensions().get(Constants.PROXY_PATH);
        if (proxyPath == null) {
            proxyPath = Constants.DEFAULT_PROXY_PATH;
        }
        if ((proxyPath.trim().length() > 0) && (proxyPath.charAt(0) != '/')) {
            proxyPath = "/" + proxyPath;
        }

        int port = Constants.DEFAULT_PORT;
        String containerPort = this.getContainerConfiguration("port");
        if (containerPort != null) {
            try {
                port = Integer.parseInt(containerPort.trim());
            } catch (NumberFormatException e) {
            }
        }
        if (port == Constants.DEFAULT_PORT) {
            String tmp = this.getComponentExtensions().get(Constants.PORT);
            if (tmp != null) {
                try {
                    port = Integer.parseInt(tmp.trim());
                } catch (NumberFormatException e) {
                }
            }
        }

        context.setProxyPath(proxyPath);
        context.setServicePath(path);
        context.setPort(port);

        ServiceManager serviceManager = new ServiceManager();
        serviceManager.add("/list/*", new ListService());
        serviceManager.add("/stats/*", new StatsService());
        serviceManager.add(proxyPath + "/*", new RESTProxyService());
        serviceManager.add(path + "/*", new REST2JBIService());
        try {
            engine.addComponent(ServiceManager.class, serviceManager);
        } catch (EngineException e3) {
            e3.printStackTrace();
        }

        JobManager jobManager = new JobManager();
        jobManager.add("RESTSERVERJob", new RESTServerJob());
        try {
            engine.addComponent(JobManager.class, jobManager);
        } catch (EngineException e2) {
        }

        MessagingEngine mEngine = new MessagingEngineImpl();
        try {
            mEngine.addClient("http", new CommonsHTTPClient(this.getLogger()));
        } catch (EngineException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            mEngine.addClient("jbi", this);
        } catch (EngineException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            engine.addComponent(MessagingEngine.class, mEngine);
        } catch (EngineException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        ReaderRegistry readers = new ReaderRegistry();
        readers.put(
                org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_X_WWW_FORM_URLENCODED,
                new XwwwFormURLEncodedReader());
        readers.put(org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_TEXT_XML,
                new TextXMLReader());
        readers.put(org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_APPLICATION_XML,
                new ApplicationXMLReader());
        readers.put(org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_TEXT_HTML,
                new TextHTMLReader());
        readers.put(org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_TEXT_PLAIN,
                new TextPlainReader());
        readers.put(org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_RAW,
                new RawReader());
        try {
            engine.addComponent(ReaderRegistry.class, readers);
        } catch (EngineException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        WriterRegistry writers = new WriterRegistry();
        writers.put(org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_TEXT_XML,
                new TextXMLWriter());
        writers.put(org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_TEXT_PLAIN,
                new TextPlainWriter());
        writers.put(org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_TEXT_HTML,
                new TextHTMLWriter());
        writers.put(org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_APPLICATION_XML,
                new TextXMLWriter());
        writers.put(org.ow2.petals.messaging.framework.Constants.MimeTypes.TYPE_RAW,
                new TextPlainWriter());
        try {
            engine.addComponent(WriterRegistry.class, writers);
        } catch (EngineException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStart() throws JBIException {

        // share information with the service bus
        RESTEngineContext context = EngineFactory.getEngine().getComponent(RESTEngineContext.class);
        String proxy = "http://$HOST:" + context.getPort() + context.getProxyPath();
        String service = "http://$HOST:" + context.getPort() + context.getServicePath();
        this.getPlugin(ComponentInformation.class).addProperty("proxy", proxy);
        this.getPlugin(ComponentInformation.class).addProperty("service", service);

        try {
            EngineFactory.getEngine().start();
        } catch (LifeCycleException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStop() throws JBIException {
        try {
            EngineFactory.getEngine().stop();
        } catch (LifeCycleException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Message send(Message message)
            throws org.ow2.petals.messaging.framework.message.MessagingException {
        Exchange exchange = null;
        try {

            exchange = this.createMessageExchange();
            if (message.getContent(javax.xml.transform.Source.class) != null) {
                exchange.setInMessageContent(message.getContent(javax.xml.transform.Source.class));
            } else {
                // create a default payload...
            }

            exchange.setProperty("wsa:to", message
                    .get(org.ow2.petals.messaging.framework.message.Constants.HTTP_URL));

            exchange.setOperation((QName) message
                    .get(org.ow2.petals.messaging.framework.message.Constants.OPERATION));
            exchange.setService((QName) message
                    .get(org.ow2.petals.messaging.framework.message.Constants.SERVICE));
            exchange.setInterfaceName((QName) message
                    .get(org.ow2.petals.messaging.framework.message.Constants.INTERFACE));

            for (String key : message.getAll().keySet()) {
                exchange.setInMessageProperty(key, message.get(key));
            }
        } catch (final Exception e) {
            final String errorMsg = "Error while transforming request to JBI MessageExchange";
            throw new org.ow2.petals.messaging.framework.message.MessagingException(errorMsg, e);
        }

        boolean sent = false;
        try {
            sent = this.getChannel().sendSync(((ExchangeImpl) exchange).getMessageExchange());
        } catch (final MessagingException e) {
            final String errorMsg = "Error while sending message through JBI NMR.";
            throw new org.ow2.petals.messaging.framework.message.MessagingException(errorMsg, e);
        }

        if (!sent) {
            String errorMsg = "A timeout occurs calling the consumed service.";
            throw new org.ow2.petals.messaging.framework.message.MessagingException(errorMsg);
        }

        return this.processResponse(exchange);
    }

    /**
     * {@inheritDoc}
     */
    public void send(Message in, Callback callback)
            throws org.ow2.petals.messaging.framework.message.MessagingException {
        throw new org.ow2.petals.messaging.framework.message.MessagingException("Not implemented");
    }

    /**
     * Return the response from the exchange
     * 
     * @param exchange
     * @return
     * @throws RESTException
     */
    protected Message processResponse(Exchange exchange)
            throws org.ow2.petals.messaging.framework.message.MessagingException {
        this.getLogger().fine("Processing response for status " + exchange.getStatus());
        Message result = new MessageImpl();

        if (exchange.isDoneStatus()) {
            this.getLogger().fine("Done status");

            return null;

        } else if (exchange.isErrorStatus()) {
            throw new org.ow2.petals.messaging.framework.message.MessagingException(exchange
                    .getError());

        } else if (exchange.isActiveStatus()) {
            try {
                if (exchange.getOutMessage() != null) {
                    NormalizedMessage nm = exchange.getOutMessage();
                    if (nm == null) {
                        final String errorMsg = "The MEP '" + exchange.getPattern()
                                + "' does not accept a null response";
                        throw new org.ow2.petals.messaging.framework.message.MessagingException(
                                errorMsg);
                    } else {
                        result = this.createMessage(nm);
                    }
                } else if (exchange.getFault() != null) {
                    result = this.createMessage(exchange.getFault());
                } else {

                }
            } finally {
                this.closeMessageExchange(exchange);
            }
        } else {
            throw new org.ow2.petals.messaging.framework.message.MessagingException(
                    "Bad exchange status");
        }
        return result;
    }

    private Message createMessage(final NormalizedMessage nm) {
        Message result = new MessageImpl();
        result.setContent(Source.class, nm.getContent());
        Set<?> props = nm.getPropertyNames();
        if (props != null) {
            for (Object object : props) {
                String key = object.toString();
                this.getLogger().fine("Setting property " + key);
                result.put(key, nm.getProperty(key).toString());
            }
        }
        return result;
    }

    protected void closeMessageExchange(final Exchange exchange)
            throws org.ow2.petals.messaging.framework.message.MessagingException {
        // close the messageExchange with the JBI NMR
        try {
            exchange.setDoneStatus();
            this.getChannel().send(((ExchangeImpl) exchange).getMessageExchange());
        } catch (final MessagingException e) {
            final String errorMsg = "Error while closing JBI MessageExchange.";
            throw new org.ow2.petals.messaging.framework.message.MessagingException(errorMsg, e);
        }
    }

    /**
     * @return
     * @throws MessagingException
     */
    protected org.ow2.petals.component.framework.api.message.Exchange createMessageExchange()
            throws MessagingException {
        MessageExchangeFactory factory = this.getChannel().createExchangeFactory();
        final MessageExchange exchange = factory
                .createExchange(MEPConstants.IN_OUT_PATTERN.value());
        return new ExchangeImpl(exchange);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractServiceUnitManager createServiceUnitManager() {
        return new SUManager(this);
    }
}
