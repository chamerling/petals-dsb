package org.ow2.petals.binding.soap;

import javax.xml.namespace.QName;

/**
 * @author chamerling - eBM WebSourcing
 */
public final class SoapConstants {
    
	/**
	 * Axis2 related constants
	 * 
	 * @author Christophe HAMERLING - eBM WebSourcing
	 */
	public static class Axis2 {

		public static final String AXIS2_XML = "axis2.xml";

		public static final String MODULE_ARCHIVE_EXTENSION = "mar";

		public static final String MODULES_PATH = "modules";

		public static final String SERVICES_PATH = "services";

		public static final String RAMPART_MODULE = "rampart";
	    
		/**
	     * The client service name prefix
	     */
	    public static final String OUTGOING_SERVICE_CLIENT_PREFIX = "OutgoingWSClient";
	    
	    public static final String PETALS_RECEIVER_SERVICE_PARAM = "org.ow2.petals.binding.soap.petalsReceiver";
	    
	    public static final String LOGGER_SERVICE_PARAM = "org.ow2.petals.binding.soap.logger";
	    
	    public static final String COMPONENT_CONTEXT_SERVICE_PARAM = "org.ow2.petals.binding.soap.componentContext";
	    
	    public static final String CONSUMES_SERVICE_PARAM = "org.ow2.petals.binding.soap.consumes";
	    
	    public static final String CONSUMES_EXTENSIONS_SERVICE_PARAM = "org.ow2.petals.binding.soap.consumesExtensions";
	    
	    public static final String WSDL_FOUND_SERVICE_PARAM = "org.ow2.petals.binding.soap.wsdlFound";
	    
	    public static final String SOAP_EXTERNAL_LISTENER_SERVICE_PARAM = "org.ow2.petals.binding.soap.soapExternalListener";
	}

	public static class Component {
		public static final String NS_PREFIX = "soapbc";

		public static final String NS_URI = "http://petals.ow2.org/components/soap/version-4";
		
		public static final String LOGGER_COMPONENT_NAME = "Petals.Container.Components.petals-bc-soap";
		
	    public static final String MAPPING_NAME = "listServices";
	}

	/**
	 * HTTP server related constants
	 * 
	 * @author Christophe HAMERLING - eBM WebSourcing
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

        /**
         * The HTTPS port the Jetty server listen incoming request on
         */
        public static final String HTTPS_PORT = "https-port";

        /**
         * Define if HTTPS is enabled
         */
        public static final String HTTPS_ENABLED = "https-enabled";

        /**
         * The type of the keystore (JKS / PKCS12)
         */
        public static final String HTTPS_KEYSTORE_TYPE = "https-keystore-type"; 
        
        /**
         * The absolute file path of the keystore
         */
        public static final String HTTPS_KEYSTORE_FILE = "https-keystore-file";        
        
        /**
         * The password of the keystore
         */
        public static final String HTTPS_KEYSTORE_PASSWORD = "https-keystore-password";        
        
        /**
         * The password of the key
         */
        public static final String HTTPS_KEYSTORE_KEY_PASSWORD = "https-key-password"; 

        /**
         * The type of the truststore (JKS / PKCS12)
         */
        public static final String HTTPS_TRUSTSTORE_TYPE = "https-truststore-type";    
        
        /**
         * The absolute file path of the truststore
         */
        public static final String HTTPS_TRUSTSTORE_FILE = "https-truststore-file";        
        
        /**
         * The password of the truststore
         */
        public static final String HTTPS_TRUSTSTORE_PASSWORD = "https-truststore-password"; 
        
		/* Default values */

		public static final int DEFAULT_HTTP_PORT = 8084;

	    public static final int DEFAULT_HTTPS_PORT = 8083;

		public static final boolean DEFAULT_HTTP_SERVICES_LIST = true;

		public static final String DEFAULT_HTTP_SERVICES_CONTEXT = "petals";

		public static final String DEFAULT_HTTP_SERVICES_MAPPING = "services";

		public static final int DEFAULT_HTTP_THREAD_POOL_SIZE_MAX = 255;

		public static final int DEFAULT_HTTP_THREAD_POOL_SIZE_MIN = 2;

		public static final int DEFAULT_HTTP_ACCEPTORS = 4;
	}

	/**
	 * JMS transport layer related constants
	 * 
	 * @author Christophe DENEUX - Capgemini Sud
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

    public static class WSSE {

        public static final String WSSE_QNAME_PREFIX = "wsse";

        public static final String WSSE_QNAME_LOCALNAME = "Security";

        public static final String WSSE_QNAME_NAMESPACE_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

        public static final QName WSSE_QNAME = new QName(WSSE_QNAME_NAMESPACE_URI,
                WSSE_QNAME_LOCALNAME, WSSE_QNAME_PREFIX);

    }
    
	/**
	 * Service Unit related constants
	 * 
	 * @author Christophe HAMERLING - eBM WebSourcing
	 */
	public static class ServiceUnit {

		public static class AVAILABLE_TRANSPORT {

			public static final String HTTP = "HTTP";

			public static final String JMS = "JMS";
		}

		public static class WSA {

			public static final String TO = "wsa-to";

			public static final String REPLY_TO = "wsa-replyto";

			public static final String FROM = "wsa-from";

			public static final String FAULT_TO = "wsa-faultto";
		}
        		
	    public static class COMPATIBILITY {
	            // Webservice stack to be compatible
	            public static final String AXIS1 = "AXIS1";
	    }

	    public static class HTTPS {
            // HTTPS configuration
            public static final String KEYSTORE_FILE = "https-keystore-file";

            public static final String KEYSTORE_PASSWORD = "https-keystore-password";
            
            public static final String TRUSTSTORE_FILE = "https-truststore-file";

            public static final String TRUSTSTORE_PASSWORD = "https-truststore-password";
            
            public static final int DEFAULT_HTTPS_PORT = 443;
	    }
	    
		public static final String SOAP_ACTION = "soap-action";

		/**
		 * The service name which will be used for Web Service creation and for
		 * Web Service exposition.
		 */
		public static final String SERVICE_NAME = "service-name";

		/**
		 * The address.
		 * 
		 */
		@Deprecated
		public static final String ADDRESS = "address";

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

		public static final String HEADERS_FILTER = "headers-filter";

		public static final String INJECT_HEADERS = "inject-headers";
		
		public static final String HEADERS_TO_INJECT = "headers-to-inject";

		/**
		 * The webservice stack to be compatible
		 */
	    public static final String COMPATIBILITY = "enable-compatibility-for";

		// DEFAULTS
		public static final String DEFAULT_MODE = "SOAP";

		public static final String DEFAULT_TRANSPORT = ServiceUnit.AVAILABLE_TRANSPORT.HTTP;
		
		public static final String HTTP_SERVICES_REDIRECTION = "http-services-redirection";

	    /**
         * Name of the extension containing the flag enabling the HTTPS transport
         * layer to use to send or receive SOAP messages.
         */
        public static final String ENABLE_HTTPS_TRANSPORT = "enable-https-transport";
        
		/**
		 * Name of the extension containing the flag enabling the HTTP transport
		 * layer to use to send or receive SOAP messages.
		 */
		public static final String ENABLE_HTTP_TRANSPORT = "enable-http-transport";

		/**
		 * Name of the extension containing the flag enabling the JMS transport
		 * layer to use to send or receive SOAP messages.
		 */
		public static final String ENABLE_JMS_TRANSPORT = "enable-jms-transport";
		
		/**
         * Name of the extension containing the flag enabling the WSA-Addressing
		 */
		public static final String ENABLE_WSA = "enable-wsa";
		
		/**
         * Basic authentication username
         */
        public static final String BASIC_AUTH_USERNAME = "http-basic-auth-username";

        /**
         * Basic authentication password
         */
        public static final String BASIC_AUTH_PASSWORD = "http-basic-auth-password";
	}

	public static class SOAP {
		public static final String ERROR_WRONG_MESSAGE_STATUS = "JBI message has wrong status";

		public static final String FAULT_SERVER = "Server";

		public static final String FAULT_CLIENT = "Client";
		
	    public static final String SOAP_VERSION_11 = "1.1";
	    
	    public static final String SOAP_VERSION_12 = "1.2";
	}
}
