/**
 * 
 */
package org.petalslink.dsb.kernel.pubsubmonitoring.service;

import javax.xml.namespace.QName;

/**
 * @author chamerling
 * 
 */
public interface Constants {

    QName MONITORING_TOPIC = new QName("http://www.petalslink.org/resources/event/1.0",
            "MonitoringTopic", "dsb");

}
