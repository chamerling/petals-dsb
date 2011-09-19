/**
 * 
 */
package org.petalslink.dsb.servicepoller.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author chamerling
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class WSNPollerServiceInformation {

    @XmlElement(name = "toPoll")
    private ServicePollerInformation toPoll;

    @XmlElement(name = "input")
    private String inputMessage;

    @XmlElement(name = "cron")
    private String cronExpression;

    @XmlElement(name = "replyTo")
    private ServicePollerInformation replyTo;

    @XmlElement(name = "topicName")
    private String topicName;

    @XmlElement(name = "toURI")
    private String topicURI;

    @XmlElement(name = "toPrefix")
    private String topicPrefix;

    @XmlElement(name = "status")
    private String status;

    @XmlElement(name = "id")
    private String id;

    /**
     * 
     */
    public WSNPollerServiceInformation() {
    }

    /**
     * @return the toPoll
     */
    public ServicePollerInformation getToPoll() {
        return toPoll;
    }

    /**
     * @param toPoll
     *            the toPoll to set
     */
    public void setToPoll(ServicePollerInformation toPoll) {
        this.toPoll = toPoll;
    }

    /**
     * @return the inputMessage
     */
    public String getInputMessage() {
        return inputMessage;
    }

    /**
     * @param inputMessage
     *            the inputMessage to set
     */
    public void setInputMessage(String inputMessage) {
        this.inputMessage = inputMessage;
    }

    /**
     * @return the cronExpression
     */
    public String getCronExpression() {
        return cronExpression;
    }

    /**
     * @param cronExpression
     *            the cronExpression to set
     */
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    /**
     * @return the replyTo
     */
    public ServicePollerInformation getReplyTo() {
        return replyTo;
    }

    /**
     * @param replyTo
     *            the replyTo to set
     */
    public void setReplyTo(ServicePollerInformation replyTo) {
        this.replyTo = replyTo;
    }

    /**
     * @return the topicName
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * @param topicName
     *            the topicName to set
     */
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    /**
     * @return the topicURI
     */
    public String getTopicURI() {
        return topicURI;
    }

    /**
     * @param topicURI
     *            the topicURI to set
     */
    public void setTopicURI(String topicURI) {
        this.topicURI = topicURI;
    }

    /**
     * @return the topicPrefix
     */
    public String getTopicPrefix() {
        return topicPrefix;
    }

    /**
     * @param topicPrefix
     *            the topicPrefix to set
     */
    public void setTopicPrefix(String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
}
