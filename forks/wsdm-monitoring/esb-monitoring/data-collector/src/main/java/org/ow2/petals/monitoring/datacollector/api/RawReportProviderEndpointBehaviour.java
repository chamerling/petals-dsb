package org.ow2.petals.monitoring.datacollector.api;

import java.util.List;
import java.util.Map;

import org.ow2.petals.esb.kernel.api.endpoint.behaviour.Behaviour;
import org.ow2.petals.monitoring.core.api.MonitoringException;
import org.ow2.petals.monitoring.model.rawreport.api.Report;

import com.ebmwebsourcing.wsstar.notification.service.basenotification.WsnbNotificationProducer;
import com.ebmwebsourcing.wsstar.notification.service.basenotification.WsnbSubscriptionManager;

public interface RawReportProviderEndpointBehaviour extends Behaviour<Object>,
		WsnbNotificationProducer, WsnbSubscriptionManager {

	void addNewReport(String messageId, Report report)
			throws MonitoringException;

	Map<String, List<Report>> getReports();

}
