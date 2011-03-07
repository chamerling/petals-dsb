package org.ow2.petals.monitoring.datacollector.api;

import javax.xml.namespace.QName;

import org.objectweb.fractal.fraclet.annotations.Interface;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.entity.ClientAndProvider;

/**
 * @author Nicolas Salatge - eBM WebSourcing
 */
@Interface(name = "data-collector")
public interface DataCollector extends ClientAndProvider {

	RawReportClientEndpoint createRawReportClientEndpoint(QName name)
			throws ESBException;

	RawReportService createRawReportService(QName service) throws ESBException;
}
