/**
 * PETALS - PETALS Services Platform. Copyright (c) 2008 EBM Websourcing,
 * http://www.ebmwebsourcing.com/
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * -------------------------------------------------------------------------
 * $Id$
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.binding.soap.rest;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

/**
 * 
 * Created on 15 févr. 08
 * 
 * @author Christophe HAMERLING - eBM WebSourcing
 * @since
 * 
 */
public class RESTClient {

    private static String toEpr = "http://192.168.1.126:8084/petals/services/ExternalHelloWorld";
    
    private static String versionEPR = "http://localhost:8080/axis2/services/Version/getVersion";


    public static void main(String[] args) throws AxisFault {
        
        RESTClient client = new RESTClient();
        client.invokeRESTVersion();
    }
    
    public void invokeRESTVersion() throws AxisFault {
        Options options = new Options();
        options.setTo(new EndpointReference(versionEPR));
        options.setProperty(Constants.Configuration.ENABLE_REST, Constants.VALUE_TRUE);
        options.setProperty(Constants.Configuration.HTTP_METHOD, null);
        ServiceClient sender = new ServiceClient();
        sender.setOptions(options);

        OMElement restMessage = buildRESTVersionMessageBody();
        System.out.println(restMessage.toString());
        OMElement result = sender.sendReceive(restMessage);

        System.out.println(result.toString());

    }

    /**
     * 
     * @return
     */
    private static OMElement buildRESTVersionMessageBody() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = null;
        OMElement method = fac.createOMElement("getAxis2Version", omNs);
        OMElement value = fac.createOMElement("getAxis2Version", omNs);
        method.addChild(value);

        return method;
    }

    private static OMElement getPayload() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://example1.org/example1", "example1");
        OMElement method = fac.createOMElement("sayHello", omNs);
        OMElement value = fac.createOMElement("Text", omNs);
        value.addChild(fac.createOMText(value, "Axis2 Echo String "));
        method.addChild(value);

        return method;
    }
}
