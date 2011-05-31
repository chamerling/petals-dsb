/**
 * 
 */
package org.petalslink.dsb.service.poller.api;

import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public class PollingContext {

    /**
     * The real job...
     */
    Job job;

    /**
     * The cron expression
     */
    String cron;

    /**
     * Service to poll
     */
    ServiceInformation toPoll;

    /**
     * The transport to use to send the message to the service.
     */
    PollingTransport transport;

    /**
     * The service to send the polling response to
     */
    ServiceInformation responseTo;

    /**
     * The input message ie the message sent to the service to poll. This
     * message is static for now but the poller can be adapted to get a document
     * from a repository.
     */
    Document inputMessage;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public ServiceInformation getToPoll() {
        return toPoll;
    }

    public void setToPoll(ServiceInformation toPoll) {
        this.toPoll = toPoll;
    }

    public ServiceInformation getResponseTo() {
        return responseTo;
    }

    public void setResponseTo(ServiceInformation responseTo) {
        this.responseTo = responseTo;
    }

    public Document getInputMessage() {
        return inputMessage;
    }

    public void setInputMessage(Document inputMessage) {
        this.inputMessage = inputMessage;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public PollingTransport getTransport() {
        return transport;
    }

    public void setTransport(PollingTransport transport) {
        this.transport = transport;
    }
}
