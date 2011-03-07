package org.ow2.petals.monitoring.core;

import org.ow2.petals.esb.kernel.api.ESBKernelFactory;
import org.ow2.petals.launcher.AbstractNodeLauncher;
import org.ow2.petals.launcher.Launcher;

public class MonitoringLauncher extends AbstractNodeLauncher implements
		Launcher {

	public MonitoringLauncher() {
		super();
		this.factory = new ESBWSDMFactoryImpl();
	}

	@Override
	protected String getDistributionName() {
		return "WSDM ESB Node version";
	}

	public ESBKernelFactory getFactory() {
		return this.factory;
	}

}
