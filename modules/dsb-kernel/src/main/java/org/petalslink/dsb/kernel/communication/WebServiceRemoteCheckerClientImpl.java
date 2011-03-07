/**
 * PETALS: PETALS Services Platform Copyright (C) 2009 EBM WebSourcing
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * Initial developer(s): EBM WebSourcing
 */
package org.petalslink.dsb.kernel.communication;

import org.objectweb.fractal.fraclet.annotation.annotations.FractalComponent;
import org.objectweb.fractal.fraclet.annotation.annotations.Interface;
import org.objectweb.fractal.fraclet.annotation.annotations.LifeCycle;
import org.objectweb.fractal.fraclet.annotation.annotations.Monolog;
import org.objectweb.fractal.fraclet.annotation.annotations.Provides;
import org.objectweb.fractal.fraclet.annotation.annotations.Requires;
import org.objectweb.fractal.fraclet.annotation.annotations.type.LifeCycleType;
import org.objectweb.util.monolog.api.Logger;
import org.ow2.petals.communication.RemoteCheckerClient;
import org.ow2.petals.communication.topology.TopologyService;
import org.ow2.petals.kernel.configuration.ContainerConfiguration;
import org.ow2.petals.util.LoggingUtil;

/**
 * Remote checker fractal component based on web service communication
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
@FractalComponent
@Provides(interfaces = @Interface(name = "service", signature = RemoteCheckerClient.class))
public class WebServiceRemoteCheckerClientImpl implements RemoteCheckerClient {

    @Monolog(name = "logger")
    private Logger logger;

    private LoggingUtil log;

    @Requires(name = "topology", signature = TopologyService.class)
    private TopologyService topologyService;

    @LifeCycle(on = LifeCycleType.START)
    protected void start() {
        this.log = new LoggingUtil(this.logger);
        this.log.debug("Starting...");
    }

    @LifeCycle(on = LifeCycleType.STOP)
    protected void stop() {
        this.log.debug("Stopping...");
    }

    /**
     * {@inheritDoc}
     */
    public boolean ping(String containerName) {
        boolean result = true;
        ContainerConfiguration cc = null;
        try {
            cc = this.topologyService.getContainerConfiguration(containerName);

            if (cc == null) {
                result = false;
            } else {
                String webServiceAddress = this.getWebServiceAddress(cc);
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Pinging remote container on " + webServiceAddress);
                }
                org.petalslink.dsb.kernel.communication.RemoteCheckerClient client = WebServiceClientFactory
                        .getInstance().get(webServiceAddress);
                if (client != null) {
                    result = client.ping();
                } else {
                    this.log.warning("Can not create the client to ping the remote container");
                    result = false;
                }
            }
        } catch (Exception e) {
            this.log.warning(e.getMessage());
            result = false;
        }
        return result;
    }

    /**
     * @param cc
     * @return
     */
    private String getWebServiceAddress(ContainerConfiguration cc) {
        // TODO = Get name from the WebService annotation of
        // RemoteCheckerService interface
        return "http://" + cc.getHost() + ":" + cc.getWebservicePort() + "/"
                + cc.getWebservicePrefix() + "/" + "RemoteCheckerService";
    }

}
