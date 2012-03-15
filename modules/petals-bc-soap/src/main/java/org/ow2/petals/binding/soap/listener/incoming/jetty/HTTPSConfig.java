
package org.ow2.petals.binding.soap.listener.incoming.jetty;

public class HTTPSConfig {
    
    private HTTPSKeystoreConfig httpsKeystoreConfig;

    private HTTPSTruststoreConfig httpsTruststoreConfig;
        
    private int httpsPort;

    private String httpsRestrictedIP;
    
    private int acceptorSize;

    HTTPSConfig(String httpsRestrictedIP, int httpsPort, int acceptorSize, HTTPSTruststoreConfig httpsTruststoreConfig, HTTPSKeystoreConfig httpsKeystoreConfig) {

        this.httpsTruststoreConfig = httpsTruststoreConfig;
        this.httpsKeystoreConfig = httpsKeystoreConfig;
        this.httpsPort = httpsPort;
        this.httpsRestrictedIP = httpsRestrictedIP;
        this.acceptorSize = acceptorSize;
    }
    
    public HTTPSConfig(String httpsRestrictedIP, int httpsPort, int acceptorSize, HTTPSKeystoreConfig httpsKeystoreConfig) {
        super();
        this.httpsKeystoreConfig = httpsKeystoreConfig;
        this.httpsPort = httpsPort;
        this.httpsRestrictedIP = httpsRestrictedIP;
        this.acceptorSize = acceptorSize;
    }

    /**
     * @return the httpsTruststoreConfig
     */
    public HTTPSTruststoreConfig getHttpsTruststoreConfig() {
        return httpsTruststoreConfig;
    }

    /**
     * @return the acceptorPoolSize
     */
    public int getAcceptorSize() {
        return acceptorSize;
    }

    /**
     * @return the httpsPort
     */
    public int getHttpsPort() {
        return httpsPort;
    }
    
    /**
     * @return the httpsRestrictedIP
     */
    public String getHttpsRestrictedIP() {
        return httpsRestrictedIP;
    }

    /**
     * @return the httpsKeystoreConfig
     */
    public HTTPSKeystoreConfig getHttpsKeystoreConfig() {
        return httpsKeystoreConfig;
    }
}
