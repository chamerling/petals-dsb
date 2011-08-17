/**
 * 
 */
package org.petalslink.dsb.soap;

import javax.xml.namespace.QName;

import org.petalslink.dsb.soap.api.SimpleExchange;
import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public class Exchange implements SimpleExchange {

    private Document in;

    private QName operation;

    private Document fault;

    private Document out;

    private Exception e;

    /**
     * 
     */
    public Exchange() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.SimpleExchange#getIn()
     */
    public Document getIn() {
        return in;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.soap.api.SimpleExchange#setIn(org.w3c.dom.Document)
     */
    public void setIn(Document in) {
        this.in = in;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.SimpleExchange#getOut()
     */
    public Document getOut() {
        return out;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.soap.api.SimpleExchange#setOut(org.w3c.dom.Document)
     */
    public void setOut(Document out) {
        this.out = out;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.SimpleExchange#getFault()
     */
    public Document getFault() {
        return fault;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.soap.api.SimpleExchange#setFault(org.w3c.dom.Document)
     */
    public void setFault(Document fault) {
        this.fault = fault;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.SimpleExchange#getOperation()
     */
    public QName getOperation() {
        return this.operation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.soap.api.SimpleExchange#setOperation(java.lang.String)
     */
    public void setOperation(QName operation) {
        this.operation = operation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.dsb.soap.api.SimpleExchange#getException()
     */
    public Exception getException() {
        return e;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.soap.api.SimpleExchange#setException(java.lang.Exception
     * )
     */
    public void setException(Exception exception) {
        this.e = exception;
    }

}
