/**
 * 
 */
package org.petalslink.dsb.notification.jaxws.api;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author chamerling
 *
 */
@WebService
public interface NotificationManager {

	@WebMethod
	List<String> getSubscribes();
}
