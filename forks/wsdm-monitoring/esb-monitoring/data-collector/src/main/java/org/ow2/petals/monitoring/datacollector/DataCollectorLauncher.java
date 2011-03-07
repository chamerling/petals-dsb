package org.ow2.petals.monitoring.datacollector;

import org.ow2.petals.esb.kernel.api.ESBKernelFactory;
import org.ow2.petals.launcher.AbstractNodeLauncher;
import org.ow2.petals.launcher.Launcher;

public class DataCollectorLauncher extends AbstractNodeLauncher implements
		Launcher {

	public DataCollectorLauncher() {
		super();
		this.factory = new ESBDataCollectorFactoryImpl();
	}

	@Override
	protected String getDistributionName() {
		return "DataCollector ESB Node version";
	}

	public ESBKernelFactory getFactory() {
		return this.factory;
	}

}
