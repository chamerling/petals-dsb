package org.ow2.petals.monitoring.datacollector.api;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.endpoint.ClientAndProviderEndpoint;

import com.ebmwebsourcing.wsstar.notification.service.basenotification.WsnbNotificationProducer;
import com.ebmwebsourcing.wsstar.notification.service.basenotification.WsnbSubscriptionManager;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
@Interface(name = "rawReportProviderEndpoint")
public interface RawReportProviderEndpoint extends ClientAndProviderEndpoint {

	QName getInterfaceName();

	WsnbNotificationProducer getNotificationProducer();

	WsnbSubscriptionManager getSubscriptionManager();

	void setInterfaceName(QName interfaceName);

	void setNotificationProducer(WsnbNotificationProducer producer);

	void setSubscriptionManager(WsnbSubscriptionManager subscriptionManager);

}
