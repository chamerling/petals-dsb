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
package org.ow2.petals.binding.restproxy.in;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public class RESTEngineContext {

    private final RESTStats restStats;

    private final Map<String, Consume> restConsumers;

    private String proxyPath;

    private String servicePath;

    private int port;

    public RESTEngineContext() {
        this.restStats = new RESTStats();
        this.restConsumers = new HashMap<String, Consume>();
    }

    /**
     * @return the restStats
     */
    public RESTStats getRestStats() {
        return this.restStats;
    }

    public String getProxyPath() {
        return this.proxyPath;
    }

    public void setProxyPath(String proxyPath) {
        this.proxyPath = proxyPath;
    }

    public String getServicePath() {
        return this.servicePath;
    }

    public void setServicePath(String servicePath) {
        this.servicePath = servicePath;
    }

    public Map<String, Consume> getRestConsumers() {
        return this.restConsumers;
    }

    public class Consume {
        private String endpointName;

        private QName serviceName;

        private QName interfaceName;

        public String getEndpointName() {
            return this.endpointName;
        }

        public void setEndpointName(String endpointName) {
            this.endpointName = endpointName;
        }

        public QName getServiceName() {
            return this.serviceName;
        }

        public void setServiceName(QName serviceName) {
            this.serviceName = serviceName;
        }

        public QName getInterfaceName() {
            return this.interfaceName;
        }

        public void setInterfaceName(QName interfaceName) {
            this.interfaceName = interfaceName;
        }
    }

    /**
     * @return
     */
    public Consume newConsume() {
        return new Consume();
    }

    /**
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return this.port;
    }
}
