/**
 * 
 */
package org.petalslink.dsb.easierbsm.connector;

import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.cxf.CXFHelper;
import org.petalslink.dsb.monitoring.api.MonitoringClient;
import org.petalslink.dsb.monitoring.api.ReportBean;
import org.petalslink.dsb.monitoring.api.ReportListBean;

import easierbsm.petalslink.com.service.rawreport._1_0.RawReportInterface;
import easybox.petalslink.com.esrawreport._1.EJaxbReportListType;
import easybox.petalslink.com.esrawreport._1.EJaxbReportTimeStampType;
import easybox.petalslink.com.esrawreport._1.EJaxbReportType;

/**
 * This client is in charge of sending report list to easierBSM.
 * 
 * @author chamerling
 * 
 */
public class EasierBSMClient implements MonitoringClient {

    private RawReportInterface bsmReportInterface;

    private String address;
    
    private static Logger logger = Logger.getLogger(EasierBSMClient.class.getName());

    /**
     * 
     */
    public EasierBSMClient(String address) {
        this.address = address;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.dsb.monitoring.api.MonitoringClient#send(org.petalslink
     * .dsb.monitoring.api.ReportListBean)
     */
    public void send(ReportListBean reportList) throws DSBException {
        if (reportList == null || reportList.getReports() == null) {
            final String message = "Can not send null reports...";
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(message);
            }
            throw new DSBException(message);
        }
        this.bsmReportInterface = getClient();

        EJaxbReportListType report = new EJaxbReportListType();
        for (ReportBean reportBean : reportList.getReports()) {
            report.getReport().add(transform(reportBean));
        }

        // call WS
        // TODO : asynchronous call...
        this.bsmReportInterface.addNewReportList(report);
    }

    /**
     * @param reportBean
     * @return
     */
    private EJaxbReportType transform(ReportBean reportBean) {
        EJaxbReportType result = new EJaxbReportType();
        result.setConsumerEndpointAddress(reportBean.getConsumer());
        result.setContentLength(reportBean.getContentLength());
        if (reportBean.getDate() != 0L) {
            final GregorianCalendar gCalendar = new GregorianCalendar();
            gCalendar.setTime(new Date(reportBean.getDate()));
            try {
                XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(gCalendar);
                result.setDateInGMT(xmlCalendar);
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
        }
        result.setDoesThisResponseIsAnException(reportBean.isException());
        result.setEndpointName(reportBean.getEndpoint());
        result.setExchangeId(reportBean.getExchangeId());
        if (reportBean.getItf() != null)
            result.setInterfaceQName(QName.valueOf(reportBean.getItf()));
        result.setOperationName(reportBean.getOperation());
        result.setProviderEndpointAddress(reportBean.getProvider());
        if (reportBean.getServiceName() != null)
            result.setServiceQName(QName.valueOf(reportBean.getServiceName()));
        if (reportBean.getType() != null) {
            try {
                result.setTimeStamp(EJaxbReportTimeStampType.fromValue(reportBean.getType()
                        .toLowerCase()));
            } catch (Exception e) {
            }
        }
        return result;
    }

    private synchronized RawReportInterface getClient() {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Get client");
        }
        if (bsmReportInterface == null) {
            bsmReportInterface = CXFHelper.getClientFromFinalURL(this.address,
                    RawReportInterface.class);
        }
        return bsmReportInterface;
    }
}
