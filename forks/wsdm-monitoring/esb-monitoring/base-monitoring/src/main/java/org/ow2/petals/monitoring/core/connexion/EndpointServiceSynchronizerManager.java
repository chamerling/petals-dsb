package org.ow2.petals.monitoring.core.connexion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.monitoring.core.api.WSDMAdminBehaviour;

public class EndpointServiceSynchronizerManager {

	private static final int THREAD_COUNTER = 1;

	private static final int DELAY = 30000;

	private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

	private String address;

	private WSDMAdminBehaviour wsdmAdminBehaviour;

	private List<Endpoint> functionnalEndpoints = Collections
			.synchronizedList(new ArrayList<Endpoint>());

	public EndpointServiceSynchronizerManager(
			final WSDMAdminBehaviour wsdmAdminBehaviour,
			final String petalsAddress) throws ESBException {
		this.address = petalsAddress;
		this.wsdmAdminBehaviour = wsdmAdminBehaviour;

		// create the listener collection
		final Collection<EndpointServiceSynchronizer> endpointServices = new HashSet<EndpointServiceSynchronizer>();

		// Start the listeners in the thread worker
		this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(
				EndpointServiceSynchronizerManager.THREAD_COUNTER);

		this.scheduledThreadPoolExecutor
				.scheduleWithFixedDelay(new EndpointServiceSynchronizer(this),
						0, EndpointServiceSynchronizerManager.DELAY,
						TimeUnit.MILLISECONDS);
	}

	public String getAddress() {
		return this.address;
	}

	public List<Endpoint> getFunctionnalEndpoints() {
		return this.functionnalEndpoints;
	}

	public WSDMAdminBehaviour getWsdmAdminBehaviour() {
		return this.wsdmAdminBehaviour;
	}

	public void setAddress(final String address) {
		this.address = address;
	}

	public void setFunctionnalEndpoints(
			final List<Endpoint> functionnalEndpoints) {
		this.functionnalEndpoints = functionnalEndpoints;
	}

	public void setWsdmAdminBehaviour(
			final WSDMAdminBehaviour wsdmAdminBehaviour) {
		this.wsdmAdminBehaviour = wsdmAdminBehaviour;
	}

	public void shutdownAllEndpointServices() {
		this.scheduledThreadPoolExecutor.shutdown();
		try {
			this.scheduledThreadPoolExecutor.awaitTermination(10,
					TimeUnit.SECONDS);
		} catch (final InterruptedException e1) {
			// do nothing
		}
	}

}
