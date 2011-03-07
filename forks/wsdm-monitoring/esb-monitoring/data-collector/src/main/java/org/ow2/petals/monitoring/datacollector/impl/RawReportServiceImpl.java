/**
 * MonitoringEngine-Core - SOA Tools Platform.
 * Copyright (c) 2008 EBM Websourcing, http://www.ebmwebsourcing.com/
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * -------------------------------------------------------------------------
 * $id.java
 * -------------------------------------------------------------------------
 */
package org.ow2.petals.monitoring.datacollector.impl;

import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.impl.endpoint.context.EndpointInitialContextImpl;
import org.ow2.petals.esb.kernel.impl.service.TechnicalServiceImpl;
import org.ow2.petals.monitoring.core.api.MonitoringException;
import org.ow2.petals.monitoring.datacollector.api.RawReportProviderEndpoint;
import org.ow2.petals.monitoring.datacollector.api.RawReportService;

import com.ebmwebsourcing.wsstar.notification.definition.utils.WSNotificationException;
import com.ebmwebsourcing.wsstar.notification.service.basenotification.WsnbNotificationProducer;
import com.ebmwebsourcing.wsstar.notification.service.basenotification.WsnbSubscriptionManager;
import com.ebmwebsourcing.wsstar.notification.service.basenotification.impl.NotificationProducerMgr;
import com.ebmwebsourcing.wsstar.notification.service.basenotification.impl.SubscriptionManagerMgr;
import com.ebmwebsourcing.wsstar.notification.service.topic.WstopTopicManager;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller = "composite")
public class RawReportServiceImpl extends TechnicalServiceImpl implements
		RawReportService {

	/**
     *
     */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(RawReportServiceImpl.class
			.getName());

	public RawReportProviderEndpoint createRawReportEndpoint(
			final String endpointName, final List<String> supportedTopics)
			throws MonitoringException {
		RawReportProviderEndpoint rawReportProviderEndpoint = null;
		try {
			rawReportProviderEndpoint = this.createProviderEndpoint(
					endpointName, "rawReportProviderEndpoint",
					RawReportProviderEndpointImpl.class,
					new EndpointInitialContextImpl(5));
			rawReportProviderEndpoint.setInterfaceName(new QName(
					"http://www.semeuse.org/services/rawReport/",
					"RawReportServiceInterface"));

			rawReportProviderEndpoint
					.setBehaviourClass(RawReportProviderEndpointBehaviourImpl.class);

			RawReportServiceImpl.log.finest("create behaviour");
			rawReportProviderEndpoint.getBehaviour();

			final WstopTopicManager wstopTopicsMgr = new WstopTopicManager(this
					.getClass().getResourceAsStream(
							"/topicNamespace/rawreport/rawreport-events.xml"),
					supportedTopics);
			final WsnbSubscriptionManager subscriptionManager = new SubscriptionManagerMgr(
					RawReportServiceImpl.log, wstopTopicsMgr, null);
			final WsnbNotificationProducer producer = new NotificationProducerMgr(
					RawReportServiceImpl.log, wstopTopicsMgr,
					(SubscriptionManagerMgr) subscriptionManager);
			rawReportProviderEndpoint
					.setSubscriptionManager(subscriptionManager);
			rawReportProviderEndpoint.setNotificationProducer(producer);

		} catch (final ESBException e) {
			throw new MonitoringException(e);
		} catch (final WSNotificationException e) {
			throw new MonitoringException(e);
		} catch (final IllegalArgumentException e) {
			throw new MonitoringException(e);
		}
		return rawReportProviderEndpoint;
	}

}
