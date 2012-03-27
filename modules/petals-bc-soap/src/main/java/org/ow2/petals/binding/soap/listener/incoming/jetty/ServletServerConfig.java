/**
 * 
 */

package org.ow2.petals.binding.soap.listener.incoming.jetty;


/**
 * @author aruffie
 * 
 */
public class ServletServerConfig {

    private String servicesMapping;

    private String servicesContext;

    private int serverMaxPoolSize;

    private int serverMinPoolSize;
    
    private HTTPConfig httpConfig;

    private HTTPSConfig httpsConfig;

    public ServletServerConfig(String servicesMapping, String servicesContext,
             int serverMaxPoolSize, int serverMinPoolSize, HTTPConfig httpConfig, HTTPSConfig httpsConfig) {
        super();
        this.serverMaxPoolSize = serverMaxPoolSize;
        this.serverMinPoolSize = serverMinPoolSize;
        this.httpConfig = httpConfig;
        this.httpsConfig = httpsConfig;
        this.servicesContext = servicesContext;
        this.servicesMapping = servicesMapping;
    }

    public ServletServerConfig(String servicesMapping, String servicesContext,
            int serverMaxPoolSize, int serverMinPoolSize, HTTPConfig httpConfig) {
        super();
        this.servicesMapping = servicesMapping;
        this.servicesContext = servicesContext;
        this.serverMaxPoolSize = serverMaxPoolSize;
        this.serverMinPoolSize = serverMinPoolSize;
        this.httpConfig = httpConfig;
    }

    public ServletServerConfig(String servicesMapping, String servicesContext,
            int serverMaxPoolSize, int serverMinPoolSize, HTTPSConfig httpsConfig) {
        super();
        this.servicesMapping = servicesMapping;
        this.servicesContext = servicesContext;
        this.serverMaxPoolSize = serverMaxPoolSize;
        this.serverMinPoolSize = serverMinPoolSize;
        this.httpsConfig = httpsConfig;
    }

    public int getServerMaxPoolSize() {
        return this.serverMaxPoolSize;
    }

    public int getServerMinPoolSize() {
        return this.serverMinPoolSize;
    }

    /**
     * @return the servicesMapping
     */
    public String getServicesMapping() {
        return servicesMapping;
    }

    /**
     * @return the servicesContext
     */
    public String getServicesContext() {
        return servicesContext;
    }
    
    /**
     * @return the httpConfig
     */
    public HTTPConfig getHttpConfig() {
        return httpConfig;
    }
    
    /**
     * @return the httpsConfig
     */
    public HTTPSConfig getHttpsConfig() {
        return this.httpsConfig;
    }
}
