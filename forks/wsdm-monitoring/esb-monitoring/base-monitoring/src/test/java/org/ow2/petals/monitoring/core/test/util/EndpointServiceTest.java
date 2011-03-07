package org.ow2.petals.monitoring.core.test.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.ow2.petals.endpoint.service.EndpointServiceDecorator;
import org.ow2.petals.kernel.ws.api.PEtALSWebServiceException_Exception;
import org.ow2.petals.ws._1.Endpoint;
import org.ow2.petals.ws._1.EndpointQuery;

public class EndpointServiceTest implements EndpointServiceDecorator {

	private final List<Endpoint> endpoints = new ArrayList<Endpoint>();

	private int numberOfEndpointsInList = 0;

	public EndpointServiceTest() {
		this.createListEndpoints();
	}

	private void createListEndpoints() {
		final Endpoint ep1 = new Endpoint();
		ep1.setComponent("component1");
		ep1.setContainer("container1");
		ep1.setDescription("wsdl1.wsdl");
		ep1.setName("endpoint1");
		ep1.setService(new QName("http://petals.ow2.org", "service1"));
		ep1.setSubdomain("subdomain1");
		this.endpoints.add(ep1);

		final Endpoint ep2 = new Endpoint();
		ep2.setComponent("component2");
		ep2.setContainer("container2");
		ep2.setDescription("wsdl2.wsdl");
		ep2.setName("endpoint2");
		ep2.setService(new QName("http://petals.ow2.org", "service2"));
		ep2.setSubdomain("subdomain2");
		this.endpoints.add(ep2);

		final Endpoint ep3 = new Endpoint();
		ep3.setComponent("component3");
		ep3.setContainer("container3");
		ep3.setDescription("wsdl3.wsdl");
		ep3.setName("endpoint3");
		ep3.setService(new QName("http://petals.ow2.org", "service3"));
		ep3.setSubdomain("subdomain3");
		this.endpoints.add(ep3);
	}

	public List<Endpoint> getEndpoints()
			throws PEtALSWebServiceException_Exception {
		System.out
				.println("********** Start getEndpoints operation: numberOfEndpointsInList = "
						+ this.numberOfEndpointsInList);
		final List<Endpoint> res = new ArrayList<Endpoint>();
		if (this.numberOfEndpointsInList > 0) {
			for (int i = 0; i < this.numberOfEndpointsInList; i++) {
				res.add(this.endpoints.get(i));
			}
		}
		System.out.println("End getEndpoints operation");
		return res;
	}

	public List<Endpoint> query(final EndpointQuery arg0)
			throws PEtALSWebServiceException_Exception {
		return null;
	}

	public void setNumberOfEndpointsInList(final int numberOfEndpointsInList) {
		this.numberOfEndpointsInList = numberOfEndpointsInList;
	}

}
