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
package org.ow2.petals.monitoring.core.impl;

import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.petals.esb.kernel.impl.endpoint.ClientAndProviderEndpointImpl;
import org.ow2.petals.monitoring.core.api.MonitoringProviderEndpoint;

import com.ebmwebsourcing.wsstar.notification.service.basenotification.WsnbNotificationProducer;
import com.ebmwebsourcing.wsstar.notification.service.basenotification.WsnbSubscriptionManager;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller = "primitive")
public class MonitoringProviderEndpointImpl extends
		ClientAndProviderEndpointImpl implements MonitoringProviderEndpoint {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger
			.getLogger(MonitoringProviderEndpointImpl.class.getName());

	private Description description;

	private QName interfaceName;

	private WsnbNotificationProducer producer = null;

	private WsnbSubscriptionManager subscriptionManager = null;

	public QName getInterfaceName() {
		return this.interfaceName;
	}

	public WsnbNotificationProducer getNotificationProducer() {
		return this.producer;
	}

	public WsnbSubscriptionManager getSubscriptionManager() {
		return this.subscriptionManager;
	}

	public void setInterfaceName(final QName interfaceName) {
		this.interfaceName = interfaceName;
	}

	public void setNotificationProducer(final WsnbNotificationProducer producer) {
		this.producer = producer;
	}

	public void setSubscriptionManager(
			final WsnbSubscriptionManager subscriptionManager) {
		this.subscriptionManager = subscriptionManager;
	}
}
