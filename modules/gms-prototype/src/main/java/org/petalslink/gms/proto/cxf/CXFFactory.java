/**
 * 
 */
package org.petalslink.gms.proto.cxf;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.petalslink.gms.GMSClient;
import org.petalslink.gms.GMSClientFactory;
import org.petalslink.gms.GMSException;
import org.petalslink.gms.GMSMessage;
import org.petalslink.gms.Peer;

/**
 * @author chamerling
 * 
 */
public class CXFFactory implements GMSClientFactory {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.gms.GMSClientFactory#getClient(org.petalslink.gms.Peer)
     */
    public GMSClient getClient(final Peer peer) {
        // peer name is the port...
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setAddress("http://localhost:" + peer.getName() + "/GMSService");
        factory.setServiceClass(GMSService.class);
        final GMSService cxfClient = (GMSService) factory.create();
        Client innerClient = ClientProxy.getClient(cxfClient);
        if (innerClient != null) {
            HTTPConduit conduit = (HTTPConduit) innerClient.getConduit();
            HTTPClientPolicy policy = new HTTPClientPolicy();
            policy.setConnectionTimeout(1000);
            policy.setReceiveTimeout(1000);
            conduit.setClient(policy);
        }
        GMSClient client = new GMSClient() {
            public boolean send(GMSMessage message) throws GMSException {
                org.petalslink.gms.proto.cxf.GMSMessage toSend = new org.petalslink.gms.proto.cxf.GMSMessage();
                toSend.source = message.getSource().getName();
                toSend.type = message.getType().toString();

                try {
                    return cxfClient.receive(toSend);
                } catch (RuntimeException e) {
                    throw new GMSException("Error wile trying to send message to remote " + peer.getName());
                }
            }
        };
        return client;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.petalslink.gms.GMSClientFactory#releaseClient(org.petalslink.gms.
     * Peer)
     */
    public void releaseClient(Peer peer) {

    }

}
