package org.ow2.petals.monitoring.core.api;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.entity.Provider;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
@Interface(name = "base-monitoring")
public interface BaseMonitoring extends Provider {

	MonitoringService createMonitoringService(QName service)
			throws ESBException;

}
