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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
import org.ow2.petals.jbi.messaging.exchange.MessageExchange;
import org.ow2.petals.jbi.messaging.routing.RoutingException;
import org.ow2.petals.jbi.messaging.routing.module.ReceiverModule;
import org.ow2.petals.jbi.messaging.routing.module.SenderModule;
import org.ow2.petals.transport.util.TransportSendContext;
import org.ow2.petals.util.LoggingUtil;
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
 * report to the monitoring layer is required...
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "reportSender", signature = SenderModule.class),
        @Interface(name = "reportReceiver", signature = ReceiverModule.class) })
public class MonitoringModule implements SenderModule, ReceiverModule {

    private org.ow2.petals.kernel.api.log.Logger log;

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
     * key = uuid exchange, value = length of in message
     */
    private final Map<String, Integer> map = Collections
            .synchronizedMap(new HashMap<String, Integer>());

    /**
     * {@inheritDoc}
     */
    public void electEndpoints(
            Map<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint, TransportSendContext> electedDestinations,
            ComponentContext sourceComponentContext, MessageExchange exchange)
            throws RoutingException {
        this.log.call();

        // FIXME : For now, bypass all if the monitoring is not active. Must
        // cache things in a next version
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
            if (reports.getReports().size() > 0) {
                this.sendRawReport(reports);
            }

        } catch (Exception e) {
            throw new RoutingException(e);
        }
    }

    public boolean receiveExchange(MessageExchange exchange, ComponentContext arg1)
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
                if (reports.getReports().size() > 0) {
                    this.sendReport(reports, exchange.getEndpoint().getEndpointName());
                }
            } catch (Exception e) {
                throw new RoutingException(e);
            }
        } else {
            // FIXME : wrong...
            this.log.warning("ERROR: TIMEOUT");
            throw new RoutingException("Impossible to create report");
        }
        return true;
    }

    private void sendReport(ReportListBean reports, String endpointName) throws Exception {
        // this may be completely async
        MonitoringClient client = this.getMonitoringClient(endpointName);
        if (client == null) {
            throw new DSBException(
                    "Can not get any client to send report to monitoring layer for endpoint %s",
                    endpointName);
        }
        client.send(reports);
    }
    
    private void sendRawReport(ReportListBean reports) throws Exception {
        MonitoringClient client = this.monitoringClientFactory.getRawMonitoringClient();
        if (client == null) {
            throw new DSBException(
                    "Can not get any client to send RAW report to monitoring layer");
        }
        client.send(reports);
    }

    /**
     * @param address
     * @return
     */
    private MonitoringClient getMonitoringClient(String endpointName) {
        return monitoringClientFactory.getMonitoringClient(endpointName);
    }

    protected ReportListBean createReportListFromExchange(MessageExchange exchange)
            throws DSBException {
        ReportListBean res = new ReportListBean();
        try {
            // flash on request in
            if (MessageExchange.Role.CONSUMER.equals(exchange.getRole())) {

                // handle in request
                if ((MessageExchange.IN_ONLY_PATTERN.equals(exchange.getPattern()) && !exchange
                        .isTerminated())
                        || (((MessageExchange.IN_OUT_PATTERN.equals(exchange.getPattern()) || (MessageExchange.IN_OPTIONAL_OUT_PATTERN
                                .equals(exchange.getPattern())))
                                && (exchange.getMessage("out") == null) && !exchange.isTerminated()))) {
                    // stock message
                    if (!this.map.containsKey(exchange.getExchangeId())) {
                        if (exchange.getMessage("in").getContent() == null) {
                            this.map.put(exchange.getExchangeId(), 0);
                        } else {
                            if (exchange.getMessage("in").getContent() instanceof StreamSource) {
                                this.map.put(exchange.getExchangeId(), ((StreamSource) exchange
                                        .getMessage("in").getContent()).getInputStream()
                                        .available());
                            } else if (exchange.getMessage("in").getContent() instanceof DOMSource) {
                                InputSource source = SourceHelper
                                        .convertDOMSource2InputSource((DOMSource) exchange
                                                .getMessage("in").getContent());
                                this.map.put(exchange.getExchangeId(), source.getByteStream()
                                        .available());
                            } else {
                                throw new DSBException("Source unknown: "
                                        + exchange.getMessage("in").getContent().getClass());
                            }
                        }
                    }
                }

                // handle done request
                if (MessageExchange.IN_ONLY_PATTERN.equals(exchange.getPattern())
                        && exchange.isTerminated()) {

                    // create date client in
                    ReportBean report1 = new ReportBean();
                    report1.setType("t1");
                    this.setSOACommonInformation(exchange, report1);
                    report1.setDate(TimeStamperHandler.getInstance().getTimeStamp(exchange)
                            .getDateClientIn());
                    report1.setContentLength(this.map.remove(exchange.getExchangeId()));

                    res.getReports().add(report1);

                    // create date provider in
                    ReportBean report2 = new ReportBean();
                    report1.setType("t2");
                    this.setSOACommonInformation(exchange, report2);
                    report2.setDate(TimeStamperHandler.getInstance().getTimeStamp(exchange)
                            .getDateProviderIn());
                    if (exchange.getMessage("in") != null) {
                        if ((exchange.getMessage("in").getContent() instanceof StreamSource)
                                && ((StreamSource) exchange.getMessage("in").getContent() != null)
                                && (((StreamSource) exchange.getMessage("in").getContent())
                                        .getInputStream() != null)) {
                            report2.setContentLength(((StreamSource) exchange.getMessage("in")
                                    .getContent()).getInputStream().available());
                        } else if ((exchange.getMessage("in").getContent() instanceof DOMSource)
                                && ((DOMSource) exchange.getMessage("in").getContent() != null)) {
                            InputSource source = SourceHelper
                                    .convertDOMSource2InputSource((DOMSource) exchange.getMessage(
                                            "in").getContent());
                            report2.setContentLength(source.getByteStream().available());
                        } else {
                            throw new DSBException("Source unknown");
                        }
                    } else {
                        report2.setContentLength(-1L);
                    }
                    res.getReports().add(report2);

                    // create date provider out
                    ReportBean report3 = new ReportBean();
                    report1.setType("t3");
                    this.setSOACommonInformation(exchange, report3);
                    report3.setDate(TimeStamperHandler.getInstance().getTimeStamp(exchange)
                            .getDateProviderOut());
                    report3.setContentLength(0L);
                    report3.setException(false);
                    res.getReports().add(report3);

                    // create date client out
                    ReportBean report4 = new ReportBean();
                    report1.setType("t4");
                    this.setSOACommonInformation(exchange, report4);
                    report4.setDate(TimeStamperHandler.getInstance().getTimeStamp(exchange)
                            .getDateClientOut());
                    report4.setContentLength(0);
                    report4.setException(false);
                    res.getReports().add(report4);
                }

                // handle out request
                if ((MessageExchange.IN_OUT_PATTERN.equals(exchange.getPattern()) || MessageExchange.IN_OPTIONAL_OUT_PATTERN
                        .equals(exchange.getPattern()))
                        && ((exchange.getMessage("out") != null) || (exchange.getFault() != null) || (exchange
                                .getError() != null))) {

                    // create date client in
                    ReportBean report1 = new ReportBean();
                    report1.setType("t1");
                    this.setSOACommonInformation(exchange, report1);
                    report1.setDate(TimeStamperHandler.getInstance().getTimeStamp(exchange)
                            .getDateClientIn());
                    report1.setContentLength(this.map.remove(exchange.getExchangeId()));
                    res.getReports().add(report1);

                    // create date provider in
                    ReportBean report2 = new ReportBean();
                    report1.setType("t2");
                    this.setSOACommonInformation(exchange, report2);
                    report2.setDate(TimeStamperHandler.getInstance().getTimeStamp(exchange)
                            .getDateProviderIn());
                    report2.setContentLength(report1.getContentLength());
                    res.getReports().add(report2);

                    // create date provider out
                    ReportBean report3 = new ReportBean();
                    report1.setType("t3");
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
                            throw new DSBException("Source unknown: "
                                    + exchange.getMessage("out").getContent());
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
                    report1.setType("t4");
                    this.setSOACommonInformation(exchange, report4);
                    report4.setDate(TimeStamperHandler.getInstance().getTimeStamp(exchange)
                            .getDateProviderOut());
                    report4.setContentLength(report3.getContentLength());
                    if ((exchange.getFault() != null) || (exchange.getError() != null)) {
                        report4.setException(true);
                    } else {
                        report4.setException(false);
                    }
                    res.getReports().add(report4);
                }
            }
        } catch (IOException e) {
            throw new DSBException(e);
        } catch (XmlException e) {
            throw new DSBException(e);
        }
        return res;
    }

    private void setSOACommonInformation(MessageExchange exchange, ReportBean report) {
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
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

}
