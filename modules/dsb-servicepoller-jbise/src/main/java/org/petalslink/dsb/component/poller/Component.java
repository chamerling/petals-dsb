package org.petalslink.dsb.component.poller;

import java.io.ByteArrayOutputStream;
import java.util.logging.Level;

import javax.jbi.JBIException;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessageExchangeFactory;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.ow2.petals.component.framework.PetalsBindingComponent;
import org.ow2.petals.component.framework.api.Message.MEPConstants;
import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.message.ExchangeImpl;
import org.ow2.petals.component.framework.su.AbstractServiceUnitManager;
import org.petalslink.dsb.service.poller.QuartzPollingManagerImpl;
import org.petalslink.dsb.service.poller.api.PollerException;
import org.petalslink.dsb.service.poller.api.PollingManager;
import org.petalslink.dsb.service.poller.api.PollingTransport;
import org.petalslink.dsb.service.poller.api.ServiceInformation;
import org.w3c.dom.Document;

/**
 * 
 * @author chamerling
 * 
 */
public class Component extends PetalsBindingComponent implements PollingTransport {

    private PollingManager pollingManager;

    @Override
    protected void postDoInit() throws JBIException {
        if (this.pollingManager == null) {
            this.pollingManager = new QuartzPollingManagerImpl();
        }
        try {
            this.pollingManager.init();
        } catch (PollerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doStart() throws JBIException {
        try {
            this.pollingManager.start();
        } catch (PollerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doStop() throws JBIException {
        try {
            this.pollingManager.stop();
        } catch (PollerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected AbstractServiceUnitManager createServiceUnitManager() {
        return new SUManager(this);
    }

    public Document send(Document inputMessage, ServiceInformation service) throws PollerException {
        if (this.getLogger().isLoggable(Level.FINE)) {
            this.getLogger().fine("Sending a message through the JBI channel...");
            this.getLogger().fine("Destination is " + service);
            try {
                Source source = new javax.xml.transform.dom.DOMSource(inputMessage);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                TransformerFactory.newInstance().newTransformer()
                        .transform(source, new StreamResult(outputStream));
                this.getLogger().fine("Input message is : " + outputStream.toString());
            } catch (Exception e) {
                this.getLogger()
                        .log(Level.FINE, "Error while creating DOM as String", e.getCause());
            }
        }

        Exchange exchange = null;
        try {
            exchange = this.createMessageExchange();
            exchange.setInMessageContent(inputMessage);
            exchange.setOperation(service.operation);
            exchange.setService(service.service);
            exchange.setInterfaceName(service.itf);

        } catch (final Exception e) {
            final String errorMsg = "Error while transforming request to JBI MessageExchange";
            if (this.getLogger().isLoggable(Level.FINE)) {
                this.getLogger().log(Level.FINE, errorMsg, e);
            }
            throw new PollerException(errorMsg, e);
        }

        boolean sent = false;
        try {
            sent = this.getChannel().sendSync(((ExchangeImpl) exchange).getMessageExchange());
        } catch (final MessagingException e) {
            final String errorMsg = "Error while sending message through JBI NMR.";
            if (this.getLogger().isLoggable(Level.FINE)) {
                this.getLogger().log(Level.FINE, errorMsg, e);
            }
            throw new PollerException(errorMsg, e);
        }

        if (!sent) {
            String errorMsg = "A timeout occurs calling the consumed service.";
            if (this.getLogger().isLoggable(Level.FINE)) {
                this.getLogger().log(Level.FINE, errorMsg);
            }
            throw new PollerException(errorMsg);
        }

        return this.processResponse(exchange);
    }

    protected org.ow2.petals.component.framework.api.message.Exchange createMessageExchange()
            throws MessagingException {
        MessageExchangeFactory factory = this.getChannel().createExchangeFactory();
        final MessageExchange exchange = factory
                .createExchange(MEPConstants.IN_OUT_PATTERN.value());
        return new ExchangeImpl(exchange);
    }

    protected void closeMessageExchange(final Exchange exchange) throws PollerException {
        // close the messageExchange with the JBI NMR
        try {
            exchange.setDoneStatus();
            this.getChannel().send(((ExchangeImpl) exchange).getMessageExchange());
        } catch (final MessagingException e) {
            final String errorMsg = "Error while closing JBI MessageExchange.";
            throw new PollerException(errorMsg, e);
        }
    }

    protected Document processResponse(Exchange exchange) throws PollerException {
        Document result = null;
        this.getLogger().fine("Processing response for status " + exchange.getStatus());

        if (exchange.isDoneStatus()) {
            this.getLogger().fine("Done status");
            return null;
        } else if (exchange.isErrorStatus()) {
            throw new PollerException(exchange.getError());

        } else if (exchange.isActiveStatus()) {
            try {
                if (exchange.getOutMessage() != null) {
                    NormalizedMessage nm = exchange.getOutMessage();
                    if (nm == null) {
                        final String errorMsg = "The MEP '" + exchange.getPattern()
                                + "' does not accept a null response";
                        throw new PollerException(errorMsg);
                    } else {
                        try {
                            result = exchange.getOutMessageContentAsDocument();
                        } catch (MessagingException e) {
                            throw new PollerException(e.getMessage());
                        }
                    }
                } else if (exchange.getFault() != null) {
                    // TODO
                    // result = this.createMessage(exchange.getFault());
                } else {
                    // No output
                }
            } finally {
                this.closeMessageExchange(exchange);
            }
        } else {
            String errorMsg = "Bad exchange status";
            if (this.getLogger().isLoggable(Level.FINE)) {
                this.getLogger().log(Level.FINE, errorMsg);
            }
            throw new PollerException(errorMsg);
        }

        if (this.getLogger().isLoggable(Level.FINE)) {
            if (result != null) {
                try {
                    Source source = new javax.xml.transform.dom.DOMSource(result);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    TransformerFactory.newInstance().newTransformer()
                            .transform(source, new StreamResult(outputStream));
                    this.getLogger().fine("Output message is : " + outputStream.toString());
                } catch (Exception e) {
                    this.getLogger().log(Level.FINE, "Error while creating DOM as String",
                            e.getCause());
                }
            } else {
                this.getLogger().fine("Output message is null");
            }
        }

        return result;
    }

    public PollingManager getPollingManager() {
        return pollingManager;
    }
}
