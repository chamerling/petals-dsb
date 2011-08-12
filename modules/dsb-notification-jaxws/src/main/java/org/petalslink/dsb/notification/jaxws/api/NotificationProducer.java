/**
 * 
 */
package org.petalslink.dsb.notification.jaxws.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.oasis_open.docs.wsn.bw_2.InvalidFilterFault;
import org.oasis_open.docs.wsn.bw_2.InvalidMessageContentExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidProducerPropertiesExpressionFault;
import org.oasis_open.docs.wsn.bw_2.InvalidTopicExpressionFault;
import org.oasis_open.docs.wsn.bw_2.MultipleTopicsSpecifiedFault;
import org.oasis_open.docs.wsn.bw_2.NoCurrentMessageOnTopicFault;
import org.oasis_open.docs.wsn.bw_2.NotifyMessageNotSupportedFault;
import org.oasis_open.docs.wsn.bw_2.SubscribeCreationFailedFault;
import org.oasis_open.docs.wsn.bw_2.TopicExpressionDialectUnknownFault;
import org.oasis_open.docs.wsn.bw_2.TopicNotSupportedFault;
import org.oasis_open.docs.wsn.bw_2.UnacceptableInitialTerminationTimeFault;
import org.oasis_open.docs.wsn.bw_2.UnrecognizedPolicyRequestFault;
import org.oasis_open.docs.wsn.bw_2.UnsupportedPolicyRequestFault;

/**
 * This interface is exactly the same than the one at
 * {@link org.oasis_open.docs.wsn.bw_2.NotificationProducer} but it seems that
 * CXF has some troubles to expose it...
 * 
 * @author chamerling
 * 
 */
@WebService(targetNamespace = "http://docs.oasis-open.org/wsn/bw-2", name = "NotificationProducer")
public interface NotificationProducer {

	@WebResult(name = "SubscribeResponse", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "SubscribeResponse")
	@WebMethod(operationName = "Subscribe", action = "http://com.ebmwebsourcing.easyesb/soa/model/endpoint/notification/Subscribe")
	public com.ebmwebsourcing.wsstar.jaxb.notification.base.SubscribeResponse subscribe(
			@WebParam(partName = "SubscribeRequest", name = "Subscribe", targetNamespace = "http://docs.oasis-open.org/wsn/b-2") com.ebmwebsourcing.wsstar.jaxb.notification.base.Subscribe subscribeRequest)
			throws InvalidTopicExpressionFault,
			org.oasis_open.docs.wsrf.rw_2.ResourceUnknownFault,
			InvalidProducerPropertiesExpressionFault,
			UnrecognizedPolicyRequestFault, TopicExpressionDialectUnknownFault,
			NotifyMessageNotSupportedFault, InvalidFilterFault,
			UnsupportedPolicyRequestFault,
			InvalidMessageContentExpressionFault, SubscribeCreationFailedFault,
			TopicNotSupportedFault, UnacceptableInitialTerminationTimeFault;

	@WebResult(name = "GetCurrentMessageResponse", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "GetCurrentMessageResponse")
	@WebMethod(operationName = "GetCurrentMessage", action = "http://com.ebmwebsourcing.easyesb/soa/model/endpoint/notification/GetCurrentMessage")
	public com.ebmwebsourcing.wsstar.jaxb.notification.base.GetCurrentMessageResponse getCurrentMessage(
			@WebParam(partName = "GetCurrentMessageRequest", name = "GetCurrentMessage", targetNamespace = "http://docs.oasis-open.org/wsn/b-2") com.ebmwebsourcing.wsstar.jaxb.notification.base.GetCurrentMessage getCurrentMessageRequest)
			throws InvalidTopicExpressionFault,
			org.oasis_open.docs.wsrf.rw_2.ResourceUnknownFault,
			TopicExpressionDialectUnknownFault, MultipleTopicsSpecifiedFault,
			NoCurrentMessageOnTopicFault, TopicNotSupportedFault;

}
