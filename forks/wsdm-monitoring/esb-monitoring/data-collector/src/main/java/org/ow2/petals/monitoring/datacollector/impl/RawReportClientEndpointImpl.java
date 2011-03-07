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

import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.petals.esb.kernel.impl.endpoint.ClientAndProviderEndpointImpl;
import org.ow2.petals.monitoring.datacollector.api.RawReportClientEndpoint;

import com.ebmwebsourcing.wsstar.notification.service.basenotification.WsnbNotificationConsumer;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller = "primitive")
public class RawReportClientEndpointImpl extends ClientAndProviderEndpointImpl
		implements RawReportClientEndpoint {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger
			.getLogger(RawReportClientEndpointImpl.class.getName());

	private Description description;

	private QName interfaceName;

	private WsnbNotificationConsumer consumer = null;

	public Description getDescription() {
		if (this.description == null) {
			// TODO: create wsdl interface
		}
		return this.description;
	}

	public QName getInterfaceName() {
		return this.interfaceName;
	}

	public WsnbNotificationConsumer getNotificationConsumer() {
		return this.consumer;
	}

	public void setInterfaceName(final QName interfaceName) {
		this.interfaceName = interfaceName;
	}

	public void setNotificationConsumer(final WsnbNotificationConsumer consumer) {
		this.consumer = consumer;
	}
}
