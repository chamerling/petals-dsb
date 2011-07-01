/**
 * 
 */
package org.petalslink.dsb.monitoring.api;

/**
 * @author chamerling
 *
 */
public interface ReportFactory {
    
    ReportList getNewReportList();
    
    Report getNewReport();

}
