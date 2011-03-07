/**
 * PETALS: PETALS Services Platform
 * Copyright (C) 2005 EBM WebSourcing
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA.
 *
 * Initial developer(s): pierre.garcia@inrialpes.fr
 *
 */

package org.ow2.petals.esb.kernel.impl.transport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.fraclet.annotations.Controller;
import org.objectweb.fractal.fraclet.extensions.Membrane;
import org.ow2.petals.base.fractal.api.FractalException;
import org.ow2.petals.base.fractal.impl.FractalHelper;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.transport.TransportersManager;
import org.ow2.petals.esb.kernel.api.transport.WakeUpKey;
import org.ow2.petals.esb.kernel.impl.endpoint.EndpointImpl;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.transporter.api.transport.TransportContext;
import org.ow2.petals.transporter.api.transport.TransportException;
import org.ow2.petals.transporter.api.transport.Transporter;

import petals.ow2.org.exchange.StatusType;

/**
 * This class represents a dispatcher used to transmit messages to the right
 * transporter. When a message arrives, it is sent the message to the
 * appropriate transportProtocol.<br>
 * <br>
 * The transportProtocol to use can be specified by two means. By increasing
 * order, here are the means :
 * <ol>
 * <li> To set the <code>transporter</code> in the <em>server.properties</em>
 * configuration file</li>
 * <li>To set the {@link TransportersManagerImpl.PROPERTY_PROTOCOLS_TO_TRY} in the
 * {@link MessageExchangeImpl message exchange}</li>
 * </ol>
 * In both cases, this property can be a single value or a list of value
 * separated by comas <br>
 * <br>
 * 
 * @author pgarcia
 */
@org.objectweb.fractal.fraclet.annotations.Component
@Membrane(controller="composite")
public class TransportersManagerImpl extends EndpointImpl implements TransportersManager {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(TransportersManagerImpl.class.getName());

	private QName name;



	private List<Component> transporters = new ArrayList<Component>(); 

	private Map<UUID, WakeUpKey> stub2awake = new HashMap<UUID, WakeUpKey>();


	@Controller
	private Component component;




	public <T extends Transporter>  T  createTransporter(String name, Class<T> clazz) throws ESBException {
		T transporter = null;
		try {
			org.objectweb.fractal.api.Component transporterComponent = FractalHelper.getFractalHelper().createNewComponent(clazz.getName(), null);
			FractalHelper.getFractalHelper().startComponent(transporterComponent);

			if(name != null) {
				FractalHelper.getFractalHelper().changeName(transporterComponent, name.toString());
			}

			transporter = (T)transporterComponent.getFcInterface("transporter");

			// init
			transporter.initFractalComponent(transporterComponent);

			// add component in list
			FractalHelper.getFractalHelper().addComponent(transporterComponent, this.getComponent(), null);
			this.transporters.add(transporterComponent);


		} catch (NoSuchInterfaceException e) {
			throw new ESBException(e);
		} catch (FractalException e) {
			throw new ESBException(e);
		}
		log.fine("transporter " + name + " created and started");
		return transporter;
	}

	/**
	 * Utility to get the matching Transport Protocol component
	 * 
	 * @param destinationEndpointName
	 *            The name of the Transport Protocol
	 */
	private Transporter findTransporter() throws TransportException {
		Transporter protocol = null; 
		try {
			if(this.transporters == null || this.transporters.size() == 0) {
				throw new TransportException("Impossible to find transporter");
			}
			protocol = (Transporter) this.transporters.get(0).getFcInterface("transporter");
		} catch (NoSuchInterfaceException e) {
			throw new TransportException(e);
		}

		return protocol;
	}

	public void setQName(QName name) {
		this.name = name;
	}

	@Override
	public QName getQName() {
		return this.name;
	}

	public synchronized Exchange pull(QName providerEndpointName, QName nodeEndpointName) throws TransportException {
		Exchange res = null;
		Transporter protocol = null; 

		if(providerEndpointName == null) {
			throw new TransportException("provider name cannot be null");
		}
		if(nodeEndpointName == null) {
			throw new TransportException("node name cannot be null");
		}
		log.finest("PULL ON: " + providerEndpointName + " - node = " + nodeEndpointName);


		protocol = findTransporter();
		res = protocol.pull(providerEndpointName, nodeEndpointName);

		return res;
	}

	public synchronized Exchange pull(UUID uuid, QName providerEndpointName, QName nodeEndpointName) throws TransportException {
		Exchange res = null;
		Transporter protocol = null; 

		log.finest("PULL WITH UUID ON: " + providerEndpointName + " - node = " + nodeEndpointName + "with uuid = " + uuid);

		if(uuid == null) {
			throw new TransportException("uuid cannot be null");
		}
		if(providerEndpointName == null) {
			throw new TransportException("provider name cannot be null");
		}
		if(nodeEndpointName == null) {
			throw new TransportException("node name cannot be null");
		}

		protocol = findTransporter();
		res = protocol.pull(uuid, providerEndpointName, nodeEndpointName);

		return res;
	}

	public void push(Exchange exchange, QName destinationNodeName) throws TransportException {

		if(destinationNodeName == null) {
			throw new TransportException("destination name cannot be null");
		}
		if(exchange == null) {
			throw new TransportException("exchange cannot be null");
		}

		QName destination = exchange.getDestination();
		if(exchange.getStatus().equals(StatusType.DONE) || exchange.getStatus().equals(StatusType.FAULT)) {
			destination = exchange.getSource();
		}
		log.info("PUSH TO: " + destination + " - node: " + destinationNodeName);


		log.finest("exchange " + exchange.getUuid() + " send to endpoint "
				+ destination + " on node " + destinationNodeName);

		Transporter protocol = this.findTransporter();
		protocol.push(exchange, destinationNodeName);
	}

	public Map<UUID, WakeUpKey> getStub2awake() {
		return stub2awake;
	}

	public TransportContext getContext() {
		return null;
	}


	public void setContext(TransportContext arg0) {
		throw new UnsupportedOperationException();
	}

	public void stop() {
		try {
			for(Component transporter: this.transporters) {
				Transporter protocol = (Transporter) transporter.getFcInterface("transporter");
				protocol.stop();
			}
		} catch (NoSuchInterfaceException e) {
			// do nothing
			e.printStackTrace();
		}
	}

}
