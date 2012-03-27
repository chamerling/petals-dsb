
package org.ow2.petals.binding.soap.listener.incoming.jetty;

public class HTTPConfig {

    private int httpPort;

    private String httpRestrictedIP;
    
    private int acceptorSize;

    public HTTPConfig(String httpRestrictedIP, int httpPort, int acceptorSize) {

        this.httpPort = httpPort;
        this.httpRestrictedIP = httpRestrictedIP;
        this.acceptorSize = acceptorSize;
    }

    /**
     * @return the httpPort
     */
    public int getHttpPort() {
        return httpPort;
    }

    /**
     * @return the httpRestrictedIP
     */
    public String getHttpRestrictedIP() {
        return httpRestrictedIP;
    }

    /**
     * @return the acceptorSize
     */
    public int getAcceptorSize() {
        return acceptorSize;
    }


}
