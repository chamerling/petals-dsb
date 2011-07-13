/**
 * 
 */
package org.petalslink.dsb.monitoring.api;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author chamerling
 * 
 */
@XmlType
public class ReportListBean {

    @XmlElement
    protected List<ReportBean> reports;

    /**
     * 
     */
    public ReportListBean() {
    }

    /**
     * @return the reports
     */
    public List<ReportBean> getReports() {
        return reports;
    }

    /**
     * @param reports
     *            the reports to set
     */
    public void setReports(List<ReportBean> reports) {
        this.reports = reports;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ReportListBean [reports=");
        if (reports != null) {
            for (ReportBean bean : reports) {
                builder.append(bean.toString());
                builder.append(",");
            }
        } else {
            builder.append("Null");
        }
        builder.append("]");
        return builder.toString();
    }
}
