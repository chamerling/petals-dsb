/**
 * 
 */
package org.petalslink.dsb.api;

/**
 * WSA Constants which are commmon to all components. A component may activate a
 * WS Addressing endpoint with these values. The endpoint will be in charge of
 * sending the incoming message to the endpoint specified as WS-Addressing
 * property.
 * 
 * @author chamerling
 * 
 */
public interface WSAConstants {

    static final String ENDPOINT_NAME = "WSAEndpoint";

    static final String INTERFACE_NAME = "WSAInterface";

    static final String SERVICE_NAME = "WSAService";

    /**
     * NS Template where %s is the component name or the protocol...
     */
    static final String NS_TEMPLATE = "http://petals.ow2.org/wsa/%s/";

}
