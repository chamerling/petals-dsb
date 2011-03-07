package org.ow2.petals.monitoring.core.api;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.entity.Client;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
@Interface(name = "client-monitoring")
public interface ClientMonitoring extends Client {

	MonitoringClientEndpoint createClientMonitoringEndpoint(QName clientName)
			throws MonitoringException;

}
