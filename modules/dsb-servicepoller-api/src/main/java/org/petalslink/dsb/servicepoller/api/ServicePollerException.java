/**
 * 
 */
package org.petalslink.dsb.servicepoller.api;

import javax.xml.ws.WebFault;

/**
 * @author chamerling
 * 
 */
@WebFault
public class ServicePollerException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -7753567226415679230L;

    public ServicePollerException() {
        super();
    }

    public ServicePollerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServicePollerException(String message) {
        super(message);
    }

    public ServicePollerException(Throwable cause) {
        super(cause);
    }

}
