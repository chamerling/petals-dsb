/**
 * 
 */
package org.petalslink.dsb.ws.api;

import javax.jws.WebService;

/**
 * Expose a DSB Service as a SOAP one so the DSb service can be called from outside of the DSB.
 * 
 * FIXME : http://jira.petalslink.com/browse/COMMONS-16
 * @author chamerling
 *
 */
@WebService
public interface SOAPServiceExposer extends ServiceExposer {

}
