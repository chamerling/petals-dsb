/**
 * PETALS - PETALS Services Platform. Copyright (c) 2009 EBM Websourcing,
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
 */
package org.petalslink.dsb.kernel.monitoring.service.routing;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jbi.messaging.MessageExchange.Role;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.easywsdl.schema.api.XmlException;
import org.ow2.easywsdl.schema.util.SourceHelper;
import org.ow2.petals.jbi.component.context.ComponentContext;
import org.ow2.petals.jbi.messaging.exchange.MessageExchangeImpl;
import org.ow2.petals.jbi.messaging.exchange.MessageExchangeWrapper;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.jbi.messaging.routing.module.ReceiverModule;
import org.ow2.petals.jbi.messaging.routing.module.SenderModule;
import org.ow2.petals.transport.util.TransportSendContext;
import org.ow2.petals.util.oldies.LoggingUtil;
import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.kernel.monitoring.service.ConfigurationService;
import org.petalslink.dsb.kernel.monitoring.service.time.TimeStamperHandler;
import org.petalslink.dsb.monitoring.api.MonitoringClient;
import org.petalslink.dsb.monitoring.api.MonitoringClientFactory;
import org.petalslink.dsb.monitoring.api.ReportBean;
import org.petalslink.dsb.monitoring.api.ReportListBean;
import org.xml.sax.InputSource;

/**
 * This module is in charge of getting information from the message and to send
 * report to the monitoring layer is required... It creates two moniotring
 * reports per exchange so that we can detect if an exchange failed (According
 * to the right moniotring layer)...
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "reportSender", signature = SenderModule.class),
        @Interface(name = "reportReceiver", signature = ReceiverModule.class) })
public class MonitoringModuleTwoReportsPerExchange implements SenderModule, ReceiverModule {

    private org.ow2.petals.kernel.api.log.Logger log;

    private ExecutorService executorService;

    /**
     * The logger.
     */
    @Monolog(name = "logger")
    private Logger logger;

    @Requires(name = "monitoringconfiguration", signature = ConfigurationService.class)
    private ConfigurationService configuration;

    /**
     * The monitoring client which is in charge of sending messages to the
     * monitoring layer. Can be changed by configuration.
     */
    @Requires(name = "monitoringclientfactory", signature = MonitoringClientFactory.class)
    private MonitoringClientFactory monitoringClientFactory;

    /**
     * {@inheritDoc}
     */
    public void electEndpoints(
            Map<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint, TransportSendContext> electedDestinations,
            ComponentContext sourceComponentContext, MessageExchangeWrapper exchange)
            throws RoutingException {
        this.log.call();

        // FIXME : For now, bypass all if the monitoring is not active. Must
        // cache things in a next version and activate/unactivate at runtime...
        if (!this.configuration.isActive()) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Monitoring is not active, do not report time");
            }
            return;
        }

        try {
            ReportListBean reports = this.createReportListFromExchange(exchange);
            if (this.log.isDebugEnabled()) {
                this.log.debug("In Report Module electDestinations");
            }
            if (reports.getReports().size() == 2) {
                this.sendReport(reports, exchange.getEndpoint().getEndpointName());
            } else {
                log.debug("Bad number of reports " + reports.getReports().size());
            }

        } catch (Exception e) {
            final String message = "Problem while generating or sending report on send...";
            if (log.isDebugEnabled()) {
                log.warning(message, e);
            } else {
                log.warning(message);
            }
        }
    }

    public boolean receiveExchange(MessageExchangeWrapper exchange, ComponentContext arg1)
            throws RoutingException {
        if (!this.configuration.isActive()) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Monitoring is not active, do not report time");
            }
            return true;
        }

        if (exchange != null) {
            try {
                if (this.log.isDebugEnabled()) {
                    this.log.info("In Report Module: receiveExchange");
                }
                ReportListBean reports = this.createReportListFromExchange(exchange);
                if (reports.getReports().size() == 2) {
                    this.sendReport(reports, exchange.getEndpoint().getEndpointName());
                } else {
                    log.debug("Bad number of reports " + reports.getReports().size());
                }
            } catch (Exception e) {
                final String message = "Problem while generating or sending report on receive...";
                if (log.isDebugEnabled()) {
                    log.warning(message, e);
                } else {
                    log.warning(message);
                }
            }
        } else {
            // FIXME : wrong...
            this.log.warning("No exchange to create report from...");
        }
        return true;
    }

    private void sendReport(final ReportListBean reports, final String endpointName)
            throws Exception {
        // this may be completely async
        final MonitoringClient client = this.getMonitoringClient(endpointName);
        if (client == null) {
            throw new DSBException(
                    "Can not get any client to send report to monitoring layer for endpoint %s",
                    endpointName);
        }

        // let's do it in multithreaded way...
        this.executorService.submit(new Runnable() {
            public void run() {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Sending report for endpoint %s : %s", endpointName,
                            reports));
                }

                try {
                    client.send(reports);
                } catch (DSBException e) {
                    final String message = "Error while sending report";
                    if (log.isDebugEnabled()) {
                        log.warning(message, e);
                    } else {
                        log.warning(message);                        
                    }
                }
            }
        });
    }

    /**
     * @param address
     * @return
     */
    private MonitoringClient getMonitoringClient(String endpointName) {
        return monitoringClientFactory.getMonitoringClient(endpointName);
    }

    protected ReportListBean createReportListFromExchange(MessageExchangeWrapper exchange)
            throws DSBException {
        ReportListBean res = new ReportListBean();
        try {
            // flash on request in
            if (MessageExchangeImpl.Role.CONSUMER.equals(exchange.getRole())) {

                // handle in request for all message patterns. This is detected
                // with the exchnage terminated flag.
                if ((MessageExchangeImpl.IN_ONLY_PATTERN.equals(exchange.getPattern()) && !exchange
                        .isTerminated())
                        || (((MessageExchangeImpl.IN_OUT_PATTERN.equals(exchange.getPattern()) || (MessageExchangeImpl.IN_OPTIONAL_OUT_PATTERN
                                .equals(exchange.getPattern())))
                                && (exchange.getMessage("out") == null) && !exchange.isTerminated()))) {

                    if (log.isDebugEnabled()) {
                        log.debug("IN* & T1 Phase : "
                                + TimeStamperHandler.getInstance().getTimeStamp(exchange)
                                        .getDateClientIn());
                    }
                }

                // handle done request for InOnly message...
                if (MessageExchangeImpl.IN_ONLY_PATTERN.equals(exchange.getPattern())
                        && exchange.isTerminated()) {

                    if (log.isDebugEnabled()) {
                        log.debug("INONLY & T3 + T4 Phase for exchange terminated");
                    }

                    // provider out date
                    ReportBean report3 = new ReportBean();
                    report3.setType("t3");
                    this.setSOACommonInformation(exchange, report3);
                    report3.setDate(TimeStamperHandler.getInstance().getTimeStamp(exchange)
                            .getDateProviderOut());
                    // no out
                    report3.setContentLength(0L);
                    report3.setException(false);
                    res.getReports().add(report3);

                    // create date client out
                    ReportBean report4 = new ReportBean();
                    report4.setType("t4");
                    this.setSOACommonInformation(exchange, report4);
                    report4.setDate(TimeStamperHandler.getInstance().getTimeStamp(exchange)
                            .getDateClientOut());
                    // no out
                    report4.setContentLength(0);
                    report4.setException(false);
                    res.getReports().add(report4);
                }

                // handle in out request for out messages
                if ((MessageExchangeImpl.IN_OUT_PATTERN.equals(exchange.getPattern()) || MessageExchangeImpl.IN_OPTIONAL_OUT_PATTERN
                        .equals(exchange.getPattern()))
                        && ((exchange.getMessage("out") != null) || (exchange.getFault() != null) || (exchange
                                .getError() != null))) {

                    if (log.isDebugEnabled()) {
                        log.debug("IN+OUT* & T3 + T4 Phase for output/fault/error message");
                    }

                    // create date provider out
                    ReportBean report3 = new ReportBean();
                    report3.setType("t3");
                    this.setSOACommonInformation(exchange, report3);
                    report3.setDate(TimeStamperHandler.getInstance().getTimeStamp(exchange)
                            .getDateProviderOut());

                    if (exchange.getMessage("out") != null) {
                        if ((exchange.getMessage("out").getContent() instanceof StreamSource)
                                && ((StreamSource) exchange.getMessage("out").getContent() != null)
                                && (((StreamSource) exchange.getMessage("out").getContent())
                                        .getInputStream() != null)) {
                            report3.setContentLength(((StreamSource) exchange.getMessage("out")
                                    .getContent()).getInputStream().available());
                        } else if ((exchange.getMessage("out").getContent() instanceof DOMSource)
                                && ((DOMSource) exchange.getMessage("out").getContent() != null)) {
                            InputSource source = SourceHelper
                                    .convertDOMSource2InputSource((DOMSource) exchange.getMessage(
                                            "out").getContent());
                            report3.setContentLength(source.getByteStream().available());
                        } else {
                            report3.setContentLength(-1);
                            log.debug("Can not calcultate size on unknown OUT message type");
                        }
                    } else {
                        report3.setContentLength(0);
                    }

                    if ((exchange.getFault() != null) || (exchange.getError() != null)) {
                        report3.setException(true);
                    } else {
                        report3.setException(false);
                    }

                    res.getReports().add(report3);

                    // create date client out
                    ReportBean report4 = new ReportBean();
                    report4.setType("t4");
                    this.setSOACommonInformation(exchange, report4);
                    report4.setDate(TimeStamperHandler.getInstance().getTimeStamp(exchange)
                            .getDateClientOut());
                    // same length as T3...
                    report4.setContentLength(report3.getContentLength());
                    if ((exchange.getFault() != null) || (exchange.getError() != null)) {
                        report4.setException(true);
                    } else {
                        report4.setException(false);
                    }
                    res.getReports().add(report4);
                }

            } else if (exchange.getRole() == Role.PROVIDER) {
                // provider, must have T2 and T1, so send the report...
                if ((MessageExchangeImpl.IN_ONLY_PATTERN.equals(exchange.getPattern()) && !exchange
                        .isTerminated())
                        || (((MessageExchangeImpl.IN_OUT_PATTERN.equals(exchange.getPattern()) || (MessageExchangeImpl.IN_OPTIONAL_OUT_PATTERN
                                .equals(exchange.getPattern())))
                                && (exchange.getMessage("out") == null) && !exchange.isTerminated()))) {
                    // we can have T3 here is exchnage.getOut is not null...

                    // creat T1 + T2 report, then send to monitoring layer...
                    int size = 0;
                    if (exchange.getMessage("in") != null) {
                        if ((exchange.getMessage("in").getContent() instanceof StreamSource)
                                && ((StreamSource) exchange.getMessage("in").getContent() != null)
                                && (((StreamSource) exchange.getMessage("in").getContent())
                                        .getInputStream() != null)) {
                            size = ((StreamSource) exchange.getMessage("in").getContent())
                                    .getInputStream().available();
                        } else if ((exchange.getMessage("in").getContent() instanceof DOMSource)
                                && ((DOMSource) exchange.getMessage("in").getContent() != null)) {
                            InputSource source = SourceHelper
                                    .convertDOMSource2InputSource((DOMSource) exchange.getMessage(
                                            "in").getContent());
                            size = source.getByteStream().available();
                        } else {
                            log.debug("Can not calcultate size on unknown message type");
                        }
                    }

                    ReportBean report1 = new ReportBean();
                    report1.setType("t1");
                    this.setSOACommonInformation(exchange, report1);
                    report1.setDate(TimeStamperHandler.getInstance().getTimeStamp(exchange)
                            .getDateClientIn());
                    report1.setContentLength(size);
                    res.getReports().add(report1);

                    ReportBean report2 = new ReportBean();
                    report2.setType("t2");
                    this.setSOACommonInformation(exchange, report2);
                    report2.setDate(TimeStamperHandler.getInstance().getTimeStamp(exchange)
                            .getDateProviderIn());
                    report2.setContentLength(size);
                    res.getReports().add(report2);

                    if (log.isDebugEnabled()) {
                        log.debug("PROVIDER IN T2? = "
                                + TimeStamperHandler.getInstance().getTimeStamp(exchange)
                                        .getDateProviderIn());
                        log.debug("PROVIDER IN T3? = "
                                + TimeStamperHandler.getInstance().getTimeStamp(exchange)
                                        .getDateProviderOut());
                    }
                }

            }
        } catch (IOException e) {
            throw new DSBException(e);
        } catch (XmlException e) {
            throw new DSBException(e);
        }
        return res;
    }

    private void setSOACommonInformation(MessageExchangeWrapper exchange, ReportBean report) {
        // TODO : Get the MEX information for WS-Addressing information and
        // inject it in the report.
        // exchange.getProperty("WSA:TO");
        if ((exchange.getProperty("wsa:to") != null)
                && (exchange.getProperty("wsa:to") instanceof String)) {
            // TODO = Put it as a suffix in a report information
        }

        report.setExchangeId(exchange.getExchangeId());
        if (exchange.getService() != null) {
            report.setServiceName(exchange.getService().toString());
        }
        if (exchange.getEndpoint() != null) {
            report.setEndpoint(exchange.getEndpoint().getEndpointName());
        }
        if (exchange.getOperation() != null) {
            report.setOperation(exchange.getOperation().toString());
        }
        if (exchange.getInterfaceName() != null) {
            report.setItf(exchange.getInterfaceName().toString());
        }

        // FIXME = Strange!
        if (exchange.getConsumerEndpoint().toString() != null) {
            report.setConsumer(exchange.getConsumerEndpoint().toString());
        } else {
            report.setConsumer(exchange.getConsumerEndpoint().toString());
        }
        if (exchange.getService() != null) {
            report.setProvider(exchange.getService().toString());
        }
    }

    /**
     * Start the Fractal component
     */
    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
        this.executorService = Executors.newFixedThreadPool(10);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
        if (executorService != null) {
            this.executorService.shutdownNow();
        }
    }

}
