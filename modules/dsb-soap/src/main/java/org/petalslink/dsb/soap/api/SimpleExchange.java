/**
 * 
 */
package org.petalslink.dsb.soap.api;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

/**
 * @author chamerling
 * 
 */
public interface SimpleExchange {

    Document getIn();

    void setIn(Document in);

    Document getOut();

    void setOut(Document out);

    Document getFault();

    void setFault(Document fault);

    Exception getException();

    void setException(Exception exception);

    QName getOperation();

    void setOperation(QName operation);

}
