package org.ow2.petals.monitoring.datacollector.api;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.endpoint.ClientAndProviderEndpoint;

import com.ebmwebsourcing.wsstar.notification.service.basenotification.WsnbNotificationConsumer;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
@Interface(name = "rawReportClientEndpoint")
public interface RawReportClientEndpoint extends ClientAndProviderEndpoint {

	QName getInterfaceName();

	WsnbNotificationConsumer getNotificationConsumer();

	void setInterfaceName(QName interfaceName);

	void setNotificationConsumer(WsnbNotificationConsumer consumer);

}
