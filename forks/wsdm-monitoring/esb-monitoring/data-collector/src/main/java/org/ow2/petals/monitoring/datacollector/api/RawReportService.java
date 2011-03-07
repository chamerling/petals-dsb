package org.ow2.petals.monitoring.datacollector.api;

import java.util.List;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.service.TechnicalService;
import org.ow2.petals.monitoring.core.api.MonitoringException;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
@Interface(name = "rawReportService")
public interface RawReportService extends TechnicalService {

	RawReportProviderEndpoint createRawReportEndpoint(String endpointName,
			List<String> supportedTopics) throws MonitoringException;
}
