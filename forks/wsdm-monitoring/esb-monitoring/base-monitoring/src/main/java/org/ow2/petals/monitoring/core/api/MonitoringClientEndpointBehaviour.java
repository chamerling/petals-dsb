package org.ow2.petals.monitoring.core.api;

import java.util.List;

import org.ow2.petals.esb.kernel.api.endpoint.behaviour.Behaviour;

import com.ebmwebsourcing.wsstar.dm.api.QoSMetrics;
import com.ebmwebsourcing.wsstar.notification.definition.basenotification.api.Notify;
import com.ebmwebsourcing.wsstar.notification.service.basenotification.WsnbNotificationConsumer;

public interface MonitoringClientEndpointBehaviour extends Behaviour<Notify>,
		WsnbNotificationConsumer {

	List<QoSMetrics> getMetrics();
}
