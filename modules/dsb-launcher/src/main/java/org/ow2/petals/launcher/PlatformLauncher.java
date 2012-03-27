/**
 * PETALS - PETALS Services Platform.
 * Copyright (c) 2005 EBM Websourcing, http://www.ebmwebsourcing.com/
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
 * $Id$
 * -------------------------------------------------------------------------
 */

package org.ow2.petals.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.naming.ConfigurationException;
import javax.xml.bind.JAXBException;

import org.ow2.petals.jmx.JMXClient;
import org.ow2.petals.jmx.exception.ConnectionErrorException;
import org.ow2.petals.jmx.exception.PetalsAdminDoesNotExistException;
import org.ow2.petals.jmx.exception.PetalsAdminServiceErrorException;
import org.ow2.petals.topology.TopologyBuilder;
import org.ow2.petals.topology.TopologyException;
import org.ow2.petals.topology.TopologyHelper;
import org.ow2.petals.topology.generated.Container;
import org.ow2.petals.topology.generated.Topology;

/**
 * This class is used to start and manage the Petals platform from command line
 * 
 * @author alouis, ddesjardins, Rafael Marins, rnaudin, chamerling
 * @since 1.0
 */
public class PlatformLauncher extends AbstractLauncher {

    /**
     * Default constructor
     */
    public PlatformLauncher() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.launcher.AbstractLauncher#getPetalsJmxClient()
     */
    protected synchronized JMXClient getJMXClient() throws IOException, JAXBException,
            TopologyException, ConnectionErrorException, PetalsAdminDoesNotExistException,
            PetalsAdminServiceErrorException, ConfigurationException {

        if (this.jmxClient == null) {
            // retrieve resources
            URL serverPropsURL = this.getClass().getResource(SERVER_PROPS_FILE);
            if (serverPropsURL == null) {

                throw new IOException("Failed to reach the resource '" + SERVER_PROPS_FILE + "'");
            }
            File serverPropsFile = null;
            try {
                serverPropsFile = new File(serverPropsURL.toURI().normalize());
            } catch (URISyntaxException e) {
                // can not happen
            }

            URL topologyURL = this.getClass().getResource(TOPOLOGY_FILE);
            if (topologyURL == null) {
                throw new IOException("Failed to reach the resource '" + TOPOLOGY_FILE + "'");
            }
            File topologyFile = null;
            try {
                topologyFile = new File(topologyURL.toURI().normalize());
            } catch (URISyntaxException e) {
                // can not happen
            }

            // build the topology object
            Topology topology = TopologyBuilder.createTopology(topologyFile);

            // retrieve the server properties
            Properties serverProperties = new Properties();
            serverProperties.load(new FileInputStream(serverPropsFile));

            String containerName = serverProperties.getProperty(PROPERTY_CONTAINER_NAME);
            if (containerName == null) {
                containerName = System.getProperty(PROPERTY_CONTAINER_NAME);
            }

            Container localContainer = TopologyHelper.findContainer(containerName, topology);

            if (localContainer == null) {
                throw new ConfigurationException(
                        "Cannot retrieve the local container configuration");
            }

            this.jmxClient = new JMXClient(localContainer.getHost(), localContainer
                    .getJmxService().getRmiPort(), localContainer.getUser(), localContainer
                    .getPassword());
        }

        return this.jmxClient;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.petals.launcher.AbstractLauncher#getDistributionName()
     */
    @Override
    protected String getDistributionName() {
        return "platform";
    }
}
