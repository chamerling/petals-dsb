package org.ow2.petals.esb.kernel.api.endpoint.behaviour;

import org.ow2.easywsdl.extensions.wsdl4complexwsdl.api.Description;
import org.ow2.petals.esb.kernel.api.endpoint.Endpoint;
import org.ow2.petals.exchange.api.Exchange;
import org.w3c.dom.Document;

public interface Behaviour<O> {

	Endpoint getEndpoint();
	
	
	O marshall(Document document) throws MarshallerException;
	
	
	Document unmarshall(O object) throws MarshallerException;
	
	
	void execute(Exchange exchange) throws BusinessException;
	
	
	Description getDescription();
	
	
	void setDescription(Description desc);
}
