/**
 * 
 */
package org.petalslink.dsb.service.poller.api;

/**
 * @author chamerling
 *
 */
public class PollerException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -3196013347597760542L;

    public PollerException() {
        super();
    }

    public PollerException(String message, Throwable cause) {
        super(message, cause);
    }

    public PollerException(String message) {
        super(message);
    }

    public PollerException(Throwable cause) {
        super(cause);
    }

}
