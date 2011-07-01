/**
 * 
 */
package org.petalslink.dsb.monitoring.api;

import java.util.List;

/**
 * TODO : XML Bean
 * @author chamerling
 *
 */
public interface ReportList {
    
    void addReport(Report report);
    
    List<Report> getReports();

}
