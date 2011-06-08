/**
 * 
 */
package org.petalslink.dsb.ws.api;

import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException;

/**
 * @author chamerling
 * 
 */
public class DSBWebServiceException extends PEtALSWebServiceException {

    /**
     * 
     */
    private static final long serialVersionUID = 1014921705502893008L;

    /**
     * 
     */
    public DSBWebServiceException(String message, Object... params) {
        super(String.format(message, params));
    }

}
