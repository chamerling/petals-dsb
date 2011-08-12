/**
 * 
 */
package org.petalslink.dsb.notification.jaxws.api;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * @author chamerling
 * 
 */
@WebService(targetNamespace = "http://docs.oasis-open.org/wsn/bw-2", name = "NotificationConsumer")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface NotificationConsumer {

	@Oneway
	@WebMethod(operationName = "Notify", action = "http://com.ebmwebsourcing.easyesb/soa/model/endpoint/notification/Notify")
	public void notify(
			@WebParam(partName = "Notify", name = "Notify", targetNamespace = "http://docs.oasis-open.org/wsn/b-2") com.ebmwebsourcing.wsstar.jaxb.notification.base.Notify notify);
}