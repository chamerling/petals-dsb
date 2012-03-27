package org.ow2.petals.binding.soap.listener.incoming.jetty;

public class HTTPSKeystoreConfig {
    public String httpsKeystoreKeyPassword;

    public String httpsKeystorePassword;

    public String httpsKeystoreFile;

    public String httpsKeystoreType;

    public HTTPSKeystoreConfig(String httpsKeystoreKeyPassword, String httpsKeystorePassword,
            String httpsKeystoreFile, String httpsKeystoreType) {
        super();
        this.httpsKeystoreKeyPassword = httpsKeystoreKeyPassword;
        this.httpsKeystorePassword = httpsKeystorePassword;
        this.httpsKeystoreFile = httpsKeystoreFile;
        this.httpsKeystoreType = httpsKeystoreType;
    }

    /**
     * @return the httpsKeystoreKeyPassword
     */
    public String getHttpsKeystoreKeyPassword() {
        return httpsKeystoreKeyPassword;
    }

    /**
     * @return the httpsKeystorePassword
     */
    public String getHttpsKeystorePassword() {
        return httpsKeystorePassword;
    }

    /**
     * @return the httpsKeystoreFile
     */
    public String getHttpsKeystoreFile() {
        return httpsKeystoreFile;
    }

    /**
     * @return the httpsKeystoreType
     */
    public String getHttpsKeystoreType() {
        return httpsKeystoreType;
    }
}
