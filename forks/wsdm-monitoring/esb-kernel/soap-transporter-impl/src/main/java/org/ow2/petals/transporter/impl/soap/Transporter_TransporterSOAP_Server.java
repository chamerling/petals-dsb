
package org.ow2.petals.transporter.impl.soap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;

import org.ow2.petals.transporter.PullWithId;
import org.ow2.petals.transporter.PushRequest;
import org.ow2.petals.transporter.TransportException_Exception;
import org.ow2.petals.transporter.Transporter;

import petals.ow2.org.exchange.StatusType;




@javax.jws.WebService(
		serviceName = "transporterService",
		portName = "TransporterImplPort",
		targetNamespace = "http://petals.ow2.org/transporter/",
		wsdlLocation = "transporter.wsdl",
		endpointInterface = "org.ow2.petals.transporter.Transporter")
		public class Transporter_TransporterSOAP_Server implements Transporter {

	private static final Logger log = Logger.getLogger(Transporter_TransporterSOAP_Server.class.getName());


	private Endpoint endpoint;

	/**
	 * The maximum size of a the Java queue.
	 */
	private static final int QUEUE_SIZE = 10000;

	private Map<QName, BlockingQueue<petals.ow2.org.exchange.ExchangeType>> exchangeQueuesMap = new HashMap<QName, BlockingQueue<petals.ow2.org.exchange.ExchangeType>>();


	public Transporter_TransporterSOAP_Server(String serverAddress) {
		log.finest("Starting transporter Server: " + serverAddress);

		String address = serverAddress;
		endpoint = Endpoint.publish(address, this);
	}


	public void stop() {
		this.endpoint.stop();
	}

	/* (non-Javadoc)
	 * @see org.petals.ow2.transporter.Transporter#push(org.petals.ow2.transporter.PushRequest  parameters )*
	 */
	public void push(PushRequest parameters) { 
		log.finest("Executing operation push:");
		log.finest("exchange status = " + parameters.getExchange().getStatus());
		log.finest("exchange destination = " + parameters.getExchange().getDestination());
		log.finest("exchange source = " + parameters.getExchange().getSource());

		if(parameters.getEndpointNodeName() == null) {
			log.warning("Endpoint node name cannot be null");
		}

		QName queueName = parameters.getExchange().getDestination();
		if(parameters.getExchange().getStatus().equals(StatusType.DONE) || parameters.getExchange().getStatus().equals(StatusType.FAULT)) {
			queueName = parameters.getExchange().getSource();
		}
		

		
		BlockingQueue<petals.ow2.org.exchange.ExchangeType> exchangesQueue = this.exchangeQueuesMap.get(queueName);

		if (exchangesQueue == null) {
			this.exchangeQueuesMap.put(queueName, new ArrayBlockingQueue<petals.ow2.org.exchange.ExchangeType>(QUEUE_SIZE));
			exchangesQueue = this.exchangeQueuesMap.get(queueName);
		}


		
		if (exchangesQueue.remainingCapacity() == 0) {
			log.warning("The SOAP Transporter has reached its maximum capacity for the target component '"
					+ parameters.getEndpointNodeName() + "'. The message exchange is rejected");
		}

		exchangesQueue.offer(parameters.getExchange());
		log.finest("exchange " + parameters.getExchange().getUuid() + " push in queue of " + queueName); 
	}

	/* (non-Javadoc)
	 * @see org.petals.ow2.transporter.Transporter#pull(javax.xml.namespace.QName  parameters )*
	 */
	public petals.ow2.org.exchange.ExchangeType pull(String providerEndpointName) throws TransportException_Exception { 
		petals.ow2.org.exchange.ExchangeType res = null;
		log.finest("Executing operation pull on " + providerEndpointName);

		if(providerEndpointName == null) {
			TransportException_Exception exception = new TransportException_Exception("pullWithId: providerEndpoint Name cannot be null", new org.ow2.petals.transporter.TransportException());
			exception.getFaultInfo().setErrorMsg("pullWithId: providerEndpoint Name cannot be null");
			throw exception;
		}

		QName provider = QName.valueOf(providerEndpointName);

		if(this.exchangeQueuesMap == null) {
			TransportException_Exception exception = new TransportException_Exception("pullWithId: the exchange queue map cannot be null", new org.ow2.petals.transporter.TransportException());
			exception.getFaultInfo().setErrorMsg("pullWithId: the exchange queue map cannot be null");
			throw exception;
		}

		BlockingQueue<petals.ow2.org.exchange.ExchangeType> exchangesQueue = this.exchangeQueuesMap.get(provider);


		if(exchangesQueue != null) {
			res = exchangesQueue.poll();
		}

		return res;
	}

	/* (non-Javadoc)
	 * @see org.petals.ow2.transporter.Transporter#pullWithId(org.petals.ow2.transporter.PullWithId  parameters )*
	 */
	public petals.ow2.org.exchange.ExchangeType pullWithId(PullWithId parameters) throws TransportException_Exception  { 
		log.finest("Executing operation pullWithId on " + parameters.getEndpointName());

		if(parameters == null) {
			TransportException_Exception exception = new TransportException_Exception("pullWithId: parameters cannot be null", new org.ow2.petals.transporter.TransportException());
			exception.getFaultInfo().setErrorMsg("pullWithId: parameters cannot be null");
			throw exception;
		}

		// pull on client queue

		if(parameters.getEndpointName() == null) {
			TransportException_Exception exception = new TransportException_Exception("pullWithId: The provider endpoint cannot be null", new org.ow2.petals.transporter.TransportException());
			exception.getFaultInfo().setErrorMsg("pullWithId: The provider endpoint cannot be null");
			throw exception;
		}




		petals.ow2.org.exchange.ExchangeType response = this.pull(parameters.getEndpointName());

		BlockingQueue<petals.ow2.org.exchange.ExchangeType> exchangesQueue = this.exchangeQueuesMap.get(QName.valueOf(parameters.getEndpointName()));

		if(exchangesQueue == null) {
			TransportException_Exception exception = new TransportException_Exception("Impossible to find queue corresponding to this endpoint " + parameters.getEndpointName(), new org.ow2.petals.transporter.TransportException());
			exception.getFaultInfo().setErrorMsg("Impossible to find queue corresponding to this endpoint " + parameters.getEndpointName());
			throw exception;
		}

		if(exchangesQueue != null) {
			if(parameters.getUuid() != null) {
				petals.ow2.org.exchange.ExchangeType first = null;
				while((response != null)&&(!response.getUuid().equals(parameters.getUuid()))&&(first != response)) {
					if(first == null) {
						first = response;
					}
					exchangesQueue.offer(response);
					response = this.pull(parameters.getEndpointName());
				}

			}
		}
	
		return response;
	}

}
