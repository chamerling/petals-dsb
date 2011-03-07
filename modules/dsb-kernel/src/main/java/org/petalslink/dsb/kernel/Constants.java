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

/**
 * Here are the DSB constants
 * 
 * @author chamerling - eBM WebSourcing
 * 
 */
public interface Constants {

    static String DSB_CFG_FILE = "dsb.cfg";

    static String EMBEDDED_COMPONENT_DELAY_PROPERTY = "embedded.component.delay";

    static long EMBEDDED_COMPONENT_DELAY = 30L;

    static String EMBEDDED_SERVICES_DELAY_PROPERTY = "embedded.services.delay";

    static long EMBEDDED_SERVICES_DELAY = 45L;

    static String EMBEDDED_COMPONENT_LIST_PROPERTY = "embedded.component.list";

    static long ENDPOINTS_POLLING_PERIOD = 60L;

    static String ENDPOINTS_POLLING_PERIOD_PROPERTY = "endpoints.polling.period";

    static long ENDPOINTS_POLLING_DELAY = 120L;

    static String ENDPOINTS_POLLING_DELAY_PROPERTY = "endpoints.polling.delay";

    static String MAPPING_PREFIX = "mapping.";

    static String DEFAULT_SOAP_COMPONENT_VERSION = "4.0";

    static String WEBSERVICE_TO_BIND_AT_STARTUP_FILE = "services2bind.cfg";

    static String WSDM_CFG = "wdsm.cfg";

    static String REST_SERVICE_BINDER = "rest";

    static String SOAP_SERVICE_BINDER = "soap";

    static String SOAP_SERVICE_EXPOSER = "soap";

    static String REST_SERVICE_EXPOSER = "rest";

    static String DEFAULT_REST_COMPONENT_VERSION = "1.0";

    static String REST_PLATFORM_ENDPOINT_PREFIX = "RestPlatform";

    static String SOAP_PLATFORM_ENDPOINT_PREFIX = "SoapPlatform";

    static String FRACTAL_EMBEDDED_COMPONENTS = "EmbeddedComponentService";

    static String FRACTAL_SERVICE_POLLER = "ServicePoller";

    static String FRACTAL_LOCAL_REGISTRY = "";

    static String FRACTAL_LISTENERS_MANAGER = "LifeCycleListenerManagerImpl";

    static String WEBAPP_PORT = "webapp.port";

    static int DEFAULT_WEBAPP_PORT = 8080;

    static String REMOTE_TRANSPORT = "remote.transport";

    static int DEFAULT_TRANSPORT = 9998;

    static String FEDERATION_AWARE = "federation.aware";

    static String FEDERATION_URL = "federation.url";

}
