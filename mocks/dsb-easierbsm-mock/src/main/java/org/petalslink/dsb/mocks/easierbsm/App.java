package org.petalslink.dsb.mocks.easierbsm;

import java.util.concurrent.TimeUnit;

import org.petalslink.dsb.cxf.CXFHelper;
import org.petalslink.dsb.cxf.Server;

import easierbsm.petalslink.com.service.rawreport._1_0.RawReportInterface;
import easyesb.petalslink.com.service.wsdmadmin._1_0.WSDMAdminItf;

/**
 * Hello world!
 * 
 */
public class App {
    public static void main(String[] args) {
        String reportURL = "";
        String wsdmURL = "";
        Server reportServer = CXFHelper.getServiceFromFinalURL(reportURL, RawReportInterface.class,
                new org.petalslink.dsb.mocks.easierbsm.RawReportInterface());
        Server wsdmServer = CXFHelper.getServiceFromFinalURL(wsdmURL, WSDMAdminItf.class,
                new org.petalslink.dsb.mocks.easierbsm.WSDMAdminItf());

        reportServer.start();
        wsdmServer.start();

        try {
            TimeUnit.MINUTES.sleep(120);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            reportServer.stop();
        } catch (Exception e) {
        }
        try {
            wsdmServer.stop();
        } catch (Exception e) {
        }
    }
}
