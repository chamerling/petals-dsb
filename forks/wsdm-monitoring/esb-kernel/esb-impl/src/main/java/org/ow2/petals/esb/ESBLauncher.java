package org.ow2.petals.esb;

import org.ow2.petals.esb.impl.ESBFactoryImpl;
import org.ow2.petals.esb.kernel.api.ESBKernelFactory;
import org.ow2.petals.launcher.AbstractNodeLauncher;
import org.ow2.petals.launcher.Launcher;

public class ESBLauncher extends AbstractNodeLauncher implements Launcher {

	private ESBKernelFactory factory = null;
	
	
	public ESBLauncher() {
		super();
		this.factory = new ESBFactoryImpl();
	}

	@Override
	protected String getDistributionName() {
		return "ESB Node version";
	}

	public ESBKernelFactory getFactory() {
		return this.factory;
	}

}
