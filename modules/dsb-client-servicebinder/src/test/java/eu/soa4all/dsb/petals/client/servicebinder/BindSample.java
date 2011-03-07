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
package eu.soa4all.dsb.petals.client.servicebinder;

import java.util.Set;

import org.petalslink.dsb.client.servicebinder.ClientFactory;
import org.petalslink.dsb.ws.api.ServiceBinder;


/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class BindSample {

    static final String BINDER_SERVICE = "http://94.23.221.97:7600/petals/ws/ServiceBinder";

    public void bindWSDLService() throws Exception {

        // This is the wsdl of the service you want to bind
        String wsdlOfServiceToBind = "http://host:8080/services/FakeService?wsdl";

        ServiceBinder client = ClientFactory.getInstance().getServiceBinderClient(BINDER_SERVICE);
        client.bindWebService(wsdlOfServiceToBind);
    }

    public void bindRESTService() throws Exception {

        // This is the base URL of the service you want to bind
        String restBaseURI = "http://host:8080/services/RestFoo";

        ServiceBinder client = ClientFactory.getInstance().getServiceBinderClient(BINDER_SERVICE);
        client.bindRESTService(restBaseURI, null);
    }

    public void getRESTServices() throws Exception {
        ServiceBinder client = ClientFactory.getInstance().getServiceBinderClient(BINDER_SERVICE);
        Set<String> set = client.getRESTServices();
        int i = 1;
        for (String string : set) {
            System.out.println("REST Platform service #" + (i++) + " : " + string);
        }
    }

    public static void main(String[] args) throws Exception {
        BindSample sample = new BindSample();
        sample.getRESTServices();
    }
}
