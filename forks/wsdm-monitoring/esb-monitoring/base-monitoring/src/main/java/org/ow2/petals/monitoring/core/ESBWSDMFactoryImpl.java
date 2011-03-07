package org.ow2.petals.monitoring.core;

import javax.xml.namespace.QName;

import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.petals.esb.api.ESBFactory;
import org.ow2.petals.esb.impl.ESBFactoryImpl;
import org.ow2.petals.esb.impl.endpoint.behaviour.AdminBehaviourImpl;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.ProviderEndpoint;
import org.ow2.petals.esb.kernel.api.node.Node;
import org.ow2.petals.esb.kernel.impl.config.Configuration;
import org.ow2.petals.monitoring.core.api.BaseMonitoring;
import org.ow2.petals.monitoring.core.api.ClientMonitoring;
import org.ow2.petals.monitoring.core.api.MonitoringEngine;
import org.ow2.petals.monitoring.core.api.MonitoringException;
import org.ow2.petals.monitoring.core.api.WSDMAdminBehaviour;
import org.ow2.petals.monitoring.core.connexion.EndpointServiceSynchronizerManager;
import org.ow2.petals.monitoring.core.impl.MonitoringEngineImpl;
import org.ow2.petals.monitoring.core.impl.WSDMAdminBehaviourImpl;

public class ESBWSDMFactoryImpl extends ESBFactoryImpl implements ESBFactory {
	
	private final String DEFAULT_PETALS_ADDRESS = "http://localhost:7600/petals/ws/EndpointService";

	private EndpointServiceSynchronizerManager epServicesManager = null;

	public void activatePEtALSConnexion(final WSDMAdminBehaviour adminBehaviour)
			throws ESBException {
		if (this.epServicesManager == null) {
			
			String petalsAddress = Configuration.getData().get(
					Configuration.REMOTE_REGISTRY_URL);
			if (petalsAddress == null) {
				petalsAddress = this.DEFAULT_PETALS_ADDRESS;
			}
			this.epServicesManager = new EndpointServiceSynchronizerManager(
					adminBehaviour, petalsAddress);
		}
	}

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

			// add wsdm support
			final MonitoringEngine monitoringEngine = node.createComponent(
					new QName("http://petals.ow2.org", "Monitoring"),
					"monitoringEngine", MonitoringEngineImpl.class);
			final BaseMonitoring wsdmProviderMonitoring = monitoringEngine
					.createBaseMonitoring();
			final ClientMonitoring wsdmClientMonitoring = monitoringEngine
					.createClientMonitoring(new QName(node.getQName()
							.getNamespaceURI(), "ClientMonitoring"));

			final WSDMAdminBehaviour adminBehaviour = this
					.getWSDMAdminBehaviour(node);
			adminBehaviour.setWsdmProvider(wsdmProviderMonitoring);
			adminBehaviour.setWsdmClient(wsdmClientMonitoring);
			final Description d = adminBehaviour.getDescription();

			// create synchronizer on petals server
			this.activatePEtALSConnexion(adminBehaviour);

		} catch (final MonitoringException e) {
			throw new ESBException(e);
		}
		return node;
	}

	public Node createNode(final QName name,
			final Class<? extends AdminBehaviourImpl> adminBehaviourClass,
			final boolean explorer, final boolean connexion2petals)
			throws ESBException {
		final Node node = this.createNode(name, adminBehaviourClass, explorer);

		return node;
	}

	public void desactivatePEtALSConnexion() {
		if (this.epServicesManager != null) {
			this.epServicesManager.shutdownAllEndpointServices();
			this.epServicesManager = null;
		}
	}

	protected WSDMAdminBehaviour getWSDMAdminBehaviour(final Node node)
			throws ESBException {
		final ProviderEndpoint pe = (ProviderEndpoint) node.getRegistry()
				.getEndpoint(
						new QName(node.getQName().getNamespaceURI(),
								"adminEndpoint"));
		return ((WSDMAdminBehaviour) pe.getBehaviour());
	}
}
