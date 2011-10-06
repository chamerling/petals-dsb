/**
 * 
 */
package org.petalslink.dsb.ukernel.binding;

import org.petalslink.dsb.ws.api.ServiceEndpoint;


/**
 * Simple route definition
 * 
 * @author chamerling
 *
 */
public class Route {
    
    /**
     * The input method
     */
    String method;
    
    /**
     * The input path
     */
    String path;
    
    /**
     * the service endpoint target
     */
    ServiceEndpoint target;

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the target
     */
    public ServiceEndpoint getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(ServiceEndpoint target) {
        this.target = target;
    }

}
