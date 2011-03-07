package org.petalslink.dsb.kernel.gms;

import org.petalslink.dsb.kernel.DSBException;

/**
 * 
 * @author chamerling - PetalsLink
 *
 */
public class GMSException extends DSBException {

    /**
     * 
     */
    private static final long serialVersionUID = -172694252222155539L;

    public GMSException() {
        super();
    }

    public GMSException(String message, Throwable cause) {
        super(message, cause);
    }

    public GMSException(String message) {
        super(message);
    }

    public GMSException(Throwable cause) {
        super(cause);
    }

}
