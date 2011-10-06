/**
 * 
 */
package org.petalslink.dsb.ukernel.binding.http;

import org.petalslink.dsb.api.DSBException;
import org.petalslink.dsb.ukernel.binding.Engine;
import org.petalslink.dsb.ukernel.binding.commons.Router;

/**
 * @author chamerling
 *
 */
public class HTTPEngine implements Engine {
    
    private String port;
    private Router router;

    /**
     * 
     */
    public HTTPEngine(String port, Router router) {
        this.port = port;
        this.router = router;
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.kernel.api.service.Server#start()
     */
    public void start() throws DSBException {
        
    }

    /* (non-Javadoc)
     * @see org.petalslink.dsb.kernel.api.service.Server#stop()
     */
    public void stop() throws DSBException {
        
    }
    
    public Router getRouter() {
        return router;
    }

}
