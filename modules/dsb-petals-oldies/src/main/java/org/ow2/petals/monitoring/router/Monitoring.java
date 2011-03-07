/**
 * PETALS - PETALS Services Platform. Copyright (c) 2005 EBM Websourcing,
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
 * $Id: Monitoring.java msauvage $
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.monitoring.router;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.Fault;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.jbi.messaging.MessageExchange.Role;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ow2.petals.commons.stream.InputStreamForker;
import org.ow2.petals.jbi.messaging.exchange.MessageExchange;
import org.ow2.petals.jbi.messaging.exchange.MessageExchangeImpl;
import org.ow2.petals.kernel.server.MBeanHelper;
import org.w3c.dom.Document;

import static org.ow2.petals.jmx.api.IPetalsMonitoringService.MEPShortcuts.IN_ONLY;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.MEPShortcuts.IN_OPTIONAL_OUT;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.MEPShortcuts.IN_OUT;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.MEPShortcuts.ROBUST_IN_ONLY;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.ReportKeyNames.COMPONENT;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.ReportKeyNames.CONTAINER;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.ReportKeyNames.CONTENT;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.ReportKeyNames.ENDPOINT_NAME;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.ReportKeyNames.EXCEPTION;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.ReportKeyNames.ID;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.ReportKeyNames.MEP;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.ReportKeyNames.OPERATION;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.ReportKeyNames.ROLE;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.ReportKeyNames.SERVICE_NAME;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.ReportKeyNames.STATUS;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.ReportKeyNames.TIME;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.ReportKeyNames.TYPE;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.Role.CONSUMER;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.Role.PROVIDER;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.Status.ACTIVE;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.Status.DONE;
import static org.ow2.petals.jmx.api.IPetalsMonitoringService.Status.ERROR;

/**
 * 
 * @author alouis
 * 
 */
public class Monitoring implements MonitoringMBean, NotificationBroadcaster {

    /**
     * Type of notification sent
     */
    private static final String NEW_MSG_NOTIF_TYPE = "petals.notif.newMsg";

    /**
     * The notifications sequence number
     */
    private static int sequenceNumber = 0;

    protected RouterMonitor routerMonitor;

    /**
     * the notification support
     */
    NotificationBroadcasterSupport notifSupport = new NotificationBroadcasterSupport();

    /**
     * Size of list of messages to send by notification. Default value is set to
     * 50
     */
    private int messagesThreshold = 50;

    /**
     * Stores the messages waiting to be sent
     */
    private final List<Map<String, Object>> messages = Collections
            .synchronizedList(new ArrayList<Map<String, Object>>(this.messagesThreshold));

    /**
     * Time in milliseconds to wait before sending messages. Default is 10000
     * (i.e. 10 seconds)
     */
    private int timeSendLimit = 10000;

    /**
     * A thread used to wait a given time before sending new messages, if
     * messages threshold has not been reached
     */
    private MonitoringThread monitoringThread;

    private final static ThreadLocal<Transformer> transformerWithoutXmlDeclarationThreadLocal = new ThreadLocal<Transformer>() {

        @Override
        protected Transformer initialValue() {
            try {

                final Transformer transformer = TransformerFactory.newInstance().newTransformer();
                Properties props = new Properties();
                props.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.setOutputProperties(props);
                return transformer;

            } catch (TransformerConfigurationException e) {
                throw new RuntimeException("Failed to create Transformer", e);
            }
        }
    };

    /**
     * Creates a simple, empty Monitoring object
     * 
     */
    public Monitoring() {
        this.monitoringThread = new MonitoringThread(this);
    }

    /**
     * start the monitoring with or without monitoring content of messages. If
     * monitoring is already started, just modify the monitoring of messages
     * content.
     * 
     * @param showMessageContent
     *            indicates if content of messages have to be monitored, true
     *            for monitor the content, false for ignoring it
     */
    public void activateMonitoring(boolean showMessageContent) {
        if (this.routerMonitor != null) {
            if (!this.routerMonitor.isMonitoring()) {
                this.routerMonitor.activateMonitoring(showMessageContent);
                this.startMonitoringThread();
            } else {
                this.routerMonitor.showMessageContent(showMessageContent);
            }
        }
    }

    /**
     * @see NotificationBroadcasterSupport#addNotificationListener(NotificationListener,
     *      NotificationFilter, Object)
     */
    public void addNotificationListener(NotificationListener listener, NotificationFilter filter,
            Object handback) throws IllegalArgumentException {
        this.notifSupport.addNotificationListener(listener, filter, handback);
    }

    /**
     * stops the monitoring of messages and their content.
     */
    public void deactivateMonitoring() {
        if (this.routerMonitor != null) {
            if (this.routerMonitor.isMonitoring()) {
                this.routerMonitor.deactivateMonitoring();
            }
            this.stopMonitoringThread();
        }
    }

    /**
     * Return the threshold number of messages to send
     */
    public int getMessagesThreshold() {
        return this.messagesThreshold;
    }

    /**
     * "id" = <code>String</code> exchangeId <br>
     * "time" = <code>long</code> capture.time <br>
     * "component" = <code>String</code> capture.component<br>
     * "role" = <code>String</code> capture.role<br>
     * "type" = <code>String</code> capture.messageType <br>
     * "mep" = <code>String</code> capture.mep <br>
     * "exception" = <code>String</code> capture.exception<br>
     * "status" = <code>String</code> capture.status <br>
     * "content" = <code>String</code> capture.content <br>
     * "serviceName" = <code>String</code> capture.serviceName <br>
     * "endpointName" = <code>String</code> capture.endpointName <br>
     * "operation" = <code>String</code> capture.operation <br>
     * 
     * @see NotificationBroadcasterSupport#getNotificationInfo()
     */
    public MBeanNotificationInfo[] getNotificationInfo() {
        String[] types = new String[] { NEW_MSG_NOTIF_TYPE };
        String name = Notification.class.getName();
        String description = "Notification containing a list of new messages : "
                + "HashMap{(String) id, (long) time,(String) component, "
                + "(String) mep, (String) exception-stacktrace,"
                + "(String) role, (String) type, (String) status, (String) content, "
                + "(String serviceName, (String) endpointName, (String) operation}";
        MBeanNotificationInfo info = new MBeanNotificationInfo(types, name, description);
        return new MBeanNotificationInfo[] { info };
    }

    /**
     * Gets the router monitor
     * 
     * @return
     */
    public RouterMonitor getRouterMonitor() {
        return this.routerMonitor;
    }

    /**
     * Returns the time to wait between two notifications if messages threshold
     * number has not been reached. Returns -1 if monitoring not started.
     * 
     * @return an int representation of the time to wait, -1 if monitoring not
     *         active
     */
    public int getTimeSendLimit() {
        return this.timeSendLimit;
    }

    /**
     * Indicates if content of messages have to be set in the messages monitored
     * 
     * @return true if content of messages have to be in the messages, false if
     *         not
     */
    public boolean isMessageContentShown() {
        return this.routerMonitor.isMessageContentShown();
    }

    /**
     * Indicates if monitoring is active or not.
     * 
     * @return true if monitoring is running, false if not.
     */
    public boolean isMonitoring() {
        return this.routerMonitor.isMonitoring();
    }

    /**
     * Sends notification of all messages stored to all listeners
     */
    public void notifyMessages() {

        if (this.monitoringThread != null) {
            this.restartMonitoringThread();
        }

        Notification notification = null;
        synchronized (this.messages) {
            if (this.messages.size() > 0) {
                try {
                    notification = new Notification(NEW_MSG_NOTIF_TYPE, new ObjectName(
                            "Petals:name=" + MBeanHelper.MONITORING_MBEAN + ",type=service"),
                            sequenceNumber++, this.messages.size() + " new messages coming");
                    // Notifying a copy of the list as the broadcast of messages
                    // is processed into separate threads.
                    notification.setUserData(new ArrayList<Map<String, Object>>(this.messages));

                    // The JMX notification is sent outside this synchronized
                    // bloc to
                    // go out of this critical section as soon as possible.

                    // reseting messages list
                    this.messages.clear();
                } catch (final MalformedObjectNameException e) {
                    e.printStackTrace();
                }
            }
        }

        if (notification != null) {
            this.notifSupport.sendNotification(notification);
        }
    }

    /**
     * @see NotificationBroadcasterSupport#removeNotificationListener(NotificationListener)
     */
    public void removeNotificationListener(NotificationListener listener)
            throws ListenerNotFoundException {
        this.notifSupport.removeNotificationListener(listener);
    }

    /**
     * Sets the threshold of messages to send.
     * 
     * @param threshold
     *            the new number of messages
     */
    public void setMessagesThreshold(int threshold) {
        this.messagesThreshold = threshold;
    }

    /**
     * Sets the router monitor
     * 
     * @param routerMonitor
     *            the new router monitor to be set
     */
    public void setRouterMonitor(RouterMonitor routerMonitor) {
        this.routerMonitor = routerMonitor;
    }

    /**
     * Sets the time to wait between two notifications if messages threshold
     * number has not been reached
     * 
     * @param time
     *            the time to wait in milliseconds
     */
    public void setTimeSendLimit(int time) {
        this.timeSendLimit = time;
        if (this.monitoringThread != null) {
            this.monitoringThread.setSendingFrequency(this.timeSendLimit);
        }
    }

    /**
     * Adds a message to the list of messages stored. If threshold number of
     * messages stored is reached, calls the notifyMessages() method to send the
     * messages via a notification to all listeners
     * 
     */
    public void addMessage(String exchangeId, MessageExchange exchange,
            boolean showMessageContent) {
        Map<String, Object> rawMessageElements = messageToRaw(exchangeId, exchange,
                showMessageContent);

        // The adding of a message must not occurs when the messages are flushed
        // in notifyMessages
        this.messages.add(rawMessageElements);

        // As waiting messages can be sent by a timer, the notifications must be
        // synchronized
        synchronized (this.messages) {
            if (this.messages.size() >= this.messagesThreshold) {
                this.notifyMessages();
            }
        }
    }

    /**
     * Gets the monitoring thread
     * 
     * @return a MonitorThread
     */
    protected MonitoringThread getMonitoringThread() {
        return this.monitoringThread;
    }

    /**
     * Restarts the monitoring thread by destroying the one existing and then
     * creating and starting a new one
     * 
     */
    protected void restartMonitoringThread() {
        if (this.monitoringThread != null) {
            this.monitoringThread.setSkipNextWakeUp(true);
        }
    }

    /**
     * Sets the monitoring thread to a thread given
     * 
     * @param monitoringThread
     *            the new MonitoringThread to use
     */
    protected void setMonitoringThread(MonitoringThread monitoringThread) {
        this.monitoringThread = monitoringThread;
    }

    /**
     * Creates a new MonitoringThread if no one is set and starts it
     * 
     */
    private void startMonitoringThread() {
        if (this.monitoringThread == null) {
            this.monitoringThread = new MonitoringThread(this);
        }
        this.monitoringThread.start();
    }

    /**
     * Stops and destroys the active monitoring thread
     * 
     */
    private void stopMonitoringThread() {
        this.monitoringThread.interrupt();
        this.monitoringThread = null;
    }

    /**
     * transform a capture to a raw Map : <br />
     * "id" = <code>String</code> exchangeId <br>
     * "time" = <code>long</code> capture.time <br>
     * "component" = <code>String</code> capture.component<br>
     * "role" = <code>String</code> capture.role<br>
     * "type" = <code>String</code> capture.messageType <br>
     * "status" = <code>String</code> capture.status <br>
     * "content" = <code>String</code> capture.content <br>
     * "serviceName" = <code>String</code> capture.serviceName <br>
     * "endpointName" = <code>String</code> capture.endpointName <br>
     * "operation" = <code>String</code> capture.operation <br>
     * "exception" = <code>String</code> capture.exception<br>
     * "mep" = <code>String</code> capture.mep<br>
     * 
     * @param capture
     * @return
     */
    private static Map<String, Object> captureToRaw(final String exchangeId,
            final ExchangeStateCapture capture) {

        final Map<String, Object> rawCapture = new HashMap<String, Object>();
        rawCapture.put(ID, exchangeId);
        rawCapture.put(TIME, capture.time);
        rawCapture.put(COMPONENT, capture.component);
        rawCapture.put(ROLE, roleToRaw(capture.role));
        rawCapture.put(TYPE, capture.messageType);
        rawCapture.put(STATUS, statusToRaw(capture.status));
        rawCapture.put(CONTENT, capture.content);
        rawCapture.put(MEP, mepToRaw(capture.mep));
        rawCapture.put(EXCEPTION, exceptionToRaw(capture.exception));
        rawCapture.put(SERVICE_NAME, capture.serviceName == null ? "UndefSvc" : capture.serviceName
                .toString());
        rawCapture.put(ENDPOINT_NAME, capture.endpointName == null ? "UndefEndpoint"
                : capture.endpointName);
        rawCapture.put(OPERATION, capture.operation == null ? "UndefOp" : capture.operation
                .toString());
        rawCapture.put(CONTAINER, capture.container);

        return rawCapture;
    }

    /**
     * Create a Source from the String. The string must be in UTF-8 format,
     * otherwise, the Source is not created.
     * 
     * @param msg
     *            an UTF-8 String
     * @return the Source built from the String, null if not an UTF-8
     */
    // TODO: Must be moved in a Source helper class (see CDK) when the JBI
    // problem about classloaders will be fixed
    private static Source createSource(final String msg) throws IOException {
        StreamSource source = new StreamSource();
        byte[] msgByte = msg.getBytes("UTF-8");
        ByteArrayInputStream in = new ByteArrayInputStream(msgByte);
        source.setInputStream(in);
        return source;
    }

    /**
     * Create a String from a Source. The String will be in UTF-8.
     * 
     * @param s
     * @return
     * @throws Exception
     */
    // TODO: Must be moved in a String helper class (see CDK) when the JBI
    // problem about classloaders will be fixed
    private static String createString(final Source s) throws Exception {

        String result = null;
        try {
            if ((s instanceof StreamSource) && (((StreamSource) s).getInputStream() != null)) {
                final StreamSource ss = (StreamSource) s;
                final InputStreamForker isf = new InputStreamForker(ss.getInputStream());

                final InputStream isOne = isf.getInputStreamOne();
                ss.setInputStream(isf.getInputStreamTwo());

                byte[] buf = new byte[isOne.available()];
                BufferedInputStream bis = new BufferedInputStream(isOne);
                bis.read(buf);
                result = new String(buf);
            } else {
                StringWriter buffer = new StringWriter();
                Result sresult = new StreamResult(buffer);
                transformerWithoutXmlDeclarationThreadLocal.get().transform(s, sresult);
                result = buffer.toString();
            }
        } catch (IOException e) {
            throw new Exception(e);
        } catch (TransformerConfigurationException e) {
            throw new Exception(e);
        } catch (TransformerException e) {
            throw new Exception(e);
        }
        return result;
    }

    /**
     * Transforms a given MessageExchange into an ExchangeStateCapture
     * 
     * @param exchange
     *            the MessageExchangeImpl to convert
     * @param showMessageContent
     *            true if content must be set in the capture, false if not
     * @return
     */
    public static ExchangeStateCapture messageToCapture(final MessageExchange exchange,
            final boolean showMessageContent) {
        final ExchangeStateCapture state = new ExchangeStateCapture();
        state.time = System.currentTimeMillis();
        state.status = exchange.getStatus();
        state.role = exchange.getRole();
        state.mep = exchange.getPattern();
        state.exception = exchange.getError();
        state.operation = exchange.getOperation();

        if (Role.CONSUMER.equals(state.role)) {
            state.component = exchange.getConsumerEndpoint().getLocation().getComponentName();
            state.container = exchange.getConsumerEndpoint().getLocation().getContainerName();
            state.serviceName = exchange.getConsumerEndpoint().getServiceName();
            state.endpointName = exchange.getConsumerEndpoint().getEndpointName();
        } else {
            state.serviceName = exchange.getEndpoint().getServiceName();
            state.endpointName = exchange.getEndpoint().getEndpointName();

            // TODO FIXME !!!!
            // state.component = exchange.getEndpoint().getComponentName();
            // state.container = ((AbstractEndpoint)
            // exchange.getEndpoint()).getContainerName();
        }

        // Copy the in, out or fault message if the exchange is active
        if (ExchangeStatus.ACTIVE.equals(exchange.getStatus())) {
            if (exchange.getFault() != null) {
                // copy the FAULT
                final Fault fault = exchange.getFault();
                state.messageType = "fault";
                if (showMessageContent) {
                    if (fault.getContent() != null) {
                        // FIXME ugly way to copy the fault content
                        try {
                            Source content = fault.getContent();
                            Source source1 = null;
                            Source source2 = null;
                            if (content instanceof StreamSource) {
                                InputStreamForker streamForker = new InputStreamForker(((StreamSource)content).getInputStream());
                                
                                //exchangeStreamForked.put(messageName, streamForker);
                                source1 = new StreamSource(streamForker.getInputStreamOne());
                                source2 = new StreamSource(streamForker.getInputStreamTwo());
                                fault.setContent(source1);
                            } else if(content instanceof DOMSource) {
                                Document doc = (Document) ((DOMSource)content).getNode();
                                source1 = new DOMSource(doc);
                                source2 = new DOMSource(doc);
                                fault.setContent(source1);
                            }
                            state.content = Monitoring.createString(source2);
                        } catch (Exception e) {
                            state.content = "<monitoring-error>Error occured "
                                    + "while getting fault content</monitoring-error>";
                        }
                        try {
                            fault.setContent(Monitoring.createSource(state.content));
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                // copy the OUT or IN or other message of the exchange
                final Map<String, NormalizedMessage> messages = exchange.getMessages();
                if ((messages != null) && (messages.size() > 0)) {
                    final NormalizedMessage message;
                    // searching for an "out" message
                    // if not found -> taking first message
                    if (messages.get("out") != null) {
                        message = messages.get("out");
                        state.messageType = "out";
                    } else {
                        state.messageType = messages.keySet().iterator().next();
                        message = messages.get(state.messageType);
                    }
                    // adding message content and fault if wanted
                    if (showMessageContent && (message.getContent() != null)) {
                        // FIXME ugly way to copy the message content
                        try {
                            Source source1 = null;
                            Source source2 = null;
                            if (message.getContent() instanceof StreamSource) {
                                InputStreamForker streamForker = new InputStreamForker(((StreamSource)message.getContent()).getInputStream());
                                
                                //exchangeStreamForked.put(messageName, streamForker);
                                source1 = new StreamSource(streamForker.getInputStreamOne());
                                source2 = new StreamSource(streamForker.getInputStreamTwo());
                                message.setContent(source1);
                            } else if(message.getContent() instanceof DOMSource) {
                                Document doc = (Document) ((DOMSource)message.getContent()).getNode();
                                source1 = new DOMSource(doc);
                                source2 = new DOMSource(doc);
                                message.setContent(source1);
                            }
                            state.content = createString(source2);
                        } catch (Exception e) {
                            state.content = "<monitoring-error>Error occured "
                                    + "while getting message content</monitoring-error>";
                        }
//                        try {
//                            message.setContent(createSource(state.content));
//                        } catch (MessagingException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
            }
        }
        return state;
    }

    /**
     * Transform a MessageExchange to a raw format :
     * <code>Map<String,Object></code> a raw map representation of the message
     * 
     * @param exchangeId
     *            the id of the exchange containing the message
     * @param exchange
     *            the message exchange
     * @param showMessageContent
     *            true if content must be set in the message, false if not
     * @return a Map<String,Object> representation of the MessageExchange
     */
    private static Map<String, Object> messageToRaw(final String exchangeId,
            final MessageExchange exchange, boolean showMessageContent) {
        return Monitoring.captureToRaw(exchangeId, messageToCapture(exchange, showMessageContent));
    }

    /**
     * Convert a Role to a string representation
     * 
     * @param role
     * @return "consumer" or "provider
     */
    protected static String roleToRaw(final Role role) {
        return (Role.CONSUMER.equals(role)) ? CONSUMER : PROVIDER;
    }

    /**
     * Convert an Exception to a string representation
     * 
     * @param exception
     *            can be null
     * @return stack trace as String
     */
    protected static String exceptionToRaw(final Exception exception) {
        String result = null;
        if (exception != null) {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final PrintWriter os = new PrintWriter(bos);
            exception.printStackTrace(os);
            os.flush();
            result = bos.toString();
        }
        return result;
    }

    /**
     * Convert a MEP to a string representation
     * 
     * @param mep
     * @return "InOnly", "RobustInOnly", ...
     */
    protected static String mepToRaw(final URI mep) {
        final String pattern;
        if (MessageExchangeImpl.IN_ONLY_PATTERN.equals(mep)) {
            pattern = IN_ONLY;
        } else if (MessageExchangeImpl.ROBUST_IN_ONLY_PATTERN.equals(mep)) {
            pattern = ROBUST_IN_ONLY;
        } else if (MessageExchangeImpl.IN_OUT_PATTERN.equals(mep)) {
            pattern = IN_OUT;
        } else if (MessageExchangeImpl.IN_OPTIONAL_OUT_PATTERN.equals(mep)) {
            pattern = IN_OPTIONAL_OUT;
        } else {
            pattern = "unknown";
        }
        return pattern;
    }

    /**
     * Convert a status to a string representation
     * 
     * @param status
     * @return "active" or "done" or "error"
     */
    protected static String statusToRaw(final ExchangeStatus status) {
        return (ExchangeStatus.DONE.equals(status)) ? DONE
                : (ExchangeStatus.ACTIVE.equals(status)) ? ACTIVE : ERROR;
    }

}
