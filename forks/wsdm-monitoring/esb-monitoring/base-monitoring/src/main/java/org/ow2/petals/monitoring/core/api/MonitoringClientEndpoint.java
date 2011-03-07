package org.ow2.petals.monitoring.core.api;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.endpoint.ClientAndProviderEndpoint;

import com.ebmwebsourcing.wsstar.notification.service.basenotification.WsnbNotificationConsumer;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
@Interface(name = "monitoringClientEndpoint")
public interface MonitoringClientEndpoint extends ClientAndProviderEndpoint {

	QName getInterfaceName();

	WsnbNotificationConsumer getNotificationConsumer();

	void setInterfaceName(QName interfaceName);

	void setNotificationConsumer(WsnbNotificationConsumer consumer);

}
