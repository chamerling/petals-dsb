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

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.impl.entity.ProviderImpl;
import org.ow2.petals.monitoring.core.api.BaseMonitoring;
import org.ow2.petals.monitoring.core.api.MonitoringService;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller = "composite")
public class BaseMonitoringImpl extends ProviderImpl implements BaseMonitoring {

	/**
     *
     */
	private static final long serialVersionUID = 1L;

	public MonitoringService createMonitoringService(final QName service)
			throws ESBException {
		return this.createService(service, "monitoringService",
				MonitoringServiceImpl.class);
	}

}
