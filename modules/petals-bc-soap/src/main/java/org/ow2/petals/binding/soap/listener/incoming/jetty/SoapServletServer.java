/**
 * 
 */

package org.ow2.petals.binding.soap.listener.incoming.jetty;

import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.security.SslSelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.log.Log;
import org.mortbay.thread.BoundedThreadPool;
import org.ow2.petals.binding.soap.SoapConstants;
import com.ebmwebsourcing.easycommons.lang.StringHelper;

/**
 * @author aruffie
 * 
 */
public class SoapServletServer {

    protected static final String WELCOME_SERVLET_NAME = "WelcomeServlet";

    protected static final String SOAP_SERVICES_LISTING_SERVLET_NAME = "ServicesListServlet";

    protected static final String SOAP_SERVICES_DISPATCHER_SERVLET_NAME = "SoapServlet";

    final Server server;

    private Context soapContext;

    private Context welcomeContext;

    private static final int HEADER_BUFFER_SIZE = 4096 * 4;

    static {
        System.setProperty("org.mortbay.log.class", JettyNullLogger.class.getName());
        Log.getLog();
    }

    public SoapServletServer(ServletServerConfig config, HttpServlet soapServicesDispatcherServlet,
            HttpServlet soapServicesListingServlet, HttpServlet welcomeServlet, Logger logger) {

        assert soapServicesDispatcherServlet != null;
        assert soapServicesListingServlet != null;
        assert welcomeServlet != null;
        assert config != null;
        assert logger != null;

        Log.setLog(new JettyLogger(logger));

        this.server = new Server();

        HTTPConfig httpConfig = config.getHttpConfig();
        if (httpConfig != null) {
            this.server.addConnector(createNIOHTTPConnector(httpConfig.getHttpPort(),
                    httpConfig.getHttpRestrictedIP(), httpConfig.getAcceptorSize()));
        }

        HTTPSConfig httpsConfig = config.getHttpsConfig();
        if (httpsConfig != null) {
            SslSelectChannelConnector nioSslConnector = createNIOHTTPSConnector(
                    httpsConfig.getHttpsPort(), httpsConfig.getHttpsRestrictedIP(),
                    httpsConfig.getAcceptorSize());

            if(httpsConfig.getHttpsKeystoreConfig() != null) {
               this.initKeyStore(nioSslConnector, httpsConfig.getHttpsKeystoreConfig());
            }
            if(httpsConfig.getHttpsTruststoreConfig() != null) {
                this.initTrustStore(nioSslConnector, httpsConfig.getHttpsTruststoreConfig());
            }

            this.server.addConnector(nioSslConnector);
        }

        this.server.setThreadPool(this.createJettyThreadPool(config.getServerMaxPoolSize(),
                config.getServerMinPoolSize()));

        // create context handlers
        final HandlerCollection handlers = new HandlerCollection();

        // create contexts
        this.soapContext = this.createSoapContext(handlers, "/" + config.getServicesContext());
        this.welcomeContext = this.createWelcomeContext(handlers, "/");

        // deploy servlets
        this.deploySoapServicesDispatcherServlet(soapServicesDispatcherServlet,
                "/" + config.getServicesMapping() + "/*");
        this.deploySoapServicesListingServlet(soapServicesListingServlet,
                "/" + config.getServicesMapping() + "/" + SoapConstants.Component.MAPPING_NAME);
        this.deployWelcomeServlet(welcomeServlet, "/*");

        this.server.setHandler(handlers);
    }

    private Context createSoapContext(HandlerCollection handlers, String contextPath) {
        final Context soapContext = new Context(handlers, contextPath, Context.SESSIONS);
        soapContext.setErrorHandler(new PetalsErrorHandler(false));
        return soapContext;
    }

    private Context createWelcomeContext(HandlerCollection handlers, String contextPath) {
        final Context welcomeContext = new Context(handlers, contextPath, Context.SESSIONS);
        return welcomeContext;
    }

    private void deploySoapServicesDispatcherServlet(HttpServlet soapServicesDispatcherServlet,
            String path) {
        final ServletHolder soapServletHolder = new ServletHolder(soapServicesDispatcherServlet);
        soapServletHolder.setName(SOAP_SERVICES_DISPATCHER_SERVLET_NAME);
        this.soapContext.addServlet(soapServletHolder, path);
    }

    private void deploySoapServicesListingServlet(HttpServlet soapServicesListingServlet,
            String path) {
        final ServletHolder listServicesServletHolder = new ServletHolder(
                soapServicesListingServlet);
        listServicesServletHolder.setName(SOAP_SERVICES_LISTING_SERVLET_NAME);
        this.soapContext.addServlet(listServicesServletHolder, path);
    }

    private void deployWelcomeServlet(HttpServlet welcomeServlet, String path) {
        final ServletHolder welcomeServletHolder = new ServletHolder(welcomeServlet);
        welcomeServletHolder.setName(WELCOME_SERVLET_NAME);
        this.welcomeContext.addServlet(welcomeServletHolder, path);
    }

    private void initTrustStore(SslSelectChannelConnector nioSslConnector, HTTPSTruststoreConfig httpsTruststoreConfig) {
        // set truststore parameters
        String httpsTruststoreFile = httpsTruststoreConfig.getHttpsTruststoreFile();
        String httpsTruststorePassword = httpsTruststoreConfig.getHttpsTruststorePassword();
        String httpsTruststoreType = httpsTruststoreConfig.getHttpsTruststoreType();

        if (httpsTruststoreFile != null && httpsTruststorePassword != null) {
            nioSslConnector.setTruststore(httpsTruststoreFile);
            nioSslConnector.setTrustPassword(httpsTruststorePassword);
            nioSslConnector.setNeedClientAuth(true);
        }

        if (httpsTruststoreType != null) {
            nioSslConnector.setTruststoreType(httpsTruststoreType);
        }
    }

    private SslSelectChannelConnector createNIOHTTPSConnector(int httpsPort, String host,
            int acceptors) {
        SslSelectChannelConnector nioSslConnector = new SslSelectChannelConnector();
        if (!StringHelper.isNullOrEmpty(host)) {
            nioSslConnector.setHost(host);
        }
        nioSslConnector.setPort(httpsPort);
        nioSslConnector.setHeaderBufferSize(HEADER_BUFFER_SIZE);
        nioSslConnector.setStatsOn(false);
        nioSslConnector.setAcceptors(acceptors);

        return nioSslConnector;
    }

    private void initKeyStore(SslSelectChannelConnector nioSslConnector, HTTPSKeystoreConfig httpsKeystoreConfig) {
        // set keystore parameters
        nioSslConnector.setKeystore(httpsKeystoreConfig.getHttpsKeystoreFile());
        nioSslConnector.setPassword(httpsKeystoreConfig.getHttpsKeystorePassword());
        nioSslConnector.setKeyPassword(httpsKeystoreConfig.getHttpsKeystoreKeyPassword());
        String httpsKeystoreType = httpsKeystoreConfig.getHttpsKeystoreType();
        if (httpsKeystoreType != null) {
            nioSslConnector.setKeystoreType(httpsKeystoreType);
        }
    }

    private SelectChannelConnector createNIOHTTPConnector(int httpPort, String host,
            int jettyAcceptors) {
        // jetty http connector configuration
        SelectChannelConnector nioConnector = new SelectChannelConnector();
        nioConnector.setPort(httpPort);

        // If we assign the host, we will only be able to contact server
        // on it. No value or a null one is a wildcard so connection is possible
        // on network interface
        // @see java.net.InetSocketAddress
        if (!StringHelper.isNullOrEmpty(host)) {
            nioConnector.setHost(host);
        }

        nioConnector.setHeaderBufferSize(HEADER_BUFFER_SIZE);
        nioConnector.setStatsOn(false);
        nioConnector.setAcceptors(jettyAcceptors);
        return nioConnector;
    }

    /**
     * Jetty threapool configuration
     * 
     * @param threadMaxPoolSize
     * @param threadMinPoolSize
     * @return
     */
    private BoundedThreadPool createJettyThreadPool(int threadMaxPoolSize, int threadMinPoolSize) {

        BoundedThreadPool threadPool = new BoundedThreadPool();
        threadPool.setName("BCSoapJettyThreadPool");
        threadPool.setMaxThreads(threadMaxPoolSize);
        threadPool.setMinThreads(threadMinPoolSize);
        return threadPool;
    }

    public void start() throws Exception {
        this.server.start();
    }

    public void stop() throws Exception {
        this.server.stop();
    }

    public boolean isRunning() {
        return this.server.isRunning();
    }
}
