/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.ow2.petals.component.framework;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.jbi.JBIException;
import javax.naming.InitialContext;

import org.ow2.petals.component.framework.bc.AbstractBindingComponent;

/**
 * FIXME = This is not only for the binding component but for all the
 * components, we will do it later!
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public class PetalsBindingComponent extends AbstractBindingComponent implements
		PluginAware {

	private final Map<Class<?>, Object> plugins;

	private final Properties containerProperties;

	/**
	 * 
	 */
	public PetalsBindingComponent() {
		super();
		this.plugins = new ConcurrentHashMap<Class<?>, Object>();
		this.containerProperties = new Properties();
	}

	/**
	 * @see #postDoInit() if you need to do some initialisation things which you
	 *      normally do in {@link #doInit()}
	 */
	@Override
	protected final void doInit() throws JBIException {
		// here we have all that is mandatory to create the plugins
		ComponentInformation componentInformation = new JNDIComponentInformation() {
			@Override
			public InitialContext getInitialContext() {
				return PetalsBindingComponent.this.getContext()
						.getNamingContext();
			}

			@Override
			protected String getComponentName() {
				return PetalsBindingComponent.this.getContext()
						.getComponentName();
			}
		};

		this.addPlugin(ComponentInformation.class, componentInformation);

		// load the container properties
		this.getLogger().fine("Loading the container properties...");
		try {
			this.containerProperties.load(PetalsBindingComponent.class
					.getClassLoader().getResourceAsStream(
							"components.properties"));
		} catch (Exception e) {
			this
					.getLogger()
					.warning(
							"Can not find the components properties file provided by the container");
		}

		this.postDoInit();
	}

	/**
	 * To be overrided if needed, replaces the #doInit method in standard petals
	 * components
	 * 
	 * @throws JBIException
	 */
	protected void postDoInit() throws JBIException {
	}

	public <T> void addPlugin(Class<T> type, T plugin) {
		if ((type == null) || (plugin == null)) {
			return;
		}
		this.plugins.put(type, plugin);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T getPlugin(Class<T> type) {
		Object o = this.plugins.get(type);
		if (o != null) {
			return type.cast(o);
		}
		return null;
	}

	/**
	 * 
	 */
	public String getContainerConfiguration(String propertyName) {
		String finalPropertyName = this.getContext().getComponentName() + "."
				+ propertyName;
		return this.containerProperties.getProperty(finalPropertyName);
	}

}
