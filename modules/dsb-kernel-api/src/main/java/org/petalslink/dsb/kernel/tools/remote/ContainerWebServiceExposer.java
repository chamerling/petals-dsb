/**
 * 
 */
package org.petalslink.dsb.kernel.tools.remote;

/**
 * The Web services manager for all the components of the current container. The
 * manager is able to expose container components as Web services.
 * 
 * @author chamerling - PetalsLink
 * 
 */
public interface ContainerWebServiceExposer {
    
    /**
     * Expose all the components as web services. Calling it once is enough...
     */
    void expose();

}
