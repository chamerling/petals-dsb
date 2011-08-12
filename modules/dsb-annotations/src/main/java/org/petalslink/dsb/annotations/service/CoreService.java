/**
 * 
 */
package org.petalslink.dsb.annotations.service;

/**
 * @author chamerling
 *
 */
public @interface CoreService {
    
    /**
     * Type of service like webservice, rest, jbi...
     * 
     * @return
     */
    String type() default "";
    
    /**
     * A standard human readable description
     * 
     * @return
     */
    String description() default "";
    
    /**
     * The service name
     * 
     * @return
     */
    String name() default "";
    
    /**
     * The URL where the service description (WSDL, WADL, ...) can be found.
     * 
     * @return
     */
    String descriptionURL() default "";
}
