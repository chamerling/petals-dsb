/**
 * MonitoringEngine-Core - SOA Tools Platform.
 * Copyright (c) 2008 EBM Websourcing, http://www.ebmwebsourcing.com/
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * -------------------------------------------------------------------------
 * $id.java
 * -------------------------------------------------------------------------
 */
package org.ow2.petals.monitoring.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.adl.FactoryFactory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalContentException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.fractal.api.control.NameController;
import org.objectweb.fractal.api.control.SuperController;
import org.objectweb.fractal.util.Fractal;
import org.ow2.petals.monitoring.core.api.MonitoringException;

/**
 * This class helps fractal management.
 * 
 * @author Nicolas Salatge - eBM WebSourcing
 */
public class FractalHelper {

	private static final Logger log = Logger.getLogger(FractalHelper.class
			.getName());

	/**
	 * the explorer composite component name.
	 */
	public static final String EXPLORER_COMPOSITE = "Explorer";

	/**
	 * the petals component name.
	 */
	public static final String MONITORING_EXPLORER_COMPOSITE = "MonitoringExplorer";

	/**
	 * the FCAPPL binding name.
	 */
	public static final String FCAPPL_BINDING = "fcAppl";

	/**
	 * The singleton internal instance.
	 */
	private static FractalHelper fractalHelper;

	/**
	 * the context.
	 */
	private static Map<Object, Object> context = null;

	/**
	 * the fractal factory.
	 */
	private static Factory factory = null;

	/**
	 * Get a FractalHelper instance.
	 * 
	 * @return the fractalHelper instance
	 * @throws ADLException
	 *             : occurs when it is impossible to get the fractal explorer
	 */
	public static FractalHelper getFractalHelper() throws FractalException {
		if (FractalHelper.fractalHelper == null) {
			FractalHelper.fractalHelper = new FractalHelper();
		}
		return FractalHelper.fractalHelper;
	}

	/**
	 * Create a FractalHelper instance.
	 * 
	 * @throws ADLException
	 *             : occurs when it is impossible to get the fractal explorer
	 */
	private FractalHelper() throws FractalException {
		super();

		// check required fractal properties
		System.setProperty("fractal.provider",
				"org.objectweb.fractal.julia.Julia");
		System.setProperty("julia.loader",
				"org.objectweb.fractal.julia.loader.DynamicLoader");
		System.setProperty("julia.config", "julia.cfg");

		// Create Fractal ADL factories
		FractalHelper.context = new HashMap<Object, Object>();
		try {
			FractalHelper.factory = FactoryFactory.getFactory(
					FactoryFactory.FRACTAL_BACKEND, FractalHelper.context);
		} catch (final ADLException e) {
			throw new FractalException(
					"Impossible to initialize the fractal helper", e);
		}
	}

	/**
	 * Add a component in a composite component.
	 * 
	 * @param newComponent
	 *            The new component
	 * @param parentComponent
	 *            The parent component
	 * @param listOfBindings
	 *            the list of binding to create between this new component and
	 *            the others
	 * @throws NoSuchInterfaceException
	 *             : Impossible to add a component
	 * @throws IllegalLifeCycleException
	 *             : Impossible to add a component
	 * @throws IllegalContentException
	 *             : Impossible to add a component
	 * @throws IllegalBindingException
	 *             : Impossible to add a component
	 */
	public void addBindings(final Component component,
			final List<Binding> listOfBindings) throws FractalException {
		try {

			// Get the binding controller of the new component
			final BindingController cBindingController = Fractal
					.getBindingController(component);

			// Add all the bindings
			if (listOfBindings != null) {
				for (int i = 0; i < listOfBindings.size(); i++) {
					cBindingController.bindFc(listOfBindings.get(i)
							.getClientInterfaceName(), (listOfBindings.get(i))
							.getServerInterface());
				}
			}
		} catch (final NoSuchInterfaceException e) {
			throw new FractalException(
					"Impossible to bind the fractal component", e);
		} catch (final IllegalLifeCycleException e) {
			throw new FractalException(
					"Impossible to bind the fractal component", e);
		} catch (final IllegalBindingException e) {
			throw new FractalException(
					"Impossible to bind the fractal component", e);
		}
	}

	/**
	 * Add a component in a composite component.
	 * 
	 * @param newComponent
	 *            The new component
	 * @param parentComponent
	 *            The parent component
	 * @param listOfBindings
	 *            the list of binding to create between this new component and
	 *            the others
	 * @throws NoSuchInterfaceException
	 *             : Impossible to add a component
	 * @throws IllegalLifeCycleException
	 *             : Impossible to add a component
	 * @throws IllegalContentException
	 *             : Impossible to add a component
	 * @throws IllegalBindingException
	 *             : Impossible to add a component
	 */
	public void addComponent(final Component newComponent,
			final Component parentComponent, final List<Binding> listOfBindings)
			throws FractalException {
		try {
			if (parentComponent == null) {
				throw new NullPointerException("The parent component "
						+ parentComponent + " cannot be null");
			}

			// Get the parent controller
			final ContentController parentContentController = Fractal
					.getContentController(parentComponent);

			parentContentController.addFcSubComponent(newComponent);

			FractalHelper.getFractalHelper().addBindings(newComponent,
					listOfBindings);

		} catch (final NoSuchInterfaceException e) {
			throw new FractalException(
					"Impossible to add a new fractal component", e);
		} catch (final IllegalContentException e) {
			throw new FractalException(
					"Impossible to add a new fractal component", e);
		} catch (final IllegalLifeCycleException e) {
			throw new FractalException(
					"Impossible to add a new fractal component", e);
		}
	}

	/**
	 * Change the name of the component
	 * 
	 * @param name
	 * @throws MonitoringException
	 */
	public void changeName(final Component comp, final String name)
			throws FractalException {
		try {
			final NameController nc = Fractal.getNameController(comp);
			nc.setFcName(name);
		} catch (final NoSuchInterfaceException e) {
			throw new FractalException(
					"Impossible to change the name ot this fractal componet: "
							+ comp);
		}
	}

	/**
	 * Create an explorer for the specified component (the component requires an
	 * explicit interface named fcAppl of type
	 * org.objectweb.fractal.api.Component).
	 * 
	 * @param component
	 *            the component to explore
	 * @throws ADLException
	 *             : occurs when it is impossible to create the explorer
	 * @throws NoSuchInterfaceException
	 *             : occurs when it is impossible to create the explorer
	 * @throws IllegalBindingException
	 *             : occurs when it is impossible to create the explorer
	 * @throws IllegalLifeCycleException
	 *             : occurs when it is impossible to create the explorer
	 * @throws IllegalContentException
	 *             : occurs when it is impossible to create the explorer
	 * @throws MonitoringException
	 */
	public void createExplorer(final Component component,
			final String fractalName, final String fractalDefinition)
			throws FractalException {
		try {
			// create global composite
			final Component globalComposite = this.createNewComponent(
					FractalHelper.MONITORING_EXPLORER_COMPOSITE, null);

			final ContentController ccg = Fractal
					.getContentController(globalComposite);
			// search the server component
			final Component appli = this.getFirstComponentByName(
					globalComposite, FractalHelper.EXPLORER_COMPOSITE);

			// Start of the petals explorer
			this.startComponent(appli);

			// add the component in the global composite
			this.addComponent(component, appli, null);

			// create explorer
			final Map<Object, Object> ctxt = new HashMap<Object, Object>();
			// ctxt.put("name",fractalName);
			// ctxt.put("definition",fractalDefinition);
			ctxt.put("config", "MonitoringExplorerProperties.xml");

			// create explorer
			final Component fractalExplorer = this
					.createNewComponent(
							"org.objectweb.fractal.explorer.GenericFractalExplorerImpl",
							ctxt);

			// create the bindings list of the server component to others
			// components
			final List<Binding> listOfBindingsExplorer = new ArrayList<Binding>();

			listOfBindingsExplorer.add(new Binding(
					FractalHelper.FCAPPL_BINDING, (Interface) appli
							.getFcInterface(FractalHelper.FCAPPL_BINDING)));

			this.addComponent(fractalExplorer, globalComposite,
					listOfBindingsExplorer);
		} catch (final NoSuchInterfaceException e) {
			throw new FractalException(e);
		}
	}

	/**
	 * Create an explorer for the specified component (the component requires an
	 * explicit interface named fcAppl of type
	 * org.objectweb.fractal.api.Component).
	 * 
	 * @param component
	 *            the component to explore
	 * @throws ADLException
	 *             : occurs when it is impossible to create the explorer
	 * @throws NoSuchInterfaceException
	 *             : occurs when it is impossible to create the explorer
	 * @throws IllegalBindingException
	 *             : occurs when it is impossible to create the explorer
	 * @throws IllegalLifeCycleException
	 *             : occurs when it is impossible to create the explorer
	 * @throws IllegalContentException
	 *             : occurs when it is impossible to create the explorer
	 * @throws MonitoringException
	 */
	public Component createExplorer(final String fractalName,
			final String fractalDefinition) throws FractalException {
		Component fpvm = null;
		try {
			// create explorer
			final Map<Object, Object> ctxt = new HashMap<Object, Object>();
			ctxt.put("name", fractalName);
			ctxt.put("definition", fractalDefinition);

			final Component fractalExplorer = this
					.createNewComponent(
							"org.objectweb.fractal.explorer.BasicFractalExplorer",
							ctxt);

			final ContentController cc = Fractal
					.getContentController(fractalExplorer);

			for (final Component element : cc.getFcSubComponents()) {
				final NameController n = Fractal.getNameController(element);
				if (n.getFcName().equals(fractalName)) {
					fpvm = element;
				}
			}

			if (fpvm == null) {
				throw new FractalException("Refecrence of FPVM cannot be found");

			}
		} catch (final NoSuchInterfaceException e) {
			throw new FractalException(
					"Impossible to create the Fractal Explorer");
		}

		return fpvm;
	}

	/**
	 * Create a new component.
	 * 
	 * @param name
	 *            The class name of the component
	 * @return the new component
	 * @throws ADLException
	 *             : occurs when the framework of Petals is incorrect
	 */
	public Component createNewComponent(final String name,
			final Map<Object, Object> ctxt) throws FractalException {
		try {
			return (Component) this.getFactory().newComponent(name, ctxt);
		} catch (final ADLException e) {
			throw new FractalException(
					"Impossible to create a new fractal component", e);
		}
	}

	/**
	 * Delete all client binding of component.
	 * 
	 * @param newComponent
	 *            The new component
	 * @param parentComponent
	 *            The parent component
	 * @param listOfBindings
	 *            the list of binding to create between this new component and
	 *            the others
	 * @throws NoSuchInterfaceException
	 *             : Impossible to add a component
	 * @throws IllegalLifeCycleException
	 *             : Impossible to add a component
	 * @throws IllegalContentException
	 *             : Impossible to add a component
	 * @throws IllegalBindingException
	 *             : Impossible to add a component
	 */
	public void deleteAllClientBindingsOfComponent(final Component component)
			throws FractalException {
		final Map<String, Interface> map = this
				.getServerInterfacesLinkedToClientInterfacesOfComponent(component);
		final List<Binding> bindings = new ArrayList<Binding>();
		for (final Entry<String, Interface> entry : map.entrySet()) {
			bindings.add(new Binding(entry.getKey(), entry.getValue()));
		}
		this.deleteBindings(component, bindings);
	}

	/**
	 * Delete a component in a composite component.
	 * 
	 * @param newComponent
	 *            The new component
	 * @param parentComponent
	 *            The parent component
	 * @param listOfBindings
	 *            the list of binding to create between this new component and
	 *            the others
	 * @throws NoSuchInterfaceException
	 *             : Impossible to add a component
	 * @throws IllegalLifeCycleException
	 *             : Impossible to add a component
	 * @throws IllegalContentException
	 *             : Impossible to add a component
	 * @throws IllegalBindingException
	 *             : Impossible to add a component
	 */
	public void deleteBindings(final Component component,
			final List<Binding> listOfBindings) throws FractalException {
		try {

			// Get the binding controller of the new component
			final BindingController cBindingController = Fractal
					.getBindingController(component);

			// Add all the bindings
			if (listOfBindings != null) {
				for (int i = 0; i < listOfBindings.size(); i++) {
					cBindingController.unbindFc(((listOfBindings.get(i))
							.getClientInterfaceName()));
				}
			}
		} catch (final NoSuchInterfaceException e) {
			throw new FractalException(
					"Impossible to bind the fractal component", e);
		} catch (final IllegalLifeCycleException e) {
			throw new FractalException(
					"Impossible to bind the fractal component", e);
		} catch (final IllegalBindingException e) {
			throw new FractalException(
					"Impossible to bind the fractal component", e);
		}
	}

	/**
	 * Delete a component in a composite component.
	 * 
	 * @param oldComponent
	 *            The component to delete
	 * @param parentComponent
	 *            The parent component
	 * @param listOfBindings
	 *            the list of binding to create between this new component and
	 *            the others
	 * @throws NoSuchInterfaceException
	 *             : Impossible to add a component
	 * @throws IllegalLifeCycleException
	 *             : Impossible to add a component
	 * @throws IllegalContentException
	 *             : Impossible to add a component
	 * @throws IllegalBindingException
	 *             : Impossible to add a component
	 */
	public void deleteComponent(final Component oldComponent)
			throws FractalException {
		try {

			final Component parentComponent = this.getParent(oldComponent);

			final List<Binding> listOfBindings = new ArrayList<Binding>();

			final BindingController bc = Fractal
					.getBindingController(oldComponent);

			for (final String clientItf : bc.listFc()) {
				bc.unbindFc(clientItf);
			}

			final List<Component> clientsOfComp = this
					.getClientComponentsLinkedToServerInterfacesOfComponent(
							parentComponent, (Interface) oldComponent
									.getFcInterface("service"));

			for (final Component clientComponent : clientsOfComp) {
				final BindingController bcClient = Fractal
						.getBindingController(clientComponent);
				for (final String clientItf : bcClient.listFc()) {
					if (bcClient.lookupFc(clientItf) == (Interface) oldComponent
							.getFcInterface("service")) {
						bcClient.unbindFc(clientItf);
					}
				}
			}

			this.deleteComponent(oldComponent, parentComponent, listOfBindings);
		} catch (final NoSuchInterfaceException e) {
			throw new FractalException("Impossible to delete component", e);
		} catch (final IllegalBindingException e) {
			throw new FractalException("Impossible to delete component", e);
		} catch (final IllegalLifeCycleException e) {
			throw new FractalException("Impossible to delete component", e);
		}
	}

	/**
	 * Delete a component in a composite component.
	 * 
	 * @param oldComponent
	 *            The component to delete
	 * @param parentComponent
	 *            The parent component
	 * @param listOfBindings
	 *            the list of binding to create between this new component and
	 *            the others
	 * @throws NoSuchInterfaceException
	 *             : Impossible to add a component
	 * @throws IllegalLifeCycleException
	 *             : Impossible to add a component
	 * @throws IllegalContentException
	 *             : Impossible to add a component
	 * @throws IllegalBindingException
	 *             : Impossible to add a component
	 */
	public void deleteComponent(final Component oldComponent,
			final Component parentComponent, final List<Binding> listOfBindings)
			throws FractalException {
		try {
			if (parentComponent == null) {
				throw new NullPointerException("The parent component "
						+ parentComponent + " cannot be null");
			}

			// Get the parent controller
			final ContentController parentContentController = Fractal
					.getContentController(parentComponent);

			if (FractalHelper.getFractalHelper().isStarted(oldComponent)) {
				FractalHelper.getFractalHelper().stopComponent(oldComponent);
			}

			FractalHelper.getFractalHelper().deleteBindings(oldComponent,
					listOfBindings);

			parentContentController.removeFcSubComponent(oldComponent);

		} catch (final NoSuchInterfaceException e) {
			throw new FractalException(
					"Impossible to remove the fractal component", e);
		} catch (final IllegalContentException e) {
			throw new FractalException(
					"Impossible to remove the fractal component", e);
		} catch (final IllegalLifeCycleException e) {
			throw new FractalException(
					"Impossible to remove the fractal component", e);
		}
	}

	/**
	 * Delete all client binding of component.
	 * 
	 * @param newComponent
	 *            The new component
	 * @param parentComponent
	 *            The parent component
	 * @param listOfBindings
	 *            the list of binding to create between this new component and
	 *            the others
	 * @throws NoSuchInterfaceException
	 *             : Impossible to add a component
	 * @throws IllegalLifeCycleException
	 *             : Impossible to add a component
	 * @throws IllegalContentException
	 *             : Impossible to add a component
	 * @throws IllegalBindingException
	 *             : Impossible to add a component
	 */
	public void deleteLinkWithAnItfClientOfComponent(final Component component,
			final String itfCLient) throws FractalException {
		final Map<String, Interface> map = this
				.getServerInterfacesLinkedToClientInterfacesOfComponent(component);
		final List<Binding> bindings = new ArrayList<Binding>();
		for (final Entry<String, Interface> entry : map.entrySet()) {
			if (entry.getKey().equals(itfCLient)) {
				bindings.add(new Binding(entry.getKey(), entry.getValue()));
				break;
			}
		}
		this.deleteBindings(component, bindings);
	}

	/**
	 * A utility function allowing to get components interface binding to the
	 * client interface of the component controller.
	 * 
	 * @param parentContentController
	 *            parentContentController
	 * @param name
	 *            component name
	 * @return the component, null if not found
	 */
	public List<Component> getClientComponentsLinkedToServerInterfacesOfComponent(
			final Component parentComponent, final Interface itfOfComponent) {

		final List<Component> listOfcomponents = new ArrayList<Component>();

		try {
			final ContentController componentContentController = Fractal
					.getContentController(parentComponent);
			// List content controller subcomponents
			for (final Component component : componentContentController
					.getFcSubComponents()) {

				final Map<String, Interface> listOfItf = FractalHelper
						.getFractalHelper()
						.getServerInterfacesLinkedToClientInterfacesOfComponent(
								component);
				for (final Interface itf : listOfItf.values()) {
					if (itf == itfOfComponent) {
						if (!listOfcomponents.contains(component)) {
							listOfcomponents.add(component);
						}
					}
				}
			}
		} catch (final NoSuchInterfaceException e1) {
			// do nothing, return null
		} catch (final FractalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listOfcomponents;
	}

	/**
	 * A utility function allowing to get a component from an interface
	 * 
	 * @param parentContentController
	 *            parentContentController
	 * @param name
	 *            component name
	 * @return the component, null if not found
	 */
	public Component getComponentByInterface(final Component parent,
			final Interface itf, final String interfaceName) {
		// the component to be returned
		Component matchedComponent = null;
		try {
			final ContentController parentContentController = Fractal
					.getContentController(parent);
			// the subcomponent content controller
			ContentController subContentController = null;

			// List content controller subcomponents
			for (final Component component : parentContentController
					.getFcSubComponents()) {

				// if the component is a composite, search the matching
				// component
				// recursively
				try {
					subContentController = Fractal
							.getContentController(component);
					if (subContentController.getFcSubComponents().length > 0) {
						matchedComponent = this.getComponentByInterface(parent,
								itf, interfaceName);
						if (matchedComponent != null) {
							break;
						}
					}
				} catch (final NoSuchInterfaceException e1) {
					// do nothing, return null
					matchedComponent = null;
				}

				try {
					final Interface itfC = (Interface) component
							.getFcInterface(interfaceName);
					if (itfC == itf) {
						matchedComponent = component;
						break;
					}
				} catch (final NoSuchInterfaceException e) {
					// do nothing, return null
					matchedComponent = null;
				}
			}
		} catch (final NoSuchInterfaceException e1) {
			// do nothing, return null
			matchedComponent = null;
		}
		return matchedComponent;
	}

	/**
	 * A utility function allowing to get a component from any content
	 * controller.
	 * 
	 * @param parentContentController
	 *            parentContentController
	 * @param name
	 *            component name
	 * @return the component, null if not found
	 */
	public List<Component> getComponents(final Component parent) {
		final List<Component> res = new ArrayList<Component>();

		try {
			final ContentController parentContentController = Fractal
					.getContentController(parent);

			// List content controller subcomponents
			for (final Component component : parentContentController
					.getFcSubComponents()) {
				res.add(component);
			}
		} catch (final NoSuchInterfaceException e1) {
			// do nothing, return null
		}
		return res;
	}

	/**
	 * A utility function allowing to get a component from any content
	 * controller.
	 * 
	 * @param parentContentController
	 *            parentContentController
	 * @param name
	 *            component name
	 * @return the component, null if not found
	 */
	public List<Component> getComponentsByName(final Component parent,
			final String name) {
		// the component to be returned
		final List<Component> matchedComponents = new ArrayList<Component>();

		try {
			final ContentController parentContentController = Fractal
					.getContentController(parent);

			// the subcomponent content controller
			ContentController subContentController = null;

			// List content controller subcomponents
			for (final Component component : parentContentController
					.getFcSubComponents()) {

				// if the component is a composite, search the matching
				// component
				// recursively
				try {
					subContentController = Fractal
							.getContentController(component);

					if (subContentController.getFcSubComponents().length > 0) {
						matchedComponents.addAll(this.getComponentsByName(
								component, name));
					}
				} catch (final NoSuchInterfaceException e1) {
					// do nothing, return null
				}

				try {
					final String componentName = Fractal.getNameController(
							component).getFcName();
					if ((componentName != null) && (componentName.equals(name))) {
						matchedComponents.add(component);
						break;
					}
				} catch (final NoSuchInterfaceException e) {
					// do nothing, return null
					// matchedComponent = null;
				}
			}
		} catch (final NoSuchInterfaceException e1) {
			// do nothing, return null
			// matchedComponent = null;
		}
		return matchedComponents;
	}

	/**
	 * Get the factory to manage components.
	 * 
	 * @return The factory
	 * @throws ADLException
	 *             : occurs when the framework of Petals is incorrect
	 */
	public Factory getFactory() throws FractalException {
		if (FractalHelper.factory == null) {
			new FractalHelper();
		}
		return FractalHelper.factory;
	}

	/**
	 * A utility function allowing to get the first component with the given
	 * name.
	 * 
	 * @param parentContentController
	 *            parentContentController
	 * @param name
	 *            component name
	 * @return the component, null if not found
	 */
	public Component getFirstComponentByName(final Component parent,
			final String name) {
		final List<Component> comps = this.getComponentsByName(parent, name);

		Component comp = null;
		if ((comps != null) && (comps.size() > 0)) {
			comp = comps.get(0);
		}

		return comp;
	}

	/**
	 * A utility function allowing to get a component LifeCycleController from a
	 * content controller.
	 * 
	 * @param parentContentController
	 *            the parent content controller
	 * @param name
	 *            the name of the component
	 * @return the LifeCycleController of the component, null if not found
	 * @throws MonitoringException
	 */
	public LifeCycleController getLifeCycleControllerByName(
			final Component parent, final String name) throws FractalException {

		LifeCycleController lifeCycleController = null;

		final List<Component> comps = this.getComponentsByName(parent, name);

		if ((comps != null) && (comps.size() > 1)) {
			throw new FractalException("Several component with the same name");
		}

		Component comp = null;
		if (comps != null) {
			comp = comps.get(0);
		}

		if (comp != null) {
			try {
				lifeCycleController = Fractal.getLifeCycleController(comp);
			} catch (final NoSuchInterfaceException e) {
				// do nothing, return null
				lifeCycleController = null;
			}
		}
		return lifeCycleController;
	}

	public List<String> getListOfBinds(final Component component)
			throws FractalException {
		final List<String> res = new ArrayList<String>();
		try {

			// Get the binding controller of the new component
			final BindingController cBindingController = Fractal
					.getBindingController(component);

			// Get all the bindings
			for (int i = 0; i < cBindingController.listFc().length; i++) {
				res.add(cBindingController.listFc()[i]
						+ " link to "
						+ cBindingController.lookupFc(cBindingController
								.listFc()[i]));
			}

		} catch (final NoSuchInterfaceException e) {
			throw new FractalException(
					"Impossible to bind the fractal component", e);
		}
		return res;
	}

	public String getName(final Component comp) {
		String res = null;
		try {
			if (comp != null) {
				final NameController nc = Fractal.getNameController(comp);
				res = nc.getFcName();
			}
		} catch (final NoSuchInterfaceException e) {
			// Do nothing
		}
		return res;
	}

	public Component getParent(final Component childrenComponent)
			throws FractalException {
		Component parent = null;
		try {
			Component parents[] = null;
			final SuperController superController = Fractal
					.getSuperController(childrenComponent);

			parents = superController.getFcSuperComponents();
			if (parents.length > 1) {
				throw new FractalException("This component ("
						+ childrenComponent
						+ ") has several parents: It is a shared component");
			}
			if (parents.length == 1) {
				parent = parents[0];
			}
		} catch (final NoSuchInterfaceException e) {
			throw new FractalException(e);
		}
		return parent;
	}

	/**
	 * A utility function allowing to get components interface binding to the
	 * client interface of the component controller.
	 * 
	 * @param parentContentController
	 *            parentContentController
	 * @param name
	 *            component name
	 * @return the component, null if not found
	 */
	public Map<String, Interface> getServerInterfacesLinkedToClientInterfacesOfComponent(
			final Component component) {
		final Map<String, Interface> res = new HashMap<String, Interface>();

		try {
			final BindingController componentBindingController = Fractal
					.getBindingController(component);

			// List content controller subcomponents
			for (final String clientItfName : componentBindingController
					.listFc()) {
				final Interface itf = (org.objectweb.fractal.api.Interface) componentBindingController
						.lookupFc(clientItfName);
				res.put(clientItfName, itf);
			}
		} catch (final NoSuchInterfaceException e1) {
			// do nothing, return null
		}
		return res;
	}

	public boolean isAlreadyBind(final Component component,
			final String clientItfName, final Interface itf)
			throws FractalException {
		final boolean res = false;
		try {

			// Get the binding controller of the new component
			final BindingController cBindingController = Fractal
					.getBindingController(component);

			// Add all the bindings
			final Object o = cBindingController.lookupFc(clientItfName);

			if (o == itf) {
				return true;
			}
		} catch (final NoSuchInterfaceException e) {
			throw new FractalException("Impossible to get the interface", e);
		}
		return res;
	}

	public boolean isBinded(final Component component,
			final String clientItfName) throws FractalException {
		boolean res = true;
		try {

			// Get the binding controller of the new component
			final BindingController cBindingController = Fractal
					.getBindingController(component);

			// Add all the bindings
			final Object o = cBindingController.lookupFc(clientItfName);

			if (o == null) {
				res = false;
				;
			}
		} catch (final NoSuchInterfaceException e) {
			throw new FractalException(
					"Impossible to bind the fractal component", e);
		}
		return res;
	}

	public boolean isStarted(final Component comp) {
		boolean res = false;
		if (comp != null) {
			try {
				final LifeCycleController lifeCycleController = Fractal
						.getLifeCycleController(comp);
				if (lifeCycleController.getFcState() == LifeCycleController.STARTED) {
					res = true;
				}
			} catch (final NoSuchInterfaceException e) {
				// do nothing, return null
				res = false;
			}
		}
		return res;
	}

	/**
	 * A utility function to start the given fractal component.
	 * 
	 * @param component
	 *            the fractal component
	 * 
	 * @return true if the component was found and stopped, false otherwise
	 * @throws NoSuchInterfaceException
	 *             : impossible to stop the component
	 * @throws IllegalLifeCycleException
	 *             : impossible to stop the component
	 */
	public boolean startComponent(final Component component)
			throws FractalException {
		boolean result = false;
		if (component != null) {
			try {
				final LifeCycleController lifeCycleController = Fractal
						.getLifeCycleController(component);

				if (lifeCycleController != null) {
					if (lifeCycleController.getFcState().equals(
							LifeCycleController.STOPPED)) {
						lifeCycleController.startFc();
						result = true;
					}
				}
			} catch (final NoSuchInterfaceException e) {
				throw new FractalException("Impossible to start the component",
						e);
			} catch (final IllegalLifeCycleException e) {
				throw new FractalException("Impossible to start the component",
						e);
			}
		}
		return result;
	}

	/**
	 * A utility function to stop the given fractal composite.
	 * 
	 * @param composite
	 * @throws NoSuchInterfaceException
	 * @throws IllegalLifeCycleException
	 * @throws NoSuchInterfaceException
	 */
	public void stopAllSubComponents(final Component composite)
			throws FractalException {
		try {
			// the subcomponent content controller

			ContentController subContentController = null;
			final ContentController parentContentController = Fractal
					.getContentController(composite);

			// List content controller sub components
			for (int i = parentContentController.getFcSubComponents().length; i != 0; i--) {
				final Component component = parentContentController
						.getFcSubComponents()[i - 1];
				// if the component is a composite, stop the composite
				// recursively
				try {
					subContentController = Fractal
							.getContentController(component);
					if (subContentController.getFcSubComponents().length > 0) {
						this.stopAllSubComponents(component);
					}
				} catch (final NoSuchInterfaceException e) {
					// stop the component
					final LifeCycleController lifeCycleController = Fractal
							.getLifeCycleController(component);
					if (lifeCycleController.getFcState().equals(
							LifeCycleController.STARTED)) {
						lifeCycleController.stopFc();
					}
				}
			}
		} catch (final NoSuchInterfaceException e) {
			throw new FractalException("Impossible to stop the component", e);
		} catch (final IllegalLifeCycleException e) {
			throw new FractalException("Impossible to stop the component", e);
		}
	}

	/**
	 * A utility function to stop the given fractal component.
	 * 
	 * @param component
	 *            the fractal component to stop
	 * 
	 * @return true if the component was found and stopped, false otherwise
	 * @throws NoSuchInterfaceException
	 *             : impossible to stop the component
	 * @throws IllegalLifeCycleException
	 *             : impossible to stop the component
	 */
	public boolean stopComponent(final Component component)
			throws FractalException {

		boolean result = false;
		try {
			final LifeCycleController lifeCycleController = Fractal
					.getLifeCycleController(component);
			if (lifeCycleController.getFcState().equals(
					LifeCycleController.STARTED)) {
				lifeCycleController.stopFc();
				result = true;
			}
		} catch (final NoSuchInterfaceException e) {
			throw new FractalException("Impossible to stop the component", e);
		} catch (final IllegalLifeCycleException e) {
			throw new FractalException("Impossible to stop the component", e);
		}
		return result;
	}

}
