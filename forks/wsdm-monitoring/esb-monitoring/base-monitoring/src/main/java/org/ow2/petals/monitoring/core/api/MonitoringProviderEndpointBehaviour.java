package org.ow2.petals.monitoring.core.api;

import org.ow2.petals.esb.kernel.api.endpoint.behaviour.Behaviour;
import org.ow2.petals.monitoring.model.rawreport.api.ReportList;

import com.ebmwebsourcing.wsstar.notification.service.basenotification.WsnbNotificationProducer;
import com.ebmwebsourcing.wsstar.notification.service.basenotification.WsnbSubscriptionManager;

public interface MonitoringProviderEndpointBehaviour extends Behaviour<Object>,
		WsnbNotificationProducer, WsnbSubscriptionManager {

	void addNewExchange(ExchangeTrace trace) throws MonitoringException;

	void addNewReportList(ReportList reports) throws MonitoringException;

}
