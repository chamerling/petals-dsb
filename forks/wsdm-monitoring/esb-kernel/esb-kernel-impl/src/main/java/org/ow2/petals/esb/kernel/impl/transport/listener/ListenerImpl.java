package org.ow2.petals.esb.kernel.impl.transport.listener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.node.Node;
import org.ow2.petals.esb.kernel.api.transport.listener.Listener;
import org.ow2.petals.exchange.api.Exchange;

public class ListenerImpl implements Listener {

	private static Logger log = Logger.getLogger(ListenerImpl.class.getName());

	private static final int REFRESH_FREQUENCY = 100;



	private Map<QName, Endpoint> endpoints = Collections.synchronizedMap(new HashMap<QName, Endpoint>());


	public ListenerImpl(Map<QName, Endpoint> endpoints) {
		this.endpoints = endpoints;
	}


	public void run() {

		try {
			Exchange exchange = null;
			Iterator<Endpoint> it = this.endpoints.values().iterator();
			while(it.hasNext()) {
				Endpoint ep = it.next();
				if(ep != null && ep.getNode() != null && ep.getTransportersManager() != null) {
					exchange = ep.getNode().getTransportersManager().pull(ep.getQName(), ep.getNode().getQName());
				}

				if(exchange != null) {
					ep.accept(exchange);
				}
			}

			Thread.sleep(REFRESH_FREQUENCY);

		} catch (Exception e) {
			e.printStackTrace();
			log.severe("Error in listener: " + e.getMessage());
			System.exit(0);
		} 
	}


}
