/**
 * 
 */
package org.petalslink.dsb.mocks.easierbsm;

import java.util.UUID;

import easybox.petalslink.com.esrawreport._1.EJaxbReportListType;
import easybox.petalslink.com.esrawreport._1.EJaxbReportType;

/**
 * @author chamerling
 * 
 */
public class RawReportInterface implements
        easierbsm.petalslink.com.service.rawreport._1_0.RawReportInterface {

    /* (non-Javadoc)
     * @see easierbsm.petalslink.com.service.rawreport._1_0.RawReportInterface#addNewReportList(easybox.petalslink.com.esrawreport._1.EJaxbReportListType)
     */
    public void addNewReportList(EJaxbReportListType addNewReportListRequest) {
        String uuid = UUID.randomUUID().toString();
        System.out.println(uuid + "+ IN METHOD : addNewReportList");
        for (EJaxbReportType report : addNewReportListRequest.getReport()) {
            System.out.println(uuid + " - Report #" +report.getTimeStamp().toString() + " Consumer Endpoint : " + report.getConsumerEndpointAddress());
            System.out.println(uuid + " - Report #" +report.getTimeStamp().toString() + " Content length : " + report.getContentLength());
            System.out.println(uuid + " - Report #" +report.getTimeStamp().toString() + " Endpoint Name : " + report.getEndpointName());
            System.out.println(uuid + " - Report #" +report.getTimeStamp().toString() + " ExchangeID : " + report.getExchangeId());
            System.out.println(uuid + " - Report #" +report.getTimeStamp().toString() + " Operation : " + report.getOperationName());
            System.out.println(uuid + " - Report #" +report.getTimeStamp().toString() + " Provider Endpoint : " + report.getProviderEndpointAddress());
            System.out.println(uuid + " - Report #" +report.getTimeStamp().toString() + " Date : " + report.getDateInGMT());
            System.out.println(uuid + " - Report #" +report.getTimeStamp().toString() + " Interface Name : " + report.getInterfaceQName());
            System.out.println(uuid + " - Report #" +report.getTimeStamp().toString() + " Service Name : " + report.getServiceQName());
            System.out.println("");
        }
    }
}
