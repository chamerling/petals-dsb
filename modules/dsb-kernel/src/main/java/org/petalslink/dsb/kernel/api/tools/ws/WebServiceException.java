/**
 * 
 */
package org.petalslink.dsb.kernel.api.tools.ws;

/**
 * @author chamerling
 *
 */
public class WebServiceException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -853170774190379520L;

    /**
     * 
     */
    public WebServiceException() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public WebServiceException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public WebServiceException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public WebServiceException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
