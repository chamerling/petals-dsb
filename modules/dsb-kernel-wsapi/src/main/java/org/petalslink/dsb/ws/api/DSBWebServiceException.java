/**
 * 
 */
package org.petalslink.dsb.ws.api;


/**
 * @author chamerling
 * 
 */
public class DSBWebServiceException extends Exception {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1014921705502893008L;

    public DSBWebServiceException() {
        super();
    }

    public DSBWebServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DSBWebServiceException(String message) {
        super(message);
    }

    public DSBWebServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * 
     */
    public DSBWebServiceException(String message, Object... params) {
        super(String.format(message, params));
    }

}
