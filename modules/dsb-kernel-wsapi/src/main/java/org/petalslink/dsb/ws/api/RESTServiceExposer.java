/**
 * 
 */
package org.petalslink.dsb.ws.api;

import javax.jws.WebService;

import org.petalslink.dsb.ws.api.SOAPServiceExposer;

/**
 * Expose DSB service as REST ones. See {@link SOAPServiceExposer} for more details.
 * 
 * FIXME : http://jira.petalslink.com/browse/COMMONS-16
 * @author chamerling
 *
 */
@WebService
public interface RESTServiceExposer extends ServiceExposer {

}
