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
package org.petalslink.dsb.kernel;

import java.util.List;
import java.util.Map;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface DSBConfigurationService {

    /**
     * Get the delay for embedded components to start
     * 
     * @return
     */
    long getEmbeddedComponentDelay();

    /**
     * Get the list of embedded components to start
     * 
     * @return
     */
    List<String> getEmbeddedComponentList();

    /**
     * Get the endpoints polling period
     * 
     * @return
     */
    long getEndpointsPollingPeriod();

    /**
     * Delay to start polling endpoints
     * 
     * @return
     */
    long getEndpointsPollingDelay();

    /**
     * Get the list of wsdl to bind at startup
     * 
     * @return
     */
    Map<String, List<String>> getServices2BindAtStartup();

    /**
     * @return
     */
    long getEmbeddedServicesDelay();

    int getWebAppPort();

    /**
     * The mapping between the protocol (soap, rest) and the component (bc-soap,
     * bc-rest)... ie soap -> petals-bc-soap for example....
     * 
     * @return
     */
    Map<String, String> getProtocolToComponentMapping();

    String getRemoteTransport();

    int getWSTransportPort();

    /**
     * @deprecated use {@link FederationConfigurationService}
     * 
     * @return
     */
    String getFederationURL();

    /**
     * @deprecated use {@link FederationConfigurationService}
     * 
     * @return
     */
    boolean isFederationAware();

}
