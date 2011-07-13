/**
 * 
 */
package org.petalslink.dsb.monitoring.api;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.petalslink.dsb.api.DSBException;

/**
 * A report client which is used to send reports to monitoring layer.
 * 
 * @author chamerling
 * 
 */
@WebService
public interface MonitoringClient {

    @WebMethod
    void send(@WebParam(name = "reportlist") ReportListBean reportList) throws DSBException;

}
