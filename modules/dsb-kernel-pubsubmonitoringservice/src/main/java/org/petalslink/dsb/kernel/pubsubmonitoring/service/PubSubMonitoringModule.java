/**
 * 
 */
package org.petalslink.dsb.kernel.pubsubmonitoring.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
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
import org.petalslink.dsb.kernel.api.Constants;
import org.petalslink.dsb.kernel.monitoring.service.time.TimeStamperHandler;
import org.petalslink.dsb.kernel.pubsub.service.NotificationCenter;
import org.petalslink.dsb.monitoring.api.JAXBHelper;
import org.petalslink.dsb.monitoring.api.ReportBean;
import org.petalslink.dsb.monitoring.api.ReportListBean;
import org.petalslink.dsb.notification.commons.NotificationException;
import org.petalslink.dsb.notification.commons.api.NotificationSender;
import org.petalslink.dsb.ws.api.PubSubMonitoringManager;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * A routing module which allows to send notifications to the internal engines
 * on different exchange steps with monitoring data inside...
 * 
 * @author chamerling
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "monitoringSender", signature = SenderModule.class),
        @Interface(name = "monitoringReceiver", signature = ReceiverModule.class),
        @Interface(name = "webservice", signature = PubSubMonitoringManager.class) })
public class PubSubMonitoringModule implements SenderModule, ReceiverModule,
        PubSubMonitoringManager {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    private MonitoringNotificationSender sender;

    private final Map<String, Integer> map = Collections
            .synchronizedMap(new HashMap<String, Integer>());

    private AtomicBoolean state = new AtomicBoolean(true);

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
    }

    public void electEndpoints(
            Map<org.ow2.petals.jbi.messaging.endpoint.ServiceEndpoint, TransportSendContext> electedDestinations,
            ComponentContext sourceComponentContext, MessageExchangeWrapper exchange)
            throws RoutingException {
        this.log.call();

        if (!state.get()) {
            return;
        }
        
        if (exchange == null) {
            return;
        }

        // skip monitoring on some messages, for example the monitoring ones...
        if (exchange.getMessage("in") != null
                && exchange.getMessage("in").getProperty(Constants.MESSAGE_SKIP_MONITORING) != null) {
            return;
        }

        try {
            ReportListBean reports = this.createReportListFromExchange(exchange);
            if (this.log.isDebugEnabled()) {
                this.log.debug("In Report Module electDestinations");
            }
            if (reports.getReports().size() > 0) {
                this.sendReport(reports);
            }

        } catch (Exception e) {
            throw new RoutingException(e);
        }
    }

    public boolean receiveExchange(MessageExchangeWrapper exchange, ComponentContext arg1)
            throws RoutingException {

        if (!state.get()) {
            return true;
        }
        
        if (exchange == null) {
            return true;
        }

        if (exchange.getMessage("in") != null
                && exchange.getMessage("in").getProperty(Constants.MESSAGE_SKIP_MONITORING) != null) {
            return true;
        }

        if (exchange != null) {
            try {
                if (this.log.isDebugEnabled()) {
                    this.log.info("In Report Module: receiveExchange");
                }
                ReportListBean reports = this.createReportListFromExchange(exchange);
                if (reports.getReports().size() > 0) {
                    this.sendReport(reports);
                }
            } catch (Exception e) {
                throw new RoutingException(e);
            }
        }
        return true;
    }

    private void sendReport(ReportListBean reports) throws Exception {

        // TODO : Another thread can do this asynchronously...

        // direct marshall to payload...
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JAXBHelper.marshall(reports, bos);
        InputStream input = new ByteArrayInputStream(bos.toByteArray());
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        Document doc = builder.parse(input);

        NotificationSender sender = getMonitoringNotificationSender();
        if (sender == null) {
            log.error("Can not get notification sender...");
        } else {
            try {
                sender.notify(
                        doc,
                        org.petalslink.dsb.kernel.pubsubmonitoring.service.Constants.MONITORING_TOPIC,
                        "http://www.w3.org/TR/1999/REC-xpath-19991116");
            } catch (NotificationException e) {
                e.printStackTrace();
            }
        }
    }

    protected ReportListBean createReportListFromExchange(MessageExchangeWrapper exchange)
            throws DSBException {
        ReportListBean res = new ReportListBean();
        try {
            // flash on request in
            if (MessageExchangeImpl.Role.CONSUMER.equals(exchange.getRole())) {

                // handle in request
                if ((MessageExchangeImpl.IN_ONLY_PATTERN.equals(exchange.getPattern()) && !exchange
                        .isTerminated())
                        || (((MessageExchangeImpl.IN_OUT_PATTERN.equals(exchange.getPattern()) || (MessageExchangeImpl.IN_OPTIONAL_OUT_PATTERN
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
                if (MessageExchangeImpl.IN_ONLY_PATTERN.equals(exchange.getPattern())
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
                    report2.setType("t2");
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
                    report3.setType("t3");
                    this.setSOACommonInformation(exchange, report3);
                    report3.setDate(TimeStamperHandler.getInstance().getTimeStamp(exchange)
                            .getDateProviderOut());
                    report3.setContentLength(0L);
                    report3.setException(false);
                    res.getReports().add(report3);

                    // create date client out
                    ReportBean report4 = new ReportBean();
                    report4.setType("t4");
                    this.setSOACommonInformation(exchange, report4);
                    report4.setDate(TimeStamperHandler.getInstance().getTimeStamp(exchange)
                            .getDateClientOut());
                    report4.setContentLength(0);
                    report4.setException(false);
                    res.getReports().add(report4);
                }

                // handle out request
                if ((MessageExchangeImpl.IN_OUT_PATTERN.equals(exchange.getPattern()) || MessageExchangeImpl.IN_OPTIONAL_OUT_PATTERN
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
                    report2.setType("t2");
                    this.setSOACommonInformation(exchange, report2);
                    report2.setDate(TimeStamperHandler.getInstance().getTimeStamp(exchange)
                            .getDateProviderIn());
                    report2.setContentLength(report1.getContentLength());
                    res.getReports().add(report2);

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
                    report4.setType("t4");
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

    private synchronized MonitoringNotificationSender getMonitoringNotificationSender() {
        if (sender == null) {
            sender = new MonitoringNotificationSender(NotificationCenter.get().getManager()
                    .getNotificationProducerEngine());
        }
        return sender;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.pubsubmonitoring.service.Manager#getTopic()
     */
    public QName getTopic() {
        return org.petalslink.dsb.kernel.pubsubmonitoring.service.Constants.MONITORING_TOPIC;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.pubsubmonitoring.service.Manager#getState()
     */
    public boolean getState() {
        return state.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.kernel.pubsubmonitoring.service.Manager#setState(boolean
     * )
     */
    public void setState(boolean state) {
        this.state.set(state);
    }
}
