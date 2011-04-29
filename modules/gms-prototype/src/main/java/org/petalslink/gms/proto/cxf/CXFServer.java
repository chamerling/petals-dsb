/**
 * 
 */
package org.petalslink.gms.proto.cxf;

import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.petalslink.gms.GMSListener;
import org.petalslink.gms.GMSServer;
import org.petalslink.gms.Peer;

/**
 * @author chamerling
 * 
 */
public class CXFServer implements GMSServer {

    private org.apache.cxf.endpoint.Server server;

    private Peer local;

    private GMSListener listener;

    public CXFServer(Peer local, GMSListener listener) {
        this.local = local;
        this.listener = listener;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.gms.GMSServer#startServer()
     */
    public void startServer() {
        JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
        GMSService service = new CXFGMSServiceImpl(listener);
        String address = "http://localhost:" + local.getName()+"/GMSService";
        svrFactory.setAddress(address);
        svrFactory.setServiceBean(service);
        this.server = svrFactory.create();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.petalslink.gms.GMSServer#stopServer()
     */
    public void stopServer() {
        if (this.server != null) {
            this.server.stop();
        }
    }
}
