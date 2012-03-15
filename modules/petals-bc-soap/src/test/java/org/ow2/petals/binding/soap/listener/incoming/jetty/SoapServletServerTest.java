/**
 * 
 */

package org.ow2.petals.binding.soap.listener.incoming.jetty;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Set;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.junit.BeforeClass;
import org.junit.Test;
import org.ow2.petals.binding.soap.SoapConstants;
import org.ow2.petals.binding.soap.util.NetworkUtil;

import com.ebmwebsourcing.easycommons.io.IOHelper;

/**
 * @author aruffie
 * 
 */
public class SoapServletServerTest {

	private final Logger testLogger = Logger.getLogger(SoapServletServerTest.class.getName());

    private static HttpServlet soapServicesDispatcher;

    private static HttpServlet soapServicesListingServlet;

    private static HttpServlet welcomeServlet;

    private static final String SERVICES_CONTEXT = "petals";

    private static final String SERVICES_MAPPING = "services";

    private static final String RESTRICTED_SERVER_IP = "127.0.0.1";

    private static final int HTTP_PORT = 9086;

    private static final int ACCEPTOR_SIZE = 5;

    private static final int SERVER_THREAD_POOL_MIN_SIZE = 10;

    private static final int SERVER_THREAD_POOL_MAX_SIZE = 50;

    private static final String HTTP_SOAP_SERVLET_SERVER_URL = "http://" + RESTRICTED_SERVER_IP
            + ":" + HTTP_PORT + "/" + SERVICES_CONTEXT + "/" + SERVICES_MAPPING + "/";

    private static final String HTTP_LIST_SERVICES_SERVLET_SERVER_URL = "http://"
            + RESTRICTED_SERVER_IP + ":" + HTTP_PORT + "/" + SERVICES_CONTEXT + "/"
            + SERVICES_MAPPING + "/" + SoapConstants.Component.MAPPING_NAME;

    private static final String HTTP_WELCOME_SERVLET_SERVER_URL = "http://"
            + RESTRICTED_SERVER_IP + ":" + HTTP_PORT + "/";
    
    private static final String HTTP_SERVLET_SERVER_URL = "http://" + RESTRICTED_SERVER_IP + ":"
            + HTTP_PORT + "/";

    private static final String HTTPS_WELCOME_SERVLET_SERVER_URL = "https://"
            + RESTRICTED_SERVER_IP + ":" + HTTP_PORT + "/";

    private static final String SERVER_KEYSTORE_FILENAME = "test.keystore";

    private static final String SERVER_KEYSTORE_KEY_PASSWORD = "testKeyPass";

    private static final String SERVER_KEYSTORE_PASSWORD = "testStorePass";

    private static final String SERVER_KEYSTORE_TYPE = "JKS";

    private static URL httpUrl;

    private static URL httpsUrl;

    @BeforeClass
    public static void beforeClass() throws Exception {
        httpUrl = new URL(HTTP_SERVLET_SERVER_URL);
        httpsUrl = new URL(HTTPS_WELCOME_SERVLET_SERVER_URL);
        soapServicesDispatcher = new ReturnServletNameServlet();
        soapServicesListingServlet = new ReturnServletNameServlet();
        welcomeServlet = new ReturnServletNameServlet();

        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
            }
        } };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }

    @Test
    public void testStartWithHttpConfWithASpecifiedIP() throws Exception {
        ServletServerConfig config = createHttpEnabledConf(RESTRICTED_SERVER_IP);
        SoapServletServer servletServerUnderTest = new SoapServletServer(config,
                soapServicesDispatcher, soapServicesListingServlet, welcomeServlet, testLogger);
        testConnectionToStartedServer(servletServerUnderTest, httpUrl);
    }
    
    @Test
    public void testStartWithHttpConfWithNoSpecifiedHost() throws Exception {
        ServletServerConfig config = createHttpEnabledConf(null);
        SoapServletServer servletServerUnderTest = new SoapServletServer(config,
                soapServicesDispatcher, soapServicesListingServlet, welcomeServlet, testLogger);
        
        Set<Inet4Address> localIPv4Addresses = NetworkUtil.getAllLocalIPv4InetAddresses();
        for(Inet4Address localIPv4Address : localIPv4Addresses) {
            URL httpUrl = new URL( "http://" + localIPv4Address.getHostAddress() + ":"  + HTTP_PORT + "/");
            testConnectionToStartedServer(servletServerUnderTest, httpUrl);
        }         
    }

    @Test
    public void testStartWithHttpsConf() throws Exception {
        ServletServerConfig config = createHttpsEnabledConf();
        SoapServletServer servletServerUnderTest = new SoapServletServer(config,
                soapServicesDispatcher, soapServicesListingServlet, welcomeServlet, testLogger);
        testConnectionToStartedServer(servletServerUnderTest, httpsUrl);
    }

    @Test
    public void testStopWithHttpConf() throws Exception {
        ServletServerConfig config = createHttpEnabledConf(RESTRICTED_SERVER_IP);
        SoapServletServer servletServerUnderTest = new SoapServletServer(config,
                soapServicesDispatcher, soapServicesListingServlet, welcomeServlet, testLogger);
        testConnectionToStoppedServer(servletServerUnderTest, httpUrl);
    }

    private void testConnectionToStartedServer(SoapServletServer servletServerUnderTest, URL testURL)
            throws IOException, Exception {
        try {
            URLConnection connection = testURL.openConnection();
            try {
                connection.connect();
                fail("Can connect to a not started server");
            } catch (ConnectException e) {
            }

            servletServerUnderTest.start();
            connection.connect();
        } finally {
            if (servletServerUnderTest.isRunning()) {
                servletServerUnderTest.stop();
            }

        }
    }

    private void testConnectionToStoppedServer(SoapServletServer servletServerUnderTest, URL testURL)
            throws Exception, IOException {
        try {
            servletServerUnderTest.start();

            URLConnection connection = testURL.openConnection();

            connection.connect();

            servletServerUnderTest.stop();

            try {
                connection = testURL.openConnection();
                connection.connect();
                fail("Can connect to a stopped server");
            } catch (ConnectException e) {
            }
        } finally {
            if (servletServerUnderTest.isRunning()) {
                servletServerUnderTest.stop();
            }

        }
    }

    @Test
    public void testSoapServletDeployment() throws Exception {
        ServletServerConfig config = createHttpEnabledConf(RESTRICTED_SERVER_IP);
        SoapServletServer servletServerUnderTest = new SoapServletServer(config,
                soapServicesDispatcher, soapServicesListingServlet, welcomeServlet, testLogger);

        this.testConnectToServlet(servletServerUnderTest, new URL(HTTP_SOAP_SERVLET_SERVER_URL),
                SoapServletServer.SOAP_SERVICES_DISPATCHER_SERVLET_NAME);
    }

    @Test
    public void testListServicesServletDeployment() throws Exception {
        ServletServerConfig config = createHttpEnabledConf(RESTRICTED_SERVER_IP);
        SoapServletServer servletServerUnderTest = new SoapServletServer(config,
                soapServicesDispatcher, soapServicesListingServlet, welcomeServlet, testLogger);

        this.testConnectToServlet(servletServerUnderTest, new URL(
                HTTP_LIST_SERVICES_SERVLET_SERVER_URL),
                SoapServletServer.SOAP_SERVICES_LISTING_SERVLET_NAME);
    }

    @Test
    public void testWelcomeServletDeployment() throws Exception {
        ServletServerConfig config = createHttpEnabledConf(RESTRICTED_SERVER_IP);
        SoapServletServer servletServerUnderTest = new SoapServletServer(config,
                soapServicesDispatcher, soapServicesListingServlet, welcomeServlet, testLogger);

        this.testConnectToServlet(servletServerUnderTest, new URL(HTTP_WELCOME_SERVLET_SERVER_URL),
                SoapServletServer.WELCOME_SERVLET_NAME);
    }

    private void testConnectToServlet(SoapServletServer servletServerUnderTest, URL testURL,
            String expectedServletResponse) throws IOException, Exception {
        BufferedReader in = null;
        try {
            servletServerUnderTest.start();
            URLConnection conn = testURL.openConnection();

            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response;
            while ((response = in.readLine()) != null) {
                assertEquals(expectedServletResponse, response);
            }
        } finally {
            IOHelper.close(in);
            if (servletServerUnderTest.isRunning()) {
                servletServerUnderTest.stop();
            }

        }
    }

    private ServletServerConfig createHttpsEnabledConf() {
        URL keystoreURL = getClass().getClassLoader().getResource(SERVER_KEYSTORE_FILENAME);
        HTTPSKeystoreConfig keystoreConfig = new HTTPSKeystoreConfig(SERVER_KEYSTORE_KEY_PASSWORD,
                SERVER_KEYSTORE_PASSWORD, keystoreURL.toString(), SERVER_KEYSTORE_TYPE);
        HTTPSConfig httpsConfig = new HTTPSConfig(RESTRICTED_SERVER_IP, HTTP_PORT, ACCEPTOR_SIZE,
                keystoreConfig);
        return new ServletServerConfig(SERVICES_MAPPING, SERVICES_CONTEXT,
                SERVER_THREAD_POOL_MAX_SIZE, SERVER_THREAD_POOL_MIN_SIZE, httpsConfig);
    }

    private static class ReturnServletNameServlet extends HttpServlet {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.servlet.http.HttpServlet#service(javax.servlet.ServletRequest,
         * javax.servlet.ServletResponse)
         */
        @Override
        public void service(ServletRequest req, ServletResponse res) throws ServletException,
                IOException {
            res.setContentType("text/html");
            res.getOutputStream().println(this.getServletName());
            res.getOutputStream().close();
        }
    }

    protected ServletServerConfig createHttpEnabledConf(String ip) {
        HTTPConfig httpConfig = new HTTPConfig(ip, HTTP_PORT, ACCEPTOR_SIZE);
        return new ServletServerConfig(SERVICES_MAPPING, SERVICES_CONTEXT,
                SERVER_THREAD_POOL_MAX_SIZE, SERVER_THREAD_POOL_MIN_SIZE, httpConfig);
    }
}
