/**
 * 
 */
package org.petalslink.dsb.transport.api;

/**
 * @author chamerling
 * 
 */
public interface Transporter extends Sender, Receiver {

    /**
     * Set the factory which is in charge of creating clients to send messages
     * to remote hosts
     * 
     * @param clientFactory
     */
    void setClientFactory(ClientFactory clientFactory);

    /**
     * Set the server which is in charge of receiving messages
     * 
     * @param server
     */
    void setServer(Server server);

    void setReceiveInterceptor(ReceiveInterceptor receiveInterceptor);

    void setSendInterceptor(SendInterceptor sendInterceptor);

}
