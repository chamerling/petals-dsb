/**
 * PETALS - PETALS Services Platform.
 * Copyright (c) 2007 EBM Websourcing, http://www.ebmwebsourcing.com/
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * -------------------------------------------------------------------------
 * $Id: TransportMonitoringImpl.java 16:40:52 ofabre $
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.monitoring.transporter;

import java.util.List;
import java.util.Map;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.transport.Transporter;
import org.ow2.petals.util.oldies.LoggingUtil;

/**
 * TODO : To be implemented!!!
 * 
 * @author ofabre
 * 
 */
@FractalComponent
@Provides(interfaces = { @Interface(name = "service", signature = org.ow2.petals.monitoring.transporter.TransportMonitoringMBean.class) })
public class TransportMonitoringImpl implements TransportMonitoringMBean {

    /**
     * the Transporter service fractal component
     */
    @Requires(name = "transporter", signature = org.ow2.petals.transport.Transporter.class)
    private Transporter transporter;

    /**
     * Logger
     */
    @Monolog(name = "logger")
    protected Logger logger;

    /**
     * Logger Wrapper
     */
    protected LoggingUtil log;

    /*
     * (non-Javadoc)
     * @see org.ow2.petals.monitoring.transporter.TransportMonitoringMBean#getCurrentQueueSizes(java.lang.String)
     */
    public Map<String, Integer> getCurrentQueueSizes(String transporterName) throws Exception {
//        try {
//            TransporterMonitor protocolMonitoring = transporter.getProtocolMonitor(transporterName);
//            if (protocolMonitoring == null) {
//                throw new Exception("The requested transporter (" + transporterName
//                        + ") doesn't exist or doesn't support monitoring");
//            }
//
//            return protocolMonitoring.getQueueSizes();
//        } catch (TransportException e) {
//            log.error("Failed to retrieve component queue sizes for transporter : "
//                    + transporterName, e);
//            throw new Exception("Failed to retrieve component queue sizes for transporter : "
//                    + transporterName);
//        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.ow2.petals.monitoring.transporter.TransportMonitoringMBean#getMaxQueueSize(java.lang.String)
     */
    public int getMaxQueueSize(String transporterName) throws Exception {
//        try {
//            TransporterMonitor protocolMonitoring = transporter.getProtocolMonitor(transporterName);
//            if (protocolMonitoring == null) {
//                throw new Exception("The requested transporter (" + transporterName
//                        + ") doesn't exist or doesn't support monitoring");
//            }
//
//            return protocolMonitoring.getQueueMaxSize();
//        } catch (TransportException e) {
//            log.error("Failed to retrieve component max queue size for transporter : "
//                    + transporterName, e);
//            throw new Exception("Failed to retrieve component max queue size for transporter : "
//                    + transporterName);
//        }
            return 0;
    }

    /*
     * (non-Javadoc)
     * @see org.ow2.petals.monitoring.transporter.TransportMonitoringMBean#getTransporters()
     */
    public List<String> getTransporters() {
        return null;
    }

    @LifeCycle(on = LifeCycleType.START)
    protected void start() throws Exception {
        this.log = new LoggingUtil(this.logger);
        this.log.call();
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() throws Exception {
        this.log.call();
    }

}
