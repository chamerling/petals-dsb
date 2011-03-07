package org.ow2.petals.monitoring.datacollector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.petals.esb.api.ESBFactory;
import org.ow2.petals.esb.impl.endpoint.behaviour.AdminBehaviourImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.node.Node;
import org.ow2.petals.monitoring.core.ESBWSDMFactoryImpl;
import org.ow2.petals.monitoring.core.api.MonitoringException;
import org.ow2.petals.monitoring.core.connexion.EndpointServiceSynchronizerManager;
import org.ow2.petals.monitoring.core.impl.WSDMAdminBehaviourImpl;
import org.ow2.petals.monitoring.datacollector.api.DataCollector;
import org.ow2.petals.monitoring.datacollector.api.MonitoringEngine;
import org.ow2.petals.monitoring.datacollector.api.RawReportProviderEndpoint;
import org.ow2.petals.monitoring.datacollector.api.RawReportService;
import org.ow2.petals.monitoring.datacollector.impl.MonitoringEngineImpl;

public class ESBDataCollectorFactoryImpl extends ESBWSDMFactoryImpl implements
		ESBFactory {
	
	private static Logger log = Logger
	.getLogger(ESBDataCollectorFactoryImpl.class.getName());

	private final EndpointServiceSynchronizerManager epServicesManager = null;

	@Override
	public Node createNode(final QName name, final boolean explorer)
			throws ESBException {
		return this.createNode(name, WSDMAdminBehaviourImpl.class, explorer);
	}

	@Override
	public Node createNode(final QName name,
			final Class<? extends AdminBehaviourImpl> adminBehaviourClass,
			final boolean explorer) throws ESBException {
		final Node node = super.createNode(name, adminBehaviourClass, explorer);
		try {
			final MonitoringEngine monitoringEngine = node.createComponent(
					new QName("http://petals.ow2.org", "Monitoring"),
					"dataEngine", MonitoringEngineImpl.class);
			final DataCollector dataCollector = monitoringEngine
					.createDataCollector();

			// create technical provider raw report service
			final QName serviceName = new QName("http://petals.ow2.org",
					"rawReportService");
			final String endpointName = "rawReportEndpoint";
			final RawReportService rawReportService = dataCollector
					.createRawReportService(serviceName);
			final RawReportProviderEndpoint rawReportProviderEndpoint = rawReportService
					.createRawReportEndpoint(endpointName,
							new ArrayList<String>(Arrays
									.asList(new String[] { "RawReportTopic" })));
			
			final Description desc = rawReportProviderEndpoint.getBehaviour()
					.getDescription();
			// try {
			// System.out.println(XMLPrettyPrinter.prettyPrint(WSDL4ComplexWsdlFactory.newInstance().newWSDLWriter().getDocument(desc)));
			// } catch (WSDL4ComplexWsdlException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// throw new ESBException(e);
			// }

			final String res = this.getWSDMAdminBehaviour(node)
					.exposeInSoapOnClient(
							new QName("http://petals.ow2.org",
									"rawReportService"), "rawReportEndpoint",
							dataCollector.getQName());


		} catch (final MonitoringException e) {
			throw new ESBException(e);
		}
		return node;
	}

	@Override
	public Node createNode(final QName name,
			final Class<? extends AdminBehaviourImpl> adminBehaviourClass,
			final boolean explorer, final boolean connexion2petals)
			throws ESBException {
		final Node node = this.createNode(name, adminBehaviourClass, explorer);

		return node;
	}

}
