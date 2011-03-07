package org.ow2.petals.esb.external.protocol.soap.impl.server;

/**
 * @author chamerling - eBM WebSourcing
 * 
 */
public final class Constants {



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

        public static final int DEFAULT_HTTP_PORT = 8085;
        
        public static final String DEFAULT_HTTP_HOST = "localhost";

        public static final boolean DEFAULT_HTTP_SERVICES_LIST = true;

        public static final String DEFAULT_PROTOCOL = "http";

        public static final String DEFAULT_HTTP_SERVICES_CONTEXT = "petals";

        public static final String DEFAULT_HTTP_SERVICES_MAPPING = "services";

        public static final int DEFAULT_HTTP_THREAD_POOL_SIZE_MAX = 512;

        public static final int DEFAULT_HTTP_THREAD_POOL_SIZE_MIN = 4;

        public static final int DEFAULT_HTTP_ACCEPTORS = 4;
    }

}
