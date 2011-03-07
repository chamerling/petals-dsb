package org.ow2.petals.esb.kernel.impl.transport;

import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.ow2.easywsdl.extensions.wsdl4complexwsdl.WSDL4ComplexWsdlFactory;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.WSDL4ComplexWsdlWriter;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.petals.esb.kernel.api.ESBException;
import org.ow2.petals.esb.kernel.api.endpoint.ClientEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.esb.kernel.api.endpoint.ProviderEndpoint;
import org.ow2.petals.esb.kernel.api.endpoint.behaviour.BusinessException;
import org.ow2.petals.esb.kernel.api.transport.Skeleton;
import org.ow2.petals.esb.kernel.api.transport.TransportersManager;
import org.ow2.petals.esb.kernel.api.transport.WakeUpKey;
import org.ow2.petals.exchange.api.Exchange;
import org.ow2.petals.transporter.api.transport.TransportException;
import org.w3c.dom.Document;

import petals.ow2.org.exchange.PatternType;
import petals.ow2.org.exchange.RoleType;
import petals.ow2.org.exchange.StatusType;


public class SkeletonImpl implements Skeleton {

	private static Logger log = Logger.getLogger(SkeletonImpl.class.getName());


	private Endpoint providerEndpoint;

	private WSDL4ComplexWsdlWriter writer = null;

	public SkeletonImpl(Endpoint providerEndpoint) {
		this.providerEndpoint = providerEndpoint;
	}

	public TransportersManager getTransportersManager() {
		TransportersManager res = null;
		if(providerEndpoint instanceof ProviderEndpoint) {
			res = ((ProviderEndpoint)this.providerEndpoint).getNode().getTransportersManager();
		} else if(providerEndpoint instanceof ClientEndpoint) {
			res = ((ClientEndpoint)this.providerEndpoint).getNode().getTransportersManager();
		}
		return res;
	}

	public synchronized void accept(Exchange exchange) throws TransportException {
		log.finest("message accepted by endpoint provider " + this.providerEndpoint.getName() + " - Echange id: " + exchange.getUuid());
		//log.finest("exchange:\n" + exchange);
		// Change role
		if(exchange.getRole() == RoleType.CONSUMER) {
			exchange.setRole(RoleType.PROVIDER);
		} else {
			exchange.setRole(RoleType.CONSUMER);
		}
		
		if((exchange.getStatus().equals(StatusType.DONE)) || (exchange.getStatus().equals(StatusType.FAULT))) {
			log.finest("exchange finished: Status = " + exchange.getStatus() + " - Role = " + exchange.getRole());
			boolean found = false;
			if((exchange.getError().getBody().getContent() != null) || (exchange.getOut().getBody().getContent() != null))  {
				// try to wake up the stub if it is wait in sendSync
				while(this.providerEndpoint.getNode().getTransportersManager().getStub2awake().containsKey(exchange.getUuid())) {
					WakeUpKey locked = this.providerEndpoint.getNode().getTransportersManager().getStub2awake().remove(exchange.getUuid());
					synchronized(locked) {
						log.finest("wake thread => notify");
						locked.setExchange(exchange);
						locked.notifyAll();
						found = true;
					}
				}
				if(! found) {
					// rechange role
					if(exchange.getRole() == RoleType.CONSUMER) {
						exchange.setRole(RoleType.PROVIDER);
					} else {
						exchange.setRole(RoleType.CONSUMER);
					}
					
					this.providerEndpoint.getNode().getTransportersManager().push(exchange, this.providerEndpoint.getNode().getQName());
				}
			}
		} else {
			
			if(providerEndpoint instanceof ProviderEndpoint) {
				try {
					boolean found = executeDefaultProviderOperation(exchange);
					if(!found) {
						// execute behaviour of endpoint
						if(this.providerEndpoint.getBehaviourClass() != null) {
							log.finest("execute behaviour : " + this.providerEndpoint.getBehaviourClass());
							this.providerEndpoint.getBehaviour().execute(exchange);
						} else {
							log.finest("execute empty behaviour");
						}
					}
				} catch (BusinessException e) {
					throw new TransportException(e);
				} catch (IllegalArgumentException e) {
					throw new TransportException(e);
				} catch (ESBException e) {
					throw new TransportException(e);
				} 
			}

			if(exchange.getRole() == RoleType.PROVIDER) {
				
				// find node of endpoint
				Endpoint sourceEndpoint = this.providerEndpoint.getNode().getRegistry().getEndpoint(exchange.getSource());
				if(sourceEndpoint == null) {
					throw new TransportException("Impossible to find the node corresponding to this source: " + exchange.getSource());
				}
				
				// Send the response
				log.finest("*** send response by the provider " + exchange.getDestination() + " to source " + sourceEndpoint.getQName());


				if(exchange.getPattern().equals(PatternType.IN_ONLY)) {

					// set status if not already done
					exchange.setStatus(StatusType.DONE);
					
					

					// Send the status
					this.providerEndpoint.getNode().getTransportersManager().push(exchange, sourceEndpoint.getNode().getQName());
				} else if(exchange.getPattern().equals(PatternType.IN_OUT)) {

					// set status if not already done
					exchange.setStatus(StatusType.DONE);
					if(exchange.getError().getBody().getContent() != null) {
						exchange.setStatus(StatusType.FAULT);
					}

					
					this.providerEndpoint.getNode().getTransportersManager().push(exchange, sourceEndpoint.getNode().getQName());
				}
			}
		}
	}

	private WSDL4ComplexWsdlWriter getWSDLWriter() throws WSDLException {
		if(this.writer == null) {
			writer = WSDL4ComplexWsdlFactory.newInstance().newWSDLWriter();
		}
		return this.writer;
	}

	private boolean executeDefaultProviderOperation(Exchange exchange) throws BusinessException {
		boolean found = false;
		try {
			if(exchange.getOperation() != null && QName.valueOf(exchange.getOperation()).equals(new QName("org.ow2.petals.esb", "description"))) {
				found = true;
				Document wsdl;
				Description desc = this.providerEndpoint.getBehaviour().getDescription();
				//if(desc.getImportedDocuments() == null) {
				//	desc.addImportedDocumentsInWsdl();
				//}


				log.finest("find description for endpoint: " + this.providerEndpoint.getQName()); 
				wsdl = this.getWSDLWriter().getDocument(desc);


				exchange.getOut().getBody().setContent(wsdl);
			}
		} catch (WSDLException e) {
			throw new BusinessException(e);
		} catch (ESBException e) {
			throw new BusinessException(e);
		}
		return found;
	}



}
