package org.ow2.petals.binding.soap;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public final class Constants {

    public static class Component {
        public static final String NS_PREFIX = "soapbc";

        public static final String NS_URI = "http://petals.ow2.org/ns/soapbc";
        
        public static final String TRANSFORMER_SYSTEM_PROPERY_NAME = "javax.xml.transform.TransformerFactory";
        
        public static final String TRANSFORMER_SYSTEM_PROPERY_VALUE = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";
    }

    /**
     * Axis2 related constants
     * 
     * @author Christophe HAMERLING - eBM WebSourcing
     * 
     */
    public static class Axis2 {

        public static final String AXIS2_XML = "axis2.xml";

        public static final String SERVICES_XML = "services.xml";

        public static final String MODULE_ARCHIVE_EXTENSION = "mar";

        public static final String MODULES_PATH = "modules";

        public static final String SERVICES_PATH = "services";

        public static final String ADDRESSING_MODULE = "addressing";

        public static final String RAMPART_MODULE = "rampart";

    }

    /**
     * JBI message related constants
     * 
     * @author Christophe HAMERLING - eBM WebSourcing
     * 
     */
    public static class JbiMessage {
        private static final String WS_PREFIX = "org.ow2.petals.ws";

        public static final String SOAP_SECURITY = WS_PREFIX + ".security";

        public static final String SOAP_VERSION = WS_PREFIX + ".version";

        public static final String SOAP_HEADER = WS_PREFIX + ".header";

        public static final String SOAP_USERNAME = WS_PREFIX + ".username";

        public static final String SOAP_PASSWORD = WS_PREFIX + ".password";
    }

    /**
     * Service Unit related constants
     * 
     * @author Christophe HAMERLING - eBM WebSourcing
     * 
     */
    public static class ServiceUnit {

        public static class WSA {
            // WS-Addressing constants which are used on provider side
            public static final String TO = "wsa-to";

            public static final String REPLY_TO = "wsa-replyto";

            public static final String FROM = "wsa-from";

            public static final String FAULT_TO = "wsa-faultto";
        }

        // FIXME : Needs and ENUM...
        public static class MODE {
            // WS-Addressing constants which are used on provider side
            public static final String SOAP = "SOAP";

            public static final String REST = "REST";

            public static final String TOPIC = "TOPIC";

            public static final String JSON = "JSON";

        }

        public static class AVAILABLE_TRANSPORT {

            public static final String HTTP = "HTTP";

            public static final String JMS = "JMS";
        }

        /**
         * Use the WSA fields from the message exchange
         */
        public static final String WSA_USE = "wsa-use";

        public static final String SOAP_ACTION = "soap-action";

        /**
         * The service name which will be used for Web Service creation and for
         * Web Service exposition.
         */
        public static final String SERVICE_NAME = "service-name";

        /**
         * The topic name which will be used to create the topic...
         */
        public static final String TOPIC_NAME = "topic-name";

        @Deprecated
        public static final String ADDRESS = "address";

        public static final String MODE = "mode";

        /**
         * The modules to be used as CSV. The Addressing module is always
         * included by default.
         */
        public static final String MODULES = "modules";

        /**
         * The service parameters, used to configure service.
         */
        public static final String SERVICE_PARAMETERS = "service-parameters";

        /**
         * The soap version of the outgoing message. Default is 1.1.
         */
        public static final String SOAP_VERSION = "soap-version";

        public static final String CLEANUP_TRANSPORT = "cleanup-transport";

        public static final String CHUNKED_MODE = "chunked-mode";

        public static final String PROXY_HOST = "proxy-host";

        public static final String PROXY_PORT = "proxy-port";

        public static final String PROXY_USER = "proxy-user";

        public static final String PROXY_PASSWORD = "proxy-password";

        public static final String PROXY_DOMAIN = "proxy-domain";

        /**
         * Basic authentication username
         */
        public static final String BASIC_AUTH_USERNAME = "authentication-basic-username";

        /**
         * Basic authentication password
         */
        public static final String BASIC_AUTH_PASSWORD = "authentication-basic-password";

        /**
         * Add a root element on outgoing soap messages.
         */
        public static final String ADD_ROOT = "add-root";

        /**
         * Remove the root element from incoming soap messages before building
         * the normalized message.
         */
        public static final String REMOVE_ROOT = "remove-root";

        /**
         * The HTTP method to be used for outgoing calls (Possible values are
         * GET, POST, PUT, DELETE).
         */
        public static final String REST_HTTP_METHOD = "rest-http-method";

        /**
         * 
         */
        public static final String REST_ADD_NS_URI = "rest-add-namespace-uri";

        /**
         * 
         */
        public static final String REST_ADD_NS_PREFIX = "rest-add-namespace-prefix";

        /**
         * 
         */
        public static final String REST_REMOVE_NS_PREFIX_RESP = "rest-remove-prefix-on-response";

        /**
         * 
         */
        public static final String HEADERS_FILTER = "headers-filter";

        /**
         * 
         */
        public static final String INJECT_HEADERS = "inject-headers";

        /**
         * A QName as string.
         */
        public static final String QNAME = "qname";

        /**
         * Timeout for synchronous request
         */
        public static final String TIMEOUT = "synchronous-timeout";

        // DEFAULTS
        public static final String DEFAULT_MODE = "SOAP";

        public static final String DEFAULT_TRANSPORT = ServiceUnit.AVAILABLE_TRANSPORT.HTTP;

        /**
         * The policy path is relative to the service unit root path.
         */
        public static final String POLICY_PATH = "policy-path";

        /**
         * Name of the extension containing the flag enabling the HTTP transport
         * layer to use to send or receive SOAP messages.
         */
        public static final String ENABLE_HTTP_TRANSPORT = "enable-http-transport";

        /**
         * Name of the extension containing the flag enabling the JMS transport
         * layer to use to send or receive SOAP messages.
         */
        public static final String ENABLE_JMS_TRANSPORT = "enable-http-transport";

    }

    /**
     * Policy constants
     * 
     * @author Christophe HAMERLING - eBM WebSourcing
     * 
     */
    public static class Policy {
        public static final String POLICY_FILE = "policy.xml";
        
        public static final String SECURITY_USER_PROPERTY = "jbi:principal";
    }

    /**
     * JMS transport layer related constants
     * 
     * @author Christophe DENEUX - Capgemini Sud
     * 
     */
    public static class JmsTransportLayer {
        /**
         * Name of the extension containing the initial context factory class
         * needed to access the JNDI server where the default JMS connection
         * factory can be found.
         */
        public static final String JNDI_INITIAL_FACTORY = "java-naming-factory-initial";

        /**
         * Name of the extension containing the JNDI provider URL where the
         * default JMS connection factory can be found.
         */
        public static final String JNDI_PROVIDER_URL = "java-naming-provider-url";

        /**
         * Name of the extension containing the JNDI name of the default JMS
         * connection factory.
         */
        public static final String CONFAC_JNDINAME = "jms-connection-factory-jndiname";
    }

    /**
     * HTTP server related constants
     * 
     * @author Christophe HAMERLING - eBM WebSourcing
     * 
     */
    public static class HttpServer {
        /**
         * The HTTP port the Jetty server listen incoming request on
         */
        public static final String HTTP_PORT = "http-port";

        /**
         * The interface address to be used for the server
         */
        public static final String HTTP_HOSTNAME = "http-host";

        /**
         * Provide or not services list
         */
        public static final String HTTP_SERVICES_LIST = "http-services-list";

        /**
         * The URL context
         */
        public static final String HTTP_SERVICES_CONTEXT = "http-services-context";

        /**
         * The service mapping URL
         */
        public static final String HTTP_SERVICES_MAPPING = "http-services-mapping";

        /**
         * The max size of the HTTP pool
         */
        public static final String HTTP_THREAD_POOL_SIZE_MAX = "http-thread-pool-size-max";

        /**
         * The min size of the HTTP pool
         */
        public static final String HTTP_THREAD_POOL_SIZE_MIN = "http-thread-pool-size-min";

        /**
         * The number of HTTP acceptors
         */
        public static final String HTTP_ACCEPTORS = "http-acceptors";

        /* Default values */

        public static final int DEFAULT_HTTP_PORT = 8084;

        public static final String DEFAULT_HTTP_HOST = "localhost";

        public static final boolean DEFAULT_HTTP_SERVICES_LIST = true;

        public static final String DEFAULT_PROTOCOL = "http";

        public static final String DEFAULT_HTTP_SERVICES_CONTEXT = "petals";

        public static final String DEFAULT_HTTP_SERVICES_MAPPING = "services";

        public static final int DEFAULT_HTTP_THREAD_POOL_SIZE_MAX = 255;

        public static final int DEFAULT_HTTP_THREAD_POOL_SIZE_MIN = 2;

        public static final int DEFAULT_HTTP_ACCEPTORS = 4;
    }

    public static class SOAP {
        public static final String ERROR_WRONG_MESSAGE_STATUS = "JBI message has wrong status";

        public static final String FAULT_SERVER = "Server";

        public static final String FAULT_CLIENT = "Client";
    }

    /**
     * The notification constants<br />
     * TODO: provide the endpoint consuming automatically the broker via WS-Addressing and remove it
     * 
     * @author Frederic Gardes
     */
    public static class Notification {
        public static final String NOTIFICATION_BROKER_SERVICE = "NotificationBrokerService";

        public static final String PUBLISHER_REGISTRATION_MANAGER_SERVICE = "PublisherRegistrationManagerService";

        public static final String SUBSCRIPTION_MANAGER_SERVICE = "SubscriptionManagerService";
    }
}
