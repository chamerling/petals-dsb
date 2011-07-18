/**
 * 
 */
package org.petalslink.dsb.monitoring.api;

import java.util.Date;

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
public class ReportBean {

    @XmlElement
    private String exchangeId;

    @XmlElement
    private String serviceName;

    @XmlElement
    private String endpoint;

    @XmlElement
    private String operation;

    @XmlElement
    private String itf;

    @XmlElement
    private String consumer;

    @XmlElement
    private String provider;

    @XmlElement
    private boolean isException;

    @XmlElement
    private long contentLength;

    @XmlElement
    private Date date;
    
    /**
     * The report type ie t1, t2, t3, t4... Not an enum to be extensible...
     */
    @XmlElement
    private String type;

    /**
     * 
     */
    public ReportBean() {
    }

    /**
     * @return the exchangeId
     */
    public String getExchangeId() {
        return exchangeId;
    }

    /**
     * @param exchangeId
     *            the exchangeId to set
     */
    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName
     *            the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the endpoint
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint
     *            the endpoint to set
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * @param operation
     *            the operation to set
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * @return the itf
     */
    public String getItf() {
        return itf;
    }

    /**
     * @param itf
     *            the itf to set
     */
    public void setItf(String itf) {
        this.itf = itf;
    }

    /**
     * @return the consumer
     */
    public String getConsumer() {
        return consumer;
    }

    /**
     * @param consumer
     *            the consumer to set
     */
    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    /**
     * @return the provider
     */
    public String getProvider() {
        return provider;
    }

    /**
     * @param provider
     *            the provider to set
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * @return the isException
     */
    public boolean isException() {
        return isException;
    }

    /**
     * @param isException
     *            the isException to set
     */
    public void setException(boolean isException) {
        this.isException = isException;
    }

    /**
     * @return the contentLenght
     */
    public long getContentLength() {
        return contentLength;
    }

    /**
     * @param contentLenght
     *            the contentLenght to set
     */
    public void setContentLength(long contentLenght) {
        this.contentLength = contentLenght;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date
     *            the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ReportBean [exchangeId=");
        builder.append(exchangeId);
        builder.append(", serviceName=");
        builder.append(serviceName);
        builder.append(", endpoint=");
        builder.append(endpoint);
        builder.append(", operation=");
        builder.append(operation);
        builder.append(", itf=");
        builder.append(itf);
        builder.append(", consumer=");
        builder.append(consumer);
        builder.append(", provider=");
        builder.append(provider);
        builder.append(", isException=");
        builder.append(isException);
        builder.append(", contentLenght=");
        builder.append(contentLength);
        builder.append(", date=");
        builder.append(date);
        builder.append("]");
        return builder.toString();
    }

}
