package org.ow2.petals.monitoring.datacollector.api;

import java.util.List;

import org.ow2.petals.esb.kernel.api.endpoint.behaviour.Behaviour;
import org.ow2.petals.monitoring.model.rawreport.api.RawReport;

import com.ebmwebsourcing.wsstar.notification.service.basenotification.WsnbNotificationConsumer;

public interface RawReportClientEndpointBehaviour extends Behaviour<RawReport>,
		WsnbNotificationConsumer {

	/**
	 * @return the current rawreports list; Once given, the rawreports are
	 *         deleted (i.e. a second call to getRawReports() will not give
	 *         already given RawReports).
	 */
	List<RawReport> getRawReports();

}
