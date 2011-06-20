/**
 * 
 */
package org.petalslink.dsb.kernel.tools.remote;

import org.petalslink.dsb.api.DSBException;

/**
 * @author chamerling
 *
 */
public class RemoteComponentException extends DSBException {

    /**
     * 
     */
    private static final long serialVersionUID = 7769667518270177276L;

    /**
     * 
     */
    public RemoteComponentException() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public RemoteComponentException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public RemoteComponentException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public RemoteComponentException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
