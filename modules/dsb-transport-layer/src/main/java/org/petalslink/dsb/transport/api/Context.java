/**
 * 
 */
package org.petalslink.dsb.transport.api;


/**
 * @author chamerling
 * 
 */
public class Context {

    public String subdomainName;

    public String containerName;

    public String componentName;
    
    /**
     * Inet hostname where we can send messages to
     */
    public String hostName;
    
    /**
     * Transport port which listens to messages
     */
    public long port;

    /**
     * The QOS of the transport
     */
    public String transport;

    /**
     * The send timeout
     */
    public long timeout = 30000L;

    /**
     * Number of send attempt, if a failure occurs during the transfer
     */
    public short attempt;

    /**
     * Delay between send attempt
     */
    public int delay;

}
