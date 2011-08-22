/**
 * 
 */
package org.petalslink.dsb.service.client;

import java.util.Map;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public class MessageImpl implements Message {

    Document payload;

    QName operation;

    QName service;

    QName itf;

    String endpoint;

    Map<String, Document> headers;

    Map<String, String> properties;

    /**
     * @return the payload
     */
    public Document getPayload() {
        return payload;
    }

    /**
     * @param payload
     *            the payload to set
     */
    public void setPayload(Document payload) {
        this.payload = payload;
    }

    /**
     * @return the operation
     */
    public QName getOperation() {
        return operation;
    }

    /**
     * @param operation
     *            the operation to set
     */
    public void setOperation(QName operation) {
        this.operation = operation;
    }

    /**
     * @return the service
     */
    public QName getService() {
        return service;
    }

    /**
     * @param service
     *            the service to set
     */
    public void setService(QName service) {
        this.service = service;
    }

    /**
     * @return the itf
     */
    public QName getInterface() {
        return itf;
    }

    /**
     * @param itf
     *            the itf to set
     */
    public void setInterface(QName itf) {
        this.itf = itf;
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
     * @return the headers
     */
    public Map<String, Document> getHeaders() {
        return headers;
    }

    /**
     * @param headers
     *            the headers to set
     */
    public void setHeaders(Map<String, Document> headers) {
        this.headers = headers;
    }

    /**
     * @return the properties
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * @param properties
     *            the properties to set
     */
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

}
