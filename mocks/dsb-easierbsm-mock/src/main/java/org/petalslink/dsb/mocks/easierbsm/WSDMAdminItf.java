/**
 * 
 */
package org.petalslink.dsb.mocks.easierbsm;

import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;

import easyesb.petalslink.com.data.wsdmadmin._1.MonitoringEndpointType;
import easyesb.petalslink.com.service.wsdmadmin._1_0.AdminExceptionMsg;

/**
 * @author chamerling
 * 
 */
public class WSDMAdminItf implements easyesb.petalslink.com.service.wsdmadmin._1_0.WSDMAdminItf {

    /*
     * (non-Javadoc)
     * 
     * @see easyesb.petalslink.com.service.wsdmadmin._1_0.WSDMAdminItf#
     * getAllMonitoringEndpoints()
     */
    public List<MonitoringEndpointType> getAllMonitoringEndpoints() throws AdminExceptionMsg {
        String uuid = UUID.randomUUID().toString();
        System.out.println(uuid + "IN : Get all Monitoring Endpoints");
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see easyesb.petalslink.com.service.wsdmadmin._1_0.WSDMAdminItf#
     * createMonitoringEndpoint(javax.xml.namespace.QName, java.lang.String,
     * boolean)
     */
    public String createMonitoringEndpoint(QName wsdmServiceName, String wsdmProviderEndpointName,
            boolean exposeInSoap) throws AdminExceptionMsg {
        String uuid = UUID.randomUUID().toString();
        System.out.println(uuid + "+ IN METHOD : createMonitoringEndpoint");
        System.out.println(uuid + " - WSDM Service Name = " + wsdmServiceName);
        System.out.println(uuid + " - WSDM ProviderEndpointName = " + wsdmProviderEndpointName);
        System.out.println(uuid + " - Expose in SOAP = " + exposeInSoap);
        
        // create a service which will be able to receive monitoring data...
        
        return "MonitoringEndpoint-" + uuid;
    }

}
