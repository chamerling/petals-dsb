package org.ow2.petals.monitoring.core.api;

import java.util.List;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.service.TechnicalService;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
@Interface(name = "monitoringService")
public interface MonitoringService extends TechnicalService {

	MonitoringProviderEndpoint createMonitoringEndpoint(String endpointName,
			List<String> supportedTopics) throws MonitoringException;
}
