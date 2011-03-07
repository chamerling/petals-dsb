/**
 * Maestro-Core - SOA Tools Platform.
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
package org.ow2.petals.base.fractal.impl;

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
import org.ow2.petals.base.fractal.api.FractalException;


/**
 * This class helps fractal management.
 *
 * @author Nicolas Salatge - eBM WebSourcing
 */
public class FractalHelper {


	private static final Logger log = Logger.getLogger(FractalHelper.class.getName());

	/**
	 * the explorer composite component name.
	 */
	public static final String EXPLORER_COMPOSITE = "Explorer";

	/**
	 * the petals component name.
	 */
	public static final String ESB_EXPLORER_COMPOSITE = "ESBExplorer";

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
	private Map<Object, Object> context = null;

	/**
	 * the fractal factory.
	 */
	private Factory factory = null;

	/**
	 * Create a FractalHelper instance.
	 *
	 * @throws ADLException :
	 *             occurs when it is impossible to get the fractal explorer
	 */
	private FractalHelper() throws FractalException {
		super();

		// check required fractal properties
		System.setProperty("fractal.provider",
		"org.objectweb.fractal.julia.Julia");
		System.setProperty("julia.loader",
		"org.objectweb.fractal.julia.loader.DynamicLoader");
		System.setProperty("julia.config",
		"julia.cfg");

		// Create Fractal ADL factories
		this.context = new HashMap<Object, Object>();
		try {
			this.factory = FactoryFactory.getFactory(
					FactoryFactory.FRACTAL_BACKEND, this.context);
		} catch (ADLException e) {
			throw new FractalException("Impossible to initialize the fractal helper", e);
		}
	}

	/**
	 * Get a FractalHelper instance.
	 *
	 * @return the fractalHelper instance
	 * @throws ADLException :
	 *             occurs when it is impossible to get the fractal explorer
	 */
	public static FractalHelper getFractalHelper() throws FractalException {
		if (FractalHelper.fractalHelper == null) {
			FractalHelper.fractalHelper = new FractalHelper();
		}
		return FractalHelper.fractalHelper;
	}

	/**
	 * Get the factory to manage components.
	 *
	 * @return The factory
	 * @throws ADLException :
	 *             occurs when the framework of Petals is incorrect
	 */
	public Factory getFactory() throws FractalException {
		if (this.factory == null) {
			new FractalHelper();
		}
		return this.factory;
	}



	/**
	 * Create a new component.
	 *
	 * @param name
	 *            The class name of the component
	 * @return the new component
	 * @throws ADLException :
	 *             occurs when the framework of Petals is incorrect
	 */
	public synchronized Component createNewComponent(final String name, final Map<Object, Object> ctxt) throws FractalException {
		try {
			return (Component) this.getFactory().newComponent(name,	ctxt);
		} catch (ADLException e) {
			throw new FractalException("Impossible to create a new fractal component", e);
		}
	}

	/** Change the name of the component
	 * @param name
	 * @throws MaestroException
	 */
	public void changeName(Component comp, String name) throws FractalException {
		try {
			NameController nc = Fractal.getNameController(comp);
			nc.setFcName(name);
		} catch (NoSuchInterfaceException e) {
			throw new FractalException("Impossible to change the name ot this fractal componet: " + comp, e);
		}
	}


	public String getName(Component comp) {
		String res = null;
		try {
			if(comp != null) {
				NameController nc = Fractal.getNameController(comp);
				res = nc.getFcName();
			}
		} catch (NoSuchInterfaceException e) {
			// Do nothing
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
	public List<Component> getComponentsByName(
			final Component parent, final String name) {
		//		the component to be returned
		List<Component> matchedComponents = new ArrayList<Component>();

		try {
			ContentController parentContentController = Fractal.getContentController(parent);

			// the subcomponent content controller
			ContentController subContentController = null;


			// List content controller subcomponents
			for (Component component : parentContentController.getFcSubComponents()) {

				// if the component is a composite, search the matching component
				// recursively
				try {
					subContentController = Fractal.getContentController(component);

					if (subContentController.getFcSubComponents().length > 0) {
						matchedComponents.addAll(this.getComponentsByName(
								component, name));
					}
				} catch (NoSuchInterfaceException e1) {
					// do nothing, return null
				}

				try {
					String componentName = Fractal.getNameController(component).getFcName();
					if ((componentName != null)&&(componentName.equals(name))) {
						matchedComponents.add(component);
						break;
					}
				} catch (NoSuchInterfaceException e) {
					// do nothing, return null
					//matchedComponent = null;
				}
			}
		} catch (NoSuchInterfaceException e1) {
			// do nothing, return null
			//matchedComponent = null;
		}
		return matchedComponents;
	}

	/**
	 * A utility function allowing to get the first component with the given name.
	 *
	 * @param parentContentController
	 *            parentContentController
	 * @param name
	 *            component name
	 * @return the component, null if not found
	 */
	public Component getFirstComponentByName(
			final Component parent, final String name) {
		List<Component> comps = this.getComponentsByName(parent, name);

		Component comp = null;
		if((comps != null)&&(comps.size() > 0)) {
			comp = comps.get(0);
		}

		return comp;
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
	public Component getComponentByInterface(
			final Component parent, final Interface itf, final String interfaceName) {
		//		the component to be returned
		Component matchedComponent = null;
		try {
			ContentController parentContentController = Fractal.getContentController(parent);
			// the subcomponent content controller
			ContentController subContentController = null;


			// List content controller subcomponents
			for (Component component : parentContentController.getFcSubComponents()) {

				// if the component is a composite, search the matching component
				// recursively
				try {
					subContentController = Fractal.getContentController(component);
					if (subContentController.getFcSubComponents().length > 0) {
						matchedComponent = this.getComponentByInterface(
								parent,  itf, interfaceName);
						if (matchedComponent != null) {
							break;
						}
					}
				} catch (NoSuchInterfaceException e1) {
					// do nothing, return null
					matchedComponent = null;
				}

				try {
					Interface itfC = (Interface)component.getFcInterface(interfaceName);
					if (itfC == itf) {
						matchedComponent = component;
						break;
					}
				} catch (NoSuchInterfaceException e) {
					// do nothing, return null
					matchedComponent = null;
				}
			}
		} catch (NoSuchInterfaceException e1) {
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
	public List<Component> getComponents(
			final Component parent) {
		List<Component> res = new ArrayList<Component>();

		try {
			ContentController parentContentController = Fractal.getContentController(parent);

			// List content controller subcomponents
			for (Component component : parentContentController.getFcSubComponents()) {
				res.add(component);
			}
		} catch (NoSuchInterfaceException e1) {
			// do nothing, return null
		}
		return res;
	}


	/**
	 * A utility function allowing to get components interface binding to the client interface of the component
	 * controller.
	 *
	 * @param parentContentController
	 *            parentContentController
	 * @param name
	 *            component name
	 * @return the component, null if not found
	 */
	public Map<String, Interface> getServerInterfacesLinkedToClientInterfacesOfComponent(
			final Component component) {
		Map<String, Interface> res = new HashMap<String, Interface>();

		try {
			BindingController componentBindingController = Fractal.getBindingController(component);

			// List content controller subcomponents
			for (String clientItfName : componentBindingController.listFc()) {
				Interface itf = (org.objectweb.fractal.api.Interface) componentBindingController.lookupFc(clientItfName);
				res.put(clientItfName, itf);
			}
		} catch (NoSuchInterfaceException e1) {
			// do nothing, return null
		}
		return res;
	}


	/**
	 * A utility function allowing to get components interface binding to the client interface of the component
	 * controller.
	 *
	 * @param parentContentController
	 *            parentContentController
	 * @param name
	 *            component name
	 * @return the component, null if not found
	 */
	public List<Component> getClientComponentsLinkedToServerInterfacesOfComponent(
			final Component parentComponent, final Interface itfOfComponent) {

		List<Component> listOfcomponents = new ArrayList<Component>();

		try {
			ContentController componentContentController = Fractal.getContentController(parentComponent);
			// List content controller subcomponents
			for (Component component : componentContentController.getFcSubComponents()) {

				Map<String, Interface> listOfItf = FractalHelper.getFractalHelper().getServerInterfacesLinkedToClientInterfacesOfComponent(component);
				for (Interface itf : listOfItf.values()) {
					if(itf == itfOfComponent) {
						if(!listOfcomponents.contains(component)) {
							listOfcomponents.add(component);
						}
					}
				}
			}
		} catch (NoSuchInterfaceException e1) {
			// do nothing, return null
		} catch (FractalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return listOfcomponents;
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
	 * @throws MaestroException
	 */
	public LifeCycleController getLifeCycleControllerByName(
			final Component parent, final String name) throws FractalException {

		LifeCycleController lifeCycleController = null;

		List<Component> comps = this.getComponentsByName(parent, name);

		if((comps != null)&&(comps.size() > 1)) {
			throw new FractalException("Several component with the same name");
		}

		Component comp = null;
		if(comps != null) {
			comp = comps.get(0);
		}

		if (comp != null) {
			try {
				lifeCycleController = Fractal.getLifeCycleController(comp);
			} catch (NoSuchInterfaceException e) {
				// do nothing, return null
				lifeCycleController = null;
			}
		}
		return lifeCycleController;
	}

	public boolean isStarted(Component comp) {
		boolean res = false;
		if (comp != null) {
			try {
				LifeCycleController lifeCycleController = Fractal.getLifeCycleController(comp);
				if(lifeCycleController.getFcState() == LifeCycleController.STARTED) {
					res = true;
				}
			} catch (NoSuchInterfaceException e) {
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
	 * @throws NoSuchInterfaceException :
	 *             impossible to stop the component
	 * @throws IllegalLifeCycleException :
	 *             impossible to stop the component
	 */
	public boolean startComponent(final Component component)
	throws FractalException {
		boolean result = false;
		if(component != null) {
			try {
				LifeCycleController lifeCycleController = Fractal
				.getLifeCycleController(component);

				if (lifeCycleController != null) {
					if (lifeCycleController.getFcState().equals(
							LifeCycleController.STOPPED)) {
						lifeCycleController.startFc();
						result = true;
					}
				}
			} catch (NoSuchInterfaceException e) {
				throw new FractalException("Impossible to start the component", e);
			} catch (IllegalLifeCycleException e) {
				throw new FractalException("Impossible to start the component", e);
			}
		}
		return result;
	}

	/**
	 * A utility function to stop the given fractal component.
	 *
	 * @param component
	 *            the fractal component to stop
	 *
	 * @return true if the component was found and stopped, false otherwise
	 * @throws NoSuchInterfaceException :
	 *             impossible to stop the component
	 * @throws IllegalLifeCycleException :
	 *             impossible to stop the component
	 */
	public boolean stopComponent(final Component component)
	throws FractalException {

		boolean result = false;
		try {
			LifeCycleController lifeCycleController = Fractal
			.getLifeCycleController(component);
			if (lifeCycleController.getFcState()
					.equals(LifeCycleController.STARTED)) {
				lifeCycleController.stopFc();
				result = true;
			}
		} catch (NoSuchInterfaceException e) {
			throw new FractalException("Impossible to stop the component", e);
		} catch (IllegalLifeCycleException e) {
			throw new FractalException("Impossible to stop the component", e);
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
			ContentController parentContentController = Fractal.getContentController(composite);

			// List content controller sub components
			for (int i = parentContentController.getFcSubComponents().length; i != 0; i--) {
				Component component = parentContentController.getFcSubComponents()[i-1];
				// if the component is a composite, stop the composite
				// recursively
				try {
					subContentController = Fractal.getContentController(component);
					if (subContentController.getFcSubComponents().length > 0) {
						stopAllSubComponents(component);
					}
				} catch (NoSuchInterfaceException e) {
					// stop the component
					LifeCycleController lifeCycleController = Fractal
					.getLifeCycleController(component);
					if (lifeCycleController.getFcState().equals(LifeCycleController.STARTED)) {
						lifeCycleController.stopFc();
					}
				}
			}
		} catch (NoSuchInterfaceException e) {
			throw new FractalException("Impossible to stop the component", e);
		} catch (IllegalLifeCycleException e) {
			throw new FractalException("Impossible to stop the component", e);
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
	 * @throws NoSuchInterfaceException :
	 *             Impossible to add a component
	 * @throws IllegalLifeCycleException :
	 *             Impossible to add a component
	 * @throws IllegalContentException :
	 *             Impossible to add a component
	 * @throws IllegalBindingException :
	 *             Impossible to add a component
	 */
	public void addComponent(final Component newComponent,
			final Component parentComponent, final List<Binding> listOfBindings)
	throws FractalException {
		try {
			if(parentComponent == null) {
				throw new NullPointerException("The parent component " + parentComponent + " cannot be null");
			}

			// Get the parent controller
			ContentController parentContentController = Fractal
			.getContentController(parentComponent);

			parentContentController.addFcSubComponent(newComponent);

			FractalHelper.getFractalHelper().addBindings(newComponent, listOfBindings);


		} catch (NoSuchInterfaceException e) {
			throw new FractalException("Impossible to add a new fractal component", e);
		} catch (IllegalContentException e) {
			throw new FractalException("Impossible to add a new fractal component", e);
		} catch (IllegalLifeCycleException e) {
			throw new FractalException("Impossible to add a new fractal component", e);
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
	 * @throws NoSuchInterfaceException :
	 *             Impossible to add a component
	 * @throws IllegalLifeCycleException :
	 *             Impossible to add a component
	 * @throws IllegalContentException :
	 *             Impossible to add a component
	 * @throws IllegalBindingException :
	 *             Impossible to add a component
	 */
	public void deleteComponent(final Component oldComponent)
	throws FractalException {
		try {

			Component parentComponent = getParent(oldComponent);

			if(parentComponent != null) {
				List<Binding> listOfBindings = new ArrayList<Binding>();

				BindingController bc = Fractal.getBindingController(oldComponent);

				for( String clientItf: bc.listFc()) {
					bc.unbindFc(clientItf);
				}

				List<Component> clientsOfComp = getClientComponentsLinkedToServerInterfacesOfComponent(parentComponent, (Interface)oldComponent.getFcInterface("service"));

				for (Component clientComponent : clientsOfComp) {
					BindingController bcClient = Fractal.getBindingController(clientComponent);
					for( String clientItf: bcClient.listFc()) {
						if(bcClient.lookupFc(clientItf) == (Interface)oldComponent.getFcInterface("service")) {
							bcClient.unbindFc(clientItf);
						}
					}
				}

				deleteComponent(oldComponent,
						parentComponent, listOfBindings);
			}
		} catch (NoSuchInterfaceException e) {
			throw new FractalException("Impossible to delete component", e);
		} catch (IllegalBindingException e) {
			throw new FractalException("Impossible to delete component", e);
		} catch (IllegalLifeCycleException e) {
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
	 * @throws NoSuchInterfaceException :
	 *             Impossible to add a component
	 * @throws IllegalLifeCycleException :
	 *             Impossible to add a component
	 * @throws IllegalContentException :
	 *             Impossible to add a component
	 * @throws IllegalBindingException :
	 *             Impossible to add a component
	 */
	public void deleteComponent(final Component oldComponent,
			final Component parentComponent, final List<Binding> listOfBindings)
	throws FractalException {
		try {
			if(parentComponent == null) {
				throw new NullPointerException("The parent component " + parentComponent + " cannot be null");
			}

			// Get the parent controller
			ContentController parentContentController = Fractal
			.getContentController(parentComponent);

			if(FractalHelper.getFractalHelper().isStarted(oldComponent))
				FractalHelper.getFractalHelper().stopComponent(oldComponent);

			FractalHelper.getFractalHelper().deleteBindings(oldComponent, listOfBindings);

			parentContentController.removeFcSubComponent(oldComponent);

		} catch (NoSuchInterfaceException e) {
			throw new FractalException("Impossible to remove the fractal component", e);
		} catch (IllegalContentException e) {
			throw new FractalException("Impossible to remove the fractal component", e);
		} catch (IllegalLifeCycleException e) {
			throw new FractalException("Impossible to remove the fractal component", e);
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
	 * @throws NoSuchInterfaceException :
	 *             Impossible to add a component
	 * @throws IllegalLifeCycleException :
	 *             Impossible to add a component
	 * @throws IllegalContentException :
	 *             Impossible to add a component
	 * @throws IllegalBindingException :
	 *             Impossible to add a component
	 */
	public void addBindings(final Component component, final List<Binding> listOfBindings)
	throws FractalException {
		try {

			// Get the binding controller of the new component
			BindingController cBindingController = Fractal
			.getBindingController(component);

			// Add all the bindings
			if (listOfBindings != null) {
				for (int i = 0; i < listOfBindings.size(); i++) {
					cBindingController.bindFc(listOfBindings.get(i)
							.getClientInterfaceName(), (listOfBindings.get(i))
							.getServerInterface());
				}
			}
		} catch (NoSuchInterfaceException e) {
			throw new FractalException("Impossible to bind the fractal component", e);
		} catch (IllegalLifeCycleException e) {
			throw new FractalException("Impossible to bind the fractal component", e);
		} catch (IllegalBindingException e) {
			throw new FractalException("Impossible to bind the fractal component", e);
		}
	}

	public boolean isBinded(final Component component, final String clientItfName)
	throws FractalException {
		boolean res = true;
		try {

			// Get the binding controller of the new component
			BindingController cBindingController = Fractal.getBindingController(component);

			// Add all the bindings
			Object o = cBindingController.lookupFc(clientItfName);

			if(o == null) {
				res = false;;
			}
		} catch (NoSuchInterfaceException e) {
			throw new FractalException("Impossible to bind the fractal component", e);
		}
		return res;
	}

	public boolean isAlreadyBind(final Component component, final String clientItfName, final Interface itf)
	throws FractalException {
		boolean res = false;
		try {

			// Get the binding controller of the new component
			BindingController cBindingController = Fractal.getBindingController(component);

			// Add all the bindings
			Object o = cBindingController.lookupFc(clientItfName);

			if(o == itf) {
				return true;
			}
		} catch (NoSuchInterfaceException e) {
			throw new FractalException("Impossible to get the interface", e);
		}
		return res;
	}

	public List<String> getListOfBinds(final Component component)
	throws FractalException {
		List<String> res = new ArrayList<String>();
		try {

			// Get the binding controller of the new component
			BindingController cBindingController = Fractal.getBindingController(component);

			// Get all the bindings
			for(int i = 0; i < cBindingController.listFc().length; i++) {
				res.add(cBindingController.listFc()[i] + " link to " + cBindingController.lookupFc(cBindingController.listFc()[i]));
			}


		} catch (NoSuchInterfaceException e) {
			throw new FractalException("Impossible to bind the fractal component", e);
		}
		return res;
	}

	public Component getParent(Component childrenComponent) throws FractalException {
		Component parent = null;
		try {
			Component parents[] = null;
			SuperController superController = Fractal.getSuperController(childrenComponent);

			parents = superController.getFcSuperComponents();
			if(parents.length > 1) {
				throw new FractalException("This component (" + childrenComponent + ") has several parents: It is a shared component");
			}
			if(parents.length == 1)
				parent = parents[0];
		} catch (NoSuchInterfaceException e) {
			throw new FractalException(e);
		}
		return parent;
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
	 * @throws NoSuchInterfaceException :
	 *             Impossible to add a component
	 * @throws IllegalLifeCycleException :
	 *             Impossible to add a component
	 * @throws IllegalContentException :
	 *             Impossible to add a component
	 * @throws IllegalBindingException :
	 *             Impossible to add a component
	 */
	public void deleteBindings(final Component component, final List<Binding> listOfBindings)
	throws FractalException {
		try {

			// Get the binding controller of the new component
			BindingController cBindingController = Fractal
			.getBindingController(component);

			// Add all the bindings
			if (listOfBindings != null) {
				for (int i = 0; i < listOfBindings.size(); i++) {
					cBindingController.unbindFc(((listOfBindings.get(i)).getClientInterfaceName()));
				}
			}
		} catch (NoSuchInterfaceException e) {
			throw new FractalException("Impossible to bind the fractal component", e);
		} catch (IllegalLifeCycleException e) {
			throw new FractalException("Impossible to bind the fractal component", e);
		} catch (IllegalBindingException e) {
			throw new FractalException("Impossible to bind the fractal component", e);
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
	 * @throws NoSuchInterfaceException :
	 *             Impossible to add a component
	 * @throws IllegalLifeCycleException :
	 *             Impossible to add a component
	 * @throws IllegalContentException :
	 *             Impossible to add a component
	 * @throws IllegalBindingException :
	 *             Impossible to add a component
	 */
	public void deleteAllClientBindingsOfComponent(final Component component)
	throws FractalException {
		Map<String, Interface> map = this.getServerInterfacesLinkedToClientInterfacesOfComponent(component);
		List<Binding> bindings = new ArrayList<Binding>();
		for (Entry<String, Interface> entry : map.entrySet()) {
			bindings.add(new Binding(entry.getKey(), entry.getValue()));
		}
		this.deleteBindings(component, bindings);
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
	 * @throws NoSuchInterfaceException :
	 *             Impossible to add a component
	 * @throws IllegalLifeCycleException :
	 *             Impossible to add a component
	 * @throws IllegalContentException :
	 *             Impossible to add a component
	 * @throws IllegalBindingException :
	 *             Impossible to add a component
	 */
	public void deleteLinkWithAnItfClientOfComponent(final Component component, final String itfCLient)
	throws FractalException {
		Map<String, Interface> map = this.getServerInterfacesLinkedToClientInterfacesOfComponent(component);
		List<Binding> bindings = new ArrayList<Binding>();
		for (Entry<String, Interface> entry : map.entrySet()) {
			if(entry.getKey().equals(itfCLient)) {
				bindings.add(new Binding(entry.getKey(), entry.getValue()));
				break;
			}
		}
		this.deleteBindings(component, bindings);
	}


	/**
	 * Create an explorer for the specified component (the component requires an
	 * explicit interface named fcAppl of type
	 * org.objectweb.fractal.api.Component).
	 *
	 * @param component
	 *            the component to explore
	 * @throws ADLException :
	 *             occurs when it is impossible to create the explorer
	 * @throws NoSuchInterfaceException :
	 *             occurs when it is impossible to create the explorer
	 * @throws IllegalBindingException :
	 *             occurs when it is impossible to create the explorer
	 * @throws IllegalLifeCycleException :
	 *             occurs when it is impossible to create the explorer
	 * @throws IllegalContentException :
	 *             occurs when it is impossible to create the explorer
	 * @throws MaestroException
	 */
	public Component createExplorer(String fractalName, String fractalDefinition) throws FractalException {
		Component fpvm = null;
		try {
			// create explorer
			Map<Object, Object> ctxt = new HashMap<Object, Object>();
			ctxt.put("name",fractalName);
			ctxt.put("definition",fractalDefinition);

			Component fractalExplorer = this.createNewComponent("org.objectweb.fractal.explorer.BasicFractalExplorer", ctxt);

			ContentController cc = Fractal.getContentController(fractalExplorer);

			for (Component element : cc.getFcSubComponents()) {
				NameController n = Fractal.getNameController(element);
				if(n.getFcName().equals(fractalName)) {
					fpvm = element;
				}
			}

			if(fpvm == null) {
				throw new FractalException("Refecrence of FPVM cannot be found");

			}
		} catch (NoSuchInterfaceException e) {
			throw new FractalException("Impossible to create the Fractal Explorer");
		}

		return fpvm;
	}


	/**
	 * Create an explorer for the specified component (the component requires an
	 * explicit interface named fcAppl of type
	 * org.objectweb.fractal.api.Component).
	 *
	 * @param component
	 *            the component to explore
	 * @throws ADLException :
	 *             occurs when it is impossible to create the explorer
	 * @throws NoSuchInterfaceException :
	 *             occurs when it is impossible to create the explorer
	 * @throws IllegalBindingException :
	 *             occurs when it is impossible to create the explorer
	 * @throws IllegalLifeCycleException :
	 *             occurs when it is impossible to create the explorer
	 * @throws IllegalContentException :
	 *             occurs when it is impossible to create the explorer
	 * @throws MaestroException
	 */
	public void createExplorer(final Component component, String fractalDefinition) throws FractalException {
		try {
			// create global composite
			Component globalComposite = this
			.createNewComponent(FractalHelper.ESB_EXPLORER_COMPOSITE, null);

			// search the server component
			Component appli = this.getFirstComponentByName(globalComposite,
					FractalHelper.EXPLORER_COMPOSITE);

			// Start of the petals explorer
			this.startComponent(appli);

			// add the component in the global composite
			this.addComponent(component, appli, null);

			// create explorer
			Map<Object, Object> ctxt = new HashMap<Object, Object>();
			//ctxt.put("name",fractalName);
			//ctxt.put("definition",fractalDefinition);
			ctxt.put("config","ESBExplorerProperties.xml");

			// create explorer
			Component fractalExplorer = this
			.createNewComponent("org.objectweb.fractal.explorer.GenericFractalExplorerImpl", ctxt);
			log.finest("Create the explorer");

			// create the bindings list of the server component to others components
			List<Binding> listOfBindingsExplorer = new ArrayList<Binding>();

			listOfBindingsExplorer.add(new Binding(FractalHelper.FCAPPL_BINDING,
					(Interface) appli.getFcInterface(FractalHelper.FCAPPL_BINDING)));

			this.addComponent(fractalExplorer, globalComposite,
					listOfBindingsExplorer);
		} catch (NoSuchInterfaceException e) {
			throw new FractalException(e);
		}
	}

}

