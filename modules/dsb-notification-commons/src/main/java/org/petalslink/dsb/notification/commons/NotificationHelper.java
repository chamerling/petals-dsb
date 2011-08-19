/**
 * 
 */
package org.petalslink.dsb.notification.commons;

import java.net.URI;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

import com.ebmwebsourcing.wsaddressing10.api.element.Address;
import com.ebmwebsourcing.wsaddressing10.api.element.ReferenceParameters;
import com.ebmwebsourcing.wsaddressing10.api.type.EndpointReferenceType;
//import com.ebmwebsourcing.wsstar.basefaults.datatypes.api.utils.SOAUtil;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.NotificationMessageHolderType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.NotificationMessageHolderType.Message;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.Notify;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.abstraction.TopicExpressionType;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.refinedabstraction.RefinedWsnbFactory;
import com.ebmwebsourcing.wsstar.basenotification.datatypes.api.utils.WsnbException;

/**
 * @author chamerling
 * 
 */
public class NotificationHelper {

	/**
	 * Create a topic expression
	 * 
	 * @param topicUsed
	 * @param dialect
	 * @return
	 * @throws NotificationException
	 */
	public static TopicExpressionType createTopicExpression(QName topicUsed,
			String dialect) throws NotificationException {
		TopicExpressionType notifyTopicExpr = null;

		try {
			notifyTopicExpr = RefinedWsnbFactory.getInstance()
					.createTopicExpressionType(URI.create(dialect));

			if (topicUsed.getPrefix() == null) {
				throw new NotificationException(
						"prefix of topicUsed cannot be null");
			}

			notifyTopicExpr.addTopicNamespace(topicUsed.getPrefix(),
					URI.create(topicUsed.getNamespaceURI()));
			notifyTopicExpr.setContent(topicUsed.getPrefix() + ":"
					+ topicUsed.getLocalPart());
		} catch (WsnbException e) {
			throw new NotificationException(e);
		}
		return notifyTopicExpr;
	}

	/**
	 * Create a Notify message
	 * 
	 * @param producerAddress
	 * @param endpointAddress
	 * @param uuid
	 * @param topicUsed
	 * @param dialect
	 * @param notifPayload
	 * @return
	 * @throws NotificationException
	 */
	public static Notify createNotification(String producerAddress,
			String endpointAddress, String uuid, QName topicUsed,
			String dialect, Document notifPayload) throws NotificationException {
		Notify notifyPayload = null;
		try {
			Message mess = null;
			if (notifPayload != null) {
				mess = RefinedWsnbFactory.getInstance()
						.createNotificationMessageHolderTypeMessage(
								notifPayload.getDocumentElement());
			} else {
				mess = RefinedWsnbFactory.getInstance()
						.createNotificationMessageHolderTypeMessage(null);
			}
			NotificationMessageHolderType msg = RefinedWsnbFactory
					.getInstance().createNotificationMessageHolderType(mess);
			notifyPayload = RefinedWsnbFactory.getInstance().createNotify(msg);

			if (topicUsed != null) {
				final TopicExpressionType notifyTopicExpr = createTopicExpression(
						topicUsed, dialect);
				msg.setTopic(notifyTopicExpr);
			}

			if (endpointAddress != null && uuid != null) {
				final EndpointReferenceType registrationRef = SOAUtil
						.getInstance().getXmlObjectFactory()
						.create(EndpointReferenceType.class);
				Address address = SOAUtil.getInstance().getXmlObjectFactory()
						.create(Address.class);
				address.setValue(URI.create(endpointAddress));
				registrationRef.setAddress(address);

				final ReferenceParameters ref = SOAUtil.getInstance()
						.getXmlObjectFactory()
						.create(ReferenceParameters.class);
				registrationRef.setReferenceParameters(ref);

				// final ResourcesUuidType rUuids =
				// WSNotificationExtensionFactory
				// .getInstance().createResourcesUuidType();
				// rUuids.addUuid(uuid);
				// WsnSpecificTypeHelper.setResourcesUuidType(rUuids, ref);

				msg.setSubscriptionReference(registrationRef);
			}
			final EndpointReferenceType producerRef = SOAUtil.getInstance()
					.getXmlObjectFactory().create(EndpointReferenceType.class);
			Address address = SOAUtil.getInstance().getXmlObjectFactory()
					.create(Address.class);
			address.setValue(URI.create(producerAddress));
			producerRef.setAddress(address);
			msg.setProducerReference(producerRef);

			notifyPayload.addNotificationMessage(msg);

		} catch (WsnbException e) {
			throw new NotificationException(e);
		}
		return notifyPayload;
	}

}
