/**
 * 
 */
package org.petalslink.dsb.kernel.rest;

import org.ow2.petals.tools.ws.WebServiceException;

/**
 * @author chamerling
 *
 */
public interface RESTServiceManager {

    void exposeAll() throws WebServiceException;
}
