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
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.impl.component.ComponentImpl;
import org.ow2.petals.monitoring.core.api.BaseMonitoring;
import org.ow2.petals.monitoring.core.api.ClientMonitoring;
import org.ow2.petals.monitoring.core.api.MonitoringEngine;
import org.ow2.petals.monitoring.core.api.MonitoringException;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller = "composite")
public class MonitoringEngineImpl extends ComponentImpl implements
		MonitoringEngine {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(MonitoringEngineImpl.class
			.getName());

	/**
	 * constructor to feed in subclasses of processes.
	 * 
	 * @throws MonitoringException
	 */
	public BaseMonitoring createBaseMonitoring() throws MonitoringException {
		BaseMonitoring res = null;

		if (this.getBaseMonitoring() != null) {
			throw new MonitoringException("Base monitoring already exist");
		}

		try {
			res = this.createProvider(new QName(this.getQName()
					.getNamespaceURI(), "base-monitoring"), "base-monitoring",
					BaseMonitoringImpl.class);
		} catch (final ESBException e) {
			throw new MonitoringException(e);
		}
		return res;
	}

	public ClientMonitoring createClientMonitoring(final QName clientName)
			throws MonitoringException {
		ClientMonitoring res = null;

		if (this.getClientMonitoring(clientName) != null) {
			throw new MonitoringException("Client monitoring with this name "
					+ clientName + " already exist");
		}

		try {
			res = this.createClient(clientName, "client-monitoring",
					ClientMonitoringImpl.class);
		} catch (final ESBException e) {
			throw new MonitoringException(e);
		}
		return res;
	}

	public BaseMonitoring getBaseMonitoring() {
		return (BaseMonitoring) this.getNode().getRegistry()
				.getEndpoint(
						new QName(this.getQName().getNamespaceURI(),
								"base-monitoring"));
	}

	public ClientMonitoring getClientMonitoring(final QName clientName) {
		return (ClientMonitoring) this.getNode().getRegistry().getEndpoint(
				clientName);
	}

}
