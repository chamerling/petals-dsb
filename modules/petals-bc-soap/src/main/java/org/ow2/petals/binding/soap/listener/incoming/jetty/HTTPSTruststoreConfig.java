package org.ow2.petals.binding.soap.listener.incoming.jetty;

public class HTTPSTruststoreConfig {
    private String httpsTruststoreType;

    private String httpsTruststoreFile;

    private String httpsTruststorePassword;

    public HTTPSTruststoreConfig(String httpsTruststoreType, String httpsTruststoreFile,
            String httpsTruststorePassword) {
        super();
        this.httpsTruststoreType = httpsTruststoreType;
        this.httpsTruststoreFile = httpsTruststoreFile;
        this.httpsTruststorePassword = httpsTruststorePassword;
    }

    /**
     * @return the httpsTruststoreType
     */
    public String getHttpsTruststoreType() {
        return httpsTruststoreType;
    }

    /**
     * @return the httpsTruststoreFile
     */
    public String getHttpsTruststoreFile() {
        return httpsTruststoreFile;
    }

    /**
     * @return the httpsTruststorePassword
     */
    public String getHttpsTruststorePassword() {
        return httpsTruststorePassword;
    }
}
